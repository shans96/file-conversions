package com.shanswanlow.fileconversions.utils;

public class FilenameUtils
{
    public static String removeExtension(String filename)
    {
        return filename
                .replaceFirst("[.][^.]+$", "");
    }
}
