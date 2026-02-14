package com.virtualpc.core;

import com.virtualpc.config.VmConfig;
import com.virtualpc.cpu.Cpu;
import com.virtualpc.input.KeyboardState;
import com.virtualpc.input.MouseState;
import com.virtualpc.memory.Ram;
import com.virtualpc.os.VirtualOperatingSystem;
import com.virtualpc.storage.VirtualDisk;
import com.virtualpc.video.DisplayPanel;
import com.virtualpc.video.FrameBuffer;
import com.virtualpc.video.VideoDevice;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Path;

public final class VirtualMachine {
    private final VmConfig config;
    private final Ram ram;
    private final Cpu cpu;
    private final VideoDevice videoDevice;
    private final VirtualDisk virtualDisk;
    private final VirtualOperatingSystem operatingSystem;
    private final MouseState mouseState;
    private final KeyboardState keyboardState;

    public VirtualMachine(VmConfig config) throws IOException {
        this.config = config;
        this.ram = new Ram(config.ramSizeBytes());
        this.cpu = new Cpu(ram, config.romLoadAddress());
        this.videoDevice = new VideoDevice(ram, config.vramStartAddress(), config.displayWidth(), config.displayHeight());
        this.virtualDisk = new VirtualDisk(Path.of(config.virtualDiskPath()), config.virtualDiskSizeBytes());
        this.mouseState = new MouseState(config.displayWidth() - 1, config.displayHeight() - 1);
        this.keyboardState = new KeyboardState();

        FrameBuffer frameBuffer = new FrameBuffer(ram, config.vramStartAddress(), config.displayWidth(), config.displayHeight());
        this.operatingSystem = new VirtualOperatingSystem(frameBuffer, ram, config.virtualDiskSizeBytes(), config.romLoadAddress());
    }

    public Ram getRam() {
        return ram;
    }

    public void setRomImage(byte[] romImage) {
        operatingSystem.setRomImage(romImage);
    }

    public void start() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Virtual PC Simulator");
            DisplayPanel panel = new DisplayPanel(videoDevice, mouseState, keyboardState,
                    config.displayWidth(), config.displayHeight(), config.pixelScale());

            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setResizable(true);
            frame.setContentPane(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            panel.requestFocusInWindow();

            Timer timer = new Timer(16, event -> {
                for (int i = 0; i < config.cyclesPerFrame() && !cpu.isHalted(); i++) {
                    cpu.step();
                }

                operatingSystem.tick(cpu, mouseState, keyboardState);
                boolean frameChanged = videoDevice.renderFrame();
                if (frameChanged) {
                    panel.repaint();
                }
            });
            timer.start();

            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    timer.stop();
                    try {
                        virtualDisk.close();
                    } catch (IOException ignored) {
                    }
                }
            });
        });
    }
}
