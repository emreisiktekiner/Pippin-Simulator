package project.model;

public enum Mode { INDIRECT(1), DIRECT(2), IMMEDIATE(3); 
	int modeNumber;
	private static Mode[] modeArray = {INDIRECT,DIRECT,IMMEDIATE};
	Mode(int mn) { modeNumber=mn; }
	public int getModeNumber() { return modeNumber; }
	public static Mode getMode(int mn) { return modeArray[mn-1]; }
	public Mode next() {
		if (this==DIRECT) return IMMEDIATE;
		if (this==INDIRECT) return DIRECT;
		return null;
	}
}
