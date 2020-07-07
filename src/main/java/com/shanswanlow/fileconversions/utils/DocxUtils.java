package com.shanswanlow.fileconversions.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

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
}
