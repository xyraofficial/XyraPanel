# XyraTermux - Full Android App

## Overview
XyraTermux adalah aplikasi Android ToolBox untuk Termux dengan tema iOS yang smooth dan modern. Fitur lengkap untuk command execution, system info, tools installation, dan backup.

## Architecture

- **Multi-Activity Structure**: MainActivity (menu) + 4 activities
- **AIDE Compatible**: Gradle-based, no external libraries
- **iOS-Style Theme**: Modern colors, smooth UI, 44dp minimum touch target
- **Material Design**: Proper spacing, typography, shadows

## Activities

1. **MainActivity** - Menu utama dengan 4 pilihan
2. **CommandActivity** - Execute Termux command
3. **SystemInfoActivity** - Display system information
4. **ToolsActivity** - Install development tools
5. **BackupActivity** - Backup configuration

## Design System

### Colors (iOS-Inspired)
- Primary: #007AFF (iOS Blue)
- Accent: #FF2D55 (iOS Red)
- Background: #F5F5F7 (iOS Light Gray)
- Success: #34C759, Warning: #FF9500, Error: #FF3B30

### Typography
- Heading: 28-32sp, Bold
- Subheading: 18sp, Bold
- Body: 16sp
- Caption: 12-14sp

### Spacing
- Padding: 16dp
- Margins: 8-24dp
- Button height: 44-56dp

## Build & Run

### With AIDE
1. Open XyraTermux in AIDE
2. Sync Gradle
3. Build & Run

### With Gradle CLI
```bash
./gradlew build
./gradlew installDebug
```

## File Structure
```
XyraTermux/
├── app/src/main/
│   ├── java/com/xyra/termux/
│   │   ├── MainActivity.java
│   │   ├── CommandActivity.java
│   │   ├── SystemInfoActivity.java
│   │   ├── ToolsActivity.java
│   │   └── BackupActivity.java
│   ├── res/
│   │   ├── drawable/ (shapes, icons)
│   │   └── values/ (colors, strings, styles)
│   └── AndroidManifest.xml
├── build.gradle
└── settings.gradle
```

## Features

✅ Command Executor - Run any Termux command
✅ System Information - Device, Android, Java, Memory info
✅ Tools Installation - Python, Node, Git, SSH, FFmpeg, etc
✅ Backup System - Config & packages backup
✅ iOS-Style Theme - Modern, smooth, professional
✅ Multi-Activity Navigation - Clean architecture
✅ AIDE Compatible - No external libraries

## Requirements
- Android SDK 21+ (Android 5.0)
- Gradle 7.4.2+
- Termux installed on device

## Status
✅ Production Ready
✅ Fully Functional
✅ AIDE Compatible
