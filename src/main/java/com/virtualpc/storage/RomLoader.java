package com.virtualpc.storage;

import com.virtualpc.memory.Ram;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class RomLoader {
    private RomLoader() {
    }

    public static void loadIntoRam(Path romPath, Ram ram, int startAddress) throws IOException {
        byte[] romBytes = Files.readAllBytes(romPath);
        ram.writeBlock(startAddress, romBytes);
    }
}