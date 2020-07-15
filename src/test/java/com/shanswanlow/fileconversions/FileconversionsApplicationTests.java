package com.shanswanlow.fileconversions;

import com.shanswanlow.fileconversions.converter.DocxConverter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.shanswanlow.fileconversions.converter.DocxConverter.docxToPDF;
import static com.shanswanlow.fileconversions.utils.DocxUtils.*;
import static com.shanswanlow.fileconversions.utils.FilenameUtils.*;
import static com.shanswanlow.fileconversions.utils.HttpHeaderUtils.*;
import static com.shanswanlow.fileconversions.utils.PDFUtils.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FileconversionsApplicationTests
{
	@Test
	void contextLoads() { }

	@Test
	@DisplayName("Ensure that file extension removal removes the extension only.")
	void testFileExtensionRemoval()
	{
		String caseOne = "foo.pdf";
		String caseTwo = "bar.docx";
		String caseThree = "baz.quux.doc";
		String caseFour = "quuux";

		assertAll("filenameExtensions",
				() -> assertEquals("foo", removeExtension(caseOne)),
				() -> assertEquals("bar", removeExtension(caseTwo)),
				() -> assertEquals("baz.quux", removeExtension(caseThree)),
				() -> assertEquals("quuux", removeExtension(caseFour))
		);
	}

	@Test
	@DisplayName("Verify that the PDF HTTP headers generated are as expected.")
	void testPDFHttpHeaderGeneration()
	{
		HttpHeaders expectedHeaders = new HttpHeaders();
		expectedHeaders.setContentDispositionFormData("attachment", "foo.pdf");
		HttpHeaders generatedHeaders = createPDFResponseHeaders("foo.doc");

		assertEquals(expectedHeaders.hashCode(), generatedHeaders.hashCode());
	}

	@Test
	@DisplayName("Verify that the correct page count is returned.")
	void testPageCountRetrieval() throws IOException
	{
		XWPFDocument emptyDocument = new XWPFDocument();
		XWPFDocument googleDocument = new XWPFDocument(
				new FileInputStream("src/test/resources/Google Docs lipsum.docx"));
		XWPFDocument wordDocument = new XWPFDocument(
				new FileInputStream("src/test/resources/MS Word lipsum.docx"));

		// Note: Google Docs does not add docProps when it creates a docx, so 0 is expected.
		assertAll("pageCount",
				() -> assertEquals(0, getPages(emptyDocument)),
				() -> assertEquals(0, getPages(googleDocument)),
				() -> assertEquals(4, getPages(wordDocument)));
	}

	@Test
	@DisplayName("Verify that documentToByteArray returns a PDF document without any modifications.")
	void testDocumentToByteArray() throws IOException
	{
		// Note: This method is not testable at a binary level due to changing metadata such as creation date.
		PDDocument testDocument = new PDDocument();
		PDPage page = new PDPage();
		testDocument.addPage(page);

		PDDocument computedDocument = PDDocument.load(
				documentToByteArray(testDocument));
		PDDocument expectedDocument = PDDocument.load(
				new File("src/test/resources/emptyGeneratedDoc.pdf"));

		PDDocumentInformation expectedInfo = expectedDocument.getDocumentInformation();
		PDDocumentInformation computedInfo = computedDocument.getDocumentInformation();

		assertAll("PDFGeneration",
				() -> assertEquals(expectedDocument.getNumberOfPages(), computedDocument.getNumberOfPages()),
				() -> assertEquals(expectedInfo.getCreator(), computedInfo.getCreator()),
				() -> assertEquals(expectedInfo.getSubject(), computedInfo.getSubject()),
				() -> assertEquals(expectedInfo.getTitle(), computedInfo.getTitle()),
				() -> assertEquals(expectedInfo.getKeywords(), computedInfo.getKeywords()),
				() -> assertEquals(expectedInfo.getAuthor(), computedInfo.getAuthor())
		);
	}

	@Test
	@DisplayName("Verify that writing to a page renders the exact text.")
	void testWriteTextToPage() throws IOException
	{
		List<XWPFParagraph> testParagraphs = new XWPFDocument(
				new FileInputStream("src/test/resources/Google Docs stylized document.docx"))
				.getParagraphs();

		String expectedText = "foo\r\nbar\r\nbaz\r\nquux\r\n";
		PDDocument testDocument = new PDDocument();
		PDPage testPage = new PDPage();
		testDocument.addPage(testPage);
		PDPageContentStream contentStream = new PDPageContentStream(testDocument, testPage);
		contentStream.beginText();
		contentStream.setFont(PDType1Font.HELVETICA, 12);

		writeTextToPage(contentStream, testParagraphs);

		contentStream.endText();
		contentStream.close();

		ByteArrayOutputStream documentOutputStream = new ByteArrayOutputStream();
		testDocument.save(documentOutputStream);
		testDocument.close();

		PDDocument createdDocument = PDDocument.load(documentOutputStream
				.toByteArray());

		String strippedText = new PDFTextStripper().getText(createdDocument);
		String expectedTextTrimmed = expectedText
				.replace("\r", "")
				.replace("\n", "");
		String strippedTextTrimmed = strippedText
				.replace("\r", "")
				.replace("\n", "");
		assertEquals(expectedTextTrimmed, strippedTextTrimmed);
	}

	@Test
	@DisplayName("Verify that a closed document cannot be written to.")
	void testCloseWriteablePage() throws IOException
	{
		PDDocument testDocument = new PDDocument();
		PDPage testPage = new PDPage();
		testDocument.addPage(testPage);
		PDPageContentStream contentStream = new PDPageContentStream(testDocument, testPage);
		contentStream.beginText();
		closeWriteablePage(contentStream);
		// Resources released, stream.output becomes null. Underlying method uses output.
		assertThrows(NullPointerException.class, contentStream::beginText);
	}

	@Test
	@DisplayName("Verify that initializing a writeable page sets the correct mode.")
	void testInitializeWriteablePage() throws IOException
	{
		PDDocument testDocument = new PDDocument();
		PDPage testPage = new PDPage();
		testDocument.addPage(testPage);
		PDPageContentStream contentStream = new PDPageContentStream(testDocument, testPage);
		initializeWriteablePage(contentStream);
		boolean isInTextMode = (Boolean)
				ReflectionTestUtils.getField(contentStream, PDPageContentStream.class, "inTextMode");
		assertTrue(isInTextMode);
	}

	@Test
	@DisplayName("Test that docx to PDF conversion copies the text exactly.")
	void testDocxToPDF() throws IOException
	{
		XWPFDocument docx = new XWPFDocument(
				new FileInputStream("src/test/resources/MS Word lipsum.docx"));
		byte[] convertedDocx = docxToPDF(
				new FileInputStream("src/test/resources/MS Word lipsum.docx"));
		PDDocument pdfDocument = PDDocument.load(convertedDocx);

		String strippedText = new PDFTextStripper()
				.getText(pdfDocument);

		List<String[]> documentParagraphs = docx
				.getParagraphs()
				.stream()
				.map(xwpfParagraph -> getTestDocWords(xwpfParagraph.getText()))
				.collect(Collectors.toList());


		List<String> words = new ArrayList<>();

		for (String[] paragraph: documentParagraphs)
		{
			for (String word: paragraph)
			{
				words.add(word);
			}
		}

		String[] docxWords = words.toArray(new String[words.size()]);

		String[] pdfWords = getTestDocWords(strippedText);

		assertArrayEquals(docxWords, pdfWords);
	}

	String[] getTestDocWords(String text) {
		return text.replace(",", "")
				.replace(".", "")
				.replace("\r", " ")
				.replace("\n", "")
				.split(" ");
	}
}
