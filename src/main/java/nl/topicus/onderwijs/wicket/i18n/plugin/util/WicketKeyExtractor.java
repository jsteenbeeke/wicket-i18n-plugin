package nl.topicus.onderwijs.wicket.i18n.plugin.util;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.apache.wicket.Application;
import org.apache.wicket.ThreadContext;
import org.apache.wicket.markup.Markup;
import org.apache.wicket.markup.MarkupParser;
import org.apache.wicket.markup.MarkupResourceStream;
import org.apache.wicket.markup.WicketTag;
import org.apache.wicket.markup.parser.filter.WicketMessageTagHandler;
import org.apache.wicket.mock.MockApplication;
import org.apache.wicket.mock.MockWebResponse;
import org.apache.wicket.protocol.http.mock.MockHttpServletRequest;
import org.apache.wicket.protocol.http.mock.MockHttpSession;
import org.apache.wicket.protocol.http.mock.MockServletContext;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;

public class WicketKeyExtractor
{
	private static final Random random = new Random();

	public static Set<String> extractKeys(File file)
	{
		if (!file.exists())
		{
			throw new WicketKeyExtractionException(String.format("File %s does not exist",
				file.getAbsolutePath()));
		}

		triggerWicket();

		Set<String> keys = new TreeSet<>();

		MarkupParser parser =
			new MarkupParser(new MarkupResourceStream(new FileResourceStream(file)));
		parser.add(new WicketMessageTagHandler());

		try
		{
			Markup markup = parser.parse();
			markup.forEach(me -> {
				if (me instanceof WicketTag)
				{
					WicketTag wt = (WicketTag) me;
					if (wt.isMessageTag() && !wt.isClose())
					{
						String attribute = wt.getAttribute("key");
						if (attribute != null)
						{
							keys.add(attribute);
						}
					}
				}

			});

		}
		catch (IOException | ResourceStreamNotFoundException e)
		{
			// Wrap
			throw new WicketKeyExtractionException(e);
		}

		return keys;
	}

	private static void triggerWicket()
	{
		if (!Application.exists())
		{
			MockApplication app = new MockApplication();
			app.setName(String.format("mock%d", random.nextInt()));
			MockServletContext servletContext = new MockServletContext(app, "/mock");
			app.setServletContext(servletContext);
			ThreadContext.setApplication(app);
			app.initApplication();

			if (RequestCycle.get() == null)
			{
				MockHttpSession sess = new MockHttpSession(servletContext);
				WebRequest webRequest =
					app.newWebRequest(new MockHttpServletRequest(app, sess, servletContext),
						"/mock");

				RequestCycle requestCycle =
					app.createRequestCycle(webRequest, new MockWebResponse());
				ThreadContext.setRequestCycle(requestCycle);
			}

		}

	}
}
