package com.shanswanlow.fileconversions.converter;

import com.shanswanlow.fileconversions.utils.DocxUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

import static com.shanswanlow.fileconversions.utils.PDFUtils.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DocxConverter
{
    public static byte[] docxToPDF(InputStream docStream) throws IOException
    {
        List<XWPFParagraph> docParagraphs = new XWPFDocument(docStream)
                .getParagraphs();
        PDDocument output = new PDDocument();

        PDPageContentStream contentStream = createWriteablePage(output);

        initializeWriteablePage(contentStream);
        writeTextToPage(contentStream, docParagraphs);
        closeWriteablePage(contentStream);

        return documentToByteArray(output);
    }

    public static byte[] docxToMarkdown(InputStream docStream) throws IOException
    {
        return new XWPFDocument(docStream)
                .getParagraphs()
                .stream()
                .map(DocxUtils::getStyledRunString)
                .collect(Collectors.joining("\n"))
                .getBytes("UTF-8");
    }
}
