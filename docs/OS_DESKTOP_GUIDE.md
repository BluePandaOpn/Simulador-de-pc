# Virtual OS Desktop Guide

## 1. Purpose

The virtual OS is a lightweight desktop shell running entirely inside the emulator frame buffer.

It is not the host OS. It is rendered as guest pixels in VRAM.

## 2. UI Modes

### Legacy Mode

- Shows system summary panel.
- Offers `BOOT DESKTOP` button.

### Desktop Mode

- Explorer panel (left)
- Terminal panel (right)
- Top status bar and bottom task/status bar

## 3. Explorer Behavior

Capabilities:
- Navigate directories
- Open files
- Open folders
- Create new folders (`N` button)
- Go to parent folder (`U` button)
- Execute `.bin` by clicking on a `.bin` entry

Data source:
- Backed by virtual filesystem root: `data/vfs`

## 4. Terminal Behavior

Terminal focus:
- Click terminal panel to focus.

Input model:
- Printable chars from `KeyboardState`
- Enter submits command
- Backspace edits input line

Output model:
- Scrollback list with clipping based on panel width/height

## 5. Command Reference

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

## 6. Binary Execution UX

There are two paths:

1. Terminal command:
- `runbin pulse.bin`

2. Explorer click:
- Click file ending with `.bin`

Both paths load bytes into ROM base address and reboot CPU.

## 7. Adaptive Layout and Text Clipping

The desktop layout adapts dynamically:
- Explorer width constrained with min/max
- Terminal width fills remaining space
- Text is clipped by pixel width to avoid overlap
- Footer and top bar texts are positioned safely with reserved regions

## 8. Interaction Limits (Current)

- Single focused terminal
- No cursor navigation keys yet
- File editing is minimal (`touch` + pre-existing file reads)
- No multi-window z-order model

## 9. Extension Ideas

- Context menu in explorer
- Inline text editor
- Process list/task manager panel
- Window manager with drag/resize semantics