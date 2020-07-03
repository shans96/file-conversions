package com.shanswanlow.fileconversions;

import com.shanswanlow.fileconversions.utils.FilenameUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
				() -> assertEquals(FilenameUtils.removeExtension(caseOne), "foo"),
				() -> assertEquals(FilenameUtils.removeExtension(caseTwo), "bar"),
				() -> assertEquals(FilenameUtils.removeExtension(caseThree), "baz.quux"),
				() -> assertEquals(FilenameUtils.removeExtension(caseFour), "quuux")
		);
	}
}
