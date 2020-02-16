package ui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.Timer;

import language.Messages;
import main.Computer;
import main.InputChannel;


@SuppressWarnings("serial")
public class ControlPanel extends JPanel implements ActionListener, InputChannel, PropertyChangeListener {
	private JButton run = new JButton(Messages.RUN_BUTTON);
	private JButton stop = new JButton(Messages.STOP_BUTTON);
	private JButton step = new JButton(Messages.STEP_BUTTON);
	private JButton faster = new JButton(Messages.FASTER_BUTTON);
	private JButton slower = new JButton(Messages.SLOWER_BUTTON);
	private JButton reset = new JButton(Messages.RESET_BUTTON);

	private Computer computer;
	private Timer timer = new Timer(10, this);

	public ControlPanel(Computer computer) {
		
		//TODO: Look into doing spring layout properly
		this.computer = computer;
		SpringLayout layout = new SpringLayout();
		layout.putConstraint(SpringLayout.WEST, run, 2, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.WEST, stop, 2, SpringLayout.EAST, run);
		layout.putConstraint(SpringLayout.WEST, step, 2, SpringLayout.EAST, stop);
		layout.putConstraint(SpringLayout.WEST, slower, 2, SpringLayout.EAST, step);
		layout.putConstraint(SpringLayout.WEST, faster, 3, SpringLayout.EAST, slower);
		layout.putConstraint(SpringLayout.WEST, reset, 2, SpringLayout.EAST, faster);
	//	layout.putConstraint(SpringLayout.EAST, reset, 2, SpringLayout.EAST, this);

		this.setLayout(layout);
        
		this.add(run);
		this.add(stop);
		this.add(step);
		this.add(slower);
		this.add(faster);
		this.add(reset);
		//this.add(speed);
		
		this.setMinimumSize(new Dimension(400,35));
		this.setPreferredSize(new Dimension(400,35));
		
		run.addActionListener(this);
		stop.addActionListener(this);
		step.addActionListener(this);
		faster.addActionListener(this);
		slower.addActionListener(this);
		reset.addActionListener(this);

		setCPUSpeed();
		setReadyToRun();
		this.setVisible(true);
		
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
				JOptionPane.showMessageDialog(this.getParent(), Messages.HALTED);

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
		String s = (String) JOptionPane.showInputDialog(parent, Messages.INPUT_PLEASE,
				Messages.INPUT_TITLE, JOptionPane.PLAIN_MESSAGE, null, null, null);

		int i = 0;
		try {

			i = Integer.parseInt(s);
			if (i < 0) {
				// TODO: warn user

				JOptionPane.showMessageDialog(this.getParent(), Messages.INPUT_MUST_NUMBER);
				setReadyToRun();
				computer.restart();
				return null;
			}

		} catch (NumberFormatException nfe) {
			JOptionPane.showMessageDialog(this.getParent(), Messages.NO_INPUT_FOUND );

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
			System.out.println("reset");
			setReadyToRun();
		}
	}
}
