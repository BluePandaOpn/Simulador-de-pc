package com.virtualpc.video;

import com.virtualpc.memory.Ram;

import java.util.HashMap;
import java.util.Map;

public final class FrameBuffer {
    private static final Map<Character, int[]> FONT = buildFont();

    private final Ram ram;
    private final int vramStart;
    private final int width;
    private final int height;
    private final byte[] backBuffer;

    public FrameBuffer(Ram ram, int vramStart, int width, int height) {
        this.ram = ram;
        this.vramStart = vramStart;
        this.width = width;
        this.height = height;
        this.backBuffer = new byte[width * height];
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public void clear(int colorIndex) {
        byte value = (byte) (colorIndex & 0xFF);
        for (int i = 0; i < backBuffer.length; i++) {
            backBuffer[i] = value;
        }
    }

    public void setPixel(int x, int y, int colorIndex) {
        if (x < 0 || y < 0 || x >= width || y >= height) {
            return;
        }
        backBuffer[y * width + x] = (byte) (colorIndex & 0xFF);
    }

    public void fillRect(int x, int y, int w, int h, int colorIndex) {
        int startX = Math.max(0, x);
        int startY = Math.max(0, y);
        int endX = Math.min(width, x + w);
        int endY = Math.min(height, y + h);

        byte value = (byte) (colorIndex & 0xFF);
        for (int py = startY; py < endY; py++) {
            int rowOffset = py * width;
            for (int px = startX; px < endX; px++) {
                backBuffer[rowOffset + px] = value;
            }
        }
    }

    public void drawRect(int x, int y, int w, int h, int colorIndex) {
        for (int px = x; px < x + w; px++) {
            setPixel(px, y, colorIndex);
            setPixel(px, y + h - 1, colorIndex);
        }
        for (int py = y; py < y + h; py++) {
            setPixel(x, py, colorIndex);
            setPixel(x + w - 1, py, colorIndex);
        }
    }

    public void drawText(int x, int y, String text, int colorIndex) {
        if (text == null || text.isEmpty()) {
            return;
        }

        int cursorX = x;
        for (int i = 0; i < text.length(); i++) {
            char c = Character.toUpperCase(text.charAt(i));
            drawChar(cursorX, y, c, colorIndex);
            cursorX += 6;
        }
    }

    public void present() {
        ram.writeBlock(vramStart, backBuffer);
    }

    private void drawChar(int x, int y, char c, int colorIndex) {
        int[] glyph = FONT.getOrDefault(c, FONT.get(' '));
        for (int row = 0; row < glyph.length; row++) {
            int rowMask = glyph[row];
            for (int col = 0; col < 5; col++) {
                if ((rowMask & (1 << (4 - col))) != 0) {
                    setPixel(x + col, y + row, colorIndex);
                }
            }
        }
    }

    private static Map<Character, int[]> buildFont() {
        Map<Character, int[]> map = new HashMap<>();
        map.put(' ', new int[]{0b00000, 0b00000, 0b00000, 0b00000, 0b00000, 0b00000, 0b00000});
        map.put(':', new int[]{0b00000, 0b00100, 0b00000, 0b00000, 0b00100, 0b00000, 0b00000});
        map.put('.', new int[]{0b00000, 0b00000, 0b00000, 0b00000, 0b00000, 0b00100, 0b00000});
        map.put('%', new int[]{0b11001, 0b11010, 0b00100, 0b01011, 0b10011, 0b00000, 0b00000});
        map.put('-', new int[]{0b00000, 0b00000, 0b00000, 0b11111, 0b00000, 0b00000, 0b00000});
        map.put('/', new int[]{0b00001, 0b00010, 0b00100, 0b01000, 0b10000, 0b00000, 0b00000});

        map.put('0', new int[]{0b01110, 0b10001, 0b10011, 0b10101, 0b11001, 0b10001, 0b01110});
        map.put('1', new int[]{0b00100, 0b01100, 0b00100, 0b00100, 0b00100, 0b00100, 0b01110});
        map.put('2', new int[]{0b01110, 0b10001, 0b00001, 0b00010, 0b00100, 0b01000, 0b11111});
        map.put('3', new int[]{0b11110, 0b00001, 0b00001, 0b01110, 0b00001, 0b00001, 0b11110});
        map.put('4', new int[]{0b00010, 0b00110, 0b01010, 0b10010, 0b11111, 0b00010, 0b00010});
        map.put('5', new int[]{0b11111, 0b10000, 0b11110, 0b00001, 0b00001, 0b10001, 0b01110});
        map.put('6', new int[]{0b00110, 0b01000, 0b10000, 0b11110, 0b10001, 0b10001, 0b01110});
        map.put('7', new int[]{0b11111, 0b00001, 0b00010, 0b00100, 0b01000, 0b01000, 0b01000});
        map.put('8', new int[]{0b01110, 0b10001, 0b10001, 0b01110, 0b10001, 0b10001, 0b01110});
        map.put('9', new int[]{0b01110, 0b10001, 0b10001, 0b01111, 0b00001, 0b00010, 0b11100});

        map.put('A', new int[]{0b01110, 0b10001, 0b10001, 0b11111, 0b10001, 0b10001, 0b10001});
        map.put('B', new int[]{0b11110, 0b10001, 0b10001, 0b11110, 0b10001, 0b10001, 0b11110});
        map.put('C', new int[]{0b01110, 0b10001, 0b10000, 0b10000, 0b10000, 0b10001, 0b01110});
        map.put('D', new int[]{0b11100, 0b10010, 0b10001, 0b10001, 0b10001, 0b10010, 0b11100});
        map.put('E', new int[]{0b11111, 0b10000, 0b10000, 0b11110, 0b10000, 0b10000, 0b11111});
        map.put('F', new int[]{0b11111, 0b10000, 0b10000, 0b11110, 0b10000, 0b10000, 0b10000});
        map.put('G', new int[]{0b01110, 0b10001, 0b10000, 0b10111, 0b10001, 0b10001, 0b01110});
        map.put('H', new int[]{0b10001, 0b10001, 0b10001, 0b11111, 0b10001, 0b10001, 0b10001});
        map.put('I', new int[]{0b01110, 0b00100, 0b00100, 0b00100, 0b00100, 0b00100, 0b01110});
        map.put('J', new int[]{0b00001, 0b00001, 0b00001, 0b00001, 0b10001, 0b10001, 0b01110});
        map.put('K', new int[]{0b10001, 0b10010, 0b10100, 0b11000, 0b10100, 0b10010, 0b10001});
        map.put('L', new int[]{0b10000, 0b10000, 0b10000, 0b10000, 0b10000, 0b10000, 0b11111});
        map.put('M', new int[]{0b10001, 0b11011, 0b10101, 0b10101, 0b10001, 0b10001, 0b10001});
        map.put('N', new int[]{0b10001, 0b10001, 0b11001, 0b10101, 0b10011, 0b10001, 0b10001});
        map.put('O', new int[]{0b01110, 0b10001, 0b10001, 0b10001, 0b10001, 0b10001, 0b01110});
        map.put('P', new int[]{0b11110, 0b10001, 0b10001, 0b11110, 0b10000, 0b10000, 0b10000});
        map.put('Q', new int[]{0b01110, 0b10001, 0b10001, 0b10001, 0b10101, 0b10010, 0b01101});
        map.put('R', new int[]{0b11110, 0b10001, 0b10001, 0b11110, 0b10100, 0b10010, 0b10001});
        map.put('S', new int[]{0b01111, 0b10000, 0b10000, 0b01110, 0b00001, 0b00001, 0b11110});
        map.put('T', new int[]{0b11111, 0b00100, 0b00100, 0b00100, 0b00100, 0b00100, 0b00100});
        map.put('U', new int[]{0b10001, 0b10001, 0b10001, 0b10001, 0b10001, 0b10001, 0b01110});
        map.put('V', new int[]{0b10001, 0b10001, 0b10001, 0b10001, 0b10001, 0b01010, 0b00100});
        map.put('W', new int[]{0b10001, 0b10001, 0b10001, 0b10101, 0b10101, 0b10101, 0b01010});
        map.put('X', new int[]{0b10001, 0b10001, 0b01010, 0b00100, 0b01010, 0b10001, 0b10001});
        map.put('Y', new int[]{0b10001, 0b10001, 0b01010, 0b00100, 0b00100, 0b00100, 0b00100});
        map.put('Z', new int[]{0b11111, 0b00001, 0b00010, 0b00100, 0b01000, 0b10000, 0b11111});

        return map;
    }
}