package project.assembler;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.util.Scanner;
import java.util.TreeMap;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(OrderAnnotation.class)
class ObjectTester {
	
	static Scanner input;
	static boolean incode;
	int op;
	int mode;
	int arg;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		TreeMap<Integer, String> errors = new TreeMap<>();
		TreeMap<Integer, String> expected = new TreeMap<>();
		Assembler assembler = new FullAssembler();
		expected.clear();
		errors.clear();
		int errorIndicator = assembler.assemble("src/project/pasm/allOpCodes.pasm", 
				"src/project/pexe/allOpCodes.pexe", errors);
		assertEquals(0,errorIndicator);
		assertEquals(expected,errors);
		input = new Scanner(new File("src/project/pexe/allOpCodes.pexe"));
		incode=true;
	}

	@Test @Order(1)
	void testNOP() {
		readInstruction();
		assertEquals(0,op);
		assertEquals(0,mode);
		assertEquals(0,arg);
	}
	
	@Test @Order(2)
	void testLOD() {
		readInstruction();
		assertEquals(1,op);
		assertEquals(3,mode);
		assertEquals(0,arg);
		
		readInstruction();
		assertEquals(1,op);
		assertEquals(2,mode);
		assertEquals(0,arg);
		
		readInstruction();
		assertEquals(1,op);
		assertEquals(1,mode);
		assertEquals(0,arg);
	}
	
	@Test @Order(3)
	void testSTO() {
		readInstruction();
		assertEquals(2,op);
		assertEquals(2,mode);
		assertEquals(0,arg);
		
		readInstruction();
		assertEquals(2,op);
		assertEquals(1,mode);
		assertEquals(0,arg);
	}
	
	@Test @Order(4)
	void testADD() {
		readInstruction();
		assertEquals(3,op);
		assertEquals(3,mode);
		assertEquals(0,arg);
		
		readInstruction();
		assertEquals(3,op);
		assertEquals(2,mode);
		assertEquals(0,arg);
		
		readInstruction();
		assertEquals(3,op);
		assertEquals(1,mode);
		assertEquals(0,arg);
	}
	
	@Test @Order(5)
	void testSUB() {
		readInstruction();
		assertEquals(4,op);
		assertEquals(3,mode);
		assertEquals(0,arg);
		
		readInstruction();
		assertEquals(4,op);
		assertEquals(2,mode);
		assertEquals(0,arg);
		
		readInstruction();
		assertEquals(4,op);
		assertEquals(1,mode);
		assertEquals(0,arg);
	}
	
	@Test @Order(6)
	void testMUL() {
		readInstruction();
		assertEquals(5,op);
		assertEquals(3,mode);
		assertEquals(0,arg);
		
		readInstruction();
		assertEquals(5,op);
		assertEquals(2,mode);
		assertEquals(0,arg);
		
		readInstruction();
		assertEquals(5,op);
		assertEquals(1,mode);
		assertEquals(0,arg);
	}
	
	@Test @Order(7)
	void testDIV() {
		readInstruction();
		assertEquals(6,op);
		assertEquals(3,mode);
		assertEquals(1,arg);
		
		readInstruction();
		assertEquals(6,op);
		assertEquals(2,mode);
		assertEquals(0,arg);
		
		readInstruction();
		assertEquals(6,op);
		assertEquals(1,mode);
		assertEquals(0,arg);
	}
	
	@Test @Order(8)
	void testAND() {
		readInstruction();
		assertEquals(7,op);
		assertEquals(3,mode);
		assertEquals(0,arg);
		
		readInstruction();
		assertEquals(7,op);
		assertEquals(2,mode);
		assertEquals(0,arg);
		
		readInstruction();
		assertEquals(7,op);
		assertEquals(1,mode);
		assertEquals(0,arg);
	}
	
	@Test @Order(9)
	void testNOT() {
		readInstruction();
		assertEquals(8,op);
		assertEquals(0,mode);
		assertEquals(0,arg);
	}
	
	@Test @Order(10)
	void testCMPL() {
		readInstruction();
		assertEquals(9,op);
		assertEquals(2,mode);
		assertEquals(0,arg);
		
		readInstruction();
		assertEquals(9,op);
		assertEquals(1,mode);
		assertEquals(0,arg);
	}
	
	@Test @Order(11)
	void testCMPZ() {
		readInstruction();
		assertEquals(10,op);
		assertEquals(2,mode);
		assertEquals(0,arg);
		
		readInstruction();
		assertEquals(10,op);
		assertEquals(1,mode);
		assertEquals(0,arg);
	}
	
	@Test @Order(12)
	void testJUMP() {
		readInstruction();
		assertEquals(11,op);
		assertEquals(0,mode);
		assertEquals(0,arg);
		
		readInstruction();
		assertEquals(11,op);
		assertEquals(3,mode);
		assertEquals(0,arg);
		
		readInstruction();
		assertEquals(11,op);
		assertEquals(2,mode);
		assertEquals(0,arg);
		
		readInstruction();
		assertEquals(11,op);
		assertEquals(1,mode);
		assertEquals(0,arg);
	}
	
	@Test @Order(13)
	void testJUMZ() {
		readInstruction();
		assertEquals(12,op);
		assertEquals(0,mode);
		assertEquals(0,arg);
		
		readInstruction();
		assertEquals(12,op);
		assertEquals(3,mode);
		assertEquals(0,arg);
		
		readInstruction();
		assertEquals(12,op);
		assertEquals(2,mode);
		assertEquals(0,arg);
		
		readInstruction();
		assertEquals(12,op);
		assertEquals(1,mode);
		assertEquals(0,arg);
	}
	
	@Test @Order(14)
	void testHALT() {
		readInstruction();
		assertEquals(15,op);
		assertEquals(0,mode);
		assertEquals(0,arg);
	}
	
	@Test @Order(15)
	void testDATA() {
		String line = input.nextLine();
		Scanner parser = new Scanner(line);
		int delim = parser.nextInt(16);
		parser.close();
		assertEquals(-1,delim);
		
		line = input.nextLine();
		parser = new Scanner(line);
		int location=parser.nextInt(16);
		int value=parser.nextInt(16);
		parser.close();
		assertEquals(16,location);
		assertEquals(4,value);
	}
	
	
	
	
	@AfterAll
	static void tearDown() throws Exception {
		input.close();
	}
	
	void readInstruction() {
		String line = input.nextLine();
		Scanner parser = new Scanner(line);
		op = parser.nextInt(16);
		mode = parser.nextInt(16);
		arg = parser.nextInt(16);
		parser.close();
	}

}
