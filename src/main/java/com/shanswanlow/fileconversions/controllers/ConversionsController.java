package com.shanswanlow.fileconversions.controllers;

import com.shanswanlow.fileconversions.converter.DocxConverter;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.shanswanlow.fileconversions.utils.HttpHeaderUtils.createFileResponseHeaders;

@RestController
public class ConversionsController
{
    @PostMapping(value = "/docx-to-pdf",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> wordToPDF(@RequestParam("file") MultipartFile document)
            throws IOException
    {
        byte[] convertedDocument = DocxConverter.docxToPDF(document.getInputStream());
        return new ResponseEntity<>(convertedDocument,
                createFileResponseHeaders(document.getOriginalFilename(), ".pdf"),
                HttpStatus.OK);
    }
}
