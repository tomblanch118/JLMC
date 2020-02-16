package main;

public enum Mnemonic {
	ADD(100), SUB(200), STA(300), LDA(500), BRA(600), BRZ(700), BRP(800), INP(901), OUT(902), OTC(922), HLT(000), DAT(0);

	private int opCode;

	private Mnemonic(int opCode) {
		this.opCode = opCode;
	}

	public int getOpCode() {
		return opCode;
	}

	/**
	 * Returns the name of the instruction opCode is
	 * 
	 * @param opCode
	 * @return
	 */
	public static String instructionName(int opCode) {
		String s;
		if (opCode == 901) {
			s = "INP";
		} else if (opCode == 902) {
			s = "OUT";
		} else if (opCode == 922) {
			s = "OTC";
		}else if (opCode == 0) {
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
			default:
				s = "ERR";
			}
		}
		return s;
	}

	/**
	 * Returns an explanation of what opCode is actually doing
	 * 
	 * @param opCode
	 * @return
	 */
	public static String explanation(int opCode) {
		String s;

		if (opCode == 901) {
			s = "Read input into the accumulator";
		} else if (opCode == 902) {
			s = "Write contents of accumulator to output";
		} else if (opCode == 922) {
			s = "Write contents of accumulator to output as a character";
		} else if (opCode == 0) {
			s = "Halts computer";
		} else {
			int instruction = opCode / 100;
			int address = opCode % 100;

			switch (instruction) {
			case 1:
				s = "Add contents of memory address " + address + " to accumulator.";
				break;
			case 2:
				s = "Subtract contents of memory address " + address + " from accumulator.";
				break;
			case 3:
				s = "Store contents of accumulator in memory address " + address + ".";
				break;
			case 5:
				s = "Load contents of memory address " + address + " into accumulator.";
				break;
			case 6:
				s = "Set program counter to " + address + " (Branch).";
				break;
			case 7:
				s = "If accumulator is 0, set program counter to " + address + "(Branch).";
				break;
			case 8:
				s = "If accumulator is positive, set program counter to " + address + "(Branch).";
				break;
			default:
				s = "I can't explain what that does!";
			}
		}

		return s;
	}
}
