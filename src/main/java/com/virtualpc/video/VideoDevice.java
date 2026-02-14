package com.virtualpc.video;

import com.virtualpc.memory.Ram;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public final class VideoDevice {
    private final Ram ram;
    private final int vramStart;
    private final int width;
    private final int height;
    private final BufferedImage frame;
    private final int[] pixels;
    private final byte[] previousVram;
    private final int[] palette;

    public VideoDevice(Ram ram, int vramStart, int width, int height) {
        this.ram = ram;
        this.vramStart = vramStart;
        this.width = width;
        this.height = height;
        this.frame = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        this.pixels = ((DataBufferInt) frame.getRaster().getDataBuffer()).getData();
        this.previousVram = new byte[width * height];
        this.palette = buildPalette();
    }

    public boolean renderFrame() {
        boolean changed = false;
        int total = width * height;

        for (int i = 0; i < total; i++) {
            int paletteIndex = ram.readByte(vramStart + i);
            byte rawValue = (byte) (paletteIndex & 0xFF);
            if (previousVram[i] != rawValue) {
                previousVram[i] = rawValue;
                pixels[i] = palette[paletteIndex];
                changed = true;
            }
        }

        return changed;
    }

    public BufferedImage frame() {
        return frame;
    }

    private static int[] buildPalette() {
        int[] colors = new int[256];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = (i << 16) | (i << 8) | i;
        }

        colors[0] = 0x10151F;
        colors[1] = 0x1E2A3A;
        colors[2] = 0x21344A;
        colors[3] = 0x2B3F58;
        colors[4] = 0x6E7C91;
        colors[5] = 0xDDE6F2;
        colors[6] = 0x53E5B0;

        return colors;
    }
}