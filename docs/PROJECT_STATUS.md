# Project Status

## Current Stage

- Stage: active prototype
- Stability: development quality, not production-grade yet
- Version track: pre-1.0 (`CHANGELOG.md`)

## What Is Already Implemented

- Virtual CPU and RAM
- VRAM-based rendering and Swing monitor
- Resizable display with adaptive viewport
- Virtual desktop shell (explorer + terminal)
- Runtime `.bin` execution from terminal and explorer
- Virtual filesystem isolation under `data/vfs`

## Why Activity May Look Low

If repository history appears short, that reflects lifecycle stage rather than abandonment.

Signals of active maintenance included in this repo:
- structured docs in `docs/`
- version history in `CHANGELOG.md`
- contribution process in `CONTRIBUTING.md`
- issue templates in `.github/ISSUE_TEMPLATE`
- roadmap in `docs/ROADMAP.md`

## Planned Next Milestones

1. Add instruction-level tests and golden traces.
2. Expand ISA with stack/call semantics.
3. Add richer desktop apps (editor/process monitor).
4. Add release tagging discipline and build artifacts.