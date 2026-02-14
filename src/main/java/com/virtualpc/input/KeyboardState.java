package com.virtualpc.input;

import java.util.ArrayDeque;
import java.util.Queue;

public final class KeyboardState {
    private final Queue<Character> typedChars = new ArrayDeque<>();
    private boolean enterPressed;
    private boolean backspacePressed;

    public synchronized void onChar(char c) {
        if (c >= 32 && c <= 126) {
            typedChars.add(c);
        }
    }

    public synchronized void onEnter() {
        enterPressed = true;
    }

    public synchronized void onBackspace() {
        backspacePressed = true;
    }

    public synchronized Character pollChar() {
        return typedChars.poll();
    }

    public synchronized boolean pollEnter() {
        boolean v = enterPressed;
        enterPressed = false;
        return v;
    }

    public synchronized boolean pollBackspace() {
        boolean v = backspacePressed;
        backspacePressed = false;
        return v;
    }
}