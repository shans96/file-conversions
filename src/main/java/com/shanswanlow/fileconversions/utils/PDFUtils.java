package com.shanswanlow.fileconversions.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PDFUtils
{
    public static byte[] documentToByteArray(PDDocument document)
            throws IOException
    {
        ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
        document.save(pdfOutputStream);
        document.close();
        return pdfOutputStream.toByteArray();
    }

    public static PDPageContentStream createWriteablePage(PDDocument document)
            throws IOException
    {
        PDPage page = new PDPage();
        document.addPage(page);
        return new PDPageContentStream(document, page);
    }

    public static void closeWriteablePage(PDPageContentStream contentStream)
            throws IOException
    {
        contentStream.endText();
        contentStream.close();
    }

    public static void writeTextToPage(PDPageContentStream contentStream,
                                       List<XWPFParagraph> paragraphs) throws IOException
    {
        for (XWPFParagraph paragraph :paragraphs)
        {
            contentStream.newLineAtOffset(0, -1.5f * 12.0f);
            contentStream.showText(paragraph.getText());
        }
    }

    public static void initializeWriteablePage(PDPageContentStream contentStream)
            throws IOException
    {
        contentStream.setFont(PDType1Font.HELVETICA, 12);
        contentStream.beginText();
        contentStream.newLineAtOffset(25, 767);
    }
}
