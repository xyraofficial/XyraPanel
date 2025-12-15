# XyraPanel

## Overview
XyraPanel adalah aplikasi Android untuk pengiriman OTP ACC.co.id. Aplikasi ini mendukung pengiriman via SMS dan WhatsApp.

**PENTING**: Ini adalah proyek Android native. Tidak bisa dijalankan di Replit karena tidak ada Android SDK. Build menggunakan AIDE atau Android Studio.

## Project Structure
```
XyraPanel/
├── app/
│   ├── libs/                    - JAR libraries (okhttp, okio)
│   ├── src/main/
│   │   ├── java/com/xyra/panel/
│   │   │   └── MainActivity.java
│   │   ├── res/
│   │   │   ├── drawable/        - Button styles, icons, backgrounds
│   │   │   ├── layout/          - Activity dan dialog layouts
│   │   │   ├── values/          - Colors, strings, styles
│   │   │   └── xml/             - Network security config
│   │   └── AndroidManifest.xml
│   └── build.gradle
├── build.gradle
└── settings.gradle
```

## Recent Changes (December 15, 2025)
- Added responsive layouts (match_parent with margins)
- Fixed NotificationCompat -> Notification.Builder (no AndroidX)
- Added clickable notifications that open History dialog
- Added history statistics (Total, Berhasil, Gagal)
- Fixed bottom buttons visibility (paddingBottom=80dp)

## Features
- Phone number input with validation
- Quick select buttons (1, 3, 5, Random)
- SMS/WhatsApp provider selection
- Progress tracking with success/fail stats
- Send history with clear option
- Privacy policy dialog
- Clickable notifications -> History

## Build Instructions
1. Copy XyraPanel folder to device
2. Open in AIDE or Android Studio
3. Sync Gradle files
4. Build APK

## Note
LSP errors in Replit (100 diagnostics) are expected - no Android SDK available.
