package project.assembler;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.TreeMap;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AssemblerTester {
	TreeMap<Integer, String> errors = new TreeMap<>();
	TreeMap<Integer, String> expected = new TreeMap<>();
	Assembler assembler = new FullAssembler(); // Change this to FullAssembler in installment 4

	@BeforeEach
	void setUp() throws Exception {
		expected.clear();
		errors.clear();
	}

	@AfterEach
	void tearDown() throws Exception {
	}
	
	@Test
	void test01() {		
		int errorIndicator = assembler.assemble("src/project/pasm/01.pasm", 
				"src/project/pexe/01.pexe", errors);
		assertEquals(0,errorIndicator);
		assertEquals(expected,errors);
	}
	
	@Test
	void test02() {		
		int errorIndicator = assembler.assemble("src/project/pasm/02.pasm", 
				"src/project/pexe/02.pexe", errors);
		assertEquals(0,errorIndicator);
		assertEquals(expected,errors);
	}
	
	@Test
	void test03e() {		
		int errorIndicator = assembler.assemble("src/project/pasm/03e.pasm", 
				"src/project/pexe/03e.pexe", errors);
		assertEquals(32,errorIndicator);
		expected.put(32, "illegal mnemonic on line 32");
		assertEquals(expected,errors);
	}
	
	@Test
	void test04e() {		
		int errorIndicator = assembler.assemble("src/project/pasm/04e.pasm", 
				"src/project/pexe/04e.pexe", errors);
		assertEquals(34,errorIndicator);
		expected.put(34,"DATA location is not a hex number on line 34");
		assertEquals(expected,errors);
	}

	@Test
	void test05e() {		
		int errorIndicator = assembler.assemble("src/project/pasm/05e.pasm", 
				"src/project/pexe/05e.pexe", errors);
		assertEquals(15,errorIndicator);
		expected.put(15,"Blank line not at the end of the file on line 15");
		expected.put(16,"Blank line not at the end of the file on line 16");
		expected.put(21,"Blank line not at the end of the file on line 21");
		assertEquals(expected,errors);
	}
	
	@Test
	void test06e() {		
		int errorIndicator = assembler.assemble("src/project/pasm/06e.pasm", 
				"src/project/pexe/06e.pexe", errors);
		assertEquals(15,errorIndicator);
		expected.put(15,"Blank line not at the end of the file on line 15");
		expected.put(21,"Line starts with illegal white space on line 21");
		assertEquals(expected,errors);
	}
	
	@Test
	void test07e() {		
		int errorIndicator = assembler.assemble("src/project/pasm/07e.pasm", 
				"src/project/pexe/07e.pexe", errors);
		assertEquals(15,errorIndicator);
		expected.put(15,"Line starts with illegal white space on line 15");
		expected.put(20,"Blank line not at the end of the file on line 20");
		assertEquals(expected,errors);
	}
	
	@Test
	void test08e() {		
		int errorIndicator = assembler.assemble("src/project/pasm/08e.pasm", 
				"src/project/pexe/08e.pexe", errors);
		assertEquals(15,errorIndicator);
		expected.put(15,"Line starts with illegal white space on line 15");
		expected.put(20,"Blank line not at the end of the file on line 20");
		assertEquals(expected,errors);
	}
	
	@Test
	void test09e() {		
		int errorIndicator = assembler.assemble("src/project/pasm/09e.pasm", 
				"src/project/pexe/09e.pexe", errors);
		assertEquals(33,errorIndicator);
		expected.put(33,"Blank line not at the end of the file on line 33");
		assertEquals(expected,errors);
	}
	
	@Test
	void test10e() {		
		int errorIndicator = assembler.assemble("src/project/pasm/10e.pasm", 
				"src/project/pexe/10e.pexe", errors);
		assertEquals(19,errorIndicator);
		expected.put(19,"DATA location is not a hex number on line 19");
		expected.put(20,"DATA location is not a hex number on line 20");
		expected.put(21,"DATA location is not a hex number on line 21");
		expected.put(22,"DATA location is not a hex number on line 22");
		expected.put(23,"DATA location is not a hex number on line 23");
		expected.put(24,"DATA location is not a hex number on line 24");
		expected.put(25,"DATA location is not a hex number on line 25");
		expected.put(26,"DATA location is not a hex number on line 26");
		expected.put(27,"DATA location is not a hex number on line 27");
		expected.put(28,"DATA location is not a hex number on line 28");
		expected.put(29,"DATA location is not a hex number on line 29");
		expected.put(30,"DATA location is not a hex number on line 30");
		expected.put(31,"Illegal second DATA delimiter on line 31");
		expected.put(34,"Blank line not at the end of the file on line 34");
		assertEquals(expected,errors);
	}
	
	@Test
	void test11e() {		
		int errorIndicator = assembler.assemble("src/project/pasm/11e.pasm", 
				"src/project/pexe/11e.pexe", errors);
		assertEquals(30,errorIndicator);
		expected.put(30,"illegal mnemonic on line 30");
		expected.put(31,"Illegal mixed or lower case DATA delimiter on line 31");
		expected.put(34,"Blank line not at the end of the file on line 34");
		assertEquals(expected,errors);
	}
	
	@Test
	void test12e() {		
		int errorIndicator = assembler.assemble("src/project/pasm/12e.pasm", 
				"src/project/pexe/12e.pexe", errors);
		assertEquals(9,errorIndicator);
		expected.put(9,"mnemonic not in uppercase on line 9");
		expected.put(30,"illegal mnemonic on line 30");
		expected.put(34,"Blank line not at the end of the file on line 34");
		assertEquals(expected,errors);
	}
	
	@Test
	void test13e() {		
		int errorIndicator = assembler.assemble("src/project/pasm/13e.pasm", 
				"src/project/pexe/13e.pexe", errors);
		assertEquals(26,errorIndicator);
		expected.put(26,"Illegal argument in no-argument instruction on line 26");
		expected.put(30,"illegal mnemonic on line 30");
		assertEquals(expected,errors);
	}
	
	@Test
	void test14e() {		
		int errorIndicator = assembler.assemble("src/project/pasm/14e.pasm", 
				"src/project/pexe/14e.pexe", errors);
		assertEquals(30,errorIndicator);
		expected.put(30,"illegal mnemonic on line 30");
		assertEquals(expected,errors);
	}
	
	@Test
	void test15e() {		
		int errorIndicator = assembler.assemble("src/project/pasm/15e.pasm", 
				"src/project/pexe/15e.pexe", errors);
		assertEquals(8,errorIndicator);
		expected.put(8,"mnemonic not in uppercase on line 8");
		expected.put(27,"Illegal immediate argument on line 27");
		assertEquals(expected,errors);
	}
	
	@Test
	void test16e() {		
		int errorIndicator = assembler.assemble("src/project/pasm/16e.pasm", 
				"src/project/pexe/16e.pexe", errors);
		assertEquals(8,errorIndicator);
		expected.put(8,"illegal mnemonic on line 8");
		expected.put(29,"Illegal argument in no-argument instruction on line 29");
		assertEquals(expected,errors);
	}
	
	@Test
	void test17e() {		
		int errorIndicator = assembler.assemble("src/project/pasm/17e.pasm", 
				"src/project/pexe/17e.pexe", errors);
		assertEquals(5,errorIndicator);
		expected.put(5,"Instruction has too many arguments on line 5");
		expected.put(8,"Instruction requires argument on line 8");
		expected.put(19,"Blank line not at the end of the file on line 19");
		expected.put(26,"Line starts with illegal white space on line 26");
		expected.put(30,"Illegal argument in no-argument instruction on line 30");
		expected.put(31,"Line starts with illegal white space on line 31");
		expected.put(34,"Blank line not at the end of the file on line 34");
		assertEquals(expected,errors);
	}
	
	@Test
	void test18e() {		
		int errorIndicator = assembler.assemble("src/project/pasm/18e.pasm", 
				"src/project/pexe/18e.pexe", errors);
		assertEquals(5,errorIndicator);
		expected.put(5,"Instruction has too many arguments on line 5");
		expected.put(28,"Illegal immediate argument on line 28");
		assertEquals(expected,errors);
	}
	
	@Test
	void test19e() {		
		int errorIndicator = assembler.assemble("src/project/pasm/19e.pasm", 
				"src/project/pexe/19e.pexe", errors);
		assertEquals(11,errorIndicator);
		expected.put(11,"Instruction has too many arguments on line 11");
		expected.put(30,"Line starts with illegal white space on line 30");
		assertEquals(expected,errors);
	}
	
	@Test
	void test20e() {		
		int errorIndicator = assembler.assemble("src/project/pasm/20e.pasm", 
				"src/project/pexe/20e.pexe", errors);
		assertEquals(10,errorIndicator);
		expected.put(10,"Argument is not a hex number on line 10");
		assertEquals(expected,errors);
	}
	
	@Test
	void test21e() {		
		int errorIndicator = assembler.assemble("src/project/pasm/21e.pasm", 
				"src/project/pexe/21e.pexe", errors);
		assertEquals(32,errorIndicator);
		expected.put(32,"DATA location is not a hex number on line 32");
		assertEquals(expected,errors);
	}
	
	@Test
	void test22e() {		
		int errorIndicator = assembler.assemble("src/project/pasm/22e.pasm", 
				"src/project/pexe/22e.pexe", errors);
		assertEquals(32,errorIndicator);
		expected.put(32,"DATA value is not a hex number on line 32");
		assertEquals(expected,errors);
	}
	
	@Test
	void test23e() {		
		int errorIndicator = assembler.assemble("src/project/pasm/23e.pasm", 
				"src/project/pexe/23e.pexe", errors);
		assertEquals(32,errorIndicator);
		expected.put(32,"DATA location is not a hex number on line 32");
		assertEquals(expected,errors);
	}
	
	@Test
	void test24e() {		
		int errorIndicator = assembler.assemble("src/project/pasm/24e.pasm", 
				"src/project/pexe/24e.pexe", errors);
		assertEquals(12,errorIndicator);
		expected.put(12,"Argument is not a hex number on line 12");
		expected.put(31,"No Data value on line 31");
		expected.put(32,"DATA value is not a hex number on line 32");
		assertEquals(expected,errors);
	}
	
	@Test
	void test25e() {		
		int errorIndicator = assembler.assemble("src/project/pasm/25e.pasm", 
				"src/project/pexe/25e.pexe", errors);
		assertEquals(33,errorIndicator);
		expected.put(33,"DATA value is not a hex number on line 33");
		assertEquals(expected,errors);
	}
	
	@Test
	void test100rt() {		
		int errorIndicator = assembler.assemble("src/project/pasm/100rt.pasm", 
				"src/project/pexe/100rt.pexe", errors);
		assertEquals(0,errorIndicator);
		assertEquals(expected,errors);
	}
	
	@Test
	void test101rt() {		
		int errorIndicator = assembler.assemble("src/project/pasm/101rt.pasm", 
				"src/project/pexe/101rt.pexe", errors);
		assertEquals(0,errorIndicator);
		assertEquals(expected,errors);
	}
	
	@Test
	void test102rt() {		
		int errorIndicator = assembler.assemble("src/project/pasm/102rt.pasm", 
				"src/project/pexe/102rt.pexe", errors);
		assertEquals(0,errorIndicator);
		assertEquals(expected,errors);
	}
	
	@Test
	void test103rt() {		
		int errorIndicator = assembler.assemble("src/project/pasm/103rt.pasm", 
				"src/project/pexe/103rt.pexe", errors);
		assertEquals(0,errorIndicator);
		assertEquals(expected,errors);
	}
	
	@Test
	void test104rt() {		
		int errorIndicator = assembler.assemble("src/project/pasm/104rt.pasm", 
				"src/project/pexe/104rt.pexe", errors);
		assertEquals(0,errorIndicator);
		assertEquals(expected,errors);
	}
	
	@Test
	void test105rt() {		
		int errorIndicator = assembler.assemble("src/project/pasm/105rt.pasm", 
				"src/project/pexe/105rt.pexe", errors);
		assertEquals(0,errorIndicator);
		assertEquals(expected,errors);
	}
	
	@Test
	void testfactorial() {		
		int errorIndicator = assembler.assemble("src/project/pasm/factorial.pasm", 
				"src/project/pexe/factorial.pexe", errors);
		assertEquals(0,errorIndicator);
		assertEquals(expected,errors);
	}
	
	@Test
	void testfactorialindirect7() {		
		int errorIndicator = assembler.assemble("src/project/pasm/factorialindirect7.pasm", 
				"src/project/pexe/factorialindirect7.pexe", errors);
		assertEquals(0,errorIndicator);
		assertEquals(expected,errors);
	}
	
	@Test
	void testmerge() {		
		int errorIndicator = assembler.assemble("src/project/pasm/merge.pasm", 
				"src/project/pexe/merge.pexe", errors);
		assertEquals(0,errorIndicator);
		assertEquals(expected,errors);
	}
	
	@Test
	void testqsort() {		
		int errorIndicator = assembler.assemble("src/project/pasm/qsort.pasm", 
				"src/project/pexe/qsort.pexe", errors);
		assertEquals(0,errorIndicator);
		assertEquals(expected,errors);
	}
}
