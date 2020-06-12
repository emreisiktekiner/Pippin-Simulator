# project.assembler - Pippin Project Installment 2

## The basic concepts

The second installment of the Pippin Project consists of adding an assembler and a loader for Pippin Assembler code. If you remember from [the Pippin Lecture](http://www.cs.binghamton.edu/~tbartens/CS140_Fall_2019/class_notes/L18_Pippin.pdf), Pippin man-readable assembler consists of a mnemonic to represent the op-code, followed by an optional argument, which may be prefixed by a mode character. At the end of the instructions, there may be a DATA delimeter, followed by data locations and values.

The project.assembler package contains (or will contain when we are done) the Java code to translate the Pippin man-readable assembler into "object" code that will be "machine readable". To make our life easier, we will actually use a readable text file for our object code. Each instruction in the object code will consist of three numbers (all written into the object code file using ASCII characters). The first number is the hexadecimal representation of the numeric op-code. The second number is a mode number, 0 for null (and "absolut-direct"), 1 for indirect, 2 for direct, and 3 for immediate (as defined in the project.model.Mode class.) The third number is the argument value, expressed in hexadecimal. (Use a zero for the third number if the mode is null, which means there is no argument.)

We have provided many Pippin assembler programs in the project.assembler.pasm sub-directory, some of which are valid Pippin assembler programs, but many have errors in them. Our convention is to use a ".pasm" suffix on files which contain Pippin Assembler instructions. When we assemble a pasm file, we will write the object code out to a new file. By convention, we will use the suffix ".pexe" for Pippin object code files.

We have also provided a simple Pippin assembler which does not check for errors in the Assembler.java file. There is an ```assemble``` method in the Assembler class which takes the input .pasm file provided as the first parameter, and converts that to object code which it writes to the .pexe file specified as the second parameter. The third parameter is a reference to a data structure called a "map" to contain error messages. The key for the error map is the line number of the .pasm file line that contained the error. The data associated with that key is the textual error message itself. Note that this map only supports a single error on any given line.

A more sophisticated assembler is required - one that checks for problems in the assembler code.

We also provide a Loader class with a load method. The load method reads the object code .pexe file specified as an argument, and uses the data in that file to update the code memory and the data memory with the instructions and data specifications in the .pexe file.

## Handling Sub-projects with GIT

When you download your project.assembler repository from the web, make the target directory your normal eclipse workspace followed by "/src/project" instead of just "/src", and modify the package name to be just "assembler" instead of "project.assembler-*userid*". Then, when you go back to the Java developer view in Eclipse and refresh your src directory, you should have a "project.assembler" package that contains all the code in the repository.

## Working on Teams

Please use the same teams to work on Installment 2 of the project as you used for Installment 1.

## Organizing Sub-Projects

The code we have supplied assumes you have a sub-package of the project package called ```project.pasm``` to hold all the Pippin assembler files we will use, and a sub-project called ```project.pexe``` to contain all the Pippin object (.pexe) files we will use. You need to create these sub-packages (or sub-directories). If you are using eclipse, you can make new packages called ```project.pasm``` and ```project.pexe```. Outside of eclipse you can use the ```mkdir``` command to create these sub-directories. When you start, these sub-diretories will be empty. Move or copy all of the .pasm files from the project.assembler.pasm sub-directory provided in this repository to the project.pasm sub-package. Leave project.pexe empty. The assmebler will write to this directory.

## Making Sure the Simple Code Works

Both the Assembler and the Loader class have ```main``` methods that perform very simple unit tests to make sure the code is working. Run the Assembler main method first. This will assemble the factorial.pasm file from the project.pasm directory, and write to the factorial.pexe in the project.pexe directory. If you have set up your sub-projects correctly, this shoud work with no problems.

The Loader.java main method will load the factorial.pexe object code into main memory, and then print out some information about what is in the code and data memories. If the code runs correctly, it should produce the following results:

```
CMPZ 0
SUB #1
JMPZ #F
CMPL 0
SUB #1
JMPZ #E
LOD 0
STO 1
LOD 0
SUB #1
STO 0
CMPZ 0
SUB #1
JMPZ #6
LOD 0
MUL 1
JUMP #-9
NOT 0
STO 1
HALT 0
--
4FF 0
0 8
10 -0
```

## Coding the Instruction Class

We have provided a more sophisticated assembler class called FullAssembler which extends the Assembler class. This more sophisticated Assembler depends on modeling each instruction using an Instruction class. The instruction class models one specific instruction as we read the instruction from the Pippin Assembler file, and write it to the Pippin Object file. Your main task for this installment of the project is to code the Instruction class. We have tried to give lots of hints on how to code this class in these instructions.

First, provide two static (class) variables, an integer called ```currentLineNum``` initialized to 1, and a boolean called ```inData``` intialized to false. We will assume that a new Instruction object will be created for each Assembler line, so when we create a new Instruction object, we will use the ```currentLineNum``` field to determine the current line number, and then increment ```currentLineNum``` in the creator so that the next Instruction object will get the next line number. The ```inData``` field is used to determine if we are currently processing instructions (inData will be false) or, after we have encountered the DATA delimiter, if we are processing data specifications.

Along with these two static fields, provide a static method in the Instruction class called "reset" which takes no arguments, and resets the ```currentLineNum``` to 1, and the ```inData``` field to false. That way, we can invoke reset when we start on a new Pippin Assembler file, and use our Instruction class again.

Our Instruction class also needs several private fields, all used to model a single instruction. The list of fields I used is as follows:

- ```int lineNumber``` - the line number in the input file that this instruction came from.
- ```boolean blankLine``` - A flag to indicate if this instruction is just a blank line
- ```boolean isData``` - A flag to indicate that this "instruction" came after the DATA delimeter, and is really a data specification.
- ```boolean isDataDelim``` - A flag to indicate that this "instruction" is really the "DATA" delimiter that separates real instructions from data specifications.
- ```String mnemonic``` - The mnemonic associated with this instruction (Not used if isData is true)
- ```int opcode``` - The numeric value of the opcode if isData is false (Not used if isData is true)
- ```int mode``` - The numeric value of the argument mode associated with this instruction (Not used if isData is true)
- ```int argument``` - The numeric value of the argument itself (Not used if isData is true)
- ```int location``` - The location specified in a data specification (Not used if isData is false)
- ```int value``` - The value specified in a data specification (Not used if isData is false)
- ```ArrayList<String> errrors``` - An array list of error messages associated with this instruction.

The most complicated method in the isntruction class is the creator method, which should take a single parameter, ```String text```, that contains the text of the instruction, as read from the .pasm file. The creator method needs to fill in all the appropriate fields for this instruction based on the text parameter. It can do this with the following steps...
<ol>
  <li>Set the <code>lineNumber</code> field to the <code>currentLineNum</code>, and increment <code>currentLineNum</code>.

<li>Check to see if the line is empty. An empty line contains only white space, so the Java idiom to check for an empty line is <code>text.trim().length()==0</code>. If the text is empty, then set <code>blankLine</code> to true and return. There is nothing else to do with this instruction.

<li>Check to make sure this line does not start with white space. To do this, make a local character variable called <code>firstChar</code> and set it to <code>text.charAt(0)</code>. Then, if firstChar is either a blank or a tab (represented by '\t'), then add the error "Line starts with illegal white space" to the errors array list, and remove the leading (and trailing) white space by running <code>text=text.trim();</code>. This will allow us to check for more errors for this instruction.

<li>Next, check to see if this line is the DATA delimiter. This can be done with the Java condition <code>text.trim().toUpperCase().equals("DATA")</code>. If that condition is true, then:
<ol>
<li>If <code>inData</code> is true, add error messageg "Illegal second DATA delimiter" to this line.
<li>Make the <code>inData</code> static flag true.
<li>If the text is not equal to "DATA", then add the error message "Illegal mixed or lower case DATA delimiter".
<li>Turn on the isDataDelim flag
<li>Return from the Creator... we don't need to do anything else with this instruction.
</ol>  
  
<li>Next, we will break up the string into words by splitting the string wherever there is white space. This can be done using the String <code>split</code> method, specifying a "regular expression" to indicate one or more white space characters as a delimiter to split on. The split method returns an array of strings where each element is a white space delimited "word" taken out of text. The Java code to do this is:
  
```
String parts[] = text.split("\\s+");
```

The argument to split, <code>\\s+</code> specifies the delimiter as one or more white space characters, where white space is a blank, a tab, or a new line. The parts array will now have at least one element - one blank delimited word because we know the instruction is not empty. If it's a valid instruction, the mnemonic will be in <code>parts[0]</code>, and the argument (if there is an argument) will be in <code>parts[1]</code>. If it's a valid data specification, the location will be in <code>parts[0]</code>, and the value will be in <code>parts[1]</code>.

<li>Processing is different depending on whether this is a real instruction or a data specification. We know by using the static <code>inData</code> flag. If <code>inData</code> is true, then the current instruction is a data specification. If not, skip to the next step.
<ol>
<li>Set the <code>isData</code> flag to true.
<li>Next we need to translate the ASCII representation of the location into an integer. To do the translation, use the static Integer.parseInt method. Since the location is a hexadecimal number, the Integer.parseInt method supports a second argument that allows us to specify what base to use. Therefore, we can use the expression:
  
  ```
  location = Integer.parseInt(parts[0],16);
  ```
  
... to translate the first word into an integer. However, if the Integer.parseInt method runs into a problem, it will throw a NumberFormatException. Therefore, we need to put our translation into a try/catch block. If the try/catch block catches a NumberFormatException, it should add the error message "DATA location is not a hex number" to the instruction, and keep processing... there may be more errors.
<li>It is possible that our instruction has a location, but no value specified. We need to check, and if parts.length is less than 2, we should add the error message "No Data Value". Otherwise, translate the second word (<code>parts[1]</code>) using the same methodology as we used for the location. If a NumberFormatExcection is caught, add the error message "DATA value is not a hex number".
<li>Finally, check to make sure there are not extra words on this line. If there are, add the error message "DATA has too many values".
</ol>

<li>Otherwise, the <code>inData</code> flag must have been false so we are processing a real instruction. In this case, do the following:
<ol>  
<li>Set the <code>isData</code> flag to false.
<li>Set <code>mnemonic = parts[0]</code>. 
<li>Check to see if this is a valid mnemonic. You can use the isValidMnemonic static method from the Opcode class, using the following Java condition: <code>Opcode.isValidMnemonic(mnemonic.toUpperCase())</code>. If it's not a valid mnemonic, add the error message "illegal mnemonic", and return. (It doesn't make sense to check the argument if we don't recognize the mnemonic.)
<li>Check to see if the mnemonic is in upper case. You can use the Java condition <code>mnemonic.toUpperCase().equals(mnemonic)</code> to check this. If the mnemonic is not in upper case, add the error message "menmonic not in upper case", and then convert the mhemonic to upper case by using the instruction <code>menemonic = mnemonic.toUpperCase();</code>. But keep on checking.
<li>Set the numeric opcde field by using the static opcode method in the project.model.Opcode class. You can use <code>opcode=Opcode.opcode(mnemonic);</code>.
<li>Now we need to start checking the argument. If the opcode is an opcode for an instruction that should not have an argument, then we need to be sure that parts.length==1. You can see if it's a no argument opcode by using the Opcode static <code>isNoArgOp</code> method. If it's a no argument op code and parts.length!=1, add the error message "Illegal argument in no-argument instruction".
<li>If it's not a no argument opcode, and parts.length < 2, add an error message "Instruction requires argument". If parts.length>2, add the error message "Instruction has too many arguments". If there are exactly 2 arguments, then you can do the following:
<li>Assume that the mode is direct by setting <code>mode = Mode.DIRECT.getModeNumber();</code>.
<li>Check to see if there is a "#" prefix on the argument using the condition <code>parse[1].startsWith("#")</code>. If so, set the mode using <code>mode = Mode.IMMEDIATE.getModeNumber();</code>. Remove the # using <code>parts[1]=parts[1].substring(1);</code>. Also check to make sure it is valid for this opcode to take an immediate argument by checking the condition <code>Opcode.isNoImmedOp(opcode)</code>. If this is not an opcode that takes an immediate argument, add the error message "Illegal Immediate Argument".
<li>Check to see if there is a "@" prefix on the argument using the condition  <code>parse[1].startsWith("@")</code>. If so, set the mode using <code>mode = Mode.INDIRECT.getModeNumber();</code>. Remove the @ using <code>parts[1]=parts[1].substring(1);</code>.
<li>Check to see if there is a "&" prefix on the argument using the condition  <code>parse[1].startsWith("&")</code>. If so, set the mode using <code>mode = 0;</code>. Remove the & using <code>parts[1]=parts[1].substring(1);</code>. Also check to see if this opcode accepts absolute arguments using the condition <code>Opcode.isAbsoluteOp(opcode)</code>. If not, add the error messsage "Illegal absolute argument".
<li>Finally, convert <code>parts[1]</code> from an ASCII hexadecimal value into the <code>argument</code> field using the Integer.parseInt method as above. If there is a NumberFormatException, add the error message "Argument is not a hex number".
  </ol>
</ol>
  
That was a challenge, but now we have created a new Instruction object! Almost done, but there are a few more methods required.

When we write out the object code we will convert values to hexadecimal. However, we need to think about how to handle negative numbers. Normally, negative hexadecimal values are represented using two's complement binary data, which means typically we get a prefix of a bunch of "0xFFFFFF... " in front of a negative number. To avoid this, let's just put a minus sign (-) followed by the hexadecimal representation of the absolute value of the number. Here's a static method to do that:

```
static String hex2String(int value) {
	if (value<0) return "-"+Integer.toHexString(-value).toUpperCase();
	else return Integer.toHexString(value).toUpperCase();
}
```

Next we need a method to return the String that represents the object code itself. Write a method called `objectCode` which takes no arguments and returns a String. 
- If the ```blankLine``` flag is on, this should just return an empty string, (""). 
- If there are any errors on this line, you should also return an empty string. 
- If this line is the data delimiter (```isDataDelim``` is true), then just return "-1" (the data delimiter in the object code.) 
- If the ```isData``` flag is on, return the upper case hexadecimal representation of the location, followed by a blank, followed by the upper case hexadecimal representation of the value. (Note... the value might be negative, so use the hex2String method.)
- If the ```isData``` flag is off, reutnr the upper case hexadecimal representation of the opcode, followed by a blank followed by the mode, followed by a blank, followed by the upper case hexadecimal representation of the argument. (Note... the argument might be negative, so use the hex2String method.)

We need an ```addErrors``` method to add the first error found on this line to the errorMap used in the assembler. This method should look like:
```
void addErrors(TreeMap<Integer, String> errorMap) {
	if (errors.isEmpty()) return;
	errorMap.put(lineNumber,errors.get(0) + " on line " + lineNumber);
}
```

Add an isBlank method that takes no arguments and returns a boolean value. It should return the value of the ```blankLine``` field.

Add a getLineNumber method that takes no arguments and returns the integer value of the ```lineNumber``` field.

Finally, add a checkBlanks method that will be used by the FullAssembler. This method should look like:
```
void checkBlanks(int lastNonBlank) {
	if (blankLine && lastNonBlank > lineNumber) {
		errors.add("Blank line not at the end of the file");
	}
}
```

Once you have finished coding the Instruction class, run the main mehtod in the FullAssembler class just to make sure everything still holds together. You should get no messages. However, if you edit the main method to compile something like 03e.pasm, you should see error messages.

There is a complete JUnit tester for the full assembler called AssemblerTester.java. If your Instruction class is correct, all 35 tests in the AssemblerTester class should pass.

## Submitting your Changes

Once you have finished and tested your changes, commit and push them, and then each member of the team should paste the commit hash code in myCourses under Content/Project Submissions/Installment 2. 

## Grading Criteria

Installment 2 is worth 30 points out of the total 100 point project grade. Deductions are as follows:

- 25 point deduction if the code does not compile

- 5 point deduction for compiler warnings

- 2 point deduction for each failing JUNIT test case in AssemblerTester.java up to a maximum of 20

