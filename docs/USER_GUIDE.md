# User Guide

## 1. What This Project Is

This project emulates a small virtual computer in Java.

It includes:
- Virtual hardware (CPU, RAM, VRAM, disk)
- A graphical virtual operating system
- A basic terminal and file explorer
- Binary program execution via `.bin`

## 2. Prerequisites

- Java JDK 17 or newer
- Windows PowerShell or CMD

Optional:
- Python 3 for helper scripts in `tools/python`

## 3. Running the Emulator

### Recommended

```powershell
init.bat
```

This compiles and runs the project.

### Manual

```powershell
New-Item -ItemType Directory -Force -Path out | Out-Null
javac -d out (Get-ChildItem -Recurse src/main/java -Filter *.java | ForEach-Object { $_.FullName })
java -cp out com.virtualpc.Main
```

## 4. Boot Flow

1. VM initializes RAM, CPU, video, and virtual disk.
2. A ROM image is loaded into RAM.
3. The graphical virtual OS starts.
4. You can switch into desktop mode and interact with explorer/terminal.

## 5. Virtual Desktop Controls

- Mouse click: focus, open explorer entries, press buttons.
- Keyboard: terminal input when terminal has focus.
- Enter: execute command.
- Backspace: delete one character.

## 6. Terminal Commands

- `help` - show available commands
- `ls` - list entries in current directory
- `pwd` - print current virtual path
- `cd <dir>` - change directory
- `mkdir <name>` - create folder
- `touch <name>` - create file
- `cat <file>` - show file content
- `clear` - clear terminal output
- `binls` - list `.bin` programs in `data/bin-programs`
- `runbin <name.bin>` - load/execute a binary in virtual CPU

## 7. Data Persistence

Persistent data locations:
- Virtual disk backend: `data/virtual-disk.bin`
- Virtual filesystem root: `data/vfs`
- Binary programs: `data/bin-programs`

## 8. Troubleshooting

### The app does not start

- Verify `javac -version` works.
- Re-run `init.bat`.

### Terminal input does not work

- Click inside terminal window to set focus.

### Program text overlaps

- Resize window. The monitor and UI perform adaptive layout and clipping.

## 9. Recommended Next Steps

- Read `docs/OS_DESKTOP_GUIDE.md` for GUI internals.
- Read `docs/BIN_EXECUTION.md` to write custom `.bin` programs.
- Read `docs/ISA_REFERENCE.md` for opcode-level programming.