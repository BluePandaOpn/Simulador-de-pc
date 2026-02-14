# Research and Reference Notes

This document records conceptual references used while designing the emulator.

## 1. CPU and Computer Architecture Foundations

### Von Neumann architecture

Relevance:
- Shared memory for code/data, fetch/decode/execute model.

Applied in project:
- Single RAM array (`Ram`) stores executable bytes and data.
- CPU fetches instructions directly from RAM via `PC`.

### Basic accumulator machine model

Relevance:
- Small ISA with accumulator register is ideal for educational emulators.

Applied in project:
- `A` register and simple arithmetic/control opcodes.

## 2. Memory-Mapped Graphics Patterns

Reference family:
- Early 8-bit systems and console-era framebuffers.

Applied in project:
- VRAM region in RAM (`vramStartAddress`).
- UI rendered by writing bytes to VRAM and converting to pixels.

## 3. Emulator Implementation Practices

Typical emulator patterns:
- Fixed update loop
- Decoupled render stage
- Host/guest separation
- Strict bounds checks for memory safety

Applied in project:
- Timer-driven VM tick.
- CPU cycle budget per frame.
- `Ram` address validation.

## 4. Virtual Filesystem Design Influence

Reference concept:
- Sandboxed path root to avoid path traversal and host escape.

Applied in project:
- `VirtualFileSystem` enforces operations under `data/vfs` root.

## 5. UI and Tooling Influence

Reference concept:
- Retro low-resolution desktop emulation with text-grid clipping.

Applied in project:
- Pixel font rendering in `FrameBuffer`.
- Adaptive panel widths and clipped text to prevent overlap.

## 6. Scope Boundaries

This project intentionally does not implement:
- Real host kernel APIs inside guest
- Direct host memory mapping
- Full Linux or Windows compatibility layers

It focuses on:
- Educational VM architecture
- Controlled binary execution
- Virtual desktop interaction model

## 7. Suggested External Reading

- Andrew S. Tanenbaum, *Structured Computer Organization*
- David A. Patterson and John L. Hennessy, *Computer Organization and Design*
- Intel/ARM ISA manuals (for instruction encoding concepts)
- CHIP-8 and 6502 emulator design guides (community resources)

These are conceptual references, not direct dependencies.