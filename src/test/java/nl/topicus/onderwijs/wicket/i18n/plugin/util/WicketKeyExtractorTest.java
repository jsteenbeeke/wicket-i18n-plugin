package nl.topicus.onderwijs.wicket.i18n.plugin.util;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Set;

import org.junit.Test;

public class WicketKeyExtractorTest
{
	@Test
	public void testExtractor()
	{
		Set<String> extractedKeys =
			WicketKeyExtractor.extractKeys(new File(
				"src/test/resources/WicketKeyExtractorTest.html"));

		assertEquals(1, extractedKeys.size());
		assertTrue(extractedKeys.contains("wicket.key.extractor"));
	}
}
