package com.shanswanlow.fileconversions;

import com.shanswanlow.fileconversions.utils.FilenameUtils;
import com.shanswanlow.fileconversions.utils.HttpHeaderUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FileconversionsApplicationTests
{
	@Test
	void contextLoads() { }

	@Test
	@DisplayName("Ensure that file extension removal removes the extension only.")
	void testFileExtensionRemoval()
	{
		String caseOne = "foo.pdf";
		String caseTwo = "bar.docx";
		String caseThree = "baz.quux.doc";
		String caseFour = "quuux";

		assertAll("filenameExtensions",
				() -> assertEquals("foo", FilenameUtils.removeExtension(caseOne)),
				() -> assertEquals("bar", FilenameUtils.removeExtension(caseTwo)),
				() -> assertEquals("baz.quux", FilenameUtils.removeExtension(caseThree)),
				() -> assertEquals("quuux", FilenameUtils.removeExtension(caseFour))
		);
	}

	@Test
	@DisplayName("Verify that the PDF HTTP headers generated are as expected.")
	void testPDFHttpHeaderGeneration()
	{
		HttpHeaders expectedHeaders = new HttpHeaders();
		expectedHeaders.setContentDispositionFormData("attachment", "foo.pdf");
		HttpHeaders generatedHeaders = HttpHeaderUtils
				.createPDFResponseHeaders("foo.doc");
		assertEquals(expectedHeaders.hashCode(), generatedHeaders.hashCode());
	}
}
