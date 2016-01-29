package nl.topicus.onderwijs.wicket.i18n.plugin.util;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.maven.plugin.logging.Log;

public class Files
{
	private static final String HTML_SUFFIX = ".html";

	private static final String JAVA_SUFFIX = ".java";

	public static List<File> getComponentHTMLFiles(File baseDir, List<String> packages, Log log)
	{
		List<File> files = new ArrayList<>();

		LinkedList<File> toExplore =
			packages.stream().map(p -> getPackage(baseDir, p)).filter(Objects::nonNull)
				.collect(Collectors.toCollection(LinkedList::new));

		toExplore.stream().map(File::getPath).forEach(log::info);

		while (!toExplore.isEmpty())
		{
			File pkgDir = toExplore.remove(0);
			File[] directoryContents = pkgDir.listFiles();
			for (File f : directoryContents)
			{
				if (f.isDirectory())
				{
					toExplore.add(f);
				}
				else
				{
					if (isComponentHTMLFile(f))
					{
						files.add(f);
					}
				}
			}

		}

		return files;

	}

	private static boolean isComponentHTMLFile(File file)
	{
		if (file.exists() && !file.isDirectory())
		{
			// Must be an HTML file
			String filename = file.getName();
			if (filename.endsWith(HTML_SUFFIX))
			{
				// And have a corresponding .java file with the same name (= Wicket
				// component). This check
				// may be a bit simplistic, but the alternative is to parse the Java file
				// to determine if it subclasses Component

				String withoutExtension =
					filename.substring(0, filename.length() - HTML_SUFFIX.length());

				File javaFile =
					new File(file.getParentFile(), withoutExtension.concat(JAVA_SUFFIX));

				return javaFile.exists() && !javaFile.isDirectory();

			}
		}

		return false;
	}

	private static File getPackage(File baseDir, String packageName)
	{
		File pkg = baseDir;
		String[] subFolders = packageName.split("\\.");
		for (String s : subFolders)
		{
			pkg = new File(pkg, s);
		}

		if (!pkg.exists())
		{
			return null;
		}

		return pkg;

	}
}
