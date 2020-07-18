package computer;

import java.util.Locale;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import computer.model.Computer;
import computer.model.Editor;
import language.Messages;
import ui.ComputerPanel;
import ui.ControlPanel;
import ui.EditorPanel;
import ui.LMCDisplay;

public class Main {
	
	//Splash screen isn't working with this but I think it needs to be a part of the manifest. Doesn't really matter anyway
	//Home/bin/jpackage --name JLMC --arguments -Xdock:name="JLMC" --arguments -Xdock:icon="jlmc.png" --arguments -splash:"splash.png" --icon ~/Documents/eclipse_workspace/LMC\ Project/res/jlmc.icns   --input ~/Documents/eclipse_workspace/LMC\ Project/ --main-jar JLMC.jar

	
	public static void main(String[] args) {
		Messages.setCurrentLocale(Locale.ENGLISH);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Computer computer = new Computer();
				Editor editor = new Editor();

				String lcOSName = System.getProperty("os.name").toLowerCase();
				boolean IS_MAC = lcOSName.startsWith("mac os x");

				if (IS_MAC) {
					System.setProperty("apple.laf.useScreenMenuBar", "true");

				}
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (UnsupportedLookAndFeelException e) {
					e.printStackTrace();
				}

				ComputerPanel computerPanel = new ComputerPanel(computer);
				ControlPanel controlPanel = new ControlPanel(computer);
				EditorPanel editorPanel = new EditorPanel(editor, computerPanel);

				computer.registerPropertyChangeListener(computerPanel);
				computer.registerPropertyChangeListener(controlPanel, "reset");
				computer.registerInputChannel(controlPanel);

				LMCDisplay display = new LMCDisplay(computerPanel, controlPanel, editorPanel);

			}
		});

	}

}
