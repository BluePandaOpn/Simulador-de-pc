package com.virtualpc.memory;

import com.virtualpc.util.ByteUtil;

public final class Ram {
    private final byte[] cells;

    public Ram(int sizeBytes) {
        if (sizeBytes <= 0) {
            throw new IllegalArgumentException("RAM size must be positive");
        }
        this.cells = new byte[sizeBytes];
    }

    public int size() {
        return cells.length;
    }

    public void writeByte(int address, int value) {
        validateAddress(address);
        cells[address] = (byte) (value & 0xFF);
    }

    public int readByte(int address) {
        validateAddress(address);
        return cells[address] & 0xFF;
    }

    public void writeBlock(int startAddress, byte[] data) {
        if (data == null) {
            throw new IllegalArgumentException("Data block cannot be null");
        }
        if (startAddress < 0 || startAddress + data.length > cells.length) {
            throw new IllegalArgumentException("Data block exceeds RAM boundaries");
        }
        System.arraycopy(data, 0, cells, startAddress, data.length);
    }

    public int readWord16(int address) {
        int hi = readByte(address);
        int lo = readByte(address + 1);
        return ByteUtil.toWord16(hi, lo);
    }

    private void validateAddress(int address) {
        if (address < 0 || address >= cells.length) {
            throw new IllegalArgumentException("RAM address out of bounds: 0x" + Integer.toHexString(address));
        }
    }
}