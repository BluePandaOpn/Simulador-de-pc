# Java/Python Boundary

## Runtime Core (Java)

The emulator runtime is fully implemented in Java:
- CPU execution loop
- RAM/VRAM handling
- Virtual disk access
- Swing monitor and input pipeline
- Virtual desktop OS (explorer + terminal)
- Runtime `.bin` loading and execution

Primary packages:
- `com.virtualpc.cpu`
- `com.virtualpc.memory`
- `com.virtualpc.video`
- `com.virtualpc.os`
- `com.virtualpc.core`

## Tooling Layer (Python)

Python scripts are optional and non-critical:
- Generate ROM/test binaries
- Produce patterned inputs for CPU validation

Current script location:
- `tools/python/generate_rom.py`

## Why This Split

- Java keeps runtime deterministic and integrated with the GUI loop.
- Python remains lightweight for automation tasks.
- No mandatory cross-language runtime bridge is required.

## Operational Rule

If Java and Python outputs conflict, Java runtime behavior is authoritative.