package com.virtualpc.os.inspectors;

import com.virtualpc.cpu.InstructionSet;

import java.util.ArrayList;
import java.util.List;

public final class RomInspector {
    private RomInspector() {
    }

    public static String[] decodeWindow(byte[] rom, int baseAddress, int startInstruction, int lineCount) {
        if (rom == null || rom.length == 0) {
            return new String[]{"NO ROM LOADED"};
        }

        List<String> lines = new ArrayList<>(lineCount);
        int pc = 0;
        int instructionIndex = 0;

        while (pc < rom.length && lines.size() < lineCount) {
            DecodedInstruction ins = decodeAt(rom, pc, baseAddress);
            if (instructionIndex >= startInstruction) {
                lines.add(ins.text);
            }
            pc += ins.size;
            instructionIndex++;
        }

        if (lines.isEmpty()) {
            return new String[]{"END OF ROM"};
        }

        return lines.toArray(new String[0]);
    }

    private static DecodedInstruction decodeAt(byte[] rom, int offset, int baseAddress) {
        int opcode = rom[offset] & 0xFF;
        int addr = (baseAddress + offset) & 0xFFFF;

        return switch (opcode) {
            case InstructionSet.NOP -> new DecodedInstruction(1, hex16(addr) + " NOP");
            case InstructionSet.LDA_IMM -> {
                int arg = readByte(rom, offset + 1);
                yield new DecodedInstruction(2, hex16(addr) + " LDA #" + hex8(arg));
            }
            case InstructionSet.STA -> {
                int target = readWord(rom, offset + 1);
                yield new DecodedInstruction(3, hex16(addr) + " STA " + hex16(target));
            }
            case InstructionSet.LDA_MEM -> {
                int target = readWord(rom, offset + 1);
                yield new DecodedInstruction(3, hex16(addr) + " LDA " + hex16(target));
            }
            case InstructionSet.ADD_IMM -> {
                int arg = readByte(rom, offset + 1);
                yield new DecodedInstruction(2, hex16(addr) + " ADD #" + hex8(arg));
            }
            case InstructionSet.SUB_IMM -> {
                int arg = readByte(rom, offset + 1);
                yield new DecodedInstruction(2, hex16(addr) + " SUB #" + hex8(arg));
            }
            case InstructionSet.JMP -> {
                int target = readWord(rom, offset + 1);
                yield new DecodedInstruction(3, hex16(addr) + " JMP " + hex16(target));
            }
            case InstructionSet.JZ -> {
                int target = readWord(rom, offset + 1);
                yield new DecodedInstruction(3, hex16(addr) + " JZ " + hex16(target));
            }
            case InstructionSet.HALT -> new DecodedInstruction(1, hex16(addr) + " HALT");
            default -> new DecodedInstruction(1, hex16(addr) + " DB " + hex8(opcode));
        };
    }

    private static int readByte(byte[] rom, int offset) {
        if (offset < 0 || offset >= rom.length) {
            return 0;
        }
        return rom[offset] & 0xFF;
    }

    private static int readWord(byte[] rom, int offset) {
        int high = readByte(rom, offset);
        int low = readByte(rom, offset + 1);
        return ((high & 0xFF) << 8) | (low & 0xFF);
    }

    private static String hex8(int value) {
        int v = value & 0xFF;
        return "" + toHex((v >> 4) & 0xF) + toHex(v & 0xF);
    }

    private static String hex16(int value) {
        int v = value & 0xFFFF;
        return "" + toHex((v >> 12) & 0xF) + toHex((v >> 8) & 0xF) + toHex((v >> 4) & 0xF) + toHex(v & 0xF);
    }

    private static char toHex(int value) {
        return (char) (value < 10 ? ('0' + value) : ('A' + (value - 10)));
    }

    private record DecodedInstruction(int size, String text) {
    }
}