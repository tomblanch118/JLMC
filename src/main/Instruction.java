package main;

public class Instruction implements Comparable<Instruction>{
	private final Mnemonic mnemonic;
	private final int instructionAddress;
	
	public Instruction(Mnemonic mnemonic, int instructionAddress)
	{
		this.instructionAddress = instructionAddress;
		this.mnemonic = mnemonic;
	}
	
	public int getInstructionAddress()
	{
		return instructionAddress;
	}
	public int toOpCode()
	{
		return mnemonic.getOpCode();
	}
	
	public Mnemonic getMnemonic()
	{
		return mnemonic;
	}
	
	public String toFormattedString()
	{
		return mnemonic.toString();
	}
	public boolean isResolved() {
		return true;
	}
	
	public String toString()
	{
		String opCode = String.format("%03d", this.toOpCode());
		return (instructionAddress < 10?"0":"")+instructionAddress+":"
				+opCode+"=>"+mnemonic.toString();
	}

	@Override
	public int compareTo(Instruction o) {
		if(o == null)
		{
			throw new NullPointerException();
		}
		
		if(this.instructionAddress == o.instructionAddress)
		{
			return 0;
		}
		if(this.instructionAddress>o.instructionAddress)
		{
			return 1;
		}
		
		return -1;
	}
}
