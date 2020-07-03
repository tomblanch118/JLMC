package computer;

import language.Messages;

@SuppressWarnings("serial")
public class ParseException extends Exception {
	
	private String badToken;
	private int lineNumber;
	

	public ParseException(String message, String badToken, int lineNumber)
	{
		super(message);
		this.badToken = badToken;
		this.lineNumber = lineNumber;
	}
	
	public String getBadToken()
	{
		return badToken;
	}
	
	public int getLineNumber()
	{
		return lineNumber;
	}
	
	public String getMessage()
	{
		return Messages.insertToken(super.getMessage(), badToken);
	}

}
