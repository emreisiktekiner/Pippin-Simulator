package project.model;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import project.model.Mode;


public class InstructionTester {

	Model machine = new Model();
	int[] dataCopy = new int[Data.DATA_SIZE];
	CPU cpu;
	int accInit;
	int ipInit;
	int offsetInit;

	@BeforeEach
	public void setup() {
		for (int i = 0; i < Data.DATA_SIZE; i++) {
			dataCopy[i] = -5*Data.DATA_SIZE + 10*i;
			machine.setData(i, -5*Data.DATA_SIZE + 10*i);
			// Initially the machine will contain a known spread
			// of different numbers: 
			// -10240, -10230, -10220, ..., 0, 10, 20, ..., 10230 
			// This allows us to check that the Model.Instructions do 
			// not corrupt machine unexpectedly.
			// 0 is at index 1024
		}
		accInit = 30;
		ipInit = 30;
		offsetInit = 200;
		cpu=machine.getCpu();
		cpu.setAccum(accInit);
		cpu.setInstrPtr(ipInit);
		cpu.setMemBase(offsetInit);
	}

	@Test
	public void testNOP(){
		cpu.runInstruction(0, 0, null);
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator untouched
				() -> assertEquals(accInit, cpu.getAccum(), "Accumulator unchanged")
				);
	}

	@Test 
	// Test whether NOP throws exception with immediate addressing mode
	public void testNOPimmediateMode() {
		Throwable exception = assertThrows(IllegalArgumentException.class,
				() -> cpu.runInstruction(0, 0, Mode.IMMEDIATE));
		assertEquals("Illegal Mode in NOP instruction - mode should be null", exception.getMessage());
	}
	 
	@Test 
	// Test whether NOP throws exception with direct addressing mode
	public void testNOPdirectMode() {
		Throwable exception = assertThrows(IllegalArgumentException.class,
				() -> cpu.runInstruction(0, 0, Mode.DIRECT)); 
		assertEquals("Illegal Mode in NOP instruction - mode should be null", exception.getMessage());

	}
	
	@Test 
	// Test whether NOP throws exception with indirect addressing mode
	public void testNOPindirectMode() {
		Throwable exception = assertThrows(IllegalArgumentException.class,
				() -> cpu.runInstruction(0, 0, Mode.INDIRECT)); 
		assertEquals("Illegal Mode in NOP instruction - mode should be null", exception.getMessage());

	}
	
	@Test
	// Test whether load is correct with immediate addressing
	public void testLODimmediate(){
		cpu.setAccum(27);
		int arg = 12;
		// should load 12 into the accumulator
		cpu.runInstruction(1, arg, Mode.IMMEDIATE);
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(12, cpu.getAccum(), "Accumulator modified")
				);
	}

	@Test
	// Test whether load is correct with direct addressing
	public void testLODdirect(){
		cpu.setAccum(27);
		int arg = 12;
		// should load dataCopy[offsetinit+12] into the accumulator
		cpu.runInstruction(1, arg, Mode.DIRECT);
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(dataCopy[offsetInit+12], cpu.getAccum(), "Accumulator modified")
				);
	}

	@Test
	// Test whether load is correct with direct addressing
	public void testLODindirect() {
		cpu.setAccum(-1);
		int arg = 1028-160;
		// if offset1 = dataCopy[offsetinit+1028-160] 
		// should load dataCopy[offsetinit+offset1] into the accumulator
		cpu.runInstruction(1, arg, Mode.INDIRECT);
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> {
					int offset1 = dataCopy[offsetInit+1028-160]; 
					assertEquals(dataCopy[offsetInit+offset1], cpu.getAccum(), "Accumulator modified");
				}
				);
	}	

	@Test 
	// Test whether LOD throws exception with null addressing mode
	public void testLODnullArg() {
		Throwable exception = assertThrows(IllegalArgumentException.class,
				() -> cpu.runInstruction(1, 0, null));
		assertEquals("Illegal Mode in LOD instruction - mode may not be null", exception.getMessage());

	}
	
	@Test
	// Test whether store is correct with direct addressing
	public void testSTOdirect() {
		int arg = 12;
		cpu.setAccum(567);
		dataCopy[offsetInit + 12] = 567;
		cpu.runInstruction(2, arg, Mode.DIRECT);
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(567, cpu.getAccum(), "Accumulator unchanged")
				);
	}

	@Test
	// Test whether store is correct with indirect addressing
	public void testSTOindirect() {
		int arg = 940; 
		cpu.setAccum(567);
		int offset1 = dataCopy[offsetInit + arg];
		// changed memory should be at offset1+offsetinit
		dataCopy[offset1+offsetInit] = 567;
		cpu.runInstruction(2, arg, Mode.INDIRECT);
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(567, cpu.getAccum(), "Accumulator unchanged")
				);
	}

	@Test 
	// Test whether STO throws exception with null addressing
	public void testSTOnullArg() {
		Throwable exception = assertThrows(IllegalArgumentException.class,
				() -> cpu.runInstruction(2, 0, null)); 	
		assertEquals("Illegal Mode in STO instruction - mode may not be null", exception.getMessage());
	}
	
	@Test 
	// Test whether STO throws exception with immediate addressing
	public void testSTOimmediateArg() {
		Throwable exception = assertThrows(IllegalArgumentException.class,
				() -> cpu.runInstruction(2, 0, Mode.IMMEDIATE)); 	
		assertEquals("Illegal Mode in STO instruction - mode may not be immediate", exception.getMessage());

	}
	
	@Test 
	// this test checks whether the add is done correctly, when
	// addressing is immediate
	public void testADDimmediate() {
		int arg = 12; 
		cpu.setAccum(200); 
		cpu.runInstruction(3, arg, Mode.IMMEDIATE);
		// should have added 12 to accumulator
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(200+12, cpu.getAccum(), "Accumulator modified")
				);
	}

	@Test 
	// this test checks whether the add is done correctly, when
	// addressing is direct
	public void testADDdirect() {
		int arg = 12; 
		cpu.setAccum(250);
		// should add dataCopy[offsetinit+12] to the accumulator
		cpu.runInstruction(3, arg, Mode.DIRECT);
		// should have added 12 to accumulator
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(250-10240+2120, cpu.getAccum(), "Accumulator modified")
				);
	}

	@Test 
	// this test checks whether the add is done correctly, when
	// addressing is indirect
	public void testADDindirect() {
		int arg = 1028-160;
		cpu.setAccum(250);
		// if offset1 = dataCopy[offsetinit+1028-160] = dataCopy[1068] = 10*(68-24) = 440
		// should add dataCopy[offsetinit+offset1] = dataCopy[640] = 6400-10240 to the accumulator	
		// -3840
		cpu.runInstruction(3, arg, Mode.INDIRECT);
		// should have added 12 to accumulator
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(250-3840, cpu.getAccum(), "Accumulator modified")
				);
	}

	@Test 
	// Test whether ADD throws exception with null addressing mode
	public void testADDnullArg() {
		Throwable exception = assertThrows(IllegalArgumentException.class,
				() -> cpu.runInstruction(3, 0, null)); 
		assertEquals("Illegal Mode in ADD instruction - mode may not be null", exception.getMessage());

	}
	
	@Test 
	// this test checks whether the subtraction is done correctly, when
	// addressing is immediate
	public void testSUBimmediate() {
		int arg = 12; 
		cpu.setAccum(200);
		cpu.runInstruction(4, arg, Mode.IMMEDIATE);
		// should have subtracted 12 from accumulator
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(200-12, cpu.getAccum(), "Accumulator modified")
				);
	}

	@Test 
	// this test checks whether the subtraction is done correctly, when
	// addressing is direct
	public void testSUBdirect() {
		int arg = 12; 
		cpu.setAccum(250);
		// should subtract dataCopy[offsetinit+12] = dataCopy[212] = -10240 + 2120 from the accumulator
		cpu.runInstruction(4, arg, Mode.DIRECT);
		// should have added 12 to accumulator
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(250+10240-2120, cpu.getAccum(), "Accumulator modified")
				);
	}

	@Test 
	// this test checks whether the subtraction is done correctly, when
	// addressing is indirect
	public void testSUBindirect() {
		int arg = 1028-160;
		cpu.setAccum(250);
		// if offset1 = dataCopy[offsetinit+1028-160] = dataCopy[1068] = 10*(68-24) = 440
		// should subtract dataCopy[offsetinit+offset1] = dataCopy[640] = 6400-10240 from the accumulator	
		// -3840
		cpu.runInstruction(4, arg, Mode.INDIRECT);
		// should have added 12 to accumulator
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(250+3840, cpu.getAccum(), "Accumulator modified")
				);
	}

	@Test 
	// Test whether SUB throws exception with null addressing mode
	public void testSUBnullArg() {
		Throwable exception = assertThrows(IllegalArgumentException.class,
				() -> cpu.runInstruction(4, 0, null)); 
		assertEquals("Illegal Mode in SUB instruction - mode may not be null", exception.getMessage());

	}
	
	@Test 
	// this test checks whether the multiplication is done correctly, when
	// addressing is immediate
	public void testMULimmediate() {
		int arg = 12; 
		cpu.setAccum(200);
		cpu.runInstruction(5, arg, Mode.IMMEDIATE);
		// should have multiplied the accumulator by 12
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(200*12, cpu.getAccum(), "Accumulator modified")
				);
	}

	@Test 
	// this test checks whether the multiplication is done correctly, when
	// addressing is direct
	public void testMULdirect() {
		int arg = 12; 
		cpu.setAccum(250);
		// should multiply the accumulator by dataCopy[offsetinit+12] = dataCopy[212] = -10240 + 2120
		cpu.runInstruction(5, arg, Mode.DIRECT);
		// should have added 12 to accumulator
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(250*(-10240+2120), cpu.getAccum(), "Accumulator modified")
				);
	}

	@Test 
	// this test checks whether the multiplication is done correctly, when
	// addressing is indirect
	public void testMULindirect() {
		int arg = 1028-160;
		cpu.setAccum(250);
		// if offset1 = dataCopy[offsetinit+1028-160] = dataCopy[1068] = 10*(68-24) = 440
		// should multiply the accumulator by dataCopy[offsetinit+offset1] = dataCopy[640] = 6400-10240	
		// -3840
		cpu.runInstruction(5, arg, Mode.INDIRECT);
		// should have added 12 to accumulator
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(250*(-3840), cpu.getAccum(), "Accumulator modified")
				);
	}

	@Test 
	// Test whether MUL throws exception with null addressing mode
	public void testMULnullArg() {
		Throwable exception = assertThrows(IllegalArgumentException.class,
				() -> cpu.runInstruction(5, 0, null));		
		assertEquals("Illegal Mode in MUL instruction - mode may not be null", exception.getMessage());

	}
	
	@Test 
	// this test checks whether the division is done correctly, when
	// addressing is immediate
	public void testDIVimmediate() {
		int arg = 12; 
		cpu.setAccum(200);
		cpu.runInstruction(6, arg, Mode.IMMEDIATE);
		// should have multiplied the accumulator by 12
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(200/12, cpu.getAccum(), "Accumulator modified")
				);
	}

	@Test 
	// this test checks whether the division is done correctly, when
	// addressing is direct
	public void testDIVdirect() {
		int arg = 12; 
		cpu.setAccum(1024011);
		// should divide the accumulator by dataCopy[offsetinit+12] = dataCopy[212] = -10240 + 2120
		cpu.runInstruction(6, arg, Mode.DIRECT); 
		// should have added 12 to accumulator
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(1024011/(-10240+2120), cpu.getAccum(), "Accumulator modified")
				);
	}

	@Test 
	// this test checks whether the division is done correctly, when
	// addressing is indirect
	public void testDIVindirect() {
		int arg = 1028-160;
		cpu.setAccum(400000);
		// if offset1 = dataCopy[offsetinit+1028-160] = dataCopy[1068] = 10*(68-24) = 440
		// should divide the accumulator by dataCopy[offsetinit+offset1] = dataCopy[640] = 6400-10240	
		// -3840
		cpu.runInstruction(6, arg, Mode.INDIRECT);
		// should have added 12 to accumulator
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(400000/(-3840), cpu.getAccum(), "Accumulator modified")
				);
	}

	@Test 
	// Test whether DIV throws exception with null addressing mode
	public void testDIVnullArg() {
		Throwable exception = assertThrows(IllegalArgumentException.class,
				() -> cpu.runInstruction(6, 0, null));		
		assertEquals("Illegal Mode in DIV instruction - mode may not be null", exception.getMessage());

	}
	
	@Test 
	// Test whether DIV throws divide by zero exception with immediate addressing mode
	public void testDIVzerodivisionImmed() {
		Throwable exception = assertThrows(DivideByZeroException.class,
				() -> cpu.runInstruction(6, 0, Mode.IMMEDIATE));	
		assertEquals("Divide by Zero", exception.getMessage());

	}

	@Test 
	// Test whether DIV throws divide by zero exception with direct addressing mode
	public void testDIVzerodivisionDirect() {
		// cpu.memoryBase = 200
		Throwable exception = assertThrows(DivideByZeroException.class,
				() -> cpu.runInstruction(6, 824, Mode.DIRECT));	
		assertEquals("Divide by Zero", exception.getMessage());

	}

	@Test 
	// Test whether DIV throws divide by zero exception with indirect addressing mode
	public void testDIVzerodivisionIndirect() {
		machine.setData(100+offsetInit, 1024-offsetInit);
		// cpu.memoryBase = 200
		Throwable exception = assertThrows(DivideByZeroException.class,
				() -> cpu.runInstruction(6, 100, Mode.INDIRECT));		
		assertEquals("Divide by Zero", exception.getMessage());

	}

	@Test 
	// Check AND when accum and arg equal to 0 gives false
	// addressing is immediate
	public void testANDimmediateAccEQ0argEQ0() {
		int arg = 0;
		cpu.setAccum(0);
		cpu.runInstruction(7, arg, Mode.IMMEDIATE);
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(0, cpu.getAccum(), "Accumulator 0")
				);
	}

	@Test 
	// Check AND when accum and arg equal to 0 gives false
	// addressing is immediate
	public void testANDimmediateAccLT0argEQ0() {
		int arg = 0;
		cpu.setAccum(-1);
		cpu.runInstruction(7, arg, Mode.IMMEDIATE);
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(0, cpu.getAccum(), "Accumulator 0")
				);
	}

	@Test 
	// Check AND when accum > 0 and arg equal to 0 gives false
	// addressing is immediate
	public void testANDimmediateAccGT0argEQ0() {
		int arg = 0;
		cpu.setAccum(1);
		cpu.runInstruction(7, arg, Mode.IMMEDIATE);
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(0, cpu.getAccum(), "Accumulator 0")
				);
	}

	@Test 
	// Check AND when accum = 0 and arg < 0 gives false
	// addressing is immediate
	public void testANDimmediateAccEQ0argLT0() {
		int arg = -1;
		cpu.setAccum(0);
		cpu.runInstruction(7, arg, Mode.IMMEDIATE);
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(0, cpu.getAccum(), "Accumulator 0")
				);
	}

	@Test 
	// Check AND when accum < 0 and arg < 0 gives true
	// addressing is immediate
	public void testANDimmediateAccLT0argLT0() {
		int arg = -1;
		cpu.setAccum(-1);
		cpu.runInstruction(7, arg, Mode.IMMEDIATE);
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(1, cpu.getAccum(), "Accumulator 1")
				);
	}

	@Test 
	// Check AND when accum = 0 and arg > 0 gives false
	// addressing is immediate
	public void testANDimmediateAccEQ0argGT0() {
		int arg = 1;
		cpu.setAccum(0);
		cpu.runInstruction(7, arg, Mode.IMMEDIATE);
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(0, cpu.getAccum(), "Accumulator 0")
				);
	}

	@Test 
	// Check AND when accum > 0 and arg > 0 gives true
	// addressing is immediate
	public void testANDimmediateAccGT0argGT0() {
		int arg = 0;
		cpu.setAccum(1);
		cpu.runInstruction(7, arg, Mode.IMMEDIATE);
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(0, cpu.getAccum(), "Accumulator 0")
				);
	}

	@Test 
	// Check AND when accum < 0 and arg > 0 gives true
	// addressing is immediate
	public void testANDimmediateAccLT0argGT0() {
		int arg = 1;
		cpu.setAccum(-1);
		cpu.runInstruction(7, arg, Mode.IMMEDIATE); 
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(1, cpu.getAccum(), "Accumulator 1")
				);
	}

	@Test 
	// Check AND when accum > 0 and arg > 0 gives true
	// addressing is immediate
	public void testANDimmediateAccGT0argLT0() {
		int arg = -1;
		cpu.setAccum(1);
		cpu.runInstruction(7, arg, Mode.IMMEDIATE);
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(1, cpu.getAccum(), "Accumulator 1")
				);
	}
	
	@Test 
	// Check AND when accum direct mem equal to 0 gives false
	// addressing is direct
	public void testANDdirectAccEQ0memEQ0() {
		int arg = 1024-offsetInit; 
		cpu.setAccum(0);
		cpu.runInstruction(7, arg, Mode.DIRECT); 
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(0, cpu.getAccum(), "Accumulator 0")
				);
	}

	@Test 
	// Check AND when accum < 0 direct mem equal to 0 gives false
	// addressing is direct
	public void testANDdirectAccLT0memEQ0() {
		int arg = 1024-offsetInit; 
		cpu.setAccum(-1);
		cpu.runInstruction(7, arg, Mode.DIRECT); 
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(0, cpu.getAccum(), "Accumulator 0")
				);
	}
	
	@Test
	// Check AND when accum > 0 direct mem equal to 0 gives false
	// addressing is direct
	public void testANDdirectAccGT0memEQ0() {
		int arg = 1024-offsetInit; 
		cpu.setAccum(1);
		cpu.runInstruction(7, arg, Mode.DIRECT); 
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(0, cpu.getAccum(), "Accumulator 0")
				);
	}

	@Test 
	// Check AND when accum = 0 direct mem < 0 gives false
	// addressing is direct
	public void testANDdirectAccEQ0memLT0() {
		int arg = 100; 
		cpu.setAccum(0);
		cpu.runInstruction(7, arg, Mode.DIRECT);
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(0, cpu.getAccum(), "Accumulator 0")
				);
	}

	@Test 
	// Check AND when accum < 0 direct mem < 0 gives true
	// addressing is direct
	public void testANDdirectAccLT0memLT0() {
		int arg = 100; 
		cpu.setAccum(-1);
		cpu.runInstruction(7, arg, Mode.DIRECT);
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(1, cpu.getAccum(), "Accumulator 1")
				);
	}

	@Test 
	// Check AND when accum > 0 direct mem < 0 gives true
	// addressing is direct
	public void testANDdirectAccGT0memLT0() {
		int arg = 100; 
		cpu.setAccum(1);
		cpu.runInstruction(7, arg, Mode.DIRECT);
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(1, cpu.getAccum(), "Accumulator 1")
				);
	}

	@Test 
	// Check AND when accum = 0 direct mem > 0 gives false
	// addressing is direct
	public void testANDdirectAccEQ0memGT0() {
		int arg = 1030-offsetInit;
		cpu.setAccum(0);
		cpu.runInstruction(7, arg, Mode.DIRECT);
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(0, cpu.getAccum(), "Accumulator 0")
				);
	}

	@Test 
	// Check AND when accum < 0 direct mem > 0 gives true
	// addressing is direct
	public void testANDdirectAccLT0memGT0() {
		int arg = 1030-offsetInit;
		cpu.setAccum(-1);
		cpu.runInstruction(7, arg, Mode.DIRECT); 
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(1, cpu.getAccum(), "Accumulator 1")
				);
	}

	@Test 
	// Check AND when accum > 0 direct mem > 0 gives true
	// addressing is direct
	public void testANDdirectAccGT0memGT0() {
		int arg = 1030-offsetInit;
		cpu.setAccum(1);
		cpu.runInstruction(7, arg, Mode.DIRECT); 
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(1, cpu.getAccum(), "Accumulator 1")
				);
	}

	@Test 
	// Check AND when accum indirect mem equal to 0 gives false
	// addressing is indirect
	public void testANDindirectAccEQ0memEQ0() {
		int arg = 1024 - offsetInit;
		cpu.setAccum(0);
		machine.setData(offsetInit, 0);
		dataCopy[offsetInit] = 0;
		cpu.runInstruction(7, arg, Mode.INDIRECT);
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(0, cpu.getAccum(), "Accumulator 0")
				);
	}

	@Test 
	// Check AND when accum < 0 indirect mem equal to 0 gives false
	// addressing is indirect
	public void testANDindirectAccLT0memEQ0() {
		int arg = 1024 - offsetInit;
		cpu.setAccum(-1);
		machine.setData(offsetInit, 0);
		dataCopy[offsetInit] = 0;
		cpu.runInstruction(7, arg, Mode.INDIRECT);
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(0, cpu.getAccum(), "Accumulator 0")
				);
	}

	@Test 
	// Check AND when accum > 0 indirect mem equal to 0 gives false
	// addressing is indirect
	public void testANDindirectAccGT0memEQ0() {
		int arg = 1024 - offsetInit;
		cpu.setAccum(1);
		machine.setData(offsetInit, 0);
		dataCopy[offsetInit] = 0;
		cpu.runInstruction(7, arg, Mode.INDIRECT); 
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(0, cpu.getAccum(), "Accumulator 0")
				);
	}

	@Test 
	// Check AND when accum = 0 indirect mem < 0 gives false
	// addressing is indirect
	public void testANDindirectAccEQ0memLT0() {
		int arg = 1020 - offsetInit;
		cpu.setAccum(0);
		cpu.runInstruction(7, arg, Mode.INDIRECT);
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(0, cpu.getAccum(), "Accumulator 0")
				);
	}

	@Test 
	// Check AND when accum < 0 indirect mem < 0 gives true
	// addressing is indirect
	public void testANDindirectAccLT0memLT0() {
		int arg = 1020 - offsetInit;
		cpu.setAccum(-1);
		cpu.runInstruction(7, arg, Mode.INDIRECT); 
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(1, cpu.getAccum(), "Accumulator 1")
				);
	}

	@Test 
	// Check AND when accum > 0 indirect mem < 0 gives true
	// addressing is indirect
	public void testANDindirectAccGT0memLT0() {
		int arg = 1020 - offsetInit;
		cpu.setAccum(1);
		cpu.runInstruction(7, arg, Mode.INDIRECT); 
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(1, cpu.getAccum(), "Accumulator 1")
				);
	}

	@Test 
	// Check AND when accum = 0 indirect mem > 0 gives false
	// addressing is indirect
	public void testANDindirectAccEQ0memGT0() {
		int arg = 1200 - offsetInit;
		cpu.setAccum(0);
		cpu.runInstruction(7, arg, Mode.INDIRECT);
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(0, cpu.getAccum(), "Accumulator 0")
				);
	}

	@Test 
	// Check AND when accum < 0 indirect mem > 0 gives true
	// addressing is indirect
	public void testANDindirectAccLT0memGT0() {
		int arg = 1200 - offsetInit;
		cpu.setAccum(-1);
		cpu.runInstruction(7, arg, Mode.INDIRECT); 
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(1, cpu.getAccum(), "Accumulator 1")
				);
	}

	@Test 
	// Check AND when accum > 0 indirect mem > 0 gives true
	// addressing is indirect
	public void testANDindirectAccGT0memGT0() {
		int arg = 1200 - offsetInit;
		cpu.setAccum(1);
		cpu.runInstruction(7, arg, Mode.INDIRECT); 
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(1, cpu.getAccum(), "Accumulator 1")
				);
	}

	@Test 
	// Test whether AND throws exception with null addressing mode
	public void testANDnullArg() {
		Throwable exception = assertThrows(IllegalArgumentException.class,
				() -> cpu.runInstruction(7, 0, null));		
		assertEquals("Illegal Mode in AND instruction - mode may not be null", exception.getMessage());

	}
	
	@Test 
	// Check NOT greater than 0 gives false
	// there is no argument and mode is null
	public void testNOTaccGT0() {
		cpu.setAccum(1);
		cpu.runInstruction(8, 0, null);
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(0, cpu.getAccum(), "Accumulator 0")
				);
	}

	@Test 
	// Check NOT equal to 0 gives true
	// there is no argument and mode is null
	public void testNOTaccEQ0() {
		cpu.setAccum(0);
		cpu.runInstruction(8, 0, null); 
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(1, cpu.getAccum(), "Accumulator 1")
				);
	}

	@Test 
	// Check NOT less than 0 gives false
	// there is no argument and mode is null
	public void testNOTaccLT0() {
		cpu.setAccum(-1);
		cpu.runInstruction(8, 0, null); 
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(0, cpu.getAccum(), "Accumulator 0")
				);
	}

	@Test 
	// Test whether NON throws exception with immediate addressing mode
	public void testNOimmediateMode() {
		Throwable exception = assertThrows(IllegalArgumentException.class,
				() -> cpu.runInstruction(8, 0, Mode.IMMEDIATE));	
		assertEquals("Illegal Mode in NOT instruction - mode should be null", exception.getMessage());
	}

	@Test 
	// Test whether NON throws exception with direct addressing mode
	public void testNOTdirectMode() {
		Throwable exception = assertThrows(IllegalArgumentException.class,
				() -> cpu.runInstruction(8, 0, Mode.DIRECT));		
		assertEquals("Illegal Mode in NOT instruction - mode should be null", exception.getMessage());
	}
	
	@Test 
	// Test whether NON throws exception with immediate addressing mode
	public void testNOTindirectMode() {
		Throwable exception = assertThrows(IllegalArgumentException.class,
				() -> cpu.runInstruction(8, 0, Mode.INDIRECT));		
		assertEquals("Illegal Mode in NOT instruction - mode should be null", exception.getMessage());
	}

	@Test 
	// Check CMPL when comparing less than 0 gives true
	// addressing is direct
	public void testCMPLdirectMemLT0() {
		int arg = 100;
		cpu.runInstruction(9, arg, Mode.DIRECT);
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(1, cpu.getAccum(), "Accumulator 1")
				);
	}

	@Test 
	// Check CMPL when comparing grater than 0 gives false
	// addressing is direct
	public void testCMPLdirectMemGT0() {
		int arg = 1024;
		cpu.runInstruction(9, arg, Mode.DIRECT);
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(0, cpu.getAccum(), "Accumulator 0")
				);
	}

	@Test 
	// Check CMPL when comparing equal to 0 gives false
	// addressing is direct
	public void testCMPLdirectMemEQ0() {
		int arg = 1024 - offsetInit ;
		cpu.runInstruction(9, arg, Mode.DIRECT); 
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(0, cpu.getAccum(), "Accumulator 0")
				);
	}

	@Test 
	// Check CMPL when comparing less than 0 gives true
	// addressing is indirect
	public void testCMPLindirectMemLT0() {
		int arg = 850;
		cpu.runInstruction(9, arg, Mode.INDIRECT);
		assertAll (
				() -> {
					int index =  machine.getData(arg+offsetInit);
					assertTrue(machine.getData(index+offsetInit) < 0); 
				},
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(1, cpu.getAccum(), "Accumulator 1")
				);
	}

	@Test 
	// Check CMPL when comparing greater than 0 gives false
	// addressing is indirect
	public void testCMPLindirectMemGT0() {
		int arg = 950;
		cpu.runInstruction(9, arg, Mode.INDIRECT);
		assertAll (
				() -> {
					int index = machine.getData(arg+offsetInit);
					assertTrue(machine.getData(index+offsetInit) > 0); 
				},
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(0, cpu.getAccum(), "Accumulator 0")
				);
	}

	@Test 
	// Check CMPL when comparing equal to 0 gives false
	// addressing is indirect
	public void testCMPLindirectMemEQ0() {
		int arg = 1024 - offsetInit;
		machine.setData(offsetInit, 0);
		dataCopy[offsetInit] = 0;
		cpu.runInstruction(9, arg, Mode.INDIRECT);
		assertAll (
				() -> {
					int index = machine.getData(arg+offsetInit);
					assertTrue(machine.getData(index+offsetInit) == 0); 
				},
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(0, cpu.getAccum(), "Accumulator 0")
				);
	}

	@Test 
	// Test whether CMPL throws exception with null addressing mode
	public void testCMPLnullMode() {
		Throwable exception = assertThrows(IllegalArgumentException.class,
				() -> cpu.runInstruction(9, 0, null));		
		assertEquals("Illegal Mode in CMPL instruction - mode may not be null", exception.getMessage());
	}

	@Test 
	// Test whether CMPL throws exception with immediate addressing mode
	public void testCMPLimmediateMode() {
		Throwable exception = assertThrows(IllegalArgumentException.class,
				() -> cpu.runInstruction(9, 0, Mode.IMMEDIATE));	
		assertEquals("Illegal Mode in CMPL instruction - mode may not be immediate", exception.getMessage());
	}
	
	@Test 
	// Check CMPZ when comparing less than 0 gives false
	// addressing is direct
	public void testCMPZdirectMemLT0() {
		int arg = 100;
		cpu.runInstruction(10, arg, Mode.DIRECT);
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(0, cpu.getAccum(), "Accumulator 0")
				);
	}

	@Test 
	// Check CMPZ when comparing grater than 0 gives false
	// addressing is direct
	public void testCMPZdirectMemGT0() {
		int arg = 1024;
		cpu.runInstruction(10, arg, Mode.DIRECT);
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(0, cpu.getAccum(), "Accumulator 0")
				);
	}

	@Test 
	// Check CMPZ when comparing equal to 0 gives true
	// addressing is direct
	public void testCMPZdirectMemEQ0() {
		int arg = 1024 - offsetInit ;
		cpu.runInstruction(10, arg, Mode.DIRECT); 
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(1, cpu.getAccum(), "Accumulator 1")
				);
	}

	@Test 
	// Check CMPL when comparing less than 0 gives false
	// addressing is indirect
	public void testCMPZindirectMemLT0() {
		int arg = 850;
		cpu.runInstruction(10, arg, Mode.INDIRECT);
		assertAll (
				() -> {
					int index = machine.getData(arg+offsetInit);
					assertTrue(machine.getData(index+offsetInit) < 0); 
				},
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(0, cpu.getAccum(), "Accumulator 0")
				);
	}

	@Test 
	// Check CMPZ when comparing greater than 0 gives false
	// addressing is indirect
	public void testCMPZindirectMemGT0() {
		int arg = 950;
		cpu.runInstruction(10, arg, Mode.INDIRECT);
		assertAll (
				() -> {
					int index = machine.getData(arg+offsetInit);
					assertTrue(machine.getData(index+offsetInit) > 0); 
				},
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(0, cpu.getAccum(), "Accumulator 0")
				);
	}

	@Test 
	// Check CMPZ when comparing equal to 0 gives true
	// addressing is indirect
	public void testCMPZindirectMemEQ0() {
		int arg = 1024 - offsetInit;
		machine.setData(offsetInit, 0);
		dataCopy[offsetInit] = 0;
		cpu.runInstruction(10, arg, Mode.INDIRECT); 
		assertAll (
				() -> {
					int index = machine.getData(arg+offsetInit);
					assertTrue(machine.getData(index+offsetInit) == 0); 
				},
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(1, cpu.getAccum(), "Accumulator 1")
				);
	}

	@Test 
	// Test whether CMPZ throws exception with null addressing mode
	public void testCMPZnullMode() {
		Throwable exception = assertThrows(IllegalArgumentException.class,
				() -> cpu.runInstruction(10, 0, null));		
		assertEquals("Illegal Mode in CMPZ instruction - mode may not be null", exception.getMessage());
	}

	@Test 
	// Test whether CMPZ throws exception with immediate addressing mode
	public void testCMPZimmediateMode() {
		Throwable exception = assertThrows(IllegalArgumentException.class,
				() -> cpu.runInstruction(10, 0, Mode.IMMEDIATE));	
		assertEquals("Illegal Mode in CMPZ instruction - mode may not be immediate", exception.getMessage());
	}
	
	@Test 
	// this test checks whether the relative JUMP is done correctly, when
	// addressing is immediate
	public void testJUMPimmediate() {
		int arg = 260;  
		cpu.runInstruction(11, arg, Mode.IMMEDIATE); 
		// should set the instruction pointer to 260
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer modified
				() -> assertEquals(260 + ipInit, cpu.getInstrPtr(), "Instruction pointer modified"),
				//Test accumulator modified
				() -> assertEquals(accInit, cpu.getAccum(), "Accumulator was not changed")
				);
	}

	@Test 
	// this test checks whether the relative JUMP is done correctly, when
	// addressing is direct
	public void testJUMPdirect() {
		int arg = 1024-160; // the memory value is data[offsetinit-160 + 1024] = 400  
		cpu.runInstruction(11, arg, Mode.DIRECT);
		// should set the instruction pointer to 400
		assertAll (
				() -> {
					assertTrue(machine.getData(1024-160+offsetInit) == 400); 
				},
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer modified
				() -> assertEquals(400 + ipInit, cpu.getInstrPtr(), "Instruction pointer modified"),
				//Test accumulator modified
				() -> assertEquals(accInit, cpu.getAccum(), "Accumulator was not changed")
				);
	}

	@Test 
	// this test checks whether the relative JUMP is done correctly, when
	// addressing is indirect
	public void testJUMPindirect() {
		int arg = 910; // the memory value is data[offsetinit-160 + 1024] = 400  
		cpu.runInstruction(11, arg, Mode.INDIRECT);
		// if index = data[offsetinit + 910] = 860
		// then the memory value is data[offsetinit + 860] = data[1060] = 360
		assertAll (
				() -> {
					int index =  machine.getData(offsetInit + 910);
					assertTrue(machine.getData(index+offsetInit) == 360); 
				},
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer modified
				() -> assertEquals(360 + ipInit, cpu.getInstrPtr(), "Instruction pointer modified"),
				//Test accumulator modified
				() -> assertEquals(accInit, cpu.getAccum(), "Accumulator was not changed")
				);
	}

	@Test 
	// this test checks whether the non-relative JUMP is done correctly, when
	// addressing is not relative to current instruction pointer
	public void testJUMPnonrelative() {
		int arg = 1024-160; // the memory value is data[offsetinit-160 + 1024] = 400  
		Job job = machine.getCurrentJob();
		job.setStartcodeIndex(777);
		cpu.runInstruction(11, arg, null);
		assertAll (
				() -> {
					assertTrue(machine.getData(1024-160+offsetInit) == 400); 
				},
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer modified
				() -> assertEquals(400 + 777, cpu.getInstrPtr(), "Instruction pointer modified"),
				//Test accumulator modified
				() -> assertEquals(accInit, cpu.getAccum(), "Accumulator was not changed")
				);
	}

	@Test 
	// this test checks whether the relative JMPZ is done like JUMP when accumulator is 0
	// addressing is immediate
	public void testJMPZimmediate() {
		cpu.setAccum(0);
		int arg = 260;  
		cpu.runInstruction(12, arg, Mode.IMMEDIATE);
		// should set the instruction pointer to 260
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer modified
				() -> assertEquals(260 + ipInit, cpu.getInstrPtr(), "Instruction pointer modified"),
				//Test accumulator modified
				() -> assertEquals(0, cpu.getAccum(), "Accumulator was not changed")
				);
	}

	@Test 
	// this test checks whether the relative JMPZ is done like JUMP when accumulator is 0
	// addressing is direct
	public void testJMPZdirect() {
		cpu.setAccum(0);
		int arg = 1024-160; // the memory value is data[offsetinit-160 + 1024] = 400  
		cpu.runInstruction(12, arg, Mode.DIRECT); 
		// should set the instruction pointer to 400
		assertAll (
				() -> {
					assertTrue(machine.getData(1024-160+offsetInit) == 400); 
				},
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer modified
				() -> assertEquals(400 + ipInit, cpu.getInstrPtr(), "Instruction pointer modified"),
				//Test accumulator modified
				() -> assertEquals(0, cpu.getAccum(), "Accumulator was not changed")
				);
	}

	@Test 
	// this test checks whether the relative JMPZ is done like JUMP when accumulator is 0
	// addressing is indirect
	public void testJMPZindirect() {
		cpu.setAccum(0);
		int arg = 910; // the memory value is data[offsetinit-160 + 1024] = 400  
		cpu.runInstruction(12, arg, Mode.INDIRECT);
		// if index = data[offsetinit + 910] = 860
		// then the memory value is data[offsetinit + 860] = data[1060] = 360
		assertAll (
				() -> {
					int index =  machine.getData(offsetInit + 910);
					assertTrue(machine.getData(index+offsetInit) == 360); 
				},
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer modified
				() -> assertEquals(360 + ipInit, cpu.getInstrPtr(), "Instruction pointer modified"),
				//Test accumulator modified
				() -> assertEquals(0, cpu.getAccum(), "Accumulator was not changed")
				);
	}

	@Test 
	// this test checks whether the non-relative JMPZ is done like JUMP when accumulator is 0
	// addressing is not relative to current instruction pointer
	public void testJMPZnonrelative() {
		cpu.setAccum(0);
		int arg = 1024-160; // the memory value is data[offsetinit-160 + 1024] = 400  
		Job job = machine.getCurrentJob();
		job.setStartcodeIndex(777);
		cpu.runInstruction(12, arg, null);
		assertAll (
				() -> {
					assertTrue(machine.getData(1024-160+offsetInit) == 400); 
				},
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer modified
				() -> assertEquals(400 + 777, cpu.getInstrPtr(), "Instruction pointer modified"),
				//Test accumulator modified
				() -> assertEquals(0, cpu.getAccum(), "Accumulator was not changed")
				);
	}

	@Test 
	// this test checks whether the relative JMPZ only increments instruction pointer
	// addressing is immediate
	public void testJMPZimmediateAccNZ() {
		int arg = 260;  
		cpu.runInstruction(12, arg, Mode.IMMEDIATE);
		// should set the instruction pointer to 260
		assertAll (
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(accInit, cpu.getAccum(), "Accumulator was not changed")
				);
	}

	@Test 
	// this test checks whether the relative JMPZ only increments instruction pointer
	// addressing is direct
	public void testJMPZdirectAccNZ() {
		int arg = 1024-160; // the memory value is data[offsetinit-160 + 1024] = 400  
		cpu.runInstruction(12, arg, Mode.DIRECT); 
		// should set the instruction pointer to 400
		assertAll (
				() -> {
					assertTrue(machine.getData(1024-160+offsetInit) == 400); 
				},
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(accInit, cpu.getAccum(), "Accumulator was not changed")
				);
	}

	@Test 
	// this test checks whether the relative JMPZ only increments instruction pointer
	// addressing is indirect
	public void testJMPZindirectAccNZ() {
		int arg = 910; // the memory value is data[offsetinit-160 + 1024] = 400  
		cpu.runInstruction(12, arg, Mode.INDIRECT);
		// if index = data[offsetinit + 910] = 860
		// then the memory value is data[offsetinit + 860] = data[1060] = 360
		assertAll (
				() -> {
					int index =  machine.getData(offsetInit + 910);
					assertTrue(machine.getData(index+offsetInit) == 360); 
				},
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(accInit, cpu.getAccum(), "Accumulator was not changed")
				);
	}

	@Test 
	// this test checks whether the non-relative JMPZ only increments instruction pointer
	// addressing is not relative to current instruction pointer
	public void testJMPZnonrelativeAccNZ() {
		int arg = 1024-160; // the memory value is data[offsetinit-160 + 1024] = 400  
		Job job = machine.getCurrentJob();
		job.setStartcodeIndex(777);
		cpu.runInstruction(12, arg, null);
		assertAll (
				() -> {
					assertTrue(machine.getData(1024-160+offsetInit) == 400); 
				},
				//Test machine is not changed
				() -> assertArrayEquals(dataCopy, machine.getData()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, cpu.getInstrPtr(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(accInit, cpu.getAccum(), "Accumulator was not changed")
				);
	}

}

