package language;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import ui.LocalisationListener;

/**
 * Deals with localising all of the text strings in JLMC.
 * Strings are requested and looked for in a ResourceBundle.
 * @author tomblanchard
 *
 */
public class Messages {
	
	//The search string used to find and replace tokens in strings.
	private static final String replaceTokenSearch = "\\*\\!\\!\\*";
	
	private static Locale currentLocale = null;
	private static ResourceBundle strings;
	
	//The default error message when a string cannot be found.
	private static final String error = "<LOCALISATION ERROR>";
	
	//The list of listeners that want to be informed of changes to the locale.
	private static List<LocalisationListener> listeners = new ArrayList<>();
	
	/**
	 * Sets the current locale. This will trigger all registered 
	 * {@link LocalisationListener} instances to be informed as long as
	 * the new locale is not the same as the current locale.
	 *  
	 * @param locale The new locale for the application.
	 */
	public static void setCurrentLocale(Locale locale) {
		
		//No point in relocalising if the locale is the same
		if(!locale.equals(currentLocale)) {
			currentLocale = locale;
			strings = ResourceBundle.getBundle("LMCStrings",currentLocale);
			
			for(LocalisationListener listener : listeners) {
				listener.relocalise();
			}
		}
	}

	/**
	 * Returns a translated string based on the current locale.
	 * 
	 * @param messageName The name of the string being requested. See 
	 * LMCStrings.properties for examples.
	 * 
	 * @return The translated string.
	 */
	public static String getTranslatedString(String messageName) {
		String localisedString = "";
		try {
			localisedString = strings.getString(messageName);
		} catch(MissingResourceException mre) {
			return error;
		}
		return localisedString;
	}
	
	/**
	 * Register a {@link LocalisationListener} to be informed of changes to the
	 * locale.
	 * 
	 * @param listener The listener to be added.
	 */
	public static void registerLocalisationListener(LocalisationListener listener) {
		if(!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}
	
	/**
	 * Remove a registered {@link LocalisationListener}.
	 * @param listener The {@link LocalisationListener} to be unregistered.
	 */
	public static void unregisterLocalisationListener(LocalisationListener listener) {
		listeners.remove(listener);
	}
	
	/**
	 * Inserts the token string into a localised string. This allows for simple
	 * flexibility to provide specific localised error messages and information.
	 * 
	 * Input string will be unchanged if the token could not be inserted.
	 * 
	 * @param inputString The string into which token will be inserted.
	 * @param token The token to be inserted into inputString.
	 * @return The modified string.
	 */
	public static String insertToken(final String inputString, String token){
		return inputString.replaceAll(replaceTokenSearch, token);
	}
}
