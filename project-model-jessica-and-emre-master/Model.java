package project.model;

import project.model.Job.State;

public class Model {
	
	private Data dataMemory;
	private Code codeMemory;
	private Job[] jobs;
	private Job currentJob;
	private CPU cpu;

	public interface HaltCallBack {
		void halt();
	}
	
	public Model() {
		this(()->System.exit(0));
	}

	public Model(HaltCallBack cb) {
		dataMemory = new Data();
		codeMemory = new Code();
		jobs = new Job[4];
				
		for(int i = 0; i < jobs.length; i++) {
			jobs[i] = new Job();
			jobs[i].setId(i);
			jobs[i].setStartcodeIndex(i*Code.CODE_MAX/jobs.length);
			jobs[i].setStartmemoryIndex(i*Data.DATA_SIZE/jobs.length);
		}
		currentJob = jobs[0];
		cpu = new CPU(codeMemory,dataMemory,currentJob,cb);
	}
	
	public int[] getData() {
		return dataMemory.getData();
	}
	
	public void addJobListener(JobListener l) {
		for(int i=0;i<4;i++) jobs[i].addJobListener(l);
	}
	
	public void setData(int index,int value) {
		dataMemory.setData(index, value);
	}
	
	public int getData(int index) {
		return dataMemory.getData(index);
	}
	
	public Job getCurrentJob() {
		return currentJob;
	}	
	
	public CPU getCpu() {
		return cpu;
	}
	
	public int getInstrPtr() {
		return cpu.getInstrPtr();
	}

	public void changeToJob(int i) {
		if (i<0 || i>3) throw new IllegalArgumentException("Invalid job- job must be 0-3");
		currentJob.setCurrentAcc(cpu.getAccum());
		currentJob.setCurrentIP(cpu.getInstrPtr());
		currentJob=jobs[i];
		currentJob.notifyListeners();
		cpu.setAccum(currentJob.getCurrentAcc());
		cpu.setInstrPtr(currentJob.getCurrentIP());
		cpu.setMemBase(currentJob.getStartmemoryIndex());
		cpu.setJob(currentJob);
	}
	
	public Code getCodeMemory() {
		return codeMemory;
	}
	
	public int getChangeIndex() {
		return dataMemory.getChangeIndex();
	}
	
	public void setCode(int index, int op, Mode mode, int arg) {
		codeMemory.setCode(index, op, mode, arg);
	}
	
	public String getHex(int i) {
		return codeMemory.getHex(i);
	}
	public String getText(int i) {
		return codeMemory.getText(i);
	}
	
	public int getOp(int i) {
		return codeMemory.getOp(i);
	}
	public Mode getMode(int i) {
		return codeMemory.getMode(i);
	}
	public int getArg(int i) {
		return codeMemory.getArg(i);
	}
	
	public Job.State getCurrentState() {
		return currentJob.getCurrentState();
	}
	
	public void step() {
		cpu.step();
	}
	
	public void clearJob() {
		dataMemory.clearData(currentJob.getStartmemoryIndex(), 
				currentJob.getStartmemoryIndex()+Data.DATA_SIZE/4);
		codeMemory.clear(currentJob.getStartcodeIndex(), 
				currentJob.getStartcodeIndex() + currentJob.getCodeSize());
		cpu.setAccum(0);
		cpu.setInstrPtr(currentJob.getStartcodeIndex());
		cpu.setMemBase(currentJob.getStartmemoryIndex());
		currentJob.reset();
	}
	
	public void setCurrentState(State state) {
		currentJob.setCurrentState(state);
	}
}
