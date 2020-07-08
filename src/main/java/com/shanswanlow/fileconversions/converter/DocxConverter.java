package com.shanswanlow.fileconversions.converter;

import com.shanswanlow.fileconversions.utils.PDFUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.*;
import java.util.List;
import static java.util.stream.Collectors.toList;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DocxConverter
{
    public static byte[] docxToPDF(InputStream docStream) throws IOException
    {
        List<XWPFParagraph> docParagraphs = new XWPFDocument(docStream)
                .getParagraphs();
        PDDocument output = new PDDocument();

        PDPageContentStream contentStream = PDFUtils.createWriteablePage(output);

        PDFUtils.initializeWriteablePage(contentStream);

        List<String> paragraphs = docParagraphs
                .stream()
                .map(xwpfParagraph -> xwpfParagraph.getText())
                .collect(toList());

        PDFUtils.writeTextToPage(contentStream, paragraphs);

        PDFUtils.closeWriteablePage(contentStream);

        return PDFUtils.documentToByteArray(output);
    }
}
