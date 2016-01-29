package nl.topicus.onderwijs.wicket.i18n.plugin.util;

public class WicketKeyExtractionException extends RuntimeException
{

	private static final long serialVersionUID = 1L;

	public WicketKeyExtractionException()
	{
	}

	public WicketKeyExtractionException(String message)
	{
		super(message);
	}

	public WicketKeyExtractionException(Throwable cause)
	{
		super(cause);
	}

	public WicketKeyExtractionException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public WicketKeyExtractionException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
