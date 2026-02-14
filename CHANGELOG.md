# Changelog

All notable changes to this project are documented in this file.

## [0.9.0] - Current

### Added
- Runtime `.bin` execution from terminal via `runbin`.
- `.bin` discovery command `binls`.
- Click-to-run `.bin` support from explorer.
- Automatic sample binary generation (`counter.bin`, `vram-fill.bin`, `pulse.bin`).
- CPU reboot method for program hot-swap.

### Improved
- Desktop layout adaptation for varying window sizes.
- Monitor viewport scaling/centering in resizable window.
- Mouse coordinate mapping for resized monitor.
- Text clipping and collision prevention in top/bottom bars and panels.

## [0.8.0]

### Added
- Desktop-style virtual OS mode with explorer and terminal.
- Virtual filesystem rooted at `data/vfs`.
- Keyboard input queue and terminal command handling.
- Folder creation and navigation UI.

## [0.7.0]

### Added
- Virtual OS launcher from legacy screen.
- RAM/ROM viewer apps.
- Mouse input handling in monitor panel.

## [0.6.0]

### Added
- Optimized video rendering pipeline with changed-pixel updates.
- `FrameBuffer` abstraction for VRAM drawing.
- Indexed palette rendering in `VideoDevice`.

## [0.5.0]

### Added
- Swing display panel and VM main loop.
- Virtual disk backing file model.
- Basic demo ROM behavior.

## [0.4.0]

### Added
- ROM loader and demo program generation.
- Base project structure with modular packages.

## [0.3.0]

### Added
- CPU instruction loop and base ISA.
- RAM abstraction with bounds checks.

## [0.2.0]

### Added
- Initial architecture and configuration model.

## [0.1.0]

### Added
- Project bootstrap and Java entry point.