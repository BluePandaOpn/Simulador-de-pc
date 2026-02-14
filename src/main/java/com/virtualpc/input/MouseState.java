package com.virtualpc.input;

public final class MouseState {
    private final int maxX;
    private final int maxY;

    private int x;
    private int y;
    private boolean pressed;
    private boolean clicked;

    public MouseState(int maxX, int maxY) {
        this.maxX = Math.max(0, maxX);
        this.maxY = Math.max(0, maxY);
    }

    public void updatePosition(int x, int y) {
        this.x = clamp(x, 0, maxX);
        this.y = clamp(y, 0, maxY);
    }

    public void onPressed() {
        pressed = true;
        clicked = true;
    }

    public void onReleased() {
        pressed = false;
    }

    public boolean pollClick() {
        boolean result = clicked;
        clicked = false;
        return result;
    }

    public boolean isPressed() {
        return pressed;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}