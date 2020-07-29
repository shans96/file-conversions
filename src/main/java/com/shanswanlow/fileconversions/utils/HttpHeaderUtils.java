package com.shanswanlow.fileconversions.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpHeaderUtils
{
    public static HttpHeaders createFileResponseHeaders(String originalFilename,
                                                        String newExtension)
    {
        org.springframework.http.HttpHeaders responseHeaders = new HttpHeaders();
        String outputFilename = FilenameUtils
                .removeExtension(originalFilename)
                .concat(newExtension);
        responseHeaders.setContentDispositionFormData("attachment",
                outputFilename);
        return responseHeaders;
    }
}
