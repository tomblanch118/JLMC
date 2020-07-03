package computer.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

import computer.instruction.AddressedInstruction;
import computer.instruction.Instruction;

/**
 * 
 * @author Tom Blanchard
 *
 */
public class Computer {
	public static final int memorySize = 100;
	private final int outputSize = 5;

	// Are we on a fetch step or an execute step
	private boolean fetch = false;
	private boolean halted = true;

	// Variable representing the internal register of our very simple CPU
	private int input = 0;
	private String[] output = new String[outputSize];
	private int accumulator = 0;
	private int programCounter = 0;
	private int instructionRegister = 0;
	private int addressRegister = 0;

	private int maxAddress = memorySize - 1;

	// Computers 'memory'.
	private int[] memory = new int[memorySize];

	public static final int MAX_CPU_SPEED = 20;
	public static final int MIN_CPU_SPEED = 1;
	private int cpuSpeed = MIN_CPU_SPEED;

	
	// Mechanism by which we can get user input.
	// Anything can register, as long as it can prompt the user for input.
	private InputChannel inputChannel;

	// Observer pattern for keeping GUI up to date.
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	/**
	 * Reset everything on construction.
	 */
	public Computer() {
		reset();
	}

	/**
	 * Prints the state of the of the CPU registers.
	 */
	public void printRegisters() {
		System.out.println("ACC: " + accumulator + ", PC: " + programCounter + "\nISR: " + instructionRegister
				+ ", AR: " + addressRegister);

	}

	/**
	 * Returns the contents of the memory address matching index.
	 * 
	 * @param index The memory address to return the contents of.
	 * @return The contents of the memory address.
	 */
	public int getMemory(int index) {
		return memory[index];
	}

	/**
	 * Runs one fetch, decode, execute cycle.
	 */
	public void step() {
		fetch = !fetch;
		halted = false;
		if (fetch) {
			fetch();
			programCounter++;
		} else {
			execute();
		}
		pcs.firePropertyChange("step", false, true);
	}

	public int getFullCurrentInstruction() {
		return instructionRegister * 100 + addressRegister;
	}

	// TODO: create log so that if we crash we have some evidence as to why
	/**
	 * Executes the decoded instruction in the instruction register. Includes
	 * writing to memory, the output, accumulator or program counter.
	 * 
	 * @throws IllegalStateException If an invalid opcode is detected.
	 */
	private void execute() throws IllegalStateException {
		switch (instructionRegister) {
		// Halt opcode
		case 0:
			halted = true;
			break;
		// Add opcode
		case 1:
			accumulator += memory[addressRegister];
			break;
		// subtract opcode
		case 2:
			accumulator -= memory[addressRegister];
			break;
		// Store opcode
		case 3:
			memory[addressRegister] = accumulator;
			break;
		// Illegal opcode
		case 4:
			// throw new IllegalStateException(
			// "Invalid instruction 4" + addressRegister + " in address " + programCounter);
			halted = true;

			// Load opcode
		case 5:
			accumulator = memory[addressRegister];
			break;
		// Branch always opcode
		case 6:
			programCounter = addressRegister;
			break;
		// Branch if zero opcode
		case 7:
			if (accumulator == 0) {
				programCounter = addressRegister;
			}
			break;
		// Branch if zero or positive opcode
		case 8:
			if (accumulator >= 0) {
				programCounter = addressRegister;
			}
			break;
		// i/o opcode
		case 9:
			// output address
			if (addressRegister == 2) {
				addToOutput(accumulator, false);
			} else if (addressRegister == 22) {
				addToOutput(accumulator, true);
			}
			// input address
			else if (addressRegister == 1) {
				if (inputChannel == null) {
					throw new IllegalStateException(
							"Computer has no registered " + "input channel and therefore is unable to read input");
				}
				Integer ip = inputChannel.readInput();
				if (ip == null) {
					return;
				}
				input = ip;
				accumulator = input;
			}
			break;
		// All other opcodes are invalid
		default:
			throw new IllegalStateException("Invalid instruction " + instructionRegister + "" + addressRegister
					+ " in address " + programCounter);
		}

	}

	/**
	 * Increments the program counter, fetches the next instruction and decodes it.
	 */
	private void fetch() {
		int instruction = memory[programCounter];
		instructionRegister = instruction / 100;
		addressRegister = instruction % 100;
	}

	/**
	 * Prints the entire contents of the computers memory
	 */
	public void printMemory() {
		for (int x = 0; x < 10; x++) {
			for (int y = 0; y < 10; y++) {
				String s = String.format("%03d ", memory[x * 10 + y]);
				System.out.print(s);
			}
			System.out.println();
		}
	}

	/**
	 * Resets the internal state of computer. This includes memory and all internal
	 * registers.
	 */
	public void reset() {
		for (int i = 0; i < memorySize; i++) {
			memory[i] = 0;
		}
		restart();
	}

	/**
	 * Loads a set of instructions into the computers memory. These can then be
	 * executed by called the step method repeatedly.
	 * 
	 * @param instructions The set of instructions to be loaded.
	 */
	public void load(ArrayList<Instruction> instructions) {
		reset();
		maxAddress = 0;
		for (Instruction i : instructions) {
			memory[i.getInstructionAddress()] = i.toOpCode();

			if (i.getInstructionAddress() > maxAddress) {
				maxAddress = i.getInstructionAddress();
			}

			// TODO: Urgh instanceof is a bit grim
			if (i instanceof AddressedInstruction) {
				AddressedInstruction ai = (AddressedInstruction) i;
				if (ai.isResolved() && ai.getTargetAddress() > maxAddress) {
					maxAddress = ai.getTargetAddress();
				}
			}
		}

	}

	private void clearOutput() {
		for (int i = 0; i < output.length; i++) {
			output[i] = "";
		}
	}

	/**
	 * Sets all of the internal registers to 0 (allows program to run from
	 * beginning)
	 */
	public void restart() {
		input = 0;
		clearOutput();
		accumulator = 0;
		programCounter = 0;
		instructionRegister = 0;
		addressRegister = 0;
		fetch = false;
		halted = true;
		pcs.firePropertyChange("step", false, true);
		pcs.firePropertyChange("reset", false, true);
	}

	/**
	 * 
	 * @return
	 */
	public boolean isHalted() {
		return halted;
	}

	/**
	 * 
	 * @return true if computer in fetch phase false otherwise.
	 */
	public boolean isFetch() {
		return fetch;
	}

	/**
	 *
	 * @return The contents of the input register.
	 */
	public int getInput() {
		return input;
	}

	/**
	 * 
	 * @return The contents of the output register.
	 */
	public String[] getOutput() {
		return output;
	}

	public void addToOutput(int value, boolean convert) {
		for (int i = output.length - 2; i >= 0; i--) {
			output[i + 1] = output[i];
		}
		if (convert) {
			output[0] = "" + (char) value;

		} else {
			output[0] = "" + value;
		}
	}

	/**
	 * Returns the highest address in memory that is used by the program
	 * 
	 * @return
	 */
	public int getHighestUsedAddress() {
		return maxAddress;
	}

	/**
	 * 
	 * @return The contents of the accumulator.
	 */
	public int getAccumulator() {
		return accumulator;
	}

	/**
	 * 
	 * @return The program counter value.
	 */
	public int getProgramCounter() {
		return programCounter;
	}

	/**
	 * 
	 * @return The current instruction in the instruction register.
	 */
	public int getInstructionRegister() {
		return instructionRegister;
	}

	public int getAddressRegister() {
		return addressRegister;
	}

	/**
	 * Register to receive notifications on the state of the computer
	 * 
	 * @param pcl Listener object that wants to receive notifications
	 */
	public void registerPropertyChangeListener(PropertyChangeListener pcl) {
		registerPropertyChangeListener(pcl, "step");
	}

	/**
	 * Unregister to receive notifications on the state of the computer
	 * 
	 * @param pcl Listener object that no longer wants to receive notifications
	 */
	public void unregisterPropertyChangeListener(PropertyChangeListener pcl) {
		unregisterPropertyChangeListener(pcl, "step");
	}

	/**
	 * Register to receive notifications on the state of the computer
	 * 
	 * @param pcl Listener object that wants to receive notifications
	 */
	public void registerPropertyChangeListener(PropertyChangeListener pcl, String parameter) {
		pcs.addPropertyChangeListener(parameter, pcl);
	}

	/**
	 * Unregister to receive notifications on the state of the computer
	 * 
	 * @param pcl Listener object that no longer wants to receive notifications
	 */
	public void unregisterPropertyChangeListener(PropertyChangeListener pcl, String parameter) {
		pcs.removePropertyChangeListener(parameter, pcl);
	}

	/**
	 * Register to provide input to the computer when required.
	 * 
	 * @param ic Input provider
	 */
	public void registerInputChannel(InputChannel ic) {
		this.inputChannel = ic;
	}
	
	public int getCpuSpeed() {
		return cpuSpeed;
	}
	
	public void setCpuSpeed(int cpuSpeed) {
		if(cpuSpeed < MIN_CPU_SPEED) {
			cpuSpeed = MIN_CPU_SPEED;
		}
		else if(cpuSpeed > MAX_CPU_SPEED) {
			cpuSpeed = MAX_CPU_SPEED;
		} else {
			this.cpuSpeed = cpuSpeed;
		}
		pcs.firePropertyChange("step", false, true);
		
	}
	
}
