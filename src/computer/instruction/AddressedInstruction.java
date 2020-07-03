package computer.instruction;

public class AddressedInstruction extends Instruction{
	private boolean addressResolved;
	private String targetLabel;
	private int targetAddress;

	
	public AddressedInstruction(Mnemonic m, int instructionAddress, String label)
	{
		super(m,instructionAddress);
		this.targetLabel = label;
		this.addressResolved=false;
	}
	
	public AddressedInstruction(Mnemonic m, int instructionAddress, int address)
	{
		super(m,instructionAddress);
		this.targetAddress = address;
		this.targetLabel = null;
		this.addressResolved=true;
	}
	
	public String toString()
	{
		return super.toString()+" -> "+
	(!addressResolved?targetLabel:targetAddress);
	}
	
	public int toOpCode()
	{
		if(!addressResolved)
		{
			throw new IllegalStateException("Address of instruction "+
					this.toString()+" not resolved");
		}
		return super.toOpCode()+targetAddress;
	}
	
	@Override
	public boolean isResolved()
	{
		return addressResolved;
	}
	
	public String getTargetLabel()
	{
		return targetLabel;
	}
	
	public String toFormattedString()
	{
		return super.toFormattedString()+" "+(targetLabel==null?targetAddress:targetLabel);
	}
	
	public void resolve(int address)
	{
		this.addressResolved = true;
		this.targetAddress = address;
	}
	
	/**
	 *  Returns the target address of the instruction, only valid if 
	 *  {@link #isResolved()} returns true
	 * @return The instructions target address.
	 */
	public int getTargetAddress()
	{
		return targetAddress;
	}
	
	
}
