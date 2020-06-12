package project.assembler;

import java.util.ArrayList;
import java.util.TreeMap;

import project.model.Opcode;
import project.model.Mode;

public class Instruction {
	static int currentLineNum=1;
	static boolean inData=false;
	private int lineNumber;
	private boolean blankLine;
	private boolean isData;
	private boolean isDataDelim;
	private String mnemonic;
	private int opcode;
	private int mode;
	private int argument;
	private int location;
	private int value;
	private ArrayList<String> errors;
	
	public Instruction(String text) {
		lineNumber=currentLineNum;
		currentLineNum=currentLineNum+1;
		errors = new ArrayList<>();
		
		if (text.trim().length()==0) {
			blankLine=true;
			return;
		}
		
		char firstChar=text.charAt(0);
		if (firstChar==' ' || firstChar=='\t') {
			errors.add("Line starts with illegal white space");
			text=text.trim();
		}
		
		if (text.trim().toUpperCase().equals("DATA")) {
			if (inData==true) {
				errors.add("Illegal second DATA delimiter");
			}
			inData=true;
			
			if (text.trim().equals("DATA")==false){
				errors.add("Illegal mixed or lower case DATA delimiter");
			}
			isDataDelim=true;
			return;
		}

	
		String parts[] = text.split("\\s+");
		if (inData==true) {
			isData=true;
			try {location = Integer.parseInt(parts[0],16);} catch(NumberFormatException e)
			{errors.add("DATA location is not a hex number");}
			
			if (parts.length<2) {
				errors.add("No Data value");
			}
			else {
				try {value = Integer.parseInt(parts[1],16);} catch(NumberFormatException e)
				{errors.add("DATA value is not a hex number");}	
			}
			if(parts.length > 2) {
				errors.add("DATA has too many values");
			}
		}
		else {
			isData=false;
			mnemonic = parts[0];
				
			if (Opcode.isValidMnemonic(mnemonic.toUpperCase())==false) {
				errors.add("illegal mnemonic");
				return;		
			}
			if (mnemonic.toUpperCase().equals(mnemonic)==false) {
				errors.add("mnemonic not in uppercase");
				mnemonic = mnemonic.toUpperCase();
			}
				
			opcode=Opcode.opcode(mnemonic);
			if (Opcode.isNoArgOp(opcode)==true) {
				if(parts.length!=1) {
					errors.add( "Illegal argument in no-argument instruction");
				}
			}
			else{
				if(parts.length<2) {
					errors.add("Instruction requires argument");
				}
				else if(parts.length>2) {
				errors.add("Instruction has too many arguments");
				}
				else{
					mode = Mode.DIRECT.getModeNumber();
				}	
				if(parts.length >1) {	
				if (parts[1].startsWith("#")) {
					mode = Mode.IMMEDIATE.getModeNumber();
					parts[1]=parts[1].substring(1);
					if (Opcode.isNoImmedOp(opcode)==true) {
						errors.add("Illegal immediate argument");
					}
				}
				if (parts[1].startsWith("@")) {
					mode = Mode.INDIRECT.getModeNumber();
					parts[1]=parts[1].substring(1);
				}
				if (parts[1].startsWith("&")) {
					mode = 0;
					parts[1]=parts[1].substring(1);
					if (Opcode.isAbsoluteOp(opcode)==false) {
						errors.add("Illegal absolute argument");						
					}
				}
				try {argument=Integer.parseInt(parts[1],16);} catch(NumberFormatException e)
				{errors.add("Argument is not a hex number");}
					
				}
			}
			return;
			}
	}
		
	public static void reset() {
		currentLineNum=1;
		inData=false;
	}
	
	public static String hex2String(int value) {
		if (value<0) return "-"+Integer.toHexString(-value).toUpperCase();
		else return Integer.toHexString(value).toUpperCase();
	}
	
	public String objectCode(){
		if(blankLine==true || errors.isEmpty()==false) {
			return ("");
		}
		if (isDataDelim==true){
			return ("-1");
		}
		if (isData==true) {
			return (hex2String(location)+ " " + hex2String(value));
		}
		else{
			return (hex2String(opcode) + " " + mode + " "+ hex2String(argument));
		}
	}
	
	public void addErrors(TreeMap<Integer, String> errorMap) {
		if (errors.isEmpty()) return;
		errorMap.put(lineNumber,errors.get(0) + " on line " + lineNumber);
	}
	
	public boolean isBlank() {
		return blankLine;
	}
	
	public int getLineNumber(){
		return lineNumber;
	}
	
	public void checkBlanks(int lastNonBlank) {
		if (blankLine && lastNonBlank > lineNumber) {
			errors.add("Blank line not at the end of the file");
		}
	}
	
	
		

}