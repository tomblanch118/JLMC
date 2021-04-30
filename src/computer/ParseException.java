package computer;

import language.Messages;

/**
 * Represents an exception when parsing the LMC assembly code.
 * Each exception also contains a reference to the offending
 * bad token and the line on which it was found.
 * @author tomblanchard
 *
 */
@SuppressWarnings("serial")
public class ParseException extends Exception {
  
  private String badToken;
  private int lineNumber;
  
  /**
   * Construct a ParseException with an error message, the offending token
   * and the bad line of source.
   * @param message The error message to be returned.
   * @param badToken The bad token that generated the exception.
   * @param lineNumber The line of the source that the badToken is from.
   */
  public ParseException(String message, String badToken, int lineNumber) {
    super(message);
    this.badToken = badToken;
    this.lineNumber = lineNumber;
  }
  
  public String getBadToken() {
    return badToken;
  }
  
  public int getLineNumber() {
    return lineNumber;
  }
  
  /**
   * Returns the message with the badToken inserted into an appropriate
   * location.
   */
  public String getMessage() {
    return Messages.insertToken(super.getMessage(), badToken);
  }

}
