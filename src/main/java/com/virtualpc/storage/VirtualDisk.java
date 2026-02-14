package com.virtualpc.storage;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;

public final class VirtualDisk implements AutoCloseable {
    private final Path diskPath;
    private final RandomAccessFile diskFile;

    public VirtualDisk(Path diskPath, int sizeBytes) throws IOException {
        this.diskPath = diskPath;
        Files.createDirectories(diskPath.toAbsolutePath().getParent());
        this.diskFile = new RandomAccessFile(diskPath.toFile(), "rw");
        if (diskFile.length() != sizeBytes) {
            diskFile.setLength(sizeBytes);
        }
    }

    public void writeByte(long offset, int value) throws IOException {
        validateOffset(offset);
        diskFile.seek(offset);
        diskFile.write(value & 0xFF);
    }

    public int readByte(long offset) throws IOException {
        validateOffset(offset);
        diskFile.seek(offset);
        return diskFile.readUnsignedByte();
    }

    public Path getDiskPath() {
        return diskPath;
    }

    @Override
    public void close() throws IOException {
        diskFile.close();
    }

    private void validateOffset(long offset) throws IOException {
        if (offset < 0 || offset >= diskFile.length()) {
            throw new IllegalArgumentException("Virtual disk offset out of bounds: " + offset);
        }
    }
}