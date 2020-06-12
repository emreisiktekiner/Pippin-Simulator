# project.model First Installment of the Pippin Simulator final project

This is the first installment of the Final Project - the Pippin Simulator.

## Final Project Organization

The final project package will consist of several sub-packages. This repository contains the first sub-package, the Java code that deals with the model of the Pippin simulator. There will be other sub-packages delivered in future installments of the project. Specifically:

- project.model - the Java code that implements the Pippin simulator itself, including the CPU, the code memory, the data memory, and how each opcode in Pippin should be interpretted.

- project.assembler - the Java code that implements a Pippin Assembler to convert ASCII Pippin assembler files into object code, and a loader to load that code into the model.

- project.pasm - A package that contains many examples of Pippin Assembler code, including a mix of code which does or does not compile, does or does not cause run-time errors, or which runs to completion. This Pippin Assembler code is intended to be used as test cases for our Pippin simulation.

- project.pexe - A package that will contain the Pippin "executable" files. These are the files produced by the Pippin Assembler, and which will be executed by our Pippin simulator.

- project.view - A package that contains a GUI to invoke the assembler, load the code, and run the simulation with the capability to watch the instructions and data as the Pippin code is simulated.

The first installment will contain just the project.model sub-package. You will be required to complete code in this package, and perform unit tests to make sure your changes are correct.

The second installment will contain the project.assembler sub-package as well as the project.pasm sub-package and an empty project.pexe sub-package. For the second installment, you need to implement assembler code and check to make sure you handle all the pasm test cases correctly. In the process, we will fill in the project.pexe package.

The third and final installment will contain the project.view sub-package. You will be asked to complete code in this package, and test this code to make sure the GUI simulator is working correctly.

## Handling Sub-projects with GIT

Before you download, create an empty **project** package in your Eclipse project (similar to hw04 or lab09). When you download your project.model repository from the web, make the target directory your normal eclipse workspace followed by "/src/project" instead of just "/src", and modify the package name to be just "model" instead of "project.model-*userid*". Then, when you go back to the Java developer view in Eclipse and refresh your src directory, you should have a "project.model" package that contains all the code in the repository.

## Working on Teams

You are encourgaed to work in pairs for this project! We will limit team size to two people, but if you want to work on your own, you may do so. Just keep in mind that there is restricted time, and hopefully two people can finish faster than one. If you work in pairs, you will both be contributing to a single repository. Make sure you use git push and pull to ensure that you both are working on the latest version of the shared repository! Both contributors to the repository will get the same grade, but the TA's and the professor reserve the right to interview team members and make sure that all team members understand the entire project... not just the part they worked on... so discuss what you do with your team members.

## The project.model sub-package

The project.model sub-package contains the class required to model the basic architecture of a Pippin "machine". The following describes the classes in the modle sub-package:

- **Model** The most important class is the Model class, which pulls together all the different components of a Pippin machine. The fields in this model are primarily references to the components of a Pippin machine, the data memory, the code memory, and array of jobs and indicator of the current job, and a CPU. The model creator initializes all of these components. Most of the methods are simple getters and setters, but the model class also contains methods to manage job switching.

- **Code** The Code class contains all the methods required to read and write from the code memory, including an array to keep track of the values in that memory. In our Code memory, each instruction will be kepts in a single long (64 bit) integer. The Code class manages the extraction of opcode, mode, and argument fields from the long value, or insertion of opcode, mode, and argument into a single long value.

- **CodeAccessException** An exception class to be thrown if and when an attempt is made to access an invalid index in the code memory.

- **Data** The Data class contains all the methods required to read and write from the data memory. Data will be kept in an array of 32 bit integers.

- **MemoryAccessException** An exception class to be thrown if and when an attempt is made to access an invalid index in the data memory.

- **Job** A class to manage all the information about a specific job being run on the Pippin machine. Since other parts of the simulation (such as the GUI) might be interested in making updates when jobs are changed, the Job class has a "Job Listener" callback capability. Users can register for a callback by implementing the **JobListener** interface, and invoking the addJobListener method in the Job class.

- **CPU** A class to manage the CPU itself, including the three registers, the Accumulator, the instruction pointer, and the memory base. Most important is the "step" method which executes a single Pippin instruction.

- **Opcode** An enumeration class to manage individual op-code related instruction information, with one entry for each op-code supported. Most importantly, each op-code has an `execute` method which implements the simulation of that op-code. Included in this class are some helper methods to do actions which are common to multiple opcode simulations, such as checking to make sure the mode is not null. The Opcode class also contains some static methods to do things line convert mnemonic strings into opcode numbers and back, or check to see if op codes should have arguments etc.

- **DivideByZeroException** An exception to be thrown if the DIV instruction tries to divide by zero.

- **Mode** An enumeration class to manage the mode of an instruction, with one entry for each valid mode. Of special interest is the `next` method which is designed to handle indirection in modes. As you de-reference a pointer, the `next` method will return the mode associated with the dereferenced value, so INDIRECT goes to DIRECT, and DIRECT goes to IMMEDIATE.

- **InstructionTester** A JUnit 5 tester to make sure the Pippin simulator correctly handles all instructions in every valid mode, and correctly rejects invalid instructions or modes with the correct excpetions and exception messages.

## Completing the First Installment

Most of the code for the project.model package has already been provided for you. However, there are two specific areas you will need to work on to finish the first installment of the project.

### Modeling Data Memory

The class that manages Code Memory, Code.java, has been provided for you. However, we have left out the class that models the data memory. You will need to create the Data class in the project.model package.

The Data class should have three fields:

- `DATA_SIZE` - an integer that is public, static, and final, initialized to 2048.

- `data` - an private array of integers. This is where the values in the data memory will be kept. Leave it uninitialized, to be initialized in the creator.

- `changeIndex` - a private integer that contains the index of the last memory item to be changed. We will use this in later installments when we add a GUI to highlight the last changed data.

The Data class should have the following methods:

- A public no argument creator method which initializes the `data` field to an array of integers of size `DATA_SIZE`. The creator should also initialize `changeIndex` to -1 to indicate that no data has been changed yet.

- A public `getData` method which takes no arguments, and returns the entire data array.

- A *second* `getData` method which takes an integer index as an argument. If the index is not a valid memory location (between 0 and `DATA_SIZE`), then the getData method should throw a `MemoryAccessException` with the message `"Illegal access to data memory, index "  + index`. Otherwise, the method should return the value in the data memory at that index. Note that since this method will only be used in the project.model package, do not specify public - let the access default to package-private.

- A `setData` method which takes an integer index as the first argument, and an integer value as the second argument. The setData method should do the same checks and throw the same exception for an invalid index as the `getData` method. If the index is valid, the method should set the data memory at the specified index using the second, value argument. The `setData` method should also update the `changeIndex` field to identify the index of the last data memory element that was changed. This method will also be invoked only from within the package.model package, so the access should default to package-private.

- A public `getChangeIndex` method which takes no arguments, and returns the value of the `changeIndex` field.

- A `clearData` method which takes two integer arguments, a start index and an end index, and returns void. You may assume the start and end indexes passed in as parameters are valid memory indexes since this method will only be invoked from trusted code in the project.model package. The method should zero the data memory for each index, starting at the start parameter, and going up to, but not including the end parameter. It can then set the `changeIndex` to -1. This method can also be package-private.

### Simulating Instructions

The most important part of the model deals with how individual opcodes are simulated. These are defined in the overriden `execute` method in each enumerated type of the Opcode class. The overriden `execute` method for the ADD class is representative of the kinds of things that need to happen, and can be coded as follows:

```
public void execute(CPU cpu,int arg,Mode mode) {
	checkNonNullMode(mode);
	cpu.setAccum(cpu.getAccum()+getArgValue(mode,arg,cpu));
	cpu.incrementIP();
}
```

Your job for this installment of the project is to override the `execute` method for each of the valid instruction types. Here is some documentation of what you need to consider for each opcode value:

   0 - NOP instruction - make sure the mode is null, and increment the instruction pointer.
   
   1 - LOD instruction - evaluates like the ADD instruction, but puts the `arg` value directly into the accumulator instead of adding it.
   
   2 - STO instruction - Note that STO does not support an IMMEDIATE mode (since the argument must represent an index in memory). Therefore STO should throw an illegal argument exception if the mode is either null or IMMEDIATE. We then need to get the resolved *indirect* argument value - the index in memory where the accumulator should be stored. Once we have that value, we need to store the value of the accumulator at the memory address resolved from the argument. (The Cpu class has a method to help.) Don't forget to increment the instruction pointer!
   
   3 - ADD instruction - see above.
   
   4 - SUB instruction - just like ADD, but subtracts the `arg` value from the accumulator instead of adding.
   
   5 - MUL instruction - just like ADD, but multiplies the accumulator by the `arg` value instead of adding.
   
   6 - DIV instruction - just like ADD, but divides the accumulator by the `arg` value instead of adding. Watch out for division by zero. Before division by zero occurs, this expression needs to check the argument. If the argument is zero, this method should throw a DivideByZeroException with the message "Divide by Zero".
   
   7 - AND instruction - Similar to ADD, except if either the `arg` value or the accumulator are zero, the accumulator should be set to 0. If both are non-zero, the accumulator should be set to 1.
   
   8 - NOT instruction - Since NOT should not take an argument, the only valid mode is null. Any other mode should cause an IllegalArgumentException. If the accumulator is 0, it should be set to 1. Otherwise, it should be set to 0.
   
   9 - CMPL instruction - It doesn't make sense to compare to an immediate value... the programmer already knows the value of an immediate value. Therefore, both IMMEDIATE and null modes should cause an IllegalArgumentException. The DIRECT and IDIRECT modes should retreive the `arg` value from memory, and if that value is less than zero, set the accumulator to 1. Otherwise, set the accumulator to 0.
   
   10 - CMPZ instruction - works just like CMPL, except compares the memory value to zero instead of less than zero.
   
   11 - JUMP instruction - The JUMP instruction abuses the null mode conventions. Normally, a null mode indicates that there is no argument associated with this instruction. JUMP (and JMPZ below) use the null mode to indicate that there *is* an argument, but that the argument should be treated in a special way. The null mode is used to indicate *ABSOLUTE_DIRECT* addressing - the argument represents an address in memory that contains the *offset from the beginning of the code*. There is a method in the Cpu class to help with this. If the mode is not null, the instruction pointer should be set to the value of the current instruction pointer pluse the resolved value of the argument (after handling indirection modes). Note... the instruction pointer should **not** be incremented after a JUMP.
      
   12 - JMPZ - The JMPZ instruction behaves just like the JUMP instruction if the accumulator has a zero value. (Note that this includes special handling of the null mode.) If not, the JMPZ instruction does nothing but increment the instruction counter.
   
   13 - There is no instruction 13.
   
   14 - There is no instructoin 14.
   
   15 - Invoke the `cpu.halt()` method.
   
## Testing your Changes
 
Run the InstructionTester JUNIT test case. It has 93 tests in it that will excercise all of your instruction lambda expressions and make sure they are all running as designed. If any of these tests fail, you did something wrong and need to fix it. Once all 93 tests run successfully, you are ready to submit this first insetallation of the project repository.

## Submitting your Changes

Once you have finished and tested your changes, commit and push them, and then each member of the team should paste the commit hash code in myCourses under Content/Project Submissions/Installment 1. 

## Grading Criteria

Installment 1 is worth 30 points out of the total 100 point project grade. Deductions are as follows:

- 25 point deduction if the code does not compile

- 5 point deduction for compiler warnings

- 2 point deduction for each failing JUNIT test case in InstructionTester.java up to a maximum of 20   

