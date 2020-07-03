package com.shanswanlow.fileconversions.utils;

public class FilenameUtils
{
    public FilenameUtils()
    {
        throw new IllegalStateException("Utility class, cannot be instantiated");
    }

    public static String removeExtension(String filename)
    {
        return filename
                .replaceFirst("[.][^.]+$", "");
    }
}
