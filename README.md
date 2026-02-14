# Virtual PC Simulator

A Java-based virtual PC emulator with:
- Virtual RAM, CPU, VRAM, and virtual disk
- A built-in desktop-style virtual operating system
- Mouse + keyboard interaction
- Terminal commands for file and binary program execution
- `.rom` and `.bin` program loading and execution

## Documentation

Full documentation is organized here:
- `docs/INDEX.md`

Direct links:
- `docs/USER_GUIDE.md`
- `docs/OS_DESKTOP_GUIDE.md`
- `docs/ARCHITECTURE.md`
- `docs/ISA_REFERENCE.md`
- `docs/BIN_EXECUTION.md`
- `docs/RESEARCH_REFERENCE.md`
- `CHANGELOG.md`
- `docs/ROADMAP.md`

## Quick Start

### Option 1: Use the provided launcher

```powershell
init.bat
```

### Option 2: Compile and run manually

```powershell
New-Item -ItemType Directory -Force -Path out | Out-Null
javac -d out (Get-ChildItem -Recurse src/main/java -Filter *.java | ForEach-Object { $_.FullName })
java -cp out com.virtualpc.Main
```

Run with external ROM:

```powershell
java -cp out com.virtualpc.Main data/generated-demo.rom
```

## Key Features

- Virtual CPU fetch/decode/execute loop (`Cpu`)
- 64KB virtual RAM (`Ram`)
- VRAM-backed renderer (`FrameBuffer` + `VideoDevice`)
- Resizable monitor panel with centered scaling (`DisplayPanel`)
- Desktop OS with explorer + terminal (`VirtualOperatingSystem`)
- Virtual file system rooted at `data/vfs`
- Binary program repository at `data/bin-programs`

## Built-in Terminal Commands

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

Created automatically on startup in `data/bin-programs`:
- `counter.bin`
- `vram-fill.bin`
- `pulse.bin`

## Project Layout

- `src/main/java/com/virtualpc/` - Java source code
- `tools/python/` - utility scripts
- `data/` - virtual disk, virtual filesystem, generated binaries
- `docs/` - complete technical and user documentation

## Versioning and History

Project evolution and update history:
- `CHANGELOG.md`

## License

No license file is currently defined in this repository.