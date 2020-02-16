package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Frame that holds all of the LMC display components. Is responsible
 * for listening for menu selections and responding to these actions.
 * @author tomblanchard
 *
 */
public class LMCDisplay extends JFrame implements ActionListener{
	
	// Main split pane to allow use to decide how much space they give to the editor and computer sim
	private JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

	// Menu system
	private JMenuBar menuBar = new JMenuBar();
	private JMenu fileMenu = new JMenu("File");
	private JMenuItem openItem = new JMenuItem("Open");
	private JMenuItem saveItem = new JMenuItem("Save");
	
	// Reference to the editor panel so save/load methods can be called
	private EditorPanel ep;
	
	/**
	 * Creates a new LMCDisplay 
	 * @param cp The computer panel to be displayed
	 * @param controlPanel The control panel to be displayed
	 * @param ep The editor panel to be display
	 */
	public LMCDisplay(ComputerPanel cp, ControlPanel controlPanel, EditorPanel ep) {
		this.ep = ep;
		
		// Set some sensible size limits so that resizing doesn't screw up everything
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setMinimumSize(new Dimension(900, 600));
		this.setSize(new Dimension(900, 600));

		openItem.addActionListener(this);
		saveItem.addActionListener(this);
		
		menuBar.add(fileMenu);
		fileMenu.add(openItem);
		fileMenu.add(saveItem);
		this.setJMenuBar(menuBar);
		
		
		// Construct the computer panel and controls into the right hand panel
		JPanel rightSidePanel = new JPanel();
		rightSidePanel.setLayout(new BorderLayout());		
		rightSidePanel.add(cp, BorderLayout.CENTER);
		rightSidePanel.add(controlPanel, BorderLayout.PAGE_END);

		// Set up the split plane
		splitPane.setLeftComponent(ep);
		splitPane.setRightComponent(rightSidePanel);

		this.getContentPane().add(splitPane);
		this.pack();
		this.setVisible(true);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// Deal with menu action events
		if(e.getSource().equals(openItem)){
			
			JFileChooser chooser = new JFileChooser();
		    FileNameExtensionFilter filter = new FileNameExtensionFilter(
		        "LMC assembly files", "lmc");
		    chooser.setFileFilter(filter);
		    int returnVal = chooser.showOpenDialog(this);
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
		       ep.loadFile(chooser.getSelectedFile());
		    }
		} 
		else if(e.getSource().equals(saveItem)){
			JFileChooser chooser = new JFileChooser();

		    int returnVal = chooser.showSaveDialog(this);
		    
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
		    	ep.saveFile(chooser.getSelectedFile());
		    }
		}
	}
	
	
}
