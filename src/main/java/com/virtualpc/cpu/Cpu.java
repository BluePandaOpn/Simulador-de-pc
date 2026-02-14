package com.virtualpc.cpu;

import com.virtualpc.memory.Ram;
import com.virtualpc.util.ByteUtil;

public final class Cpu {
    private final Ram ram;

    private int programCounter;
    private int registerA;
    private boolean halted;

    public Cpu(Ram ram, int bootAddress) {
        this.ram = ram;
        this.programCounter = bootAddress & 0xFFFF;
    }

    public void step() {
        if (halted) {
            return;
        }

        int opcode = fetchByte();
        switch (opcode) {
            case InstructionSet.NOP -> {
            }
            case InstructionSet.LDA_IMM -> registerA = fetchByte();
            case InstructionSet.STA -> {
                int address = fetchWord();
                ram.writeByte(address, registerA);
            }
            case InstructionSet.LDA_MEM -> {
                int address = fetchWord();
                registerA = ram.readByte(address);
            }
            case InstructionSet.ADD_IMM -> registerA = (registerA + fetchByte()) & 0xFF;
            case InstructionSet.SUB_IMM -> registerA = (registerA - fetchByte()) & 0xFF;
            case InstructionSet.JMP -> programCounter = fetchWord();
            case InstructionSet.JZ -> {
                int address = fetchWord();
                if (registerA == 0) {
                    programCounter = address;
                }
            }
            case InstructionSet.HALT -> halted = true;
            default -> throw new IllegalStateException("Unknown opcode " + ByteUtil.hex8(opcode) +
                    " at PC=" + ByteUtil.hex16((programCounter - 1) & 0xFFFF));
        }
    }

    public boolean isHalted() {
        return halted;
    }

    public int getProgramCounter() {
        return programCounter;
    }

    public int getRegisterA() {
        return registerA;
    }

    public void reboot(int bootAddress) {
        this.programCounter = bootAddress & 0xFFFF;
        this.registerA = 0;
        this.halted = false;
    }

    private int fetchByte() {
        int value = ram.readByte(programCounter);
        programCounter = (programCounter + 1) & 0xFFFF;
        return value;
    }

    private int fetchWord() {
        int high = fetchByte();
        int low = fetchByte();
        return ByteUtil.toWord16(high, low);
    }
}
