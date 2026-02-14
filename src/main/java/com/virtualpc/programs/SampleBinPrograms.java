package com.virtualpc.programs;

import com.virtualpc.cpu.InstructionSet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class SampleBinPrograms {
    private SampleBinPrograms() {
    }

    public static void ensureDefaultPrograms(Path binDirectory) throws IOException {
        Files.createDirectories(binDirectory);
        writeIfMissing(binDirectory.resolve("counter.bin"), buildCounterProgram());
        writeIfMissing(binDirectory.resolve("vram-fill.bin"), buildVramFillProgram());
        writeIfMissing(binDirectory.resolve("pulse.bin"), buildPulseProgram());
    }

    private static void writeIfMissing(Path path, byte[] content) throws IOException {
        if (!Files.exists(path)) {
            Files.write(path, content);
        }
    }

    private static byte[] buildCounterProgram() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // A = 0
        out.write(InstructionSet.LDA_IMM);
        out.write(0x00);
        // Loop start at 0x0002
        out.write(InstructionSet.ADD_IMM);
        out.write(0x01);
        out.write(InstructionSet.STA);
        out.write(0x90);
        out.write(0x00);
        out.write(InstructionSet.JMP);
        out.write(0x00);
        out.write(0x02);

        return out.toByteArray();
    }

    private static byte[] buildVramFillProgram() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // Fill first 256 VRAM bytes with white and halt.
        for (int i = 0; i < 256; i++) {
            int address = 0x8000 + i;
            out.write(InstructionSet.LDA_IMM);
            out.write(0xE0);
            out.write(InstructionSet.STA);
            out.write((address >> 8) & 0xFF);
            out.write(address & 0xFF);
        }
        out.write(InstructionSet.HALT);

        return out.toByteArray();
    }

    private static byte[] buildPulseProgram() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // Increment one pixel forever to generate a visible pulse.
        // 0x0000: LDA 0x8000
        out.write(InstructionSet.LDA_MEM);
        out.write(0x80);
        out.write(0x00);
        // 0x0003: ADD #0x11
        out.write(InstructionSet.ADD_IMM);
        out.write(0x11);
        // 0x0005: STA 0x8000
        out.write(InstructionSet.STA);
        out.write(0x80);
        out.write(0x00);
        // 0x0008: JMP 0x0000
        out.write(InstructionSet.JMP);
        out.write(0x00);
        out.write(0x00);

        return out.toByteArray();
    }
}