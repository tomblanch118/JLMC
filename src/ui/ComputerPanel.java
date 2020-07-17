package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.JPanel;

import computer.instruction.Mnemonic;
import computer.model.Computer;
import language.Messages;

//TODO: property listeners to register running, step, stop events, current line
//TODO: Tidy and comment
//TODO: highlight current executing line.
//TODO: Wrap text.
public final class ComputerPanel extends JPanel implements PropertyChangeListener, LocalisationListener {

	private static final long serialVersionUID = 1L;
	private Computer computer;
	private int spacing = 35;
	private final int offset = 15;
	private final int memoryOffsetx = 135;
	private final int memoryOffsety = 20;

	private int nextBoxY = 0;
	private final Font font = new Font(Font.MONOSPACED, Font.PLAIN, 12);

	public ComputerPanel(Computer computer) {
		this.computer = computer;
		this.setMinimumSize(new Dimension(500, 400));
		this.setVisible(true);
		Messages.registerLocalisationListener(this);
	}

	public Computer getComputer() {
		return computer;
	}

	private void drawInBox(Graphics2D g, String label, int value, int x) {
		String[] parts = label.split("\\ ");
		if(parts.length == 2) {
			g.setColor(ColorScheme.orange);
			g.drawRect(x, nextBoxY, 115, 49);
			g.setColor(ColorScheme.green);
			g.drawString(parts[0], x + 2, nextBoxY + 12);
			g.drawString(parts[1], x + 2, nextBoxY + 26);
			g.drawString("" + value, x + 2, nextBoxY + 40);
			nextBoxY += 54;
		} else {
			g.setColor(ColorScheme.orange);
			g.drawRect(x, nextBoxY, 115, 35);
			g.setColor(ColorScheme.green);
			g.drawString(label, x + 2, nextBoxY + 12);
			g.drawString("" + value, x + 2, nextBoxY + 26);
			nextBoxY += 39;
		}
	}


	private void drawOutputBox(Graphics2D g, String label, String[] values, int x) {
		g.setColor(ColorScheme.orange);
		g.drawRect(x, nextBoxY, 115, 88);
		g.setColor(ColorScheme.green);

		g.drawString(label, x + 2, nextBoxY + 12);

		for (int i = 0; i < values.length; i++) {
			g.drawString(values[i], x + 2, nextBoxY + 12 + ((i + 1) * 14));
		}
		nextBoxY += 93;
	}
	
	private ArrayList<String> fitToWidth(int width, String text, Graphics context) {
		
		String[] tokens = text.split("\\ ");
		
		ArrayList<String> lines = new ArrayList<String> ();
		FontMetrics fontMetrics = context.getFontMetrics();
		
		String currentString = "";
		for(String token : tokens) {
			
			String temp = currentString + token;
			// Check if we can add the next token without exceeding the width
			if( width < fontMetrics.stringWidth(temp) ) {
				lines.add(currentString);
				currentString = token+" ";
			}
			else {
				currentString = currentString + token+ " ";
			}
		}
		// If there is something left in the currentString add it
		if(!currentString.equals("")) {
			lines.add(currentString);
		}
		
		return lines;
	}

	public void paint(Graphics g) {
		Dimension size = this.getSize();
		Graphics2D graphic2d = (Graphics2D) g;

		graphic2d.setColor(ColorScheme.background);
		graphic2d.fillRect(0, 0, size.width, size.height);

		graphic2d.setFont(font);
		graphic2d.setColor(ColorScheme.orange);

		int availableWidth = ((size.width < size.height) ? size.width : size.height) - 150;

		spacing = availableWidth / 10;

		int currentPC = computer.getProgramCounter();

		graphic2d.drawRect(memoryOffsetx, memoryOffsety - 10, availableWidth, availableWidth);

		for (int i = 0; i < 100; i++) {
			int x = memoryOffsetx + spacing * (i % 10);
			int y = memoryOffsety + spacing * (i / 10);

			String memory = String.format("%03d", computer.getMemory(i));

			if (i < computer.getHighestUsedAddress() + 1) {
				graphic2d.setColor(ColorScheme.blueLight);

			} else {
				graphic2d.setColor(ColorScheme.blueDark);
			}
			graphic2d.drawString(memory, x + 7, offset + y);

			if (i == currentPC) {
				graphic2d.setColor(ColorScheme.green);
			}

			graphic2d.drawString(""+i, x + 8, y);

			graphic2d.drawRect(x + 6, y + 4, 25, 15);
		}

		graphic2d.setColor(ColorScheme.green);

		// Draw various registers
		int textX = 10;
		int textY = 10;
		nextBoxY = textY;
		drawInBox(graphic2d, Messages.getTranslatedString("PROGRAM_COUNTER"), computer.getProgramCounter(), textX);
		drawInBox(graphic2d, Messages.getTranslatedString("INSTRUCTION_REGISTER"), computer.getInstructionRegister(), textX);
		drawInBox(graphic2d, Messages.getTranslatedString("ADDRESS_REGISTER"), computer.getAddressRegister(), textX);
		drawInBox(graphic2d, Messages.getTranslatedString("ACCUMULATOR"), computer.getAccumulator(), textX);
		drawInBox(graphic2d, Messages.getTranslatedString("INPUT"), computer.getInput(), textX);
		drawOutputBox(graphic2d, Messages.getTranslatedString("OUTPUT"), computer.getOutput(), textX);

		graphic2d.setColor(ColorScheme.orange);
		graphic2d.drawRect(memoryOffsetx, memoryOffsety + spacing * 10, availableWidth, 100);
		graphic2d.setColor(ColorScheme.green);
		
		String message = "";
		if (computer.isHalted()) {
			message = Messages.getTranslatedString("HALTED");
		} else {
			boolean fetch = computer.isFetch();
			String name = Mnemonic.instructionName(computer.getFullCurrentInstruction());

			if (fetch) {
				message = Messages.getTranslatedString("FETCHED")+": " + computer.getFullCurrentInstruction() + "(" + name + ")";
			} else {
				message = Messages.getTranslatedString("EXECUTED")+": " + computer.getFullCurrentInstruction() + "(" + name + ")\n";
				
				
				//Fit the text to the available width
				String description = Mnemonic.explanation(computer.getFullCurrentInstruction());
				
				ArrayList<String> explanations = fitToWidth(availableWidth,description
						, graphic2d);
				//
				int sep = 0;
				for(String line : explanations) {
					graphic2d.drawString(line, memoryOffsetx +2, (sep * 20)+36 + memoryOffsety + spacing * 10);
					sep++;
				}
			}
		}

		graphic2d.drawString(message, memoryOffsetx + 2, 16 + memoryOffsety + spacing * 10);

		drawInBox(graphic2d, Messages.getTranslatedString("CPU_SPEED"), computer.getCpuSpeed(), textX);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		if (evt.getPropertyName().equals("step")) {
			this.repaint();
		}

	}

	@Override
	public void relocalise() {
		this.repaint();
	}
}
