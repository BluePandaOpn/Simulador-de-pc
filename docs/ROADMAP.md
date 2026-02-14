# Roadmap

## 1. CPU and ISA

- Add additional registers (B, C, flags).
- Add stack pointer and call/return instructions.
- Add interrupt and trap model.

## 2. Program Format

- Define executable header format (magic, entrypoint, metadata).
- Add loader validation and error codes.
- Support symbol/debug sections for disassembly tooling.

## 3. OS Layer

- Add process table and scheduler abstraction.
- Add syscall interface for file and video services.
- Add text editor app and richer terminal parser.

## 4. Storage

- Add virtual block abstraction on top of `virtual-disk.bin`.
- Add filesystem metadata simulation (permissions, timestamps, inode-like IDs).

## 5. Developer Experience

- Add integration tests for ISA behavior.
- Add deterministic replay traces.
- Add profiling hooks (frame time, cycles/sec).

## 6. UX

- Add draggable/resizable guest windows.
- Add theme system and status widgets.
- Add keyboard shortcuts for terminal and explorer.