package computer.instruction;

/**
 * Represents an instruction that relates to an address in memory in some way.
 * Eg ADD or STA both take an address to operated on; either to add the contents
 * of the accumulator or store the contents of the accumulator.
 * 
 * <p>Each AddressedInstruction stores the targetAddress, the name of the target address
 * (if it has one) and whether the label was resolved to an actual address (during
 * assembly).
 * 
 * @author tomblanchard
 *
 */
public class AddressedInstruction extends Instruction {

  //Has the target address label been resolved into a valid numeric address?
  private boolean addressResolved; 
  //The label and numeric address of.... the address
  private String targetLabel;
  private int targetAddress;

  /**
   * Construct an AddressedInstruction.
   * 
   * @param mnemonic The mnemonic of this instruction. eg (ADD, STA)
   * @param instructionAddress The address of this instruction in memory.
   * @param label The label associated with the target address. Implies that the
   *     label has not yet been resolved into a numeric address.
   */
  public AddressedInstruction(Mnemonic mnemonic, int instructionAddress, String label) {
    super(mnemonic, instructionAddress);
    this.targetLabel = label;
    this.addressResolved = false;
  }
  
  /**
   * Construct an AddressedInstruction when an actual numeric address has been
   * provided rather than a label eg:
   * ADD 33 - Specifies that ADD will add the contents of address 33 to the 
   * accumulator.
   * 
   * @param mnemonic The mnemonic of this instruction. eg (ADD, STA)
   * @param instructionAddress The address of this instruction in memory.
   * @param address The numeric address of the target address for this instruction. 
   * 
   */
  public AddressedInstruction(Mnemonic mnemonic, int instructionAddress, int address) {
    super(mnemonic, instructionAddress);
    this.targetAddress = address;
    this.targetLabel = null;
    this.addressResolved = true;
  }
  
  public String toString() {
    return super.toString() + " -> " + (!addressResolved ? targetLabel : targetAddress);
  }
  
  /**
   * Returns the associated opcode with this instruction. This combines the
   * target address with the base opcode eg:
   * STA 55 becomes 355. 300 (base opcode of STA) + 55 (the target memory address)
   * 
   * @throws IllegalStateException If the target address has not been resolved into
   *     a numeric address.
   */
  public int toOpCode() {
    if (!addressResolved) {
      throw new IllegalStateException("Address of instruction "
         + this.toString() + " not resolved");
    }
    return super.toOpCode() + targetAddress;
  }
  
  @Override
  public boolean isResolved() {
    return addressResolved;
  }
  
  public String getTargetLabel() {
    return targetLabel;
  }
  
  public String toFormattedString() {
    return super.toFormattedString() + " " 
          + (targetLabel == null ? targetAddress : targetLabel);
  }
  
  public void resolve(int address) {
    this.addressResolved = true;
    this.targetAddress = address;
  }
  
  /**
   *  Returns the target address of the instruction, only valid if 
   *  {@link #isResolved()} returns true.
   * @return The instructions target address.
   */
  public int getTargetAddress() {
    return targetAddress; 
  }
  
  
}
