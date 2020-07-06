package com.shanswanlow.fileconversions.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
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
