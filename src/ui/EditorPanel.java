package ui;

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
import javax.swing.JLabel;
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


import computer.ParseException;
import computer.model.Editor;
import language.Messages;

import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.Document;
import javax.swing.text.Element;

//TODO: Intermediate output (ops to numeric code and labels resolved to addresses)

@SuppressWarnings("serial")
public class EditorPanel extends JPanel implements ActionListener, LocalisationListener, KeyListener {

	// Reference to the Editor that is responsible for parsing the content
	// of this Editor Panel
	private Editor codeEditor;

	private JTextPane editor = new JTextPane();
	private JTextArea error = new JTextArea();
	private JLabel heading = new JLabel(Messages.getTranslatedString("CODE"));
	private JButton compile = new JButton(Messages.getTranslatedString("COMPILE_BUTTON"));
	private UndoManager undo = new UndoManager();
	private ComputerPanel cp;

	private AbstractDocument doc;
	
	private TitledBorder border;

	// Error message colours
	private Color green = new Color(133, 252, 131);
	private Color red = new Color(235, 142, 131);
	
	private SimpleAttributeSet instr = new SimpleAttributeSet();
	private SimpleAttributeSet label = new SimpleAttributeSet();
	private SimpleAttributeSet comment = new SimpleAttributeSet();
	private SimpleAttributeSet number = new SimpleAttributeSet();


	private Font font = new Font(Font.MONOSPACED, Font.PLAIN, 14);

	public EditorPanel(Editor codeEditor, ComputerPanel cp) {
		this.codeEditor = codeEditor;
		this.cp = cp;
		this.setMinimumSize(new Dimension(400, 400));
		this.setPreferredSize(new Dimension(400, 400));
		this.setBackground(ColorScheme.background);
		
		StyleConstants.setForeground(instr, ColorScheme.blueLight);
		StyleConstants.setForeground(label, ColorScheme.green);
		StyleConstants.setForeground(comment, ColorScheme.comment);
		StyleConstants.setForeground(number, ColorScheme.orange);

		
		editor.setEditorKit(new ExtendedStyledEditorKit());
		editor.setBackground(ColorScheme.background);
		editor.setForeground(Color.WHITE);
		editor.setCaretColor(Color.WHITE);

		editor.addKeyListener(this);
		StyledDocument styledDoc = editor.getStyledDocument();
		if (styledDoc instanceof AbstractDocument) {
			doc = (AbstractDocument) styledDoc;
			// doc.setDocumentFilter(new DocumentSizeFilter(MAX_CHARACTERS));
		} else {
			System.err.println("Text pane's document isn't an AbstractDocument!");
			System.exit(-1);
		}
		// editor.setMargin(new Insets(20, 20, 20, 20));
		Messages.registerLocalisationListener(this);

		heading.setForeground(Color.white);
		// TODO: stop undo from going to blank
		// Set up the undo/redo key bindings
		editor.getInputMap().put(KeyStroke.getKeyStroke("control Z"), new AbstractAction("Undo") {
			public void actionPerformed(ActionEvent evt) {
				try {
					if (undo.canUndo()) {
						undo.undo();
					}
				} catch (CannotUndoException e) {
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
				}
			}
		});

		editor.getDocument().addUndoableEditListener(new UndoableEditListener() {
			public void undoableEditHappened(UndoableEditEvent evt) {
				undo.addEdit(evt.getEdit());
			}
		});

		error.setEditable(false);
		error.setLineWrap(true);

		compile.addActionListener(this);

		error.setOpaque(true);
		error.setBackground(green);

		//TODO: Sort out translating the border
		Border lineBorder = BorderFactory.createLineBorder(ColorScheme.orange);
		border = BorderFactory.createTitledBorder(lineBorder, Messages.getTranslatedString("CODE"), TitledBorder.CENTER, TitledBorder.TOP,
				font, ColorScheme.orange);
		

		Border margin = new EmptyBorder(10, 10, 10, 10);
		editor.setBorder(new CompoundBorder(border, margin));

		editor.setEditable(true);
		editor.setFont(font);
	
		JScrollPane scroller = new JScrollPane(editor);		
		scroller.setBorder(null);
		
		
		this.setLayout(new BorderLayout());
		// this.add(heading, BorderLayout.PAGE_START);
		this.add(scroller, BorderLayout.CENTER);

		JPanel extras = new JPanel();
		extras.setBorder(new EmptyBorder(4,4,4,4));
		extras.setLayout(new BorderLayout());
		extras.setMinimumSize(new Dimension(400, 100));
		extras.setPreferredSize(new Dimension(400, 100));
		this.add(extras, BorderLayout.PAGE_END);
		extras.add(error, BorderLayout.CENTER);
		extras.add(compile, BorderLayout.PAGE_END);
		extras.setBackground(ColorScheme.background);
		compile.setContentAreaFilled(false);
		compile.setOpaque(true);
		compile.setBorderPainted(false);
		compile.setBackground(ColorScheme.button);
		compile.setForeground(Color.WHITE);

	}

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

	public void loadFile(File file) {
		try (Scanner scanner = new Scanner(file)) {
			StringBuilder sb = new StringBuilder();
			while (scanner.hasNextLine()) {
				sb.append(scanner.nextLine());
				sb.append("\n");
			}
			editor.setText(sb.toString());
		} catch (FileNotFoundException e) {

		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// System.out.println(e.getActionCommand());

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
			
			Highlighter h = editor.getHighlighter();
			h.removeAllHighlights();


			try {

				codeEditor.parse();
				editor.setText(codeEditor.format());
				error.setText(Messages.getTranslatedString("EDITOR_SUCCESS_MESSAGE"));
				error.setBackground(green);
				cp.getComputer().load(codeEditor.getInstructions());
				
				cp.repaint();

			} catch (ParseException e2) {

				// System.out.println(e2.getMessage() + "\n" + e2.getBadToken() + "\n" +
				// e2.getLineNumber());
				error.setText(Messages.getTranslatedString("LINE") + " " + e2.getLineNumber() + ": " + e2.getMessage());
				error.setBackground(red);


				highlightLine(e2.getLineNumber(), h);

			}
			//TODO: fix error highlighting
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
		// System.out.println("start: "+start+", end: "+end);
		try {

			DefaultHighlightPainter p = new DefaultHighlighter.DefaultHighlightPainter(red);
			h.addHighlight(start, end, p);
		} catch (BadLocationException eee) {
			eee.printStackTrace();
		}
	}

	@Override
	public void relocalise() {
		border.setTitle(Messages.getTranslatedString("CODE"));
		compile.setText(Messages.getTranslatedString("COMPILE_BUTTON"));
		compile.doClick();
		this.repaint();
	}

	// test

	/**
	 * Does syntax highlighting of the editor textpane
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
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		int start = 0;
		int end = code.length();
		if(currentLineOnly) {
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
				if(token.startsWith("//")) {
					inComment = true;
				}
			
			} else if (Character.isWhitespace(code.charAt(i)) && inComment && code.charAt(i)!='\n') {
				token += code.charAt(i);				
			} else {
				if (!token.equals("")) {
					colourToken(token, i);
					token = "";
					inComment = false;
				}
			}
		}
		
		if(!token.equals("")) {
			colourToken(token, code.length());
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
		int a = e.getModifiersEx() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();

		if(!(e.getKeyCode() == KeyEvent.VK_A && a == Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()) &&
				font.canDisplay(e.getKeyChar())) {
			doSyntax(true);
		}
		
	}
	/** To enable no wrap to JTextPane **/
	static class ExtendedStyledEditorKit extends StyledEditorKit {
	    private static final long serialVersionUID = 1L;

	    private static final ViewFactory styledEditorKitFactory = (new StyledEditorKit()).getViewFactory();

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




