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

import main.Computer;
import main.Mnemonic;

//TODO: property listeners to register running, step, stop events, current line
//TODO: Tidy and comment
//TODO: highlight current executing line.
//TODO: Wrap text.
public final class ComputerPanel extends JPanel implements PropertyChangeListener {

	private static final long serialVersionUID = 1L;
	private Computer computer;
	private int spacing = 35;
	private final int offset = 15;
	private final int memoryOffsetx = 135;
	private final int memoryOffsety = 20;

	private Color background = Color.black;
	private Color foreground = Color.white;
	private Color highlight = Color.green;
	private Color foregroundDim = Color.gray;

	private final Font font = new Font(Font.MONOSPACED, Font.PLAIN, 12);

	public ComputerPanel(Computer computer) {
		this.computer = computer;
		this.setMinimumSize(new Dimension(500, 400));
		this.setVisible(true);
	}

	public Computer getComputer() {
		return computer;
	}

	private void drawInBox(Graphics2D g, String label, int value, int x, int y) {
		g.drawRect(x, y, 115, 35);
		g.drawString(label, x + 2, y + 12);
		g.drawString("" + value, x + 2, y + 26);
	}

	private void drawInBox(Graphics2D g, String label, String label2, int value, int x, int y) {
		g.drawRect(x, y, 115, 49);
		g.drawString(label, x + 2, y + 12);
		g.drawString(label2, x + 2, y + 26);
		g.drawString("" + value, x + 2, y + 40);
	}

	private void drawOutputBox(Graphics2D g, String label, String[] values, int x, int y) {
		g.drawRect(x, y, 115, 88);
		g.drawString(label, x + 2, y + 12);

		for (int i = 0; i < values.length; i++) {
			g.drawString(values[i], x + 2, y + 12 + ((i + 1) * 14));
		}

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

		graphic2d.setColor(background);
		graphic2d.fillRect(0, 0, size.width, size.height);

		graphic2d.setFont(font);
		graphic2d.setColor(foreground);

		int availableWidth = ((size.width < size.height) ? size.width : size.height) - 150;

		spacing = availableWidth / 10;

		int currentPC = computer.getProgramCounter();

		graphic2d.drawRect(memoryOffsetx, memoryOffsety - 10, availableWidth, availableWidth);

		for (int i = 0; i < 100; i++) {
			int x = memoryOffsetx + spacing * (i % 10);
			int y = memoryOffsety + spacing * (i / 10);

			String memory = String.format("%03d", computer.getMemory(i));

			if (i < computer.getHighestUsedAddress() + 1) {
				graphic2d.setColor(foreground);

			} else {
				graphic2d.setColor(foregroundDim);
			}
			graphic2d.drawString(memory, x + 7, offset + y);

			if (i == currentPC) {
				graphic2d.setColor(highlight);
			}

			graphic2d.drawString(""+i, x + 8, y);

			graphic2d.drawRect(x + 6, y + 4, 25, 15);
		}

		graphic2d.setColor(foreground);

		// Draw various registers
		int textX = 10;
		int textY = 10;
		drawInBox(graphic2d, "Program Counter", computer.getProgramCounter(), textX, textY);
		drawInBox(graphic2d, "Instruction", "Register", computer.getInstructionRegister(), textX, textY + 40);
		drawInBox(graphic2d, "Address", "Register", computer.getAddressRegister(), textX, textY + 94);
		drawInBox(graphic2d, "Accumulator", computer.getAccumulator(), textX, textY + 148);
		drawInBox(graphic2d, "Input", computer.getInput(), textX, textY + 188);
		drawOutputBox(graphic2d, "Output", computer.getOutput(), textX, textY + 228);

		graphic2d.drawRect(memoryOffsetx, memoryOffsety + spacing * 10, availableWidth, 100);

		String message = "";
		if (computer.isHalted()) {
			message = "Halted.";
		} else {
			boolean fetch = computer.isFetch();
			String name = Mnemonic.instructionName(computer.getFullCurrentInstruction());

			if (fetch) {
				message = "Fetched: " + computer.getFullCurrentInstruction() + "(" + name + ")";
			} else {
				message = "Executed: " + computer.getFullCurrentInstruction() + "(" + name + ")\n";
				
				
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
		String speed = "CPU Speed: "+computer.getCpuSpeed()+" Hz";
		graphic2d.drawString(speed, 2, 16 + memoryOffsety + spacing * 10);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		if (evt.getPropertyName().equals("step")) {
			this.repaint();
		}

	}
}
