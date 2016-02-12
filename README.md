# wicket-i18n-plugin

A Maven plugin for gathering `wicket:message` keys from Wicket components and property files into a convenient constant class.

## Configuration

Add the following segment to the `<plugins>` section of your POM's `<build>` tag.

```xml
<plugin>
	<groupId>nl.topicus.onderwijs</groupId>
	<artifactId>wicket-i18n-maven-plugin</artifactId>
	<version>1.0-SNAPSHOT</version>
	<configuration>
		<packagePrefix>com.periselene.accounting.web.i18n</packagePrefix>
		<monitoredPackages>
			<monitoredPackage>path.to.your.web.package</monitoredPackage>
		</monitoredPackages>
	</configuration>
	<executions>
		<execution>
			<id>generate-sources</id>
			<phase>generate-sources</phase>
			<goals>
				<goal>generate</goal>
			</goals>
		</execution>
	</executions>
</plugin>
```

This will create a minimal config that will scan the indicated `monitoredPackage`, and read the keys from any Wicket component HTML file it finds. In addition to this basic setup, there are a number of options to expand the functionality of this plugin.

| Option            | Required                                               | Type            |
| ----------------- |:------------------------------------------------------:| --------------- |
| packagePrefix     | Yes                                                    | String          |
| monitoredPackages | Yes                                                    | List of Strings |
| propertyFiles     | No                                                     | List of Strings |
| rootClassName     | No (defaults to `I18N`)                                | String          |
| javaDirectory     | No (defaults to `src/main/java`)                       | String          |
| outputDirectory   | No (defaults to `target/generated-sources/wicket-i18n` | String          |

## Execution

To run the plugin, simply run `mvn generate-sources` in your project directory.
