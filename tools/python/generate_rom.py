#!/usr/bin/env python3
"""Generate a simple ROM binary for the Virtual PC simulator."""

from pathlib import Path

NOP = 0x00
LDA_IMM = 0x01
STA = 0x02
HALT = 0xFF


def build_checkerboard_rom(width=64, height=64, vram_start=0x8000, pixels=512):
    rom = bytearray()
    for i in range(min(width * height, pixels)):
        value = 1 if (i % 2 == 0) else 0
        addr = vram_start + i
        rom.extend([LDA_IMM, value, STA, (addr >> 8) & 0xFF, addr & 0xFF])
    rom.append(HALT)
    return rom


def main():
    output = Path("data/generated-demo.rom")
    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_bytes(build_checkerboard_rom())
    print(f"ROM generated: {output} ({output.stat().st_size} bytes)")


if __name__ == "__main__":
    main()