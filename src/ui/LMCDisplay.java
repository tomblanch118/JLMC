package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Locale;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import language.Messages;

/**
 * Frame that holds all of the LMC display components. Is responsible
 * for listening for menu selections and responding to these actions.
 * @author tomblanchard
 *
 */
public class LMCDisplay extends JFrame implements 
            ActionListener, LocalisationListener {
  
  private static final long serialVersionUID = 3357141141986186046L;

  // Main split pane to allow use to decide how much space they give to the editor and computer sim
  private JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

  // Menu system
  private JMenuBar menuBar = new JMenuBar();
  
  private JMenu fileMenu = new JMenu(Messages.getTranslatedString("FILE"));
  private JMenuItem openItem = new JMenuItem(Messages.getTranslatedString("OPEN"));
  private JMenuItem saveItem = new JMenuItem(Messages.getTranslatedString("SAVE"));
  private JMenuItem exitItem = new JMenuItem(Messages.getTranslatedString("EXIT"));
  
  private JMenu languageMenu = new JMenu(Messages.getTranslatedString("LANGUAGE"));
  private JMenuItem englishItem = new JMenuItem("English");
  private JMenuItem welshItem = new JMenuItem("Cymraeg");
  
  private JMenu helpMenu = new JMenu(Messages.getTranslatedString("INFORMATION"));
  private JMenuItem aboutItem = new JMenuItem("About");
  
  // Reference to the editor panel so save/load methods can be called
  private EditorPanel ep;
  
  /**
   * Creates a new LMCDisplay.
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
    this.setBackground(ColorScheme.background);
  
    
    Messages.registerLocalisationListener(this);

    openItem.addActionListener(this);
    saveItem.addActionListener(this);
    exitItem.addActionListener(this);
    aboutItem.addActionListener(this);
    
    englishItem.addActionListener(this);
    welshItem.addActionListener(this);
    
    menuBar.add(fileMenu);
    fileMenu.add(openItem);
    fileMenu.add(saveItem);
    fileMenu.add(new JSeparator());
    fileMenu.add(exitItem);
    
    menuBar.add(languageMenu);
    languageMenu.add(englishItem);
    languageMenu.add(welshItem);
    
    menuBar.add(helpMenu);
    helpMenu.add(aboutItem);
  
    URL imageURL = this.getClass().getResource("/uk.png");
    ImageIcon image = new ImageIcon(imageURL);
    englishItem.setIcon(image);
    imageURL = this.getClass().getResource("/wales.png");
    image = new ImageIcon(imageURL);
    welshItem.setIcon(image);
    
    this.setJMenuBar(menuBar);
    
    
    // Construct the computer panel and controls into the right hand panel
    JPanel rightSidePanel = new JPanel();
    rightSidePanel.setLayout(new BorderLayout());    
    rightSidePanel.add(cp, BorderLayout.CENTER);
    rightSidePanel.add(controlPanel, BorderLayout.PAGE_END);

    // Set up the split plane
    splitPane.setLeftComponent(ep);
    splitPane.setRightComponent(rightSidePanel);
    splitPane.setBackground(ColorScheme.comment);
    splitPane.setDividerSize(4);
    splitPane.setUI(new BasicSplitPaneUI() {
      @Override
      public BasicSplitPaneDivider createDefaultDivider() {
        return new BasicSplitPaneDivider(this) {
          private static final long serialVersionUID = -6000773723083732304L;

          @Override
          public void paint(Graphics g) {
          }
        };
      }
    });
    //splitPane.setForeground(Color.WHITE);
    splitPane.setBorder(null);
    this.getContentPane().add(splitPane);
    this.pack();
    this.setVisible(true);

  }

  @Override
  public void actionPerformed(ActionEvent e) {
    // Deal with menu action events
    if (e.getSource().equals(openItem)) {
      
      JFileChooser chooser = new JFileChooser();
      FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "LMC assembly files", "lmc");
      chooser.setFileFilter(filter);
      int returnVal = chooser.showOpenDialog(this);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        ep.loadFile(chooser.getSelectedFile());
      }
    } else if (e.getSource().equals(saveItem)) {
      JFileChooser chooser = new JFileChooser();

      int returnVal = chooser.showSaveDialog(this);
        
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        ep.saveFile(chooser.getSelectedFile());
      }
    } else if (e.getSource().equals(exitItem)) {
      System.exit(1);
    } else if (e.getSource().equals(englishItem)) {
      Messages.setCurrentLocale(Locale.ENGLISH);
      englishItem.setSelected(true);
      welshItem.setSelected(false);
    } else if (e.getSource().equals(welshItem)) {
      Messages.setCurrentLocale(new Locale("cy"));
      englishItem.setSelected(false);
      welshItem.setSelected(true);
    } else if (e.getSource().equals(aboutItem)) {
      JFrame frame = new JFrame("About JLMC...");
      Dimension size = new Dimension(200, 200);
      frame.setSize(size);
      frame.setMinimumSize(size);
      frame.setResizable(false);
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.setVisible(true);
      frame.setBackground(ColorScheme.background);
      
      JPanel panel = new JPanel();
      frame.getContentPane().add(panel);
      panel.setLayout(new BorderLayout());
      
      JTextArea text = new JTextArea(
          "Written by Tom Blanchard with Welsh translations by Luke Clement. \n\n"
          + "Supported by the Technocamps Project.");
      
      text.setForeground(Color.WHITE);
      text.setOpaque(false);
      text.setEditable(false);
      text.setLineWrap(true);
      text.setWrapStyleWord(true);
      panel.setBackground(ColorScheme.background);
      panel.add(text, BorderLayout.CENTER);
    }
  }

  @Override
  public void relocalise() {
    fileMenu.setText(Messages.getTranslatedString("FILE"));
    openItem.setText(Messages.getTranslatedString("OPEN"));
    saveItem.setText(Messages.getTranslatedString("SAVE"));
    exitItem.setText(Messages.getTranslatedString("EXIT"));
    languageMenu.setText(Messages.getTranslatedString("LANGUAGE"));
    helpMenu.setText(Messages.getTranslatedString("INFORMATION"));
  }
}
