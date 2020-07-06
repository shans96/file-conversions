package com.shanswanlow.fileconversions.utils;

import org.springframework.http.HttpHeaders;

public class HttpHeaderUtils
{
    public static HttpHeaders createPDFResponseHeaders(String originalFilename)
    {
        org.springframework.http.HttpHeaders responseHeaders = new HttpHeaders();
        String outputFilename = FilenameUtils
                .removeExtension(originalFilename)
                .concat(".pdf");
        responseHeaders.setContentDispositionFormData("attachment",
                outputFilename);
        return responseHeaders;
    }
}
