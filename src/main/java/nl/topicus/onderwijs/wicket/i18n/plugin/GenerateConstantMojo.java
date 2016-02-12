package nl.topicus.onderwijs.wicket.i18n.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

import nl.topicus.onderwijs.wicket.i18n.plugin.model.WicketMessageKeyTree;
import nl.topicus.onderwijs.wicket.i18n.plugin.util.ConstantFileGenerator;
import nl.topicus.onderwijs.wicket.i18n.plugin.util.Files;
import nl.topicus.onderwijs.wicket.i18n.plugin.util.WicketKeyExtractor;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.sonatype.plexus.build.incremental.BuildContext;

/**
 * Generates an internationalization class in the package specified by the user. Fills
 * this class with constants based on keys used in wicket pages.
 *
 * @author Jeroen Steenbeeke
 */
@Mojo(defaultPhase = LifecyclePhase.GENERATE_SOURCES, name = "generate", threadSafe = true,
		requiresDependencyResolution = ResolutionScope.TEST)
public class GenerateConstantMojo extends AbstractMojo
{
	/**
	 * The prefix for the generated I18N class
	 */
	@Parameter(required = true)
	public String packagePrefix;

	@Parameter(required = true, defaultValue = "I18N")
	public String rootClassName;

	/**
	 * The name of the folder to place output in {@code }, defaults to
	 * {@code $(target)/generated-sources/wicket-i18n}
	 */
	@Parameter(required = true,
			defaultValue = "${project.build.directory}/generated-sources/wicket-i18n")
	public File outputDirectory;

	@Parameter(required = true, defaultValue = "src/main/java")
	public File javaDirectory;

	@Parameter(required = true)
	public List<String> monitoredPackages;

	@Parameter(required = false)
	public List<File> propertyFiles;

	@Component
	public BuildContext buildContext;

	@Parameter(required = true, property = "project")
	protected MavenProject project;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException
	{
		if (monitoredPackages == null || packagePrefix == null)
		{
			getLog().warn("Missing monitoredPackages and/or packagePrefix, skipping I18N plugin");
			return;
		}

		getLog().info("Generating internationalization constants");

		File packageDir = createOutputDirectories();

		// if (buildContext == null || !buildContext.isIncremental())
		// {
		WicketMessageKeyTree baseTree = new WicketMessageKeyTree();
		List<File> componentHTMLFiles =
			Files.getComponentHTMLFiles(javaDirectory, monitoredPackages, getLog());

		componentHTMLFiles.forEach(f -> getLog().info("\t".concat(f.getPath())));
		componentHTMLFiles.stream().map(WicketKeyExtractor::extractKeys) //
			.flatMap(Set::stream) //
			.filter(Objects::nonNull) //
			.forEach(baseTree::add);
		if (propertyFiles != null)
		{
			propertyFiles.forEach(f -> getLog().info("\t".concat(f.getPath())));
			propertyFiles.forEach(f -> {
				Properties p = new Properties();
				try
				{
					p.load(new FileInputStream(f));
					p.forEach((k, v) -> {
						getLog().info("\t\t".concat(k.toString()));
						baseTree.add(k.toString());
					});
				}
				catch (IOException ioe)
				{
					getLog().warn(
						String.format("Failed to parse property file %s: %s", f.getPath(),
							ioe.getMessage()));
				}
			});
		}

		File output = new File(packageDir, String.format("%s.java", rootClassName));
		try
		{
			ConstantFileGenerator.writeToFile(baseTree, output, packagePrefix, rootClassName);
		}
		catch (IOException e)
		{
			throw new MojoExecutionException(e.getMessage(), e);
		}
		// }
	}

	private File createOutputDirectories() throws MojoFailureException
	{

		if (!outputDirectory.exists() && !outputDirectory.mkdirs())
		{
			throw new MojoFailureException("Could not create output folder");
		}

		project.addCompileSourceRoot(outputDirectory.getPath());

		String[] pkg = packagePrefix.split("\\.");

		File packageDir = outputDirectory;

		if (pkg[0].length() > 0)
		{
			packageDir = new File(outputDirectory, pkg[0]);
		}

		if (pkg.length > 1)
		{
			for (int i = 1; i < pkg.length; i++)
			{
				packageDir = new File(packageDir, pkg[i]);
			}
		}

		if (!packageDir.exists() && !packageDir.mkdirs())
		{
			throw new MojoFailureException(String.format("Could not create package %s in %s",
				packagePrefix, outputDirectory.getPath()));
		}

		return packageDir;
	}

}
