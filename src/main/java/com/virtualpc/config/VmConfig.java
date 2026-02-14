package com.virtualpc.config;

public record VmConfig(
        int ramSizeBytes,
        int romLoadAddress,
        int vramStartAddress,
        int displayWidth,
        int displayHeight,
        int pixelScale,
        int cyclesPerFrame,
        String virtualDiskPath,
        int virtualDiskSizeBytes
) {
    public static VmConfig defaultConfig() {
        return new VmConfig(
                65_536,
                0x0000,
                0x8000,
                256,
                128,
                3,
                3_000,
                "data/virtual-disk.bin",
                1_048_576
        );
    }
}
