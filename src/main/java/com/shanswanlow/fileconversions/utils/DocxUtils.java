package com.shanswanlow.fileconversions.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.util.Optional;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DocxUtils
{
    public static int getPages(XWPFDocument document)
    {
        return document
                .getProperties()
                .getExtendedProperties()
                .getUnderlyingProperties()
                .getPages();
    }

    public static String getStyledRunString(XWPFParagraph paragraph)
    {
        return paragraph
                .getRuns()
                .stream()
                .map(xwpfRun -> {
                    if (xwpfRun.isBold())
                    {
                        return "**" + xwpfRun.getText(0) + "**";
                    }
                    else if (xwpfRun.isItalic())
                    {
                        return "*" + xwpfRun.getText(0) + "*";
                    }
                    return Optional.ofNullable(xwpfRun.getText(0)).orElse("");
                })
                .collect(Collectors.joining());
    }
}
