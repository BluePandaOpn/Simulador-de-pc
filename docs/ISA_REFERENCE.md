# Instruction Set Reference

This is the current virtual CPU ISA used by `.rom` and `.bin` programs.

## Encoding Rules

- Opcodes are 1 byte.
- Immediate values are 1 byte.
- Addresses are 16-bit big-endian (`high`, then `low`).

## Registers and State

- `PC` (program counter): 16-bit
- `A` (accumulator): 8-bit
- `HALT` state flag

## Opcodes

### `0x00` `NOP`

No operation.

### `0x01` `LDA_IMM imm8`

`A <- imm8`

Size: 2 bytes

### `0x02` `STA addr16`

`RAM[addr16] <- A`

Size: 3 bytes

### `0x03` `LDA_MEM addr16`

`A <- RAM[addr16]`

Size: 3 bytes

### `0x04` `ADD_IMM imm8`

`A <- (A + imm8) & 0xFF`

Size: 2 bytes

### `0x05` `JMP addr16`

`PC <- addr16`

Size: 3 bytes

### `0x06` `JZ addr16`

If `A == 0`, `PC <- addr16`

Size: 3 bytes

### `0x07` `SUB_IMM imm8`

`A <- (A - imm8) & 0xFF`

Size: 2 bytes

### `0xFF` `HALT`

Stops instruction execution until CPU reboot.

Size: 1 byte

## Program Entry

By default programs are loaded at ROM base (`VmConfig.romLoadAddress`, currently `0x0000`) and executed from that address.

## CPU Reboot

`Cpu.reboot(bootAddress)` resets:
- `PC = bootAddress`
- `A = 0`
- `HALT = false`

This is used when running new `.bin` programs at runtime.