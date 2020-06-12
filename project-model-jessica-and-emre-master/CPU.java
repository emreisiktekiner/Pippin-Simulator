package project.model;

import project.model.Job.State;
import project.model.Model.HaltCallBack;

public class CPU {
	private int accumulator;
	private int instructionPointer;
	private int memoryBase;
	private Code codeMemory;
	private Data dataMemory;
	private HaltCallBack callback;
	private Job currentJob;
	
	public CPU(Code codeMemory,Data dataMemory,Job currentJob,HaltCallBack callback	) {
		this.codeMemory=codeMemory;
		this.dataMemory=dataMemory;
		this.currentJob=currentJob;
		this.callback=callback;
	}
	
	public int getInstrPtr() {
		return instructionPointer;
	}
	
	public void incrementIP() {
		instructionPointer++;
	}
	
	public void setAbsoluteIP(int offset) {
		instructionPointer= currentJob.getStartcodeIndex() + offset;
	}
	
	public int getAccum() {
		return accumulator;
	}
	
	public void setAccum(int accInit) {
		accumulator=accInit;
	}
	
	public void setInstrPtr(int ipInit) {
		instructionPointer = ipInit;
	}
	
	public int getMemBase() {
		return memoryBase;
	}
	
	void setMemBase(int offsetInit) {
		memoryBase = offsetInit;
	}
	
	public int getData(int loc) {
		return dataMemory.getData(memoryBase + loc);
	}
	
	public void setData(int loc) {
		dataMemory.setData(memoryBase+loc, accumulator);
	}
	
	public void step() {
		try {
			if (getInstrPtr() < currentJob.getStartcodeIndex()) {
				throw new CodeAccessException("instruction pointer less than the start of the current job code");
			}
			if (getInstrPtr() >= currentJob.getStartcodeIndex() + currentJob.getCodeSize()) {
				throw new CodeAccessException("instruction pointer greater than the end of the current job code");
			}
			int opcode=codeMemory.getOp(instructionPointer);
			Mode mode=codeMemory.getMode(instructionPointer);
			int arg=codeMemory.getArg(instructionPointer);
			runInstruction(opcode,arg,mode);
		} catch(Exception e) {
			currentJob.setCurrentState(Job.State.PROGRAM_HALTED);
			callback.halt();
			throw e;
		}
	}
	
	void runInstruction(int opcode,int arg,Mode mode) {
		Opcode.opArray[opcode].execute(this, arg, mode);
	}
	
	public void setJob(Job currentJob) {
		this.currentJob=currentJob;
	}

	public Job getJob() {
		return currentJob;
	}
	
	public void halt() {
		currentJob.setCurrentState(State.PROGRAM_HALTED);
		callback.halt();
	}
}
