package com.shanswanlow.fileconversions.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FilenameUtils
{
    public static String removeExtension(String filename)
    {
        return filename
                .replaceFirst("[.][^.]+$", "");
    }
}
