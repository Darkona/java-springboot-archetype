# Console UTF-8 Configuration

This document explains how to ensure your console displays UTF-8 characters correctly, including the ASCII art banner and ANSI colors.

## Overview

The project is now configured to use UTF-8 encoding by default:

- JVM runs with `-Dfile.encoding=UTF-8` and `-Dconsole.encoding=UTF-8`
- Logback appenders use `charset="UTF-8"`
- Local profile has `spring.output.ansi.enabled: ALWAYS`

## Platform-Specific Setup

### Windows

#### Option 1: Use Windows Terminal (Recommended)

- Install [Windows Terminal](https://aka.ms/terminal) from Microsoft Store
- It has built-in UTF-8 support and renders ANSI colors correctly
- Simply run: `.\gradlew.bat bootRun --args="--spring.profiles.active=local"`

#### Option 2: Configure Command Prompt (cmd.exe)

Before running the application, set the console code page to UTF-8:

```cmd
chcp 65001
.\gradlew.bat bootRun --args="--spring.profiles.active=local"
```

**Note**: Some font families in cmd.exe may not render all Unicode characters. If you see boxes or question marks, switch to:

- Consolas
- Lucida Console
- NSimSun (for CJK characters)

#### Option 3: Configure PowerShell

Add to your PowerShell profile or run before executing gradlew:

```powershell
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$OutputEncoding = [System.Text.Encoding]::UTF8
.\gradlew.bat bootRun --args="--spring.profiles.active=local"
```

#### Option 4: Use Git Bash or WSL

Both Git Bash and Windows Subsystem for Linux (WSL) handle UTF-8 natively:

```bash
./gradlew bootRun --args="--spring.profiles.active=local"
```

### macOS / Linux

UTF-8 is typically the default. Verify with:

```bash
echo $LANG
```

If not set to UTF-8, add to your shell profile (~/.bashrc, ~/.zshrc, etc.):

```bash
export LANG=en_US.UTF-8
export LC_ALL=en_US.UTF-8
```

Then run:

```bash
./gradlew bootRun --args="--spring.profiles.active=local"
```

## IntelliJ IDEA Configuration

IntelliJ IDEA should use UTF-8 by default. To verify:

1. **File Encoding**:
    - Settings → Editor → File Encodings
    - Set "Global Encoding" to UTF-8
    - Set "Project Encoding" to UTF-8

2. **Console Encoding**:
    - Run/Debug Configurations → Application → VM options
    - Add: `-Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8`
    - (Already configured in build.gradle for bootRun)

3. **Terminal**:
    - Settings → Tools → Terminal
    - Ensure the shell path is correct
    - Terminal should inherit UTF-8 from system

## Troubleshooting

### Banner not displaying correctly

- Verify console code page (Windows): `chcp` should return `65001`
- Check font supports Unicode characters
- Ensure no proxy/interceptor is modifying output

### Colors not working

- Verify `spring.output.ansi.enabled: ALWAYS` in application-local.yaml
- Check terminal supports ANSI escape codes
- Windows cmd.exe: Enable Virtual Terminal Processing (automatic in Windows 10+)

### Characters appear as boxes (□) or question marks (?)

- Change console font to one that supports Unicode
- On Windows cmd: Right-click title bar → Properties → Font → Select "Consolas" or "Lucida Console"

## Verification

After setting up, run the application and you should see:

1. ASCII art banner rendered correctly
2. Colored log output (if ANSI is enabled)
3. No encoding errors in logs

Example command:

```bash
.\gradlew.bat bootRun --args="--spring.profiles.active=local"
```

The banner in `src/main/resources/banner.txt` should display correctly with all special characters.
