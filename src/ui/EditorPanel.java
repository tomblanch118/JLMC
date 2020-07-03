package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import computer.ParseException;
import computer.model.Editor;
import language.Messages;

import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;

//TODO: LANGUAGE PACKS!
//TODO: Intermediate output (ops to numeric code and labels resolved to addresses)

@SuppressWarnings("serial")
public class EditorPanel extends JPanel implements ActionListener, LocalisationListener {
	
	// Reference to the Editor that is responsible for parsing the content
	// of this Editor Panel
	private Editor codeEditor;
	
	private JTextArea editor = new JTextArea();
	private JTextArea error = new JTextArea();
	private JLabel heading = new JLabel(Messages.getTranslatedString("CODE"));
	private JButton compile = new JButton(Messages.getTranslatedString("COMPILE_BUTTON"));
	private UndoManager undo = new UndoManager();
	private ComputerPanel cp;

	// Error message colours
	private Color green = new Color(133, 252, 131);
	private Color red = new Color(235, 142, 131);


	public EditorPanel(Editor codeEditor, ComputerPanel cp) {
		this.codeEditor = codeEditor;
		this.cp = cp;
		this.setMinimumSize(new Dimension(400, 400));
		this.setPreferredSize(new Dimension(400, 400));
		
		Messages.registerLocalisationListener(this);
		//TODO: stop undo from going to blank
		// Set up the undo/redo key bindings
		editor.getInputMap().put(KeyStroke.getKeyStroke("control Z"), new AbstractAction("Undo")
        {
            public void actionPerformed(ActionEvent evt)
            {
                try
                {
                    if (undo.canUndo())
                    {
                        undo.undo();
                    }
                } catch (CannotUndoException e)
                {
                }
            }
        });
		
		editor.getInputMap().put(KeyStroke.getKeyStroke("control R"), new AbstractAction("Redo")
        {
            public void actionPerformed(ActionEvent evt)
            {
                try
                {
                    if (undo.canRedo())
                    {
                        undo.redo();
                    }
                } catch (CannotUndoException e)
                {
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
		// testUndo.addActionListener(this);
		compile.addActionListener(this);
		error.setOpaque(true);
		error.setBackground(green);

		editor.setEditable(true);
		editor.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));

		JScrollPane scroller = new JScrollPane(editor);
		this.setLayout(new BorderLayout());
		this.add(heading, BorderLayout.PAGE_START);
		this.add(scroller, BorderLayout.CENTER);

		JPanel extras = new JPanel();
		extras.setLayout(new BorderLayout());
		extras.setMinimumSize(new Dimension(400,100));
		extras.setPreferredSize(new Dimension(400,100));
		this.add(extras, BorderLayout.PAGE_END);
		extras.add(error, BorderLayout.CENTER);
		extras.add(compile, BorderLayout.PAGE_END);

		// extras.add(testUndo, BorderLayout.PAGE_END);
	}

	public void saveFile(File file) {
		
		String filename = file.getName();
		
		if(!filename.endsWith(".lmc"))
		{
			file = new File(file.getAbsolutePath()+".lmc");
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
		//System.out.println(e.getActionCommand());
	
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
				error.setText(Messages.getTranslatedString("LINE")+ " " + e2.getLineNumber() + ": " + e2.getMessage());
				error.setBackground(red);
				try {
					Highlighter h = editor.getHighlighter();

					h.removeAllHighlights();
					int a = editor.getLineStartOffset(e2.getLineNumber());
					int b = editor.getLineEndOffset(e2.getLineNumber());

					DefaultHighlightPainter p = new DefaultHighlighter.DefaultHighlightPainter(red);
					h.addHighlight(a, b, p);
				} catch (BadLocationException eee) {
					eee.printStackTrace();
				}
			}
			cp.repaint();
		}
	}

	@Override
	public void relocalise() {
		heading.setText(Messages.getTranslatedString("CODE"));
		compile.setText(Messages.getTranslatedString("COMPILE_BUTTON"));
		compile.doClick();
	}
}
