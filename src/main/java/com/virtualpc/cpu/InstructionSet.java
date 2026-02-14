package com.virtualpc.cpu;

public final class InstructionSet {
    private InstructionSet() {
    }

    public static final int NOP = 0x00;
    public static final int LDA_IMM = 0x01;
    public static final int STA = 0x02;
    public static final int LDA_MEM = 0x03;
    public static final int ADD_IMM = 0x04;
    public static final int JMP = 0x05;
    public static final int JZ = 0x06;
    public static final int SUB_IMM = 0x07;
    public static final int HALT = 0xFF;
}