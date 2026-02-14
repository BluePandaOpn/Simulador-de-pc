# Architecture

## 1. High-Level Overview

The emulator follows a layered model:

1. Host Runtime Layer
- Java process, Swing window, host filesystem access.

2. Emulated Hardware Layer
- `Ram`
- `Cpu`
- `VideoDevice` + VRAM region
- `VirtualDisk`

3. Virtual OS Layer
- `VirtualOperatingSystem` drawing UI into VRAM through `FrameBuffer`
- Explorer + terminal + `.bin` launcher

## 2. Component Map

- Entry point: `src/main/java/com/virtualpc/Main.java`
- VM orchestration: `src/main/java/com/virtualpc/core/VirtualMachine.java`
- Config: `src/main/java/com/virtualpc/config/VmConfig.java`
- Memory: `src/main/java/com/virtualpc/memory/Ram.java`
- CPU: `src/main/java/com/virtualpc/cpu/Cpu.java`
- ISA constants: `src/main/java/com/virtualpc/cpu/InstructionSet.java`
- Video front-end: `src/main/java/com/virtualpc/video/DisplayPanel.java`
- Video conversion: `src/main/java/com/virtualpc/video/VideoDevice.java`
- UI rasterizer: `src/main/java/com/virtualpc/video/FrameBuffer.java`
- OS UI: `src/main/java/com/virtualpc/os/VirtualOperatingSystem.java`
- VFS: `src/main/java/com/virtualpc/os/fs/VirtualFileSystem.java`

## 3. Memory Layout (Current)

- Address space: 16-bit (`0x0000` to `0xFFFF`)
- ROM load address: `0x0000` (configurable)
- VRAM start: `0x8000` (configurable)

All bytes are interpreted unsigned using `& 0xFF` semantics.

## 4. Execution Model

Per frame:
1. CPU executes `cyclesPerFrame` instructions (or until halted)
2. Virtual OS updates UI state and draws to framebuffer
3. Framebuffer is copied to VRAM (`present()`)
4. Video device converts VRAM bytes to image pixels
5. Panel repaints if frame changed

## 5. Rendering Pipeline

1. `VirtualOperatingSystem` draws primitives/text via `FrameBuffer`
2. `FrameBuffer` writes contiguous bytes to RAM VRAM
3. `VideoDevice` diffs VRAM against previous frame
4. Changed pixels are updated in `BufferedImage` data buffer
5. `DisplayPanel` scales + centers output in a resizable window

## 6. Input Pipeline

- `DisplayPanel` captures mouse and keyboard events.
- `MouseState` stores pointer state and click edges.
- `KeyboardState` queues typed chars and special keys.
- `VirtualOperatingSystem` consumes those states each tick.

## 7. Binary Program Pipeline

1. `.bin` file is discovered (`binls`) or selected in explorer.
2. `runbin` loads bytes into RAM at ROM base.
3. CPU reboots at ROM base (`Cpu.reboot`).
4. Program executes in the next tick cycle.

## 8. Isolation Model

This is a software emulation model:
- Guest RAM is an internal Java byte array.
- Guest disk is a host file acting as virtual backing storage.
- Guest cannot read host RAM directly.

## 9. Performance Techniques

- Changed-pixel diff rendering
- Direct pixel buffer updates (`DataBufferInt`)
- Batch CPU cycles per frame
- Lightweight integer-based text/shape rendering

## 10. Extensibility Directions

- Add registers, stack pointer, and flags
- Add syscall table for OS services
- Add executable format with headers/metadata
- Add scheduler/process abstraction in virtual OS
- Add debug instrumentation (breakpoints, traces)

## 11. Legacy Document

The previous architecture file remains at:
- `docs/virtual-pc-architecture.md`