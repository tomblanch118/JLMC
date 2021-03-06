package computer.model;

import computer.ParseException;
import computer.instruction.AddressedInstruction;
import computer.instruction.Instruction;
import computer.instruction.Mnemonic;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import language.Messages;

/**
 * Editor Model class that deals with parsing the contents of the code editor
 * and assembling it. As a by product it is also responsible for generating 
 * parse errors, resolving labels and formatting.
 * 
 * @author tomblanchard
 *
 */
public class Editor {
  /**
   * Data structures to hold the code / syntax as it is parsed.
   */
  private ArrayList<String> lines = new ArrayList<String>();
  private HashMap<String, Integer> labels = new HashMap<String, Integer>();
  private ArrayList<Instruction> instructions = new ArrayList<Instruction>();
  private HashMap<Integer, String> comments = new HashMap<Integer, String>();

  public Editor() {
  }

  /**
   * Reset the model.
   */
  private void reset() {
    lines.clear();
    labels.clear();
    instructions.clear();
    comments.clear();
  }

  
  /**
   * Add code to the editor model. One of the setText methods must
   * be called before parsing can commence.
   * @param text The text
   */
  public void setText(String text) {
    setText(Arrays.asList(text.split("\n")));
  }


  /**
   * Add code text to the editor model. One of the setText methods must
   * be called before parsing can commence.
   * @param lines the lines
   */
  public void setText(List<String> lines) {
    reset();

    for (String line : lines) {
      if (line.length()   != 0) {
        this.lines.add(line);
      }
      
    }
  }
  
  /**
   * Formats the code.
   * @return
   */
  public String format() {
    StringBuilder sb = new StringBuilder();
    
    int longestLabel = 0;

    // store labels by line number and figure out the longest label
    HashMap<Integer, String> labelsByLine = new HashMap<Integer, String>();

    for (String s : labels.keySet()) {
      labelsByLine.put(labels.get(s), s);
      if (s.length() > longestLabel) {
        longestLabel = s.length();
      }
    }

    if (longestLabel != 0) {
      longestLabel += 1;
    }

    // Make sure the instructions are in the right order
    Collections.sort(instructions);

    int lineNumber = 0;
    for (Instruction i : instructions) {
      
      //Pad each line by the longest label length + 1
      String label = labelsByLine.get(lineNumber);
      
      if (label == null) {
        
        for (int j = 0; j < longestLabel; j++) {
          sb.append(" ");
        }
      } else {
        
        int labelLength = label.length();
        sb.append(label);
        
        for (int j = 0; j < longestLabel - labelLength; j++) {
          sb.append(" ");
        }
      }
      
      sb.append(i.toFormattedString());
      
      // subtract the length of the instruction from the longest label length
      int len = (5 + longestLabel - i.toFormattedString().length());
      
      for (int it = 0; it < len; it++) {
        sb.append(" ");  
      }
      
      
      String comment = comments.get(lineNumber);
      
      if (comment != null) {
        sb.append(comment);
      }
      
      sb.append("\n");
      lineNumber++;
    }
    return sb.toString();
  }

 
  /**
   * Parse a set of syntax tokens.
   * @param tokens the tokens
   * @param lineNumber the line number
   * @throws ParseException describing the type of parsing error
   */
  private void parseInstruction(String[] tokens, int lineNumber) throws ParseException {
    //First token is a label
    if (!isInstruction(tokens[0])) {
      throw new ParseException(
          Messages.getTranslatedString("S_ERR_INSTRUCTION_EXPECTED"), tokens[0], lineNumber);
    
    //First token is NOT a label it is a valid instruction
    } else {
      //Turn it into an uppercase version (neatness)
      tokens[0] = tokens[0].toUpperCase();
      
      //If the instruction does not take a parameter 
      if (!takesParameter(tokens[0])) {
        
        //If the instruction does not take a parameter but there is a parameter throw exception
        if (tokens.length == 2) {
          throw new ParseException(
              Messages.getTranslatedString("S_ERR_POINTLESS_PARAMETER"), tokens[0], lineNumber);
        }
        
        //Create a new instruction and add it
        Instruction instruction = new Instruction(Mnemonic.valueOf(tokens[0]), lineNumber);
        instructions.add(instruction);
        
      //Instruction does take a parameter
      } else {
        
        //Must be a parameter available
        if (tokens.length < 2) {
          
          //Unless it is DAT which defaults to a parameter of 0
          if (tokens[0].equals("DAT")) {

            //Create a new instruction and add it
            AddressedInstruction instruction = new AddressedInstruction(
                Mnemonic.valueOf(tokens[0]), lineNumber, 0);
            instructions.add(instruction);

          // If instruction is not DAT and parameter is missing throw exception
          } else {
            throw new ParseException(
                Messages.getTranslatedString("S_ERR_PARAMETER_EXPECTED"), tokens[0], lineNumber);
          }
        //Parameter is available for this instruction
        } else {
          
          //Make sure the parameter is not a label with the same value as an instruction
          if (!isInstruction(tokens[1])) {
            
            //Try to parse the parameter as a number unless
            AddressedInstruction instruction = null;
            try {
              int a = Integer.parseInt(tokens[1]);
              // bugfix: don't check dat values 
              if (!(tokens[0].equals("DAT")) && a >= Computer.memorySize || a < 0) {
                throw new ParseException(
                    Messages.getTranslatedString("S_ERR_ADDRESS"), tokens[1], lineNumber);

              }
              instruction = new AddressedInstruction(Mnemonic.valueOf(tokens[0]), lineNumber, a);
            //Nope, parameter must be a label so add it
            } catch (NumberFormatException e) {
              labels.putIfAbsent(tokens[1], -1);
              instruction = new AddressedInstruction(
                  Mnemonic.valueOf(tokens[0]), lineNumber, tokens[1]);
            }
            
            instructions.add(instruction);

          } else {
            throw new ParseException(
                Messages.getTranslatedString("S_ERR_LABEL_NAME"), tokens[1], lineNumber);
          }
        }
      }
    }
  }

  /**
   * Goes through all of the parsed instructions and if they use a label
   * try to find that label in the parsed labels. This allows labels to be 
   * resolved into numeric addresses.
   * 
   * @throws ParseException If an instruction is found with a label that cannot
   *     be resolved into an address.
   */
  private void resolveInstructions() throws ParseException {

    // Go through each instruction
    for (Instruction i : instructions) {
      
      // This is not nice, but currently only addressed instructions can have labels
      if (i instanceof AddressedInstruction) {
        AddressedInstruction ai = (AddressedInstruction) i;
        if (!ai.isResolved()) {
          String label = ai.getTargetLabel();

          if (!labels.containsKey(label)) {
            throw new ParseException(
                Messages.getTranslatedString("L_ERR_LABEL_UNLINKED"), ai.getMnemonic().toString(),
                ai.getInstructionAddress());
          } else {
            int address = labels.get(label);
            ai.resolve(address);
          }
        }
      }
    }
  }
  
  //TODO: if not halt found, tell user
  /**
   * Attempts to parse the code currently in the editor model.
   * 
   * @throws ParseException If the parsing fails at any point. Exceptions will 
   *     contains information on the offending token.
   */
  public void parse() throws ParseException {

    int address = 0;
    
    // Quick out
    if (lines.size() == 0) {
      return;
    }
    
    // Process each line in turn
    for (String line : lines) {

      // Remove any comments and whitespace as the are not relevant lexically
      line = stripComments(line, address);
      line = line.trim();
      
      // Can't have empty lines
      if (line.length() == 0) {
        throw new ParseException(Messages.getTranslatedString("S_ERR_COMMENT_EOL"), "", address);
      }

      // parse each line and increment the current instruction address
      parse(line, address);
      address++;

    }

    
    //TODO: Tidy up this and probably merge with resolveInstructions
    // Resolve the labels into numeric addresses
    for (String s : labels.keySet()) {
      
      if (labels.get(s).equals(-1)) {
        
        int lineNumber = -1;
        
        for (Instruction i : instructions) {
          
          // If the instruction takes an address check if it takes this label
          if (i instanceof AddressedInstruction) {
            AddressedInstruction ai = (AddressedInstruction) i;
            String label = ai.getTargetLabel();
            
            //Resolve this instructions label / address
            if (s.equals(label)) {
              lineNumber = ai.getInstructionAddress();
            }
          }
        }
        // Potentially an instruction refers to a label that was never defined. If so, report it.
        throw new ParseException(Messages.getTranslatedString("L_ERR_NOT_DEFINED"), s, lineNumber);
      }
    }

    resolveInstructions();
    Collections.sort(instructions);

  }

  private void parse(String line, int lineNumber) throws ParseException {
    String[] tokens = line.split("\\s+");

    if (tokens.length > 3) {
      throw new ParseException(
          Messages.getTranslatedString("S_ERR_TOO_MANY_TOKENS"), line, lineNumber);
    }

    if (!isInstruction(tokens[0])) {
      
      // If we dont have an instruction, we have a label and a label cant be a number
      if (isNumber(tokens[0])) {
        throw new ParseException(
            Messages.getTranslatedString("S_ERR_NUMBER_NOT_LABEL"), tokens[0], lineNumber);
      }
      
      // Cannot have a label all by itself
      if (tokens.length == 1) {
        throw new ParseException(
            Messages.getTranslatedString("S_ERR_LONELY_LABEL"), line, lineNumber);
      }

      // If we have alread 
      if (labels.containsKey(tokens[0]) && labels.get(tokens[0]) != -1) {
        throw new ParseException(
            "Label '" + tokens[0] + "' already used on line "
          + labels.get(tokens[0]), line, lineNumber);
      }
      labels.putIfAbsent(tokens[0], lineNumber);
      
      if (labels.get(tokens[0]).equals(-1)) {
        labels.put(tokens[0], lineNumber);
      }

      String[] remainingTokens = new String[tokens.length - 1];
      for (int i = 1; i < tokens.length; i++) {
        remainingTokens[i - 1] = tokens[i];
      }
      parseInstruction(remainingTokens, lineNumber);
    } else {
      parseInstruction(tokens, lineNumber);
    }
  }

  /**
   * Checks whether a token is an instruction.
   * @param token The string to be checked.
   * @return true if token is an instruction, false otherwise.
   */
  public static boolean isInstruction(String token) {
    token = token.toUpperCase();
    switch (token) {
      case "INP":
      case "OUT":
      case "OTC":
      case "STA":
      case "LDA":
      case "DAT":
      case "ADD":
      case "SUB":
      case "BRA":
      case "BRZ":
      case "BRP":
      case "HLT":
        return true;
      default:
        return false;
    }
  }
  
  /**
   * Check if a token is a number.
   *
   * @return true of token is a number, false otherwise.
   */
  private boolean isNumber(String token) {
    try {
      Float.parseFloat(token);
      return true;
    } catch (NumberFormatException nfe) {
      return false;
    }
  }

  /**
   * Checks if token takes a paratmer.
   * @param token String to be checked
   * @return true if the instruction represented by this string takes a parameter,
   *     false otherwise.
   */
  private boolean takesParameter(String token) {
    token = token.toUpperCase();
    switch (token) {
      case "STA":
      case "LDA":
      case "DAT":
      case "ADD":
      case "SUB":
      case "BRA":
      case "BRZ":
      case "BRP":
        return true;
      default:
        return false;   
    }
  }

  /**
   * Strips comments from the String line.
   * @param line The string to remove comments from (single line supported only).
   * @param lineNumber The line number of code that this string represents.
   * @return The stripped string.
   */
  public String stripComments(String line, int lineNumber) {
    if (line.contains("//")) {
      int index = line.indexOf("//");

      comments.put(lineNumber, line.substring(index, line.length()));
      line = line.substring(0, index);
    }
    return line;
  }
  
  /**
   * Gets the instructions from the editor.
   * @return all of the instructions that have been parsed.
   */
  public ArrayList<Instruction> getInstructions() {
    return instructions;
  }
}
