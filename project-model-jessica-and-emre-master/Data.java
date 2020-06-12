package project.model;

public class Data {
	public static final int DATA_SIZE = 2048;
	private int[] data;
	private int changeIndex;
	
	public Data() {
		data = new int[DATA_SIZE];
		changeIndex = -1;
	}
	
	public int[] getData() {
		return data;
	}
	
	int getData(int index) {
		if (index < 0 || index > DATA_SIZE) throw new MemoryAccessException(
			"Illegal access to data memory, index" + index);
		else {
			return data[index];
		}
	}
	
	void setData(int index, int value) {
		if (index < 0 || index > DATA_SIZE)
			throw new MemoryAccessException("Illegal access to data memory, index" + index);
		else {
			data[index] = value;
			changeIndex = index;
		}
	}		
	public int getChangeIndex() {
		return changeIndex;
	}
	
	void clearData(int startindex, int endindex) {
		for (int i=0; i >= startindex && i < endindex;) {
			data[i]=0;
		}
		changeIndex=-1;
	}

}