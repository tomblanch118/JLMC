package computer;

import java.util.Locale;

import javax.swing.SwingUtilities;

import computer.model.Computer;
import computer.model.Editor;
import language.Messages;
import ui.ComputerPanel;
import ui.ControlPanel;
import ui.EditorPanel;
import ui.LMCDisplay;

/**
 * Entry point for the program. Deals with setting the localisation and
 * constructing key UI elements.
 * 
 * @author tomblanchard
 *
 */
public class Main {
	
	/**
	 * Don't allow construction. Arguably this should be refactored.
	 */
	private Main() {
		
	}
	//Splash screen isn't working with this but I think it needs to be a part of the manifest. Doesn't really matter anyway
	//Home/bin/jpackage --name JLMC --arguments -Xdock:name="JLMC" --arguments -Xdock:icon="jlmc.png" --arguments -splash:"splash.png" --icon ~/Documents/eclipse_workspace/LMC\ Project/res/jlmc.icns   --input ~/Documents/eclipse_workspace/LMC\ Project/ --main-jar JLMC.jar

	/* ???
	 * jpackage --name JLMC --arguments -Xdock:name="JLMC" --arguments -Xdock:icon="jlmc.png" --arguments -splash:"splash.png" --icon ~/Documents/eclipse_workspace/LMC\ Project/res/jlmc.icns   --input ~/Documents/eclipse_workspace/LMC\ Project/ --main-jar JLMC.jar
	 */
	/** 
	 * Standard entry point
	 * @param args
	 */
	public static void main(String[] args) {
		
		//Set the current language, defaults to English
		Messages.setCurrentLocale(Locale.ENGLISH);

		//Create the UI on the Swing thread
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				//Create our computer and editor models
				Computer computer = new Computer();
				Editor editor = new Editor();

				//If we are aon OSX then use the platform standard style menu
				String lcOSName = System.getProperty("os.name").toLowerCase();
				boolean IS_MAC = lcOSName.startsWith("mac os x");

				if (IS_MAC) {
					System.setProperty("apple.laf.useScreenMenuBar", "true");

				}

				//Create the major components of the UI
				ComputerPanel computerPanel = new ComputerPanel(computer);
				ControlPanel controlPanel = new ControlPanel(computer);
				EditorPanel editorPanel = new EditorPanel(editor, computerPanel);

				//Register various parts of the UI as computer model listeners 
				computer.registerPropertyChangeListener(computerPanel);
				computer.registerPropertyChangeListener(controlPanel, "reset");
				
				//Register a default input channel to the computer model (for future dev)
				computer.registerInputChannel(controlPanel);

				//Create the JFrame that holds the rest of the display.
				new LMCDisplay(computerPanel, controlPanel, editorPanel);

			}
		});

	}

}
