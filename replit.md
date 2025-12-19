# XyraTermux

## Overview
XyraTermux adalah aplikasi Android untuk mengeksekusi command langsung di Termux. Aplikasi ini menyediakan interface sederhana untuk mengirim command ke Termux dan melihat hasilnya.

**Project Type**: Android Native (Java) - AIDE Compatible

## Project Structure
```
XyraTermux/
├── app/
│   ├── src/main/
│   │   ├── java/com/xyra/termux/
│   │   │   └── MainActivity.java         - Main UI Activity
│   │   ├── res/
│   │   │   ├── drawable/                 - Drawable resources
│   │   │   ├── layout/                   - Layout files (optional)
│   │   │   ├── mipmap-mdpi/              - App icons
│   │   │   └── values/
│   │   │       ├── colors.xml            - Color definitions
│   │   │       ├── strings.xml           - String resources
│   │   │       └── styles.xml            - Theme styles
│   │   └── AndroidManifest.xml           - App manifest
│   └── build.gradle                      - App build configuration
├── build.gradle                          - Root build configuration
├── settings.gradle                       - Gradle settings
└── app/proguard-rules.pro               - ProGuard rules
```

## Features

### 1. Command Input
- EditText untuk menginput custom Termux command
- Minimal validation

### 2. Execute Button
- Mengirim command ke Termux via Intent
- Support Termux API (com.termux.RUN_COMMAND)

### 3. Open Termux Button
- Launch Termux app jika terinstall
- Link ke Play Store jika belum terinstall

### 4. Result Display
- TextView untuk menampilkan output
- ScrollView untuk long text

## Permissions
- `INTERNET` - Network access
- `READ/WRITE_EXTERNAL_STORAGE` - File access
- `VIBRATE` - Haptic feedback
- `com.termux.permission.RUN_COMMAND` - Termux command execution

## Build Instructions

### Using AIDE:
1. Open project di AIDE
2. Sync Gradle
3. Build & Run APK

### Using Gradle CLI:
```bash
./gradlew build
./gradlew installDebug
```

## Requirements
- Android SDK 21+ (Android 5.0)
- Java 11+
- Gradle 7.4.2+
- Termux installed (F-Droid recommended)

## Developer Info
- **Package**: com.xyra.termux
- **Min SDK**: 21
- **Target SDK**: 33
- **Build System**: Gradle
- **External Libraries**: None (Minimal dependencies for AIDE)

## Struktur untuk AIDE
- ✅ Tanpa Maven external library
- ✅ Gradle-based (AIDE support)
- ✅ Material Design
- ✅ Single Activity Architecture
- ✅ Programmatic UI (no XML layouts needed)

## Status
- Siap untuk di-build dengan AIDE atau Android Studio
- Minimal dependencies - cocok untuk low-spec devices
