# Contributing Guide

Thanks for contributing.

## Workflow

1. Open an issue describing bug/feature.
2. Create a small, focused branch.
3. Keep commits atomic and documented.
4. Update docs when behavior changes.
5. Submit PR with test/validation notes.

## Coding Style

- Java 17+
- Keep methods cohesive and bounded.
- Prefer clear naming over terse naming.
- Add comments only when behavior is non-obvious.
- Avoid dead code and commented-out blocks.

## Validation

Run before PR:

```powershell
javac -d out (Get-ChildItem -Recurse src/main/java -Filter *.java | ForEach-Object { $_.FullName })
```

If GUI behavior changed, attach:
- one screenshot in `assets/screenshots`
- one short GIF in `assets/gifs` (optional but recommended)

## Documentation

Update these when relevant:
- `README.md`
- `CHANGELOG.md`
- `docs/ARCHITECTURE.md`
- `docs/USER_GUIDE.md`