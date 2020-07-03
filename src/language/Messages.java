package language;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import ui.LocalisationListener;

//import java.util.HashMap;

//Should really do this the right way

//TODO: https://docs.oracle.com/javase/tutorial/i18n/intro/steps.html
public class Messages {
	private static final String replaceToken = "*!!*";
	private static final String replaceTokenSearch = "\\*\\!\\!\\*";
	private static Locale currentLocale = null;
	
	private static ResourceBundle strings;
	
	private static final String error = "<LOCALISATION ERROR>";
	
	private static List<LocalisationListener> listeners = new ArrayList<>();
	
	public static void setCurrentLocale(Locale locale) {

		if(!locale.equals(currentLocale)) {
			currentLocale = locale;
			strings = ResourceBundle.getBundle("LMCStrings",currentLocale);
			
			for(LocalisationListener listener : listeners) {
				listener.relocalise();
			}
		}
	}

	public static String getTranslatedString(String messageName) {
		return strings.getString(messageName);
	}
	
	public static void registerLocalisationListener(LocalisationListener listener) {
		listeners.add(listener);
	}
	
	
	public static String insertToken(final String inputString, String token)
	{
		String replacedString = inputString.replaceAll(replaceTokenSearch, token);
		return replacedString;
	}
}
