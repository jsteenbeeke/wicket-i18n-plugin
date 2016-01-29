package nl.topicus.onderwijs.wicket.i18n.plugin.model;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import nl.topicus.onderwijs.wicket.i18n.plugin.util.ConstantFileGenerator;

import org.junit.Test;

public class WicketMessageKeyTreeTest
{
	@Test
	public void testSingleEntry()
	{
		WicketMessageKeyTree tree = new WicketMessageKeyTree();

		tree.add("test");

		Set<String> terminals = tree.getTerminals();
		assertEquals(1, terminals.size());
		assertEquals("test", terminals.iterator().next());
	}

	@Test
	public void testSingleChainedEntry()
	{
		WicketMessageKeyTree tree = new WicketMessageKeyTree();

		tree.add("test.topicus");

		Set<String> terminals = tree.getTerminals();
		assertEquals(0, terminals.size());

		Set<String> branchNames = tree.getBranchNames();
		assertEquals(1, branchNames.size());

		String next = branchNames.iterator().next();

		assertEquals("test", next);

		WicketMessageKeyTree branch = tree.getBranch(next);
		terminals = branch.getTerminals();
		assertEquals(1, terminals.size());
		assertEquals("topicus", terminals.iterator().next());

	}

	@Test
	public void testWriteTree() throws IOException
	{
		WicketMessageKeyTree tree = new WicketMessageKeyTree();
		tree.add("component.tree.whatever");
		tree.add("page.admin.users");
		tree.add("concept.user.name");
		tree.add("concept.user.email");
		tree.add("concept.cheese.name");
		tree.add("concept.cheese.odor");

		ConstantFileGenerator.writeToFile(tree, File.createTempFile("I18N", ".java"),
			"nl.topicus.i18n.test", "I18N");
	}
}
