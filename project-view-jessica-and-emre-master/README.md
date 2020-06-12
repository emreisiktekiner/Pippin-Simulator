# project.view - Pippin Project Installment 3

## Handling Sub-projects with GIT

When you download your project.view repository from the web, make the target directory your normal eclipse workspace followed by "/src/project" instead of just "/src", and modify the package name to be just "view" instead of "project.view-*userid*". Then, when you go back to the Java developer view in Eclipse and refresh your src directory, you should have a "project.view" package that contains all the code in the repository.

## Working on Teams

Please use the same teams to work on Installment 3 of the project as you used for Installment 1 and Installment 2.

## The Pippin Simulation GUI

The final installment of the Pippin GUI consists of finishing up a Graphical User Interface that allows us to use the Pippin simulation model we created in installment 1, and the assembler and loader we created in installment 2. Most of this GUI has been provided to you, but there is still one missing piece... the piece that manages changing GUI states, and using the change of states to manage which control buttons and menu options are enabled or disabled.

The GUI that is provided to you consists of a main frame, which is managed by the PippinGUI class. The main frame has several sub-parts, from top to bottom:
<ul>
  <li>The top line is the standard title bar, with the title and minimize/maximize/quit icons on the right.
  <li>The next line is the menu bar, which has three main entries:
    <ul>
      <li>File - The File entry has three sub-menu items:
        <ul>
          <li>Assemble Source... which puts up a selection dialog to select the PASM source, and then another selection dialog to select the PEXE destination, and then invokes project.assembler.FullAssembler to perform the assembly. The dialog remembers where you selected both the PASM and PEXE previously, so once you set this to the project.pasm and project.pexe directories the first time, you won't need to navigate there again.
          <li>Load Program... which puts up a selection dialog to select the PEXE object code, and loads that into the current job in the model and GUI.
          <li>Exit - Puts up an "are you sure" dialog, and exits the GUI.
        </ul>
      <li>Execute - which only has a single sub-entry
        <ul>
          <li>Go which runs the current job loaded in the model and GUI until it halts.
        </ul>
      <li>Change Job = which has four sub-entries
        <ul>
          <li>Job 0 - to set the current job to job 0
          <li>Job 1 - to set the current job to job 1
          <li>Job 2 - to set the current job to job 2
          <li>Job 3 - to set the current job to job 3
        </ul>
    </ul>
    <li>The next line Shows the current status of the CPU. It has the label "CPU->", followed by sub-fields that show the current job number, the current value of the Accumulator, the current value of the Instruction Pointer, and the current value of the memory base.
    <li>The next big rectangle displays the current values in the code and data memories currently being simulated. This is broken up into four columns, as follows:
    <ul>
      <li>Code Memory View - This is a scrollable view of each instruction loaded in memory. It will be blank until you do a "File/Load" from the menu. Each entry is labeled with the address on the left, followed by the disassembled view of the instruction, and the object code view of the instruction. The current instruction about to execute will be highlighted in yellow, and as you step through the code or change jobs, scrolling will occur automatically to keep the current instruction in view.
      <li>Data Memory View [ 0 - 512 ] - This column shows the data for Job 0 in a scrollable view with the address on the left, followed by the decimal value, and the hexadecimal value at that data location. The last changed data item will be highlighted in yellow, and scrolling will occur automatically to keep the last changed data item in view.
      <li>Data Memory View [ 512 - 1024 ] - The view of the data memory for Job 1, similar to the previous column.
      <li>Data Memory View [ 1024 - 2048 ] - The view of the data memory for Jobs 2 and 3, similar to the previous column.
    </ul>
    <li>The bottom line contains the simulation control buttons, which perform the following actions:
      <ul>
        <li>Step - Executes a single instruction in the current job.
        <li>Clear - Erases the instructions and data associated with the current job. 
        <li>Run/Pause - Toggles the "auto-step" mode which periodically steps through the instructions in the current job until that job halts.
        <li>Reload - Clears the current job, and then re-loads the job that was running so that you can start from the beginning.
        <li>Unlabeled Slider bar - This slider controls the amount of wait time between each step when auto-step mode is running. Slide to the left to go faster, or the right to go slower.
      </ul>
</ul>

## Fixing a bug from Installment 1

When we created the Model class, I forgot to implement one of the methods required. The model should have an ```addJobListener``` method that listens to all four jobs. To make things work, please add the following to your Model class in the project.model package...

```
public void addJobListener(JobListener l) {
	for(int i=0;i<4;i++) jobs[i].addJobListener(l);
}
```

Notice that the PippinGUI class invokes this method from the createAndShowGUI method.

## Adding View States

If you run the main method in the PippinGUI class, the GUI will appear, and things seems to be working correctly. However, there is an important piece missing. We have already seen in the model.Job class that each job in the Pippin simulation can have one of three states:

<ul>
  <li>NOTHING_LOADED to indicate that no job has been loaded and the code and data memory are empty.
  <li>PROGRAM_LOADED to indicate that program and data have been loaded and may be partly simulated, but that job has not yet reached the HALT instruction.
  <li>PROGRAM_HALTED to indicate that the program has been run and reached the HALT instruction.
</ul>

Remember also that there is a JobListener interface that allows us to get a callback whenever the job state changes. The PippinGUI class implements the JobListener interface, and gets a callback whenever any of the jobs (Job 0, Job 1, Job 2, or Job 3) changes state.

From a GUI point of view, we need to keep track of what state the current job is in to control which buttons are enabled or disabled. For instance, the Clear button should not be enabled if there is no program loaded for the current job... there is nothing to clear. Since the GUI has the concept of auto-stepping through the code, and since the GUI can manage different jobs, we need to keep track of a slightly different set of states in the GUI:

<ul>
  <li>NOTHING_LOADED There is no code or data loaded for the current job.
  <li>PROGRAM_LOADED_NOT_AUTOSTEPPING The data and instructions for the current job are loaded into the code and data memory, and you may have stepped into the code, but you are not auto-stepping through the code right now.
  <li>AUTO_STEPPING The data and instructions for the current job are loaded into the code and data memory, and you are currently auto-stepping through those instructions.
  <li>PROGRAM_HALTED The data and instructions for the current job are loaded into the code and data memory, but the simulation of those instructions has reached a HALT instruction.
</ul>

The entire list of buttons and menu items which need to be enabled or disabled based on the current view state are:

<ul>
  <li>ASSEMBLE The File/Assemble Source... menu item needs to be disabled when auto-stepping.
  <li>CLEAR The Clear control button needs to be disabled when auto-stepping and when nothing is loaded.
  <li>LOAD The File/Load Program... menu item needs to be disabled when auto-stepping, but enabled otherwise.
  <li>RELOAD The Reload control button needs to be disabled when auto-stepping, and when nothing is loaded.
  <li>RUN The Run/Pause control button needs to be disabled when nothing is loaded, and when the program has halted.
  <li>STEP The Step Control button needs to be disabled when auto-stepping, nothing is loaded, and when the program has halted.
  <li>CHANGE_JOBThe Change Job menu sub-items (Job 0, Job 1, Job 2, and Job 3) need to be disabled when auto-stepping, but enabled otherwise.
</ul>

Currently, all of these buttons and menu items are disabled. Your job will be to implement a view state class which keeps track of the current view state, and can be used to swap between states.

We have provided a "dumb" ViewStates class that has a method for each item named in the previous list to return the boolean state for that button. Currently, all of these methods, such as ```getAssembleFileActive()``` returns false to say that the File/Assemble Source... menu item should be disabled. Your job is to add the intelligence into this class.

## Modifying the ViewStates class

Since the view state is really an enumerated type, change the declaration of ViewStates from a class to an enum, and specify values *NOTHING_LOADED*, *PROGRAM_LOADED_NOT_AUTOSTEPPING*, *AUTO_STEPPING*, and *PROGRAM_HALTED*.

For each of these states, we need to keep track of the status of all the buttons and menu items. We can do that by creating an array of boolean states, one for each menu item or button. Make a field in the ViewStates enum that is an array of boolean values as follows:

```
boolean[] states = new boolean[7];
```

In order to keep track of which column in this array maps to which button or menu item, lets make a set of private static final fields, one for each column. I used:

```
private static final int ASSEMBLE = 0;
private static final int CLEAR = 1;
private static final int LOAD = 2; 
private static final int RELOAD = 3;
private static final int RUN = 4;
private static final int STEP = 5;
private static final int CHANGE_JOB = 6; 
```

Now, we make an abstract method in the ViewStates enum called ```enter()```, declared as:

```
public abstract void enter();
```

Remember that each enum value is really a sub-class of the enum. That means that we need to override the ```enter()``` method in each enum value. For instance, in the *NOTHING_LOADED* value, we can actually define the overriding ```enter``` method, and use the enter method to set each entry in the state array to the boolean value it should be for the *NOTHING_LOADED* state. Here's an example...

```
NOTHING_LOADED {
	  public void enter(){
		    states[ASSEMBLE] = true;
		    states[CLEAR] = false;
		    // ... do the rest of the entries here
		    states[CHANGE_JOB] = true;
		  } 
		},
```

Do this for all the enum values and all entries in the states array.

Then change the methods to get the values to return the proper element of the states array. For instance, 

```
public boolean getAssembleFileActive() {
	 return states[ASSEMBLE];
}
```

Now we have our mechanism in place, but we still need to switch states and enter each state at the right time, and tell the other GUI objects to react to these changes in state.

## Managing changes in View States

The PippinGUI class already has a field called ```currentViewState``` which keeps the current view state, but not much is done with that field right now, other than making it available so other GUI classes can query whether to enable or disable buttons or menu items. 

The ```currentViewState``` field is currently initialized in the PippinGUI.createAndSHowGUI() method using the old class initialization method.  This needs to be modified now that we have made ViewStates into an enumeration.  Change the initailization line to:

```
currentViewState=ViewStates.NOTHING_LOADED;
```

Add a new method to the PippinGUI class to set the current state. This method needs to take two arguments:

  - The ViewState value to change to, and
  
  - A String "notify message" which is used by the various GUI classes to determine how to handle notifications.
  
If the new view state is *PROGRAM_HALTED*, then we need to make sure auto-stepping is turned off by invoking ```stepControl.setAutoStepOn(false)```. The set the currentViewState to the argument value, and run the ```currentViewState.enter()``` method to update the ViewStates states array. Finally, invoke the ```notifyObservers()``` method, passing in the notify message specified as the second parameter. This will notify all the other classes to allow them to change menu item enabled status or button enalbed status (among other things.)

## Triggering State Changes

There are a couple of places where we need to trigger a view state change, as follows:

<ol>
  <li>In the PippinGUI.toggleAutoStep() method. This method currently toggles the auto-step mode that is controled by the TimerControl object referenced by the stepControl field. If <code>setControl.isAutoStepOn()</code> returns true, then we need to <code>setCurrentState(ViewStates.AUTO_STEPPING,"")</code>. Otherwise, we need to <code>setCurrentState(ViewStates.PROGRAM_LOADED_NOT_AUTOSTEPPING,"")</code>.</li>
  <li>The PippinGUI.updateJob() method is the JobListener callback that is called when the job status changes. In this method, we need to run <code>currentJob.getCurrentState()</code> to determine the current job state, and then based on that result:
    <ul>
      <li>If the job state is NOTHING_LOADED, then <code>setCurrentState(ViewStates.NOTHING_LOADED,"")</code></li>
      <li>If the job state is PROGRAM_LOADED, then <code>setCurrentState(ViewStates.PROGRAM_LOADED_NOT_AUTOSTEPPING,"")</code></li>
      <li>If the job state is PROGRAM_HALTED, then <code>setCurrentState(ViewStates.PROGRAM_HALTED,"")</code></li>
    </ul></li>
</ol>
 
## Testing The Results

You should now be able to run the Pippin Simulation GUI, run the assembler, run the loader, try different jobs, try stepping and auto-stepping through the code. Keep a very close eye on how buttons and menu items are enabled and disabled. If you have completed the code correctly, everything should work as expected.

## Submitting your Changes

Once you have finished and tested your changes, commit and push them, and then each member of the team should paste the commit hash code in myCourses under Content/Project Submissions/Installment 3. 

## Grading Criteria

Installment 3 is worth 40 points out of the total 100 point project grade. Deductions are as follows:

- 35 point deduction if the code does not compile

- 5 point deduction for compiler warnings

- 5 point deduction for each button or menu item which does not get enabled or disabled correctly.


