package computer.instruction;

import language.Messages;

/**
 * Mnemonic enum that provides the text and numeric representation of the 
 * mnemonics of this assembly language.
 * 
 * @author tomblanchard
 *
 */
public enum Mnemonic {
  ADD(100),
  SUB(200), 
  STA(300), 
  LDA(500), 
  BRA(600), 
  BRZ(700), 
  BRP(800), 
  INP(901), 
  OUT(902), 
  OTC(922), 
  HLT(000), 
  DAT(0);

  private int opCode;

  private Mnemonic(int opCode) {
    this.opCode = opCode;
  }

  public int getOpCode() {
    return opCode;
  }

  /**
   * Returns the name of the instruction opCode.
   * 
   * @param opCode the opcode
   * @return the name of the opcode
   */
  public static String instructionName(int opCode) {
    String s;
    if (opCode == 901) {
      s = "INP";
    } else if (opCode == 902) {
      s = "OUT";
    } else if (opCode == 922) {
      s = "OTC";
    } else if (opCode == 0) {
      s = "HLT";
    } else {
      int instruction = opCode / 100;

      switch (instruction) {
        case 1:
          s = "ADD";
          break;
        case 2:
          s = "SUB";
          break;
        case 3:
          s = "STA";
          break;
        case 5:
          s = "LDA";
          break;
        case 6:
          s = "BRA";
          break;
        case 7:
          s = "BRZ";
          break;
        case 8:
          s = "BRP";
          break;
        default:
          s = "ERR";
      }
    }
    return s;
  }

  /**
   * Returns an explanation of what opCode is actually doing. 
   * These strings will be localised.
   * 
   * @param opCode the opcode
   * @return the explanation of what this opcode does
   */
  public static String explanation(int opCode) {
    String s;

    if (opCode == 901) {
      s = Messages.getTranslatedString("READ_INPUT");
    } else if (opCode == 902) {
      s = Messages.getTranslatedString("WRITE_ACCUMULATOR");
    } else if (opCode == 922) {
      s = Messages.getTranslatedString("WRITE_ACCUMULATOR_AS_CHAR");
    } else if (opCode == 0) {
      s = Messages.getTranslatedString("HALT_COMPUTER");
    } else {
      int instruction = opCode / 100;
      int address = opCode % 100;

      switch (instruction) {
        case 1:
          //TODO: REally it shouldn't be up to the called to request a token is inserted...
          s = Messages.insertToken(Messages.getTranslatedString("ADD_MEM_ADDRESS"), "" + address);
          break;
        case 2:
          s = Messages.insertToken(Messages.getTranslatedString("SUB_MEM_ADDRESS"), "" + address);
          break;
        case 3:
          s = Messages.insertToken(Messages.getTranslatedString("STORE_IN_ADDRESS"), "" + address);;
          break;
        case 5:
          s = Messages.insertToken(Messages.getTranslatedString("LOAD_MEM_ADDRESS"), "" + address);
          break;
        case 6:
          s = Messages.insertToken(Messages.getTranslatedString("SET_PC"), "" + address);
          break;
        case 7:
          s = Messages.insertToken(Messages.getTranslatedString("IF_ZERO"), "" + address);
          break;
        case 8:
          s = Messages.insertToken(Messages.getTranslatedString("IF_POSITIVE"), "" + address);
          break;
        default:
          s = Messages.insertToken(Messages.getTranslatedString("CONFUSED"), "" + address);
      }
    }

    return s;
  }
}
