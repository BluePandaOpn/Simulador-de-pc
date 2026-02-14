package com.virtualpc.os;

import com.virtualpc.cpu.Cpu;
import com.virtualpc.input.KeyboardState;
import com.virtualpc.input.MouseState;
import com.virtualpc.memory.Ram;
import com.virtualpc.os.fs.VirtualFileSystem;
import com.virtualpc.video.FrameBuffer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class VirtualOperatingSystem {
    private static final int C_BG = 1;
    private static final int C_PANEL = 2;
    private static final int C_WINDOW = 3;
    private static final int C_WINDOW_BORDER = 4;
    private static final int C_TEXT = 5;
    private static final int C_ACCENT = 6;

    private final FrameBuffer frameBuffer;
    private final Ram ram;
    private final int width;
    private final int height;
    private final int diskSizeBytes;
    private final int romBaseAddress;
    private final VirtualFileSystem vfs;
    private final Path binDirectory;

    private long frameCounter;
    private byte[] romImage = new byte[0];
    private UiMode mode = UiMode.LEGACY;
    private Cpu runtimeCpu;

    private Path currentDir;
    private List<VirtualFileSystem.Entry> entries = new ArrayList<>();
    private final List<String> terminalLines = new ArrayList<>();
    private String terminalInput = "";
    private boolean terminalFocused = true;
    private int folderCounter = 1;
    private String activeProgram = "rom";

    public VirtualOperatingSystem(FrameBuffer frameBuffer, Ram ram, int diskSizeBytes, int romBaseAddress) throws IOException {
        this.frameBuffer = frameBuffer;
        this.ram = ram;
        this.width = frameBuffer.width();
        this.height = frameBuffer.height();
        this.diskSizeBytes = diskSizeBytes;
        this.romBaseAddress = romBaseAddress;
        this.vfs = new VirtualFileSystem(Path.of("data", "vfs"));
        this.binDirectory = Path.of("data", "bin-programs").toAbsolutePath().normalize();
        Files.createDirectories(binDirectory);
        this.currentDir = vfs.root();
        refreshEntries();
        terminalLines.add("VTERM READY. TYPE HELP");
        terminalLines.add("BIN DIR /data/bin-programs");
    }

    public void setRomImage(byte[] romImage) {
        this.romImage = romImage == null ? new byte[0] : romImage.clone();
    }

    public void tick(Cpu cpu, MouseState mouse, KeyboardState keyboard) {
        frameCounter++;
        runtimeCpu = cpu;

        if (mouse.pollClick()) {
            handleClick(mouse.x(), mouse.y());
        }
        if (mode == UiMode.DESKTOP) {
            handleKeyboard(keyboard);
        }

        drawBackground();
        if (mode == UiMode.LEGACY) {
            drawLegacyScreen(cpu);
        } else {
            drawDesktop(cpu);
        }

        drawMouseCursor(mouse.x(), mouse.y(), mouse.isPressed());
        frameBuffer.present();
    }

    private void handleClick(int mx, int my) {
        if (mode == UiMode.LEGACY) {
            if (hit(mx, my, width / 2 - 36, height / 2 - 6, 72, 14)) {
                mode = UiMode.DESKTOP;
                terminalLines.add("BOOTING DESKTOP OS...");
            }
            return;
        }

        DesktopLayout layout = computeDesktopLayout();
        int expX = layout.explorerX;
        int expY = layout.windowY;
        int expW = layout.explorerW;
        int expH = layout.windowH;
        int termX = layout.terminalX;
        int termY = layout.windowY;
        int termW = layout.terminalW;
        int termH = layout.windowH;

        if (hit(mx, my, expX + expW - 38, expY + 2, 16, 8)) {
            createFolderQuick();
            return;
        }
        if (hit(mx, my, expX + expW - 20, expY + 2, 16, 8)) {
            goParent();
            return;
        }

        if (hit(mx, my, expX + 2, expY + 20, expW - 4, expH - 22)) {
            int index = (my - (expY + 20)) / 8;
            if (index >= 0 && index < entries.size()) {
                openEntry(entries.get(index));
            }
            terminalFocused = false;
            return;
        }

        if (hit(mx, my, termX, termY, termW, termH)) {
            terminalFocused = true;
        }
    }

    private void handleKeyboard(KeyboardState keyboard) {
        if (!terminalFocused) {
            return;
        }

        Character ch;
        while ((ch = keyboard.pollChar()) != null) {
            if (terminalInput.length() < 70) {
                terminalInput += ch;
            }
        }

        if (keyboard.pollBackspace() && !terminalInput.isEmpty()) {
            terminalInput = terminalInput.substring(0, terminalInput.length() - 1);
        }

        if (keyboard.pollEnter()) {
            executeCommand(terminalInput.trim());
            terminalInput = "";
        }
    }

    private void executeCommand(String command) {
        if (command.isEmpty()) {
            return;
        }

        appendTerminal("CMD " + command);

        String[] parts = command.split("\\s+");
        String op = parts[0].toLowerCase();

        try {
            switch (op) {
                case "help" -> {
                    appendTerminal("help ls pwd cd mkdir touch");
                    appendTerminal("cat clear binls runbin");
                }
                case "ls" -> {
                    refreshEntries();
                    if (entries.isEmpty()) {
                        appendTerminal("EMPTY");
                    } else {
                        for (VirtualFileSystem.Entry entry : entries) {
                            appendTerminal((entry.directory() ? "DIR " : "FIL ") + entry.name());
                        }
                    }
                }
                case "pwd" -> appendTerminal(vfs.displayPath(currentDir));
                case "cd" -> {
                    if (parts.length < 2) {
                        appendTerminal("USAGE: cd NAME");
                    } else {
                        Path target = vfs.resolve(currentDir, parts[1]);
                        if (Files.isDirectory(target)) {
                            currentDir = target;
                            refreshEntries();
                            appendTerminal("OK " + vfs.displayPath(currentDir));
                        } else {
                            appendTerminal("NOT A DIR");
                        }
                    }
                }
                case "mkdir" -> {
                    if (parts.length < 2) {
                        appendTerminal("USAGE: mkdir NAME");
                    } else {
                        vfs.mkdir(currentDir, parts[1]);
                        refreshEntries();
                        appendTerminal("DIR CREATED");
                    }
                }
                case "touch" -> {
                    if (parts.length < 2) {
                        appendTerminal("USAGE: touch NAME");
                    } else {
                        vfs.touch(currentDir, parts[1]);
                        refreshEntries();
                        appendTerminal("FILE CREATED");
                    }
                }
                case "cat" -> {
                    if (parts.length < 2) {
                        appendTerminal("USAGE: cat NAME");
                    } else {
                        Path file = vfs.resolve(currentDir, parts[1]);
                        String text = vfs.readFile(file);
                        appendTerminal(text.isBlank() ? "EMPTY FILE" : text.replace('\n', ' '));
                    }
                }
                case "binls" -> listBinPrograms();
                case "runbin" -> {
                    if (parts.length < 2) {
                        appendTerminal("USAGE: runbin NAME.bin");
                    } else {
                        runBinProgram(parts[1]);
                    }
                }
                case "clear" -> terminalLines.clear();
                default -> appendTerminal("UNKNOWN CMD");
            }
        } catch (Exception ex) {
            appendTerminal("ERROR " + ex.getMessage());
        }
    }

    private void listBinPrograms() throws IOException {
        appendTerminal("BIN PROGRAMS:");
        try (Stream<Path> stream = Files.list(binDirectory)) {
            List<Path> bins = stream
                    .filter(path -> Files.isRegularFile(path) && path.getFileName().toString().toLowerCase().endsWith(".bin"))
                    .sorted()
                    .toList();
            if (bins.isEmpty()) {
                appendTerminal("NO .BIN FILES");
                return;
            }
            for (Path bin : bins) {
                appendTerminal(bin.getFileName().toString());
            }
        }
    }

    private void runBinProgram(String programName) throws IOException {
        if (runtimeCpu == null) {
            appendTerminal("CPU NOT READY");
            return;
        }

        String fileName = programName.toLowerCase().endsWith(".bin") ? programName : programName + ".bin";
        Path path = binDirectory.resolve(fileName).normalize();

        if (!path.startsWith(binDirectory)) {
            appendTerminal("INVALID BIN NAME");
            return;
        }
        if (!Files.exists(path)) {
            appendTerminal("BIN NOT FOUND");
            return;
        }

        byte[] binBytes = Files.readAllBytes(path);
        if (binBytes.length == 0) {
            appendTerminal("BIN EMPTY");
            return;
        }
        if (romBaseAddress + binBytes.length > ram.size()) {
            appendTerminal("BIN TOO LARGE");
            return;
        }

        runProgramBytes(fileName, binBytes);
    }

    private void drawBackground() {
        for (int y = 0; y < height; y++) {
            int tone = (y + (int) (frameCounter % 18)) % 18;
            frameBuffer.fillRect(0, y, width, 1, tone < 9 ? C_BG : C_PANEL);
        }
    }

    private void drawLegacyScreen(Cpu cpu) {
        frameBuffer.fillRect(0, 0, width, 10, C_PANEL);
        frameBuffer.drawRect(0, 0, width, 10, C_WINDOW_BORDER);
        drawTitleBar("VOS CORE");

        frameBuffer.fillRect(8, 16, width - 16, height - 24, C_WINDOW);
        frameBuffer.drawRect(8, 16, width - 16, height - 24, C_WINDOW_BORDER);
        frameBuffer.fillRect(8, 16, width - 16, 10, C_ACCENT);
        frameBuffer.drawText(11, 18, "PRIMARY OS", C_TEXT);

        frameBuffer.drawText(14, 32, "CPU PC " + hex16(cpu.getProgramCounter()), C_TEXT);
        frameBuffer.drawText(14, 40, "CPU A  " + hex8(cpu.getRegisterA()), C_TEXT);
        frameBuffer.drawText(14, 48, "RAM    " + ram.size() + " B", C_TEXT);
        frameBuffer.drawText(14, 56, "DISK   " + (diskSizeBytes / 1024) + " KB", C_TEXT);
        frameBuffer.drawText(14, 64, "ROM    " + romImage.length + " B", C_TEXT);

        int bx = width / 2 - 36;
        int by = height / 2 - 6;
        frameBuffer.fillRect(bx, by, 72, 14, C_ACCENT);
        frameBuffer.drawRect(bx, by, 72, 14, C_WINDOW_BORDER);
        frameBuffer.drawText(bx + 7, by + 4, "BOOT DESKTOP", C_TEXT);
    }

    private void drawDesktop(Cpu cpu) {
        DesktopLayout layout = computeDesktopLayout();

        frameBuffer.fillRect(0, 0, width, 10, C_PANEL);
        frameBuffer.drawRect(0, 0, width, 10, C_WINDOW_BORDER);
        drawTitleBar("DESKTOP OS");

        drawExplorerWindow(layout);
        drawTerminalWindow(layout);

        frameBuffer.fillRect(0, height - 12, width, 12, C_PANEL);
        frameBuffer.drawRect(0, height - 12, width, 12, C_WINDOW_BORDER);
        drawFooter(cpu, layout);

        if (terminalFocused) {
            frameBuffer.drawText(layout.terminalX, height - 10, "TERM FOCUS", C_ACCENT);
        }
    }

    private void drawExplorerWindow(DesktopLayout layout) {
        int x = layout.explorerX;
        int y = layout.windowY;
        int w = layout.explorerW;
        int h = layout.windowH;

        frameBuffer.fillRect(x, y, w, h, C_WINDOW);
        frameBuffer.drawRect(x, y, w, h, C_WINDOW_BORDER);
        frameBuffer.fillRect(x, y, w, 10, C_ACCENT);
        frameBuffer.drawText(x + 2, y + 2, "EXPLORER", C_TEXT);

        frameBuffer.fillRect(x + w - 38, y + 2, 16, 8, C_PANEL);
        frameBuffer.drawRect(x + w - 38, y + 2, 16, 8, C_WINDOW_BORDER);
        frameBuffer.drawText(x + w - 36, y + 3, "N", C_TEXT);

        frameBuffer.fillRect(x + w - 20, y + 2, 16, 8, C_PANEL);
        frameBuffer.drawRect(x + w - 20, y + 2, 16, 8, C_WINDOW_BORDER);
        frameBuffer.drawText(x + w - 18, y + 3, "U", C_TEXT);

        frameBuffer.drawText(x + 2, y + 12, fit(vfs.displayPath(currentDir), Math.max(8, (w - 4) / 6)), C_TEXT);

        int lineY = y + 20;
        int maxLines = Math.min(Math.max(2, (h - 24) / 8), entries.size());
        int cols = Math.max(8, (w - 4) / 6);
        for (int i = 0; i < maxLines; i++) {
            VirtualFileSystem.Entry e = entries.get(i);
            String label = (e.directory() ? "DIR " : "FIL ") + e.name();
            frameBuffer.drawText(x + 2, lineY, fit(label, cols), C_TEXT);
            lineY += 8;
        }
    }

    private void drawTerminalWindow(DesktopLayout layout) {
        int x = layout.terminalX;
        int y = layout.windowY;
        int w = layout.terminalW;
        int h = layout.windowH;
        int textCols = Math.max(10, (w - 4) / 6);

        frameBuffer.fillRect(x, y, w, h, C_WINDOW);
        frameBuffer.drawRect(x, y, w, h, C_WINDOW_BORDER);
        frameBuffer.fillRect(x, y, w, 10, C_ACCENT);
        frameBuffer.drawText(x + 2, y + 2, "TERMINAL", C_TEXT);

        int visible = Math.min(Math.max(3, (h - 24) / 8), terminalLines.size());
        int start = Math.max(0, terminalLines.size() - visible);
        int lineY = y + 14;
        for (int i = start; i < terminalLines.size(); i++) {
            frameBuffer.drawText(x + 2, lineY, fit(terminalLines.get(i), textCols), C_TEXT);
            lineY += 8;
        }

        String inputLine = fit(terminalInput + (frameCounter % 30 < 15 ? "_" : ""), textCols);
        frameBuffer.drawText(x + 2, y + h - 10, inputLine, terminalFocused ? C_ACCENT : C_TEXT);
    }

    private void refreshEntries() throws IOException {
        entries = vfs.list(currentDir);
    }

    private void createFolderQuick() {
        try {
            String name = "NEWF" + folderCounter++;
            vfs.mkdir(currentDir, name);
            refreshEntries();
            appendTerminal("DIR " + name + " CREATED");
        } catch (Exception ex) {
            appendTerminal("MKDIR ERROR");
        }
    }

    private void goParent() {
        try {
            Path parent = currentDir.getParent();
            if (parent != null && parent.startsWith(vfs.root())) {
                currentDir = parent;
                refreshEntries();
            }
        } catch (Exception ex) {
            appendTerminal("UP ERROR");
        }
    }

    private void openEntry(VirtualFileSystem.Entry entry) {
        try {
            Path target = vfs.resolve(currentDir, entry.name());
            if (entry.directory()) {
                currentDir = target;
                refreshEntries();
                appendTerminal("OPEN " + entry.name());
            } else {
                if (entry.name().toLowerCase().endsWith(".bin")) {
                    byte[] binBytes = Files.readAllBytes(target);
                    runProgramBytes(entry.name(), binBytes);
                } else {
                    String text = vfs.readFile(target);
                    appendTerminal("FILE " + entry.name());
                    appendTerminal(text.isBlank() ? "EMPTY" : text.replace('\n', ' '));
                }
            }
        } catch (Exception ex) {
            appendTerminal("OPEN ERROR");
        }
    }

    private void runProgramBytes(String displayName, byte[] binBytes) {
        if (runtimeCpu == null) {
            appendTerminal("CPU NOT READY");
            return;
        }
        if (binBytes.length == 0) {
            appendTerminal("BIN EMPTY");
            return;
        }
        if (romBaseAddress + binBytes.length > ram.size()) {
            appendTerminal("BIN TOO LARGE");
            return;
        }

        ram.writeBlock(romBaseAddress, binBytes);
        romImage = binBytes;
        activeProgram = displayName;
        runtimeCpu.reboot(romBaseAddress);

        appendTerminal("RUNNING " + displayName);
        appendTerminal("SIZE " + binBytes.length + " BYTES");
    }

    private DesktopLayout computeDesktopLayout() {
        int margin = 6;
        int gap = 2;
        int windowY = 14;
        int windowH = Math.max(40, height - 28);
        int contentW = Math.max(120, width - (margin * 2));

        int explorerW = Math.max(72, Math.min(140, contentW / 3));
        int terminalW = contentW - explorerW - gap;
        if (terminalW < 90) {
            int required = 90 - terminalW;
            explorerW = Math.max(60, explorerW - required);
            terminalW = contentW - explorerW - gap;
        }

        int explorerX = margin;
        int terminalX = explorerX + explorerW + gap;
        return new DesktopLayout(windowY, windowH, explorerX, explorerW, terminalX, terminalW);
    }

    private void drawTitleBar(String title) {
        String uptime = "UP " + (frameCounter / 60) + "S";
        int uptimePixels = uptime.length() * 6;
        int rightTextX = Math.max(3, width - uptimePixels - 3);
        int leftPixels = Math.max(6, rightTextX - 9);
        if (leftPixels > 24) {
            frameBuffer.drawText(3, 2, fitToPixels(title, leftPixels), C_TEXT);
        }
        frameBuffer.drawText(rightTextX, 2, uptime, C_TEXT);
    }

    private void drawFooter(Cpu cpu, DesktopLayout layout) {
        int y = height - 10;
        String left = "START CPU " + hex8(cpu.getRegisterA());
        String run = "RUN " + activeProgram;
        String path = vfs.displayPath(currentDir);
        int maxPathPixels = Math.max(30, width / 2);
        String pathShown = fitToPixels(path, maxPathPixels);
        int pathX = Math.max(3, width - (pathShown.length() * 6) - 3);
        int leftMax = Math.max(6, pathX - 6);
        String leftText = fitToPixels(left + " " + run, leftMax);
        frameBuffer.drawText(3, y, leftText, C_TEXT);
        frameBuffer.drawText(pathX, y, pathShown, C_TEXT);

        if (terminalFocused) {
            int focusX = Math.max(layout.terminalX, 3);
            if (focusX + ("TERM".length() * 6) < pathX) {
                frameBuffer.drawText(focusX, y, "TERM", C_ACCENT);
            }
        }
    }

    private void appendTerminal(String line) {
        terminalLines.add(fit(line == null ? "" : line, 70));
        while (terminalLines.size() > 50) {
            terminalLines.remove(0);
        }
    }

    private void drawMouseCursor(int x, int y, boolean pressed) {
        int color = pressed ? C_ACCENT : C_TEXT;
        frameBuffer.setPixel(x, y, color);
        frameBuffer.setPixel(x + 1, y, color);
        frameBuffer.setPixel(x, y + 1, color);
        frameBuffer.setPixel(x + 1, y + 1, color);
        frameBuffer.setPixel(x + 2, y + 2, color);
        frameBuffer.setPixel(x + 3, y + 3, color);
    }

    private static boolean hit(int px, int py, int x, int y, int w, int h) {
        return px >= x && py >= y && px < x + w && py < y + h;
    }

    private static String fit(String text, int maxChars) {
        if (text == null) {
            return "";
        }
        String clean = text.replace('\t', ' ').replace('\r', ' ').replace('\n', ' ');
        if (clean.length() <= maxChars) {
            return clean;
        }
        return clean.substring(0, Math.max(0, maxChars));
    }

    private static String fitToPixels(String text, int pixels) {
        return fit(text, Math.max(1, pixels / 6));
    }

    private static String hex8(int value) {
        int v = value & 0xFF;
        return "" + toHex((v >> 4) & 0xF) + toHex(v & 0xF);
    }

    private static String hex16(int value) {
        int v = value & 0xFFFF;
        return "" + toHex((v >> 12) & 0xF) + toHex((v >> 8) & 0xF) + toHex((v >> 4) & 0xF) + toHex(v & 0xF);
    }

    private static char toHex(int value) {
        return (char) (value < 10 ? ('0' + value) : ('A' + (value - 10)));
    }

    private enum UiMode {
        LEGACY,
        DESKTOP
    }

    private record DesktopLayout(
            int windowY,
            int windowH,
            int explorerX,
            int explorerW,
            int terminalX,
            int terminalW
    ) {
    }
}
