# Binary Program Execution (`.bin`)

## 1. Overview

The emulator supports raw `.bin` executables that contain opcode bytes for the virtual CPU.

Execution path:
1. Load bytes to ROM base address in RAM
2. Reboot CPU to ROM base
3. Continue normal VM tick loop

## 2. Program Directory

Default program directory:
- `data/bin-programs`

Default sample programs (auto-created at startup):
- `counter.bin`
- `vram-fill.bin`
- `pulse.bin`

## 3. Running Programs

### From terminal

```text
binls
runbin counter.bin
```

### From explorer

Click any `.bin` file entry.

## 4. Binary Constraints

- File must exist under `data/bin-programs` (for `runbin`).
- Empty binaries are rejected.
- Binaries larger than available ROM region are rejected.

## 5. Error Handling

Common messages:
- `BIN NOT FOUND`
- `BIN EMPTY`
- `BIN TOO LARGE`
- `INVALID BIN NAME`
- `CPU NOT READY`

## 6. Writing Your Own `.bin`

Guidelines:
1. Follow opcode list in `docs/ISA_REFERENCE.md`.
2. Keep addresses within RAM bounds.
3. End with `HALT` for finite programs, or loop with `JMP`.

## 7. Minimal Example

Program bytes (hex):
- `01 00` (`LDA_IMM 0`)
- `04 01` (`ADD_IMM 1`)
- `05 00 02` (`JMP 0x0002`)

This increments accumulator forever.

## 8. Relationship to `.rom`

- `.rom` is usually startup payload.
- `.bin` is runtime payload launched inside desktop mode.
- Both use same ISA and same CPU core.