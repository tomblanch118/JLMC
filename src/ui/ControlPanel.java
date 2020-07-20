package ui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import computer.model.Computer;
import computer.model.InputChannel;
import language.Messages;


@SuppressWarnings("serial")
public class ControlPanel extends JPanel implements ActionListener, InputChannel, PropertyChangeListener, LocalisationListener {
	private JButton run = new JButton(Messages.getTranslatedString("RUN_BUTTON"));
	private JButton stop = new JButton(Messages.getTranslatedString("STOP_BUTTON"));
	private JButton step = new JButton(Messages.getTranslatedString("STEP_BUTTON"));
	private JButton faster = new JButton(Messages.getTranslatedString("FASTER_BUTTON"));
	private JButton slower = new JButton(Messages.getTranslatedString("SLOWER_BUTTON"));
	private JButton reset = new JButton(Messages.getTranslatedString("RESET_BUTTON"));
	
	private static final int UI_PADDING = 4;

	private Computer computer;
	private Timer timer = new Timer(10, this);

	public ControlPanel(Computer computer) {
		
		Messages.registerLocalisationListener(this);
		this.computer = computer;
		this.setBorder(new EmptyBorder(UI_PADDING,UI_PADDING,UI_PADDING,UI_PADDING));
		this.setLayout(new GridLayout(1, 6, UI_PADDING, 0));
		

		this.add(run);
		this.add(stop);
		this.add(step);
		this.add(slower);
		this.add(faster);
		this.add(reset);
		
		styleButton(run);
		styleButton(stop);
		styleButton(step);
		styleButton(slower);
		styleButton(faster);
		styleButton(reset);
	
		//this.add(speed);
		
		this.setMinimumSize(new Dimension(400,35));
		this.setPreferredSize(new Dimension(400,35));
		
		run.addActionListener(this);
		stop.addActionListener(this);
		step.addActionListener(this);
		faster.addActionListener(this);
		slower.addActionListener(this);
		reset.addActionListener(this);
		
		this.setBackground(ColorScheme.background);

		setCPUSpeed();
		setReadyToRun();
		this.setVisible(true);
		
	}

	private void styleButton(JButton button) {
		button.setContentAreaFilled(false);
		button.setOpaque(true);
		button.setBorderPainted(false);
		button.setBackground(ColorScheme.button);
		button.setForeground(Color.WHITE);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(timer) || e.getSource().equals(step)) {
			computer.step();
			if(computer.isHalted())
			{
				setReadyToRun();
				run.setEnabled(false);
				step.setEnabled(false);
				stop.setEnabled(false);
				reset.setEnabled(true);
				JOptionPane.showMessageDialog(this.getParent(), Messages.getTranslatedString("HALTED"));

			}
		} else if (e.getSource().equals(run)) {
			setRunning();

		} else if (e.getSource().equals(stop)) {
			setReadyToRun();

		} else if (e.getSource().equals(faster)) {
			if (computer.getCpuSpeed() == Computer.MIN_CPU_SPEED) {
				slower.setEnabled(true);
			}
			computer.setCpuSpeed(computer.getCpuSpeed()+1);
			if (computer.getCpuSpeed() == Computer.MAX_CPU_SPEED) {
				faster.setEnabled(false);
			}

			setCPUSpeed();
		} else if (e.getSource().equals(slower)) {
			if (computer.getCpuSpeed() == Computer.MAX_CPU_SPEED) {
				faster.setEnabled(true);
			}
			computer.setCpuSpeed(computer.getCpuSpeed()-1);

			if (computer.getCpuSpeed() == Computer.MIN_CPU_SPEED) {
				slower.setEnabled(false);
			}
			setCPUSpeed();
		} else if (e.getSource().equals(reset)) {
			setReadyToRun();
			computer.restart();
		}
	}

	private void setCPUSpeed() {
		timer.setDelay(1000 / computer.getCpuSpeed());
	}

	private void setRunning() {
		run.setEnabled(false);
		step.setEnabled(false);
		stop.setEnabled(true);
		reset.setEnabled(false);
		timer.start();
		//speed.setText(Messages.CPU_SPEED + ": " + cpuSpeed + "Hz");
	}

	private void setReadyToRun() {
		run.setEnabled(true);
		step.setEnabled(true);
		stop.setEnabled(false);
		reset.setEnabled(true);

		timer.stop();
		//speed.setText(Messages.CPU_SPEED + ": " + cpuSpeed + "Hz");
	}

	@Override
	public Integer readInput() {

		Container parent = this.getParent();
		String s = (String) JOptionPane.showInputDialog(parent, Messages.getTranslatedString("INPUT_PLEASE"),
				Messages.getTranslatedString("INPUT_TITLE"), JOptionPane.PLAIN_MESSAGE, null, null, null);

		int i = 0;
		try {

			i = Integer.parseInt(s);
			if (i < 0) {
				JOptionPane.showMessageDialog(this.getParent(), Messages.getTranslatedString("INPUT_MUST_NUMBER"));
				setReadyToRun();
				computer.restart();
				return null;
			}

		} catch (NumberFormatException nfe) {
			JOptionPane.showMessageDialog(this.getParent(), Messages.getTranslatedString("NO_INPUT_FOUND") );

			setReadyToRun();
			computer.restart();
			return null;
		}
		return i;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals("reset"))
		{
			//System.out.println("reset");
			setReadyToRun();
		}
	}

	@Override
	public void relocalise() {
		run.setText(Messages.getTranslatedString("RUN_BUTTON"));
		stop.setText(Messages.getTranslatedString("STOP_BUTTON"));
		step.setText(Messages.getTranslatedString("STEP_BUTTON"));
		faster.setText(Messages.getTranslatedString("FASTER_BUTTON"));
		slower.setText(Messages.getTranslatedString("SLOWER_BUTTON"));
		reset.setText(Messages.getTranslatedString("RESET_BUTTON"));
		
	}
}
