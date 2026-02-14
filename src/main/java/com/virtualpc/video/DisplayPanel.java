package com.virtualpc.video;

import com.virtualpc.input.KeyboardState;
import com.virtualpc.input.MouseState;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class DisplayPanel extends JPanel {
    private final VideoDevice videoDevice;
    private final MouseState mouseState;
    private final KeyboardState keyboardState;
    private final int width;
    private final int height;
    private final int scale;

    public DisplayPanel(VideoDevice videoDevice, MouseState mouseState, KeyboardState keyboardState,
                        int width, int height, int scale) {
        this.videoDevice = videoDevice;
        this.mouseState = mouseState;
        this.keyboardState = keyboardState;
        this.width = width;
        this.height = height;
        this.scale = scale;
        setDoubleBuffered(true);
        setFocusable(true);
        setPreferredSize(new Dimension(width * scale, height * scale));
        wireMouse();
        wireKeyboard();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        int[] view = computeViewport();
        g2.drawImage(videoDevice.frame(), view[0], view[1], view[2], view[3], null);
    }

    private void wireMouse() {
        MouseAdapter adapter = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                updateMousePosition(e);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                updateMousePosition(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
                updateMousePosition(e);
                mouseState.onPressed();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                updateMousePosition(e);
                mouseState.onReleased();
            }
        };

        addMouseListener(adapter);
        addMouseMotionListener(adapter);
    }

    private void wireKeyboard() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                keyboardState.onChar(e.getKeyChar());
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    keyboardState.onEnter();
                } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    keyboardState.onBackspace();
                }
            }
        });
    }

    private void updateMousePosition(MouseEvent e) {
        int[] view = computeViewport();
        int drawX = view[0];
        int drawY = view[1];
        int drawW = Math.max(1, view[2]);
        int drawH = Math.max(1, view[3]);

        int localX = ((e.getX() - drawX) * width) / drawW;
        int localY = ((e.getY() - drawY) * height) / drawH;

        int x = Math.max(0, Math.min(width - 1, localX));
        int y = Math.max(0, Math.min(height - 1, localY));
        mouseState.updatePosition(x, y);
    }

    private int[] computeViewport() {
        int panelW = Math.max(1, getWidth());
        int panelH = Math.max(1, getHeight());
        int baseScale = Math.max(1, scale);
        int fitScale = Math.max(1, Math.min(panelW / width, panelH / height));
        int usedScale = Math.max(baseScale, fitScale);

        int drawW = width * usedScale;
        int drawH = height * usedScale;
        if (drawW > panelW || drawH > panelH) {
            usedScale = fitScale;
            drawW = width * usedScale;
            drawH = height * usedScale;
        }

        int offsetX = (panelW - drawW) / 2;
        int offsetY = (panelH - drawH) / 2;
        return new int[]{offsetX, offsetY, drawW, drawH};
    }
}
