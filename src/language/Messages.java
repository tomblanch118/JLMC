package language;

//import java.util.HashMap;

//TODO: https://docs.oracle.com/javase/tutorial/i18n/intro/steps.html
public class Messages {
	private static final String replaceToken = "*!!*";
	private static final String replaceTokenSearch = "\\*\\!\\!\\*";


	public static final String RUN_BUTTON = "Run";
	public static final String STOP_BUTTON = "Stop";
	public static final String STEP_BUTTON = "Step";
	public static final String FASTER_BUTTON = "Faster";
	public static final String SLOWER_BUTTON = "Slower";
	public static final String RESET_BUTTON = "Reset";
	public static final String CPU_SPEED = "CPU Speed";
	public static final String COMPILE_BUTTON = "Compile";
	public static final String EDITOR_SUCCESS_MESSAGE = "Code Assembled";
	public static final String L_ERR_NOT_DEFINED = "Label Error. Label '"+replaceToken+"' is not defined.";
	public static final String S_ERR_INSTRUCTION_EXPECTED = "Syntax error. Expected an instruction but found '"+replaceToken+"'.";
	public static final String S_ERR_POINTLESS_PARAMETER = "Syntax error. Instruction '"+replaceToken+"' doesn't take a parameter.";
	public static final String S_ERR_PARAMETER_EXPECTED = "Syntax error. Expected label or numeric address after '"+replaceToken+"'.";
	public static final String S_ERR_LABEL_NAME = "Syntax error. Label '"+replaceToken+"' has the same name as an instruction.";
	public static final String L_ERR_LABEL_UNLINKED = "Label Error. Label '"+replaceToken+"' does not link to an address.";
	public static final String S_ERR_TOO_MANY_TOKENS = "Syntax error. Too many tokens on line.";
	public static final String S_ERR_NUMBER_NOT_LABEL = "Syntax error. Cannot use the number '"+replaceToken+"' as a label";
	public static final String S_ERR_LONELY_LABEL = "Syntax Error. Cannot have a label by itself on a line.";
	public static final String S_ERR_ADDRESS = "Syntax error. Address "+replaceToken+" not valid. May be too large or small.";
	public static final String INPUT_PLEASE = "Please provide input:";
	public static final String INPUT_TITLE = "Computer needs input";
	public static final String NO_INPUT_FOUND = "No input found. Computer reset.";
	public static final String INPUT_MUST_NUMBER = "Input must be an integer number. Computer reset.";
	public static final String HALTED = "Computer halted.";
	public static final String S_ERR_COMMENT_EOL = "Syntax Error. Comments must go at the end of a line.";
	
	
	public static String insertToken(final String inputString, String token)
	{
		String replacedString = inputString.replaceAll(replaceTokenSearch, token);
		return replacedString;
	}
}
