![alt text](https://user-images.githubusercontent.com/7421219/87862661-66579480-c94a-11ea-86dd-451b8e89cf6c.png "Screen of JLMC")

# JLMC
A desktop version of the Little Man Computer created by Peter Higginson ( https://peterhigginson.co.uk/lmc/ ). Has the same feature set as the original but also some quality of life upgrades such as more **support for different languages** (currently english & Welsh), **helpful and verbose error checking** (including looking at labels) and **syntax highlighting**. Just download JLMC.jar and double click it to run. Jave 8+ required.

There is currently no bus / bus animation as from experience this tends to get old quite quickly.

Working on generating installers.

# Usage
Write code in the editor pane on the left and click compile to try to compile the code.
If the compilation is successful the status bar at the bottom of the editor will be green. 
If some kind of error was detected the status bar will go red, an error message will be shown and the
offending line will be highlighted. 

The computer will only be loaded with instructions if the program assembled correctly. There are currently no warnings given
on potential problems with the code.

You can save your program using File->Save. Programs will be saved with a .lmc extension. 
You can load lmc programs using File->Open. Programs must have a .lmc extension to be loaded.
You can undo typing/formatting using Ctrl-Z and redo using Ctrl-R.

# Supported Features
Instructions:
* INP - take input and store in accumulator
* OUT - output accumulator to output register
* HLT - Halt the computer
* SUB - Subtract contents of address from accumulator, eg SUB 06 subtracts the contents of address 06 from accumulator
* ADD - Add contents of address to accumulator, eg ADD 06 adds the contents of address 06 to accumulator
* STA - Store accumulator in given address, eg STA 06 store accumulator in address 06
* LDA - Load address into accumulator, eg LDA 06 loads the contents of addres 06 into the accumulator
* BRP - Branch to given address if accumulator is 0 or positive 
* BRA - Branch to given address 
* BRZ - Branch to given address if accumulator is 0
* OTC - output accumulator to output register but convert number to character
* DAT - define a memory address to store data 

Computer can be single stepped using the step button. This will perform one fetch or one execute cycle. Program counter 
is advanced after the fetch step and the memory location pointed to by the program counter is shown in green. 

Computer can be set to run using the Run button and stopped using the Stop button. The cpu speed can be changed using the 
Faster and Slower buttons. 

When input is required a pop up box will appear for you to type in your input. Input must be an Integer.


