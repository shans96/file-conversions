package com.shanswanlow.fileconversions;

import com.shanswanlow.fileconversions.utils.DocxUtils;
import com.shanswanlow.fileconversions.utils.FilenameUtils;
import com.shanswanlow.fileconversions.utils.HttpHeaderUtils;
import com.shanswanlow.fileconversions.utils.PDFUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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
				() -> assertEquals("foo", FilenameUtils.removeExtension(caseOne)),
				() -> assertEquals("bar", FilenameUtils.removeExtension(caseTwo)),
				() -> assertEquals("baz.quux", FilenameUtils.removeExtension(caseThree)),
				() -> assertEquals("quuux", FilenameUtils.removeExtension(caseFour))
		);
	}

	@Test
	@DisplayName("Verify that the PDF HTTP headers generated are as expected.")
	void testPDFHttpHeaderGeneration()
	{
		HttpHeaders expectedHeaders = new HttpHeaders();
		expectedHeaders.setContentDispositionFormData("attachment", "foo.pdf");
		HttpHeaders generatedHeaders = HttpHeaderUtils
				.createPDFResponseHeaders("foo.doc");

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
				() -> assertEquals(0, DocxUtils.getPages(emptyDocument)),
				() -> assertEquals(0, DocxUtils.getPages(googleDocument)),
				() -> assertEquals(4, DocxUtils.getPages(wordDocument)));
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
				PDFUtils.documentToByteArray(testDocument));
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
		List<String> testParagraphs = Arrays.asList("foo", "bar");
		String expectedText = "foo\r\nbar\r\n";
		PDDocument testDocument = new PDDocument();
		PDPage testPage = new PDPage();
		testDocument.addPage(testPage);
		PDPageContentStream contentStream = new PDPageContentStream(testDocument, testPage);
		contentStream.beginText();
		contentStream.setFont(PDType1Font.HELVETICA, 12);

		PDFUtils.writeTextToPage(contentStream, testParagraphs);

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
				.replace("\n", "")
				.trim();
		String strippedTextTrimmed = strippedText
				.replace("\r", "")
				.replace("\n", "")
				.trim();
		assertEquals(expectedTextTrimmed, strippedTextTrimmed);
	}

}
