package com.virtualpc.os.fs;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class VirtualFileSystem {
    private final Path root;

    public VirtualFileSystem(Path root) throws IOException {
        this.root = root.toAbsolutePath().normalize();
        Files.createDirectories(this.root);
        seedDefaultContent();
    }

    public Path root() {
        return root;
    }

    public List<Entry> list(Path directory) throws IOException {
        Path dir = sanitize(directory);
        List<Entry> entries = new ArrayList<>();

        try (var stream = Files.list(dir)) {
            stream.sorted(Comparator.comparing(path -> path.getFileName().toString().toUpperCase()))
                    .forEach(path -> entries.add(new Entry(path.getFileName().toString(), Files.isDirectory(path))));
        }

        return entries;
    }

    public Path mkdir(Path directory, String name) throws IOException {
        String clean = sanitizeName(name);
        Path created = sanitize(directory).resolve(clean).normalize();
        enforceInsideRoot(created);
        Files.createDirectories(created);
        return created;
    }

    public Path touch(Path directory, String name) throws IOException {
        String clean = sanitizeName(name);
        Path file = sanitize(directory).resolve(clean).normalize();
        enforceInsideRoot(file);
        if (!Files.exists(file)) {
            Files.writeString(file, "", StandardCharsets.UTF_8);
        }
        return file;
    }

    public String readFile(Path file) throws IOException {
        Path f = sanitize(file);
        if (Files.isDirectory(f)) {
            throw new IOException("Is a directory");
        }
        return Files.readString(f, StandardCharsets.UTF_8);
    }

    public void writeFile(Path file, String content) throws IOException {
        Path f = sanitize(file);
        if (Files.isDirectory(f)) {
            throw new IOException("Is a directory");
        }
        Files.writeString(f, content, StandardCharsets.UTF_8);
    }

    public Path resolve(Path base, String target) {
        Path resolved;
        if (target == null || target.isBlank()) {
            resolved = sanitize(base);
        } else {
            resolved = sanitize(base).resolve(target.trim()).normalize();
        }
        enforceInsideRoot(resolved);
        return resolved;
    }

    public String displayPath(Path path) {
        Path normalized = sanitize(path);
        Path relative = root.relativize(normalized);
        String p = relative.toString().replace('\\', '/');
        return p.isEmpty() ? "/" : "/" + p;
    }

    private Path sanitize(Path path) {
        Path normalized = path.toAbsolutePath().normalize();
        enforceInsideRoot(normalized);
        return normalized;
    }

    private void enforceInsideRoot(Path path) {
        if (!path.startsWith(root)) {
            throw new IllegalArgumentException("Path escapes virtual root");
        }
    }

    private static String sanitizeName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name is required");
        }
        String clean = name.trim().replace("/", "").replace("\\", "");
        if (clean.isEmpty() || clean.equals(".") || clean.equals("..")) {
            throw new IllegalArgumentException("Invalid name");
        }
        return clean;
    }

    private void seedDefaultContent() throws IOException {
        Path desktop = root.resolve("Desktop");
        Path documents = root.resolve("Documents");
        Files.createDirectories(desktop);
        Files.createDirectories(documents);

        Path readme = desktop.resolve("WELCOME.TXT");
        if (!Files.exists(readme)) {
            Files.writeString(readme,
                    "Welcome to VOS Desktop.\nUse terminal commands: help, ls, cd, mkdir, touch, cat, clear.\n",
                    StandardCharsets.UTF_8);
        }
    }

    public record Entry(String name, boolean directory) {
    }
}