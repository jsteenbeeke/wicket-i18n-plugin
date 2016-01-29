package nl.topicus.onderwijs.wicket.i18n.plugin.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import nl.topicus.onderwijs.wicket.i18n.plugin.model.WicketMessageKeyTree;

public class ConstantFileGenerator
{
	public static void writeToFile(WicketMessageKeyTree tree, File target, String packagePrefix,
			String className) throws IOException
	{
		try (PrintWriter pw = new PrintWriter(new FileWriter(target)))
		{

			pw.printf("package %s;", packagePrefix).println();
			pw.println();
			pw.printf("public class %s {\n", className).println();
			fillClass(pw, "\t", "", tree);

			pw.println("}");

			pw.flush();
		}
	}

	private static void fillClass(PrintWriter pw, String indent, String prefix,
			WicketMessageKeyTree tree)
	{
		tree.getTerminals().forEach(
			t -> {
				pw.println();
				pw.printf("%spublic static final String %s = \"%s.%s\";", indent, t.toUpperCase(),
					prefix, t).println();
			});

		tree.getBranchNames().forEach(b -> {
			WicketMessageKeyTree branch = tree.getBranch(b);

			if (branch.hasChildren())
			{

				pw.printf("%spublic static class %s {", indent, capitalizeFirst(b)).println();

				String newPrefix = prefix.isEmpty() ? b : String.format("%s.%s", prefix, b);

				fillClass(pw, "\t".concat(indent), newPrefix, branch);

				pw.printf("%s}", indent).println();
				pw.println();
			}
		});

	}

	private static String capitalizeFirst(String input)
	{
		if (input.length() == 0)
		{
			return input;
		}

		return Character.toUpperCase(input.charAt(0)) + input.substring(1);
	}
}
