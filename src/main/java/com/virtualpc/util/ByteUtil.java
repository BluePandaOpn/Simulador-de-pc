package com.virtualpc.util;

public final class ByteUtil {
    private ByteUtil() {
    }

    public static int toWord16(int highByte, int lowByte) {
        return ((highByte & 0xFF) << 8) | (lowByte & 0xFF);
    }

    public static String hex8(int value) {
        return String.format("0x%02X", value & 0xFF);
    }

    public static String hex16(int value) {
        return String.format("0x%04X", value & 0xFFFF);
    }
}