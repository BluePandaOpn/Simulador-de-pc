@echo off
setlocal
setlocal enabledelayedexpansion

set "ROOT=%~dp0"
cd /d "%ROOT%"

if not exist out mkdir out

for /r src\main\java %%f in (*.java) do (
    set "SOURCES=!SOURCES! "%%f""
)

if "!SOURCES!"=="" (
    echo No Java sources found in src\main\java
    exit /b 1
)

javac -d out !SOURCES!
if errorlevel 1 (
    echo Compilation failed.
    exit /b 1
)

if "%~1"=="" (
    if exist "data\generated-demo.rom" (
        java -cp out com.virtualpc.Main "data\generated-demo.rom"
    ) else (
        java -cp out com.virtualpc.Main
    )
) else (
    java -cp out com.virtualpc.Main "%~1"
)

endlocal
endlocal
