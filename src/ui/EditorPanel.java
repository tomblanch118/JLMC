package ui;

import computer.ParseException;
import computer.model.Editor;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import javax.swing.text.ParagraphView;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.Utilities;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import language.Messages;

@SuppressWarnings("serial")
public class EditorPanel extends JPanel implements 
            ActionListener, LocalisationListener, KeyListener {

  // Reference to the Editor that is responsible for parsing the content
  // of this Editor Panel
  private Editor codeEditor;

  private JTextPane editor = new JTextPane();
  private JTextArea error = new JTextArea();
  private JButton compile = new JButton(Messages.getTranslatedString("COMPILE_BUTTON"));
  private UndoManager undo = new UndoManager();
  private ComputerPanel cp;

  private AbstractDocument doc;
  
  private TitledBorder border;

  // Error message colours
  private Color green = new Color(133, 252, 131);
  private Color red = new Color(235, 142, 131);
  
  // Attribute sets that are used for syntax highlighting
  private SimpleAttributeSet instr = new SimpleAttributeSet();
  private SimpleAttributeSet label = new SimpleAttributeSet();
  private SimpleAttributeSet comment = new SimpleAttributeSet();
  private SimpleAttributeSet number = new SimpleAttributeSet();

  // Editor font
  private Font font = new Font(Font.MONOSPACED, Font.PLAIN, 14);

  /**
   * Constructs an editor panel.
   * @param codeEditor the code editor responsible for the code in the panel
   * @param cp the associated computer panel
   */
  public EditorPanel(Editor codeEditor, ComputerPanel cp) {
    this.codeEditor = codeEditor;
    this.cp = cp;
    
    //Set some reasonable defaults so that the splitpane has something to work with
    this.setMinimumSize(new Dimension(400, 400));
    this.setPreferredSize(new Dimension(400, 400));
    this.setBackground(ColorScheme.background);
    
    //Setup our syntax highlight styles/colours
    StyleConstants.setForeground(instr, ColorScheme.blueLight);
    StyleConstants.setForeground(label, ColorScheme.green);
    StyleConstants.setForeground(comment, ColorScheme.comment);
    StyleConstants.setForeground(number, ColorScheme.orange);

    //Needed to stop words from wrapping in the JTextPane
    editor.setEditorKit(new ExtendedStyledEditorKit());
    
    //Style the editor
    editor.setBackground(ColorScheme.background);
    editor.setForeground(Color.WHITE);
    editor.setCaretColor(Color.WHITE);
    editor.setEditable(true);
    editor.setFont(font);
    editor.addKeyListener(this);
    
    //Create a nice border for the editor
    Border lineBorder = BorderFactory.createLineBorder(ColorScheme.orange);
    border = BorderFactory.createTitledBorder(
        lineBorder, Messages.getTranslatedString("CODE"), 
        TitledBorder.CENTER, TitledBorder.TOP, font, ColorScheme.orange);
    

    Border margin = new EmptyBorder(10, 10, 10, 10);
    editor.setBorder(new CompoundBorder(border, margin));


    //Get the document model that backs the TextPane
    StyledDocument styledDoc = editor.getStyledDocument();
    if (styledDoc instanceof AbstractDocument) {
      doc = (AbstractDocument) styledDoc;
    } else {
      System.err.println("Text pane's document isn't an AbstractDocument!");
      System.exit(-1);
    }
    
    //Register ourselves to receive localisation events (allows translating of the UI at runtime)
    Messages.registerLocalisationListener(this);

    
    // TODO: stop undo from going to blank
    // Set up the undo/redo key bindings
    editor.getInputMap().put(KeyStroke.getKeyStroke("control Z"), new AbstractAction("Undo") {
      public void actionPerformed(ActionEvent evt) {
        try {
          if (undo.canUndo()) {
            undo.undo();
          }
        } catch (CannotUndoException e) {
          System.err.println("cannot undo");
        }
      }
    });

    editor.getInputMap().put(KeyStroke.getKeyStroke("control R"), new AbstractAction("Redo") {
      public void actionPerformed(ActionEvent evt) {
        try {
          if (undo.canRedo()) {
            undo.redo();
          }
        } catch (CannotUndoException e) {
          System.err.println("Cannto redo");
        }
      }
    });

    editor.getDocument().addUndoableEditListener(new UndoableEditListener() {
      public void undoableEditHappened(UndoableEditEvent evt) {
        undo.addEdit(evt.getEdit());
      }
    });

    //Style the error/info textarea
    error.setEditable(false);
    error.setLineWrap(true);
    error.setOpaque(true);
    error.setBackground(green);
    
    //TODO: Probably a good place for a factory to get styled UI components
    //TODO: should Compile actually be Assemble?
    //Style the compile button
    compile.setContentAreaFilled(false);
    compile.setOpaque(true);
    compile.setBorderPainted(false);
    compile.setBackground(ColorScheme.button);
    compile.setForeground(Color.WHITE);
    compile.addActionListener(this);

    //Setup the layouts for the Editor
    JScrollPane scroller = new JScrollPane(editor);    
    scroller.setBorder(null);    
    this.setLayout(new BorderLayout());
    this.add(scroller, BorderLayout.CENTER);

    //Extra panel contains the compile button and error box
    JPanel extras = new JPanel();
    extras.setBorder(new EmptyBorder(4, 4, 4, 4));
    extras.setLayout(new BorderLayout());
    extras.setMinimumSize(new Dimension(400, 100));
    extras.setPreferredSize(new Dimension(400, 100));
    
    this.add(extras, BorderLayout.PAGE_END);
    extras.add(error, BorderLayout.CENTER);
    extras.add(compile, BorderLayout.PAGE_END);
    extras.setBackground(ColorScheme.background);
  }

  /**
   * Saves the content of the editor.
   * @param file the file to save to
   */
  public void saveFile(File file) {

    String filename = file.getName();

    if (!filename.endsWith(".lmc")) {
      file = new File(file.getAbsolutePath() + ".lmc");
    }

    try {
      Files.write(file.toPath(), editor.getText().getBytes());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Loads in an lmc file.
   * @param file the file to load
   */
  public void loadFile(File file) {
    try (Scanner scanner = new Scanner(file)) {
      StringBuilder sb = new StringBuilder();
      while (scanner.hasNextLine()) {
        sb.append(scanner.nextLine());
        sb.append("\n");
      }
      editor.setText(sb.toString());
    } catch (FileNotFoundException e) {
      System.err.println("Couldn't find file to load");
    }
    compile.doClick();
  }

  @Override
  public void actionPerformed(ActionEvent e) {

    if (e.getSource().equals(compile)) {

      // Remove empty lines/whitespace
      String[] temp = editor.getText().split("\n");

      StringBuilder sb = new StringBuilder();
      for (String line : temp) {
        String trimmed = line.trim();
        if (trimmed.length() != 0) {
          sb.append(line + "\n");
        }
      }
      editor.setText(sb.toString());
      // ----end of shittyness-----

      codeEditor.setText(editor.getText());
      
      //Remove any highlights that have been applied (showing errors etc)
      Highlighter h = editor.getHighlighter();
      h.removeAllHighlights();

      // Try to parse the contents of the editor
      try {
        codeEditor.parse();
        editor.setText(codeEditor.format());
        error.setText(Messages.getTranslatedString("EDITOR_SUCCESS_MESSAGE"));
        error.setBackground(green);
        cp.getComputer().load(codeEditor.getInstructions());
        
        cp.repaint();

      } catch (ParseException e2) {

        error.setText(Messages.getTranslatedString("LINE")
            + " " + e2.getLineNumber() + ": " + e2.getMessage());
        error.setBackground(red);

        highlightLine(e2.getLineNumber(), h);

      }
      doSyntax(false);
      cp.repaint();
    }
  }

  private void highlightLine(int line, Highlighter h) {
    // Exit early if invalid line
    if (line == -1) {
      return;
    }


    String text = editor.getText();
    int currentLine = 0;
    int start = -1;
    int end = text.length();

    for (int i = 0; i < text.length(); i++) {

      if (start == -1 && line == currentLine) {
        start = i;
      }

      if (text.charAt(i) == '\n') {
        if (line == currentLine) {
          end = i;
          break;
        }
        currentLine++;
      }
    }
    try {

      DefaultHighlightPainter p = new DefaultHighlighter.DefaultHighlightPainter(red);
      h.addHighlight(start, end, p);
    } catch (BadLocationException eee) {
      eee.printStackTrace();
    }
  }

  /**
   * Relocalises the text in this UI.
   */
  @Override
  public void relocalise() {
    border.setTitle(Messages.getTranslatedString("CODE"));
    compile.setText(Messages.getTranslatedString("COMPILE_BUTTON"));
    compile.doClick();
    this.repaint();
  }


  /**
   * Does syntax highlighting of the editor textpane.
   * @param currentLineOnly Whether we should process the current line only or the entire contents
   */
  private void doSyntax(boolean currentLineOnly) {
    int caretPos = editor.getCaretPosition();
    
    Document doc = editor.getDocument();
    String code = "";
    
    //Fix for windows as the carriage return interferes if we just go editor.getText()
    try {
      code = doc.getText(0, doc.getLength());
    } catch (BadLocationException e1) {
      e1.printStackTrace();
    }
    
    int start = 0;
    int end = code.length();
    if (currentLineOnly) {
      try {
        start = Utilities.getRowStart(editor, caretPos);
        end = Utilities.getRowEnd(editor, caretPos);
      } catch (BadLocationException e) {
        //Ignore and use defaults
        //TODO: probably log this
      }
    }
    String token = "";
    boolean inComment = false;
    for (int i = start; i < end; i++) {
      
      
      if (!Character.isWhitespace(code.charAt(i))) {
        token += code.charAt(i);
        if (token.startsWith("//")) {
          inComment = true;
        }
      
      } else if (Character.isWhitespace(code.charAt(i)) && inComment && code.charAt(i) != '\n') {
        token += code.charAt(i);        
      } else {
        if (!token.equals("")) {
          colourToken(token, i);
          token = "";
          inComment = false;
        }
      }
    }
    
    if (!token.equals("")) {
      colourToken(token, end);
      
    }

    editor.setCaretPosition(caretPos);
  }

  private void colourToken(String token, int pos) {

    try {
      if (Editor.isInstruction(token)) {
        doc.replace(pos - token.length(), token.length(), token, instr);
      } else if (token.startsWith("//")) {
        doc.replace(pos - token.length(), token.length(), token, comment);
      } else if (isInt(token)) {
        doc.replace(pos - token.length(), token.length(), token, number);
      } else {
        doc.replace(pos - token.length(), token.length(), token, label);
      }
    } catch (BadLocationException ble) {
      System.out.println("Badd location");
    }
  }

  private boolean isInt(String token) {
    try {
      Integer.parseInt(token);
    } catch (NumberFormatException nfe) {
      return false;
    }
    return true;
  }


  @Override
  public void keyTyped(KeyEvent e) {
  }

  @Override
  public void keyPressed(KeyEvent e) {
  }

  @Override
  public void keyReleased(KeyEvent e) {
    int a = e.getModifiersEx() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

    if (!(e.getKeyCode() == KeyEvent.VK_A 
        && a == Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) 
        && font.canDisplay(e.getKeyChar())) {
      doSyntax(true);
    }
    
  }
  
  /** To enable no wrap to JTextPane.
   */
  static class ExtendedStyledEditorKit extends StyledEditorKit {
    private static final long serialVersionUID = 1L;

    private static final ViewFactory styledEditorKitFactory = (
        new StyledEditorKit()).getViewFactory();

    private static final ViewFactory defaultFactory = new ExtendedStyledViewFactory();

    public Object clone() {
      return new ExtendedStyledEditorKit();
    }

    public ViewFactory getViewFactory() {
      return defaultFactory;
    }

    /* The extended view factory */
    static class ExtendedStyledViewFactory implements ViewFactory {
      public View create(Element elem) {
        String elementName = elem.getName();
        if (elementName != null) {
          if (elementName.equals(AbstractDocument.ParagraphElementName)) {
            return new ExtendedParagraphView(elem);
          }
        }

        // Delegate others to StyledEditorKit
        return styledEditorKitFactory.create(elem);
      }
    }
  }
  
  static class ExtendedParagraphView extends ParagraphView {
    public ExtendedParagraphView(Element elem) {
      super(elem);
    }

    @Override
    public float getMinimumSpan(int axis) {
      return super.getPreferredSpan(axis);
    }
  }
}




