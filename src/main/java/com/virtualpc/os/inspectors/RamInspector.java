package com.virtualpc.os.inspectors;

import com.virtualpc.memory.Ram;

public final class RamInspector {
    private RamInspector() {
    }

    public static String[] dumpLines(Ram ram, int startAddress, int lineCount, int bytesPerLine) {
        String[] lines = new String[lineCount];
        int address = startAddress;

        for (int line = 0; line < lineCount; line++) {
            StringBuilder sb = new StringBuilder();
            sb.append(hex16(address)).append(": ");
            for (int i = 0; i < bytesPerLine; i++) {
                int current = (address + i) % ram.size();
                sb.append(hex8(ram.readByte(current)));
                if (i < bytesPerLine - 1) {
                    sb.append(' ');
                }
            }
            lines[line] = sb.toString();
            address = (address + bytesPerLine) % ram.size();
        }

        return lines;
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
}