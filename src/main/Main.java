package main;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import ui.ComputerPanel;
import ui.ControlPanel;
import ui.EditorPanel;
import ui.LMCDisplay;

public class Main {
	//TODO: packaging
	/*
	 * ~/Downloads/jdk-14.jdk/Contents/Home/bin/jpackage  --input ./ --java-options -splash:splash.png --name JLMC --mac-package-name JLMC --main-jar JLMC.jar
	 */
	
	// -Xdock:name="JLMC" -Xdock:icon=jlmc.png
	// Splashscreen must be set in jar manifest as SplashScreen-Image: splash.png
	// splash 
	// https://www.youtube.com/watch?v=kwdK6Dg1a_Y

	public static void main(String[] args) {
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
