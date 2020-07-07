package com.shanswanlow.fileconversions.converter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.*;
import java.util.List;

public class DocxConverter
{
    public static byte[] docxToPDF(InputStream docStream) throws IOException
    {
        XWPFDocument document = new XWPFDocument(docStream);
        List<XWPFParagraph> docParagraphs = document.getParagraphs();

        PDDocument output = new PDDocument();
        PDPage page = new PDPage();
        output.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(output, page);

        int fontSize = 12;
        contentStream.setFont(PDType1Font.HELVETICA, fontSize);
        contentStream.beginText();
        contentStream.newLineAtOffset(25, page.getMediaBox().getHeight() - 25);

        docParagraphs
                .forEach(xwpfParagraph -> {
                    try
                    {
                        contentStream.newLineAtOffset(0, -1.5f * 12.0f);
                        contentStream.newLine();
                        contentStream.showText(xwpfParagraph.getText());
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    } // ew
                });

        contentStream.endText();
        contentStream.close();

        ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();

        output.save(pdfOutputStream);
        output.close();
        return pdfOutputStream.toByteArray();
    }
}
