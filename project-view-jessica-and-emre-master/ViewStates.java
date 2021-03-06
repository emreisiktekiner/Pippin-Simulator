package project.view;

public enum ViewStates {
	NOTHING_LOADED {
	  public void enter(){
		    states[ASSEMBLE] = true;
		    states[CLEAR] = false;
		    states[LOAD] = true;
		    states[RELOAD] = false;
		    states[RUN] = false;
		    states[STEP] = false;
		    states[CHANGE_JOB] = true;
		  } 
	},PROGRAM_LOADED_NOT_AUTOSTEPPING{
		public void enter(){
			states[ASSEMBLE] = true;
		    states[CLEAR] = true;
		    states[LOAD] = true;
		    states[RELOAD] = true;
		    states[RUN] = true;
		    states[STEP] = true;
		    states[CHANGE_JOB] = true;
		  } 
	}, AUTO_STEPPING{
		public void enter(){
			states[ASSEMBLE] = false;
		    states[CLEAR] = false;
		    states[LOAD] = false;
		    states[RELOAD] = false;
		    states[RUN] = true;
			states[STEP] = false;
		    states[CHANGE_JOB] = false;
	      } 
	}, PROGRAM_HALTED{
		public void enter() {
			states[ASSEMBLE] = true;
		    states[CLEAR] = true;
		    states[LOAD] = true;
		    states[RELOAD] = true;
		    states[RUN] = false;
			states[STEP] = false;
		    states[CHANGE_JOB] = true;
		  } 
	}; 
	

	boolean[] states = new boolean[7];
	private static final int ASSEMBLE = 0;
	private static final int CLEAR = 1;
	private static final int LOAD = 2; 
	private static final int RELOAD = 3;
	private static final int RUN = 4;
	private static final int STEP = 5;
	private static final int CHANGE_JOB = 6; 
	
	public abstract void enter();
	
	public boolean getAssembleFileActive() {
		return states[ASSEMBLE];
	}
	public boolean getClearActive() {
		return states[CLEAR];
	}
	public boolean getLoadFileActive() {
		return states[LOAD];
	}
	public boolean getReloadActive() {
		return states[RELOAD];
	}
	public boolean getRunPauseActive() {
		return states[RUN];
	}
	public boolean getStepActive() {
		return states[STEP];
	}
	public boolean getChangeJobActive() {
		return states[CHANGE_JOB];
	}
}