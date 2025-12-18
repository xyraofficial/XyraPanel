# TermuxToolBox

## Overview
TermuxToolBox adalah aplikasi Android untuk mengelola dan berinteraksi dengan Termux. Aplikasi ini menyediakan toolbox lengkap untuk install packages, menjalankan commands, dan menggunakan Termux API.

**CATATAN PENTING**: Ini adalah proyek Android Native (Java) yang harus di-build dengan Android Studio atau AIDE.

## Project Structure
```
TermuxToolBox/
├── app/
│   ├── src/main/
│   │   ├── java/com/termux/toolbox/
│   │   │   ├── MainActivity.java      - Main UI dengan toolbox
│   │   │   ├── TermuxApiHelper.java   - Helper untuk Termux API
│   │   │   ├── TermuxResultReceiver.java - Receiver untuk hasil command
│   │   │   └── ToolItem.java          - Model untuk tool items
│   │   ├── res/
│   │   │   ├── drawable/     - Button styles, icons, backgrounds
│   │   │   ├── layout/       - Activity dan item layouts
│   │   │   └── values/       - Colors, strings, styles
│   │   └── AndroidManifest.xml
│   └── build.gradle
├── build.gradle
└── settings.gradle
```

## Features

### 1. Termux Connection Status
- Cek apakah Termux terinstall
- Cek apakah Termux:API terinstall
- Tombol untuk install/open Termux

### 2. Custom Command Execution
- Input field untuk menjalankan command custom
- Command dijalankan langsung di Termux

### 3. Quick Tools (20+ Tools)
**System:**
- Update System (pkg update & upgrade)
- Setup Storage (termux-setup-storage)
- Install Termux:API

**Development:**
- Python, Node.js, PHP, Ruby, Golang, Git, Clang

**Network:**
- Nmap, Wget, cURL, OpenSSH

**Utilities:**
- Nano, Vim, Htop, Neofetch

**Media:**
- FFmpeg, ImageMagick

### 4. Termux API Integration
- Run commands via Intent
- Shell command execution
- Background execution support
- Toast, Vibrate, Notification API

## Permissions
- `INTERNET` - Network access
- `READ/WRITE_EXTERNAL_STORAGE` - File access
- `VIBRATE` - Haptic feedback
- `com.termux.permission.RUN_COMMAND` - Termux command execution

## How It Works
1. App checks if Termux is installed
2. User selects tool or enters custom command
3. App sends Intent to Termux RunCommandService
4. Termux executes the command
5. Result shown in Termux terminal

## Build Instructions
1. Open project in Android Studio or AIDE
2. Sync Gradle files
3. Build APK
4. Install on Android device with Termux

## Requirements
- Android 5.0+ (API 21)
- Termux installed from F-Droid (recommended)
- Termux:API for extended features

## Developer Info
- Package: com.termux.toolbox
- Min SDK: 21
- Target SDK: 33
