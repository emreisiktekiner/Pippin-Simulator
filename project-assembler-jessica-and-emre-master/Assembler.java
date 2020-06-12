package project.assembler;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.TreeMap;
import java.util.stream.Stream;

import project.model.Mode;
import project.model.Opcode;

public class Assembler {
	private boolean readingCode=true;

	private String makeOutputCode(String line) {
		if (readingCode) {
			if(line.equals("DATA")) { 
				readingCode = false;
				return "-1"; // Indicator in output code that data follows
			}
			String[] parts = line.split("\\s+"); // Split line based on blanks
			int opcode = Opcode.opcode(parts[0]);
			if(parts.length == 1) return Integer.toHexString(opcode).toUpperCase() + " 0 0";
			else {
				Mode mode = Mode.DIRECT; // the default mode is DIRECT
				if(parts[1].startsWith("#")) {
					mode = Mode.IMMEDIATE;
					parts[1] = parts[1].substring(1);
				}
				if(parts[1].startsWith("@")) {
					mode = Mode.INDIRECT;
					parts[1] = parts[1].substring(1);
				}
				if(parts[1].startsWith("&")) {
					mode = null;
					parts[1] = parts[1].substring(1);
				}
				int modeNumber = 0;
				if(mode != null) {
					modeNumber = mode.getModeNumber();
				}			
				return Integer.toHexString(opcode).toUpperCase() + " " + modeNumber + " " + parts[1];
			} 
		} else { // Not reading code... in the data part
			return line; // Data lines are the same in the output code
		}
	}
	
	public int assemble(String inputFileName, String outputFileName, TreeMap<Integer, String> errors) {
		readingCode=true;
		try (Stream<String> lines = Files.lines(Paths.get(inputFileName))) {
			PrintWriter output = new PrintWriter(outputFileName);
			lines
				.map(line -> line.trim()) //remove any spaces or tabs from the end (or start) of the file
				.filter(line -> line.length() > 0) // remove blank lines at end of file
				.map(this::makeOutputCode)
				.forEach(output::println);
			output.close();
		} catch (IOException e) {
			// e.printStackTrace();
			errors.put(-1, "Unable to open the source file"); 
			return -1;
		}
		return 0;
	}
	
	public static void main(String[] args) {
		TreeMap<Integer, String> errors = new TreeMap<>();
		Assembler test = new Assembler();
		test.assemble("src/project/pasm/factorial.pasm", "src/project/pexe/factorial.pexe", errors);		
	}
}
