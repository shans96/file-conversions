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

import static com.shanswanlow.fileconversions.utils.PDFUtils.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DocxConverter
{
    public static byte[] docxToPDF(InputStream docStream) throws IOException
    {
        List<XWPFParagraph> docParagraphs = new XWPFDocument(docStream)
                .getParagraphs();
        PDDocument output = new PDDocument();

        PDPageContentStream contentStream = PDFUtils.createWriteablePage(output);

        initializeWriteablePage(contentStream);
        writeTextToPage(contentStream, docParagraphs);
        closeWriteablePage(contentStream);

        return PDFUtils.documentToByteArray(output);
    }
}
