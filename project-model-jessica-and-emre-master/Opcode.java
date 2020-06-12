package project.model;

import java.util.Set;

public enum Opcode {
	NOP(0) {
		public void execute(CPU cpu,int arg,Mode mode) {
			checkNullMode(mode);
			cpu.incrementIP();
		}
	},
	LOD(1) {
		public void execute(CPU cpu,int arg,Mode mode) {
			checkNonNullMode(mode);
			cpu.setAccum(getArgValue(mode,arg,cpu));
			cpu.incrementIP();
		}
	},
	STO(2) {
		public void execute(CPU cpu,int arg,Mode mode) {
			checkNonImmediateMode(mode);
			checkNonNullMode(mode);
			arg = getIndirectArgValue(mode, arg, cpu);
			cpu.setData(arg);
			cpu.incrementIP();
		}
	},
	ADD(3) {
		public void execute(CPU cpu,int arg,Mode mode) {
			checkNonNullMode(mode);
			cpu.setAccum(cpu.getAccum()+getArgValue(mode,arg,cpu));
			cpu.incrementIP();
		}
	},
	SUB(4) {
		public void execute(CPU cpu,int arg,Mode mode) {
			checkNonNullMode(mode);
			cpu.setAccum(cpu.getAccum()-getArgValue(mode,arg,cpu));
			cpu.incrementIP();
		}
	},
	MUL(5) {
		public void execute(CPU cpu,int arg,Mode mode) {
			checkNonNullMode(mode);
			cpu.setAccum(cpu.getAccum()*getArgValue(mode,arg,cpu));
			cpu.incrementIP();
	}
		
	},
	DIV(6) {
		public void execute(CPU cpu,int arg,Mode mode) {
			checkNonNullMode(mode);
			if (getArgValue(mode,arg,cpu)==0)
				throw new DivideByZeroException("Divide by Zero");
			cpu.setAccum(cpu.getAccum()/getArgValue(mode,arg,cpu));
			cpu.incrementIP();
		}
	},
	AND(7) {
		public void execute(CPU cpu,int arg,Mode mode) {
			arg = getArgValue(mode, arg, cpu);
			checkNonNullMode(mode);
			if (arg==0 || cpu.getAccum() ==0) {
					cpu.setAccum(0);
			}
			else {
				cpu.setAccum(1);
			}
			cpu.incrementIP();
		}
	},
	NOT(8) {
		public void execute(CPU cpu,int arg,Mode mode) {
			checkNullMode(mode);
			if (cpu.getAccum()==0) {
				cpu.setAccum(1);
			}
			else {
				cpu.setAccum(0);
			}
			cpu.incrementIP();
		}
	},
	CMPL(9) {
		public void execute(CPU cpu,int arg,Mode mode) {
			checkNonImmediateMode(mode);
			checkNonNullMode(mode);
			if (getArgValue(mode, arg, cpu)<0) {
					cpu.setAccum(1);
			}
			else {
					cpu.setAccum(0);
			}
			cpu.incrementIP();
		}
	},
	CMPZ(10) {
		public void execute(CPU cpu,int arg,Mode mode) {
			checkNonImmediateMode(mode);
			checkNonNullMode(mode);
			if (getArgValue(mode, arg, cpu)==0) {
				cpu.setAccum(1);
			}
			else {
				cpu.setAccum(0);
			}
			cpu.incrementIP();
		}
	},
	JUMP(11) {
		public void execute(CPU cpu,int arg,Mode mode) {
			if (mode==null) cpu.setAbsoluteIP(getArgValue(mode,arg,cpu));
			if (mode==null) cpu.setAbsoluteIP(cpu.getData(getArgValue(mode,arg,cpu)));
			else cpu.setInstrPtr(cpu.getInstrPtr()+getArgValue(mode,arg,cpu));
		}
	},
	JMPZ(12) {
		public void execute(CPU cpu,int arg,Mode mode) {
			if (cpu.getAccum()==0){ 
				if (mode==null) cpu.setAbsoluteIP(getArgValue(mode,arg,cpu));
				if (mode==null) cpu.setAbsoluteIP(cpu.getData(getArgValue(mode,arg,cpu)));
				else cpu.setInstrPtr(cpu.getInstrPtr()+getArgValue(mode,arg,cpu));
			}
			else {cpu.incrementIP();
			}
		}
	},
	HALT(15) {
		public void execute(CPU cpu,int arg,Mode mode) {
			cpu.halt();
		}
		
	};
	
	int opcode;
	Opcode(int o) {
		opcode = o;
	}
	public int getOpcode() {
		return opcode;
	}
	public abstract void execute(CPU cpu,int arg,Mode mode);
	static Opcode[] opArray = {NOP,LOD,STO,ADD,SUB,MUL,DIV,AND,NOT,CMPL,CMPZ,JUMP,JMPZ,null,null,HALT};
	
	protected void checkNullMode(Mode mode) {
		if (mode != null) 
			throw new IllegalArgumentException("Illegal Mode in " + this.toString() + " instruction - mode should be null");
	}
	
	protected void checkNonNullMode(Mode mode) {
		if (mode == null) 
			throw new IllegalArgumentException("Illegal Mode in " + this.toString() + " instruction - mode may not be null");
	}
	
	protected void checkNonImmediateMode(Mode mode) {
		if (mode == Mode.IMMEDIATE) 
			throw new IllegalArgumentException("Illegal Mode in " + this.toString() + " instruction - mode may not be immediate");
	}
	
	static private int getArgValue(Mode mode,int arg,CPU cpu) {
		// Perform indirection 
	    while(mode != null && mode != Mode.IMMEDIATE) {
			arg = cpu.getData(arg);
			mode = mode.next();
		}
	    return arg;
	}
	
	static private int getIndirectArgValue(Mode mode,int arg,CPU cpu) {
		// Perform indirection 
	    while(mode != null && mode != Mode.IMMEDIATE && mode != Mode.DIRECT) {
			arg = cpu.getData(arg);
			mode = mode.next();
		}
	    return arg;
	}
	
	private static final Set<Integer> NO_ARG_OPS = Set.of(0, 8, 15);
	private static final Set<Integer> NO_IMM_OPS = Set.of(2,9,10);
	private static final Set<Integer> ABSOLUTE_OPS = Set.of(11,12);

	
	
	static public String mnemonic(int opcode) {
		return Opcode.opArray[opcode].toString();
	}
	
	static public int opcode(String mnemonic) {
		return Opcode.valueOf(mnemonic).getOpcode();
	}
	
	static public boolean isNoArgOp(int opcode) {
		return NO_ARG_OPS.contains(opcode);
	}
	
	static public boolean isNoImmedOp(int opcode) {
		return NO_IMM_OPS.contains(opcode);
	}
	
	static public boolean isAbsoluteOp(int opcode) {
		return ABSOLUTE_OPS.contains(opcode);
	}
	
	static public boolean isValidMnemonic(String mnemonic) {
		boolean valid=true;
		try { Opcode.valueOf(mnemonic); } catch (IllegalArgumentException e) { valid=false; }
		return valid;
	}
}