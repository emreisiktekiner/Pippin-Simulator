package project.model;

import java.util.ArrayList;

public class Job {
	private int id;
	private int startcodeIndex;
	private int codeSize;
	private int startmemoryIndex;
	private int currentIP;
	private int currentAcc;
	private ArrayList<JobListener> jobListeners=new ArrayList<JobListener>();
	
	public static enum State {
		NOTHING_LOADED, PROGRAM_LOADED, PROGRAM_HALTED
	};
	
	private State currentState=State.NOTHING_LOADED;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public State getCurrentState() {
		return currentState;
	}
	
	public void setCurrentState(State currentState) {
		this.currentState = currentState;
		notifyListeners();
	}
	
	public int getCurrentAcc() {
		return currentAcc;
	}
	public void setCurrentAcc(int currentAccumulator) {
		this.currentAcc = currentAccumulator;
	}
	public int getStartcodeIndex() {
		return startcodeIndex;
	}
	public void setStartcodeIndex(int startcodeIndex) {
		this.startcodeIndex = startcodeIndex;
	}
	public int getCodeSize() {
		return codeSize;
	}
	public void setCodeSize(int codeSize) {
		this.codeSize = codeSize;
	}
	public int getStartmemoryIndex() {
		return startmemoryIndex;
	}
	public void setStartmemoryIndex(int startmemoryIndex) {
		this.startmemoryIndex = startmemoryIndex;
	}
	public int getCurrentIP() {
		return currentIP;
	}
	public void setCurrentIP(int currentIP) {
		this.currentIP = currentIP;
	}
	public void reset() {
		codeSize = 0;
		currentState = State.NOTHING_LOADED;
		currentAcc = 0;
		currentIP = startcodeIndex;
		notifyListeners();
	}
	
	public void addJobListener(JobListener l) {
		jobListeners.add(l);
	}
	
	public void notifyListeners() {
		// need to notify listeners of a change in job
		for(JobListener l : jobListeners) l.updateJob(this);
		
	}
}
