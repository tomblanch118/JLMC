package computer.instruction;

/**
 * Represents an LMC instruction and its location in memory (may be
 * invalid if the instruction has not been processed). Also allows 
 * translation from the opcode to a number and contains various
 * other relevant helper methods.
 * @author tomblanchard
 *
 */
public class Instruction implements Comparable<Instruction>{
	private final Mnemonic mnemonic;
	private final int instructionAddress;
	
	/**
	 * Constructs an Instruction from a mnemonic and address
	 * @param mnemonic The instructions mnemonic eg (ADD, SUB)
	 * @param instructionAddress The address in memory in which this 
	 * instruction resides.
	 */
	public Instruction(Mnemonic mnemonic, int instructionAddress)
	{
		this.instructionAddress = instructionAddress;
		this.mnemonic = mnemonic;
	}
	
	/**
	 * @return The instructions address in memory.
	 */
	public int getInstructionAddress()
	{
		return instructionAddress;
	}
	
	/*
	 * Returns the numeric opcode associated with this instruction.
	 */
	public int toOpCode()
	{
		return mnemonic.getOpCode();
	}
	
	/**
	 * 
	 * @return This instructions mnemonic eg ADD, SUB, STA etc
	 */
	public Mnemonic getMnemonic()
	{
		return mnemonic;
	}
	
	/**
	 * @return A nicely formatted representation of this instruction.
	 */
	public String toFormattedString()
	{
		return mnemonic.toString();
	}
	
	/**
	 * Whether any labels associated with this instruction are resolved.
	 * @return true always as a simple instruction has no related labels.
	 */
	public boolean isResolved() {
		return true;
	}
	
	/**
	 * Returns a text representation of the instruction.
	 */
	public String toString()
	{
		String opCode = String.format("%03d", this.toOpCode());
		
		return (instructionAddress < 10?"0":"")+instructionAddress+":"
				+opCode+"=>"+mnemonic.toString();
	}

	/**
	 * Compares two instructions based on their address.
	 * 
	 * @param instruction The instruction to compare against.
	 * @return 0 if the two instructions are at the same address,
	 * 1 if this instruction address is greater than the address of 
	 * instruction and -1 otherwise.
	 */
	@Override
	public int compareTo(Instruction instruction) {
		if(instruction == null){
			throw new NullPointerException();
		}
		
		if(this.instructionAddress == instruction.instructionAddress){
			return 0;
		}
		
		if(this.instructionAddress > instruction.instructionAddress){
			return 1;
		}
		
		return -1;
	}
}
