package com.virtualpc.programs;

import com.virtualpc.cpu.InstructionSet;

import java.io.ByteArrayOutputStream;

public final class DemoProgram {
    private DemoProgram() {
    }

    public static byte[] build() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // Tiny busy loop to keep CPU active while the Java OS draws the desktop.
        // 0x0000: LDA #00
        out.write(InstructionSet.LDA_IMM);
        out.write(0x00);
        // 0x0002: ADD #01
        out.write(InstructionSet.ADD_IMM);
        out.write(0x01);
        // 0x0004: JMP 0x0002
        out.write(InstructionSet.JMP);
        out.write(0x00);
        out.write(0x02);

        return out.toByteArray();
    }
}
