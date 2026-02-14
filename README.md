# Virtual PC Simulator

A Java-based virtual PC emulator featuring:
- Virtual RAM, CPU, VRAM, and virtual disk
- A desktop-style virtual operating system rendered in guest VRAM
- Mouse + keyboard input handling
- Runtime execution of `.rom` and `.bin` programs

## Project Status

Current maturity: **active prototype / pre-1.0**

This repository started as a focused core implementation. It now includes:
- structured docs
- changelog history
- contribution workflow
- issue templates

See:
- `CHANGELOG.md`
- `CONTRIBUTING.md`

## License

Licensed under the MIT License.

See:
- `LICENSE`

## Visual Preview

Screenshots/GIF documentation:
- `docs/VISUALS.md`
- `assets/screenshots`
- `assets/gifs`

## Java and Python Responsibilities

Java is the runtime core. Python is optional tooling.

- Java: CPU, RAM, VRAM, GUI, virtual OS, runtime execution
- Python: ROM/binary helper scripts and test data generation

Detailed boundary doc:
- `docs/JAVA_PYTHON_BOUNDARY.md`

## Documentation Index

- `docs/INDEX.md`
- `docs/USER_GUIDE.md`
- `docs/OS_DESKTOP_GUIDE.md`
- `docs/ARCHITECTURE.md`
- `docs/ISA_REFERENCE.md`
- `docs/BIN_EXECUTION.md`
- `docs/RESEARCH_REFERENCE.md`
- `docs/ROADMAP.md`

## Quick Start

### Option 1

```powershell
init.bat
```

### Option 2

```powershell
New-Item -ItemType Directory -Force -Path out | Out-Null
javac -d out (Get-ChildItem -Recurse src/main/java -Filter *.java | ForEach-Object { $_.FullName })
java -cp out com.virtualpc.Main
```

Run with external ROM:

```powershell
java -cp out com.virtualpc.Main data/generated-demo.rom
```

## Terminal Commands

- `help`
- `ls`
- `pwd`
- `cd <dir>`
- `mkdir <name>`
- `touch <name>`
- `cat <file>`
- `clear`
- `binls`
- `runbin <name.bin>`

## Sample `.bin` Programs

Auto-generated in `data/bin-programs`:
- `counter.bin`
- `vram-fill.bin`
- `pulse.bin`