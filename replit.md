# XyraPanel

## Overview
XyraPanel adalah aplikasi Android untuk pengiriman OTP ACC.co.id. Aplikasi ini mendukung pengiriman via SMS dan WhatsApp.

## Project Structure
```
XyraPanel/
├── app/
│   ├── src/main/
│   │   ├── java/com/xyra/panel/
│   │   │   └── MainActivity.java
│   │   ├── res/
│   │   │   ├── drawable/     - Button styles, icons, backgrounds
│   │   │   ├── layout/       - Activity dan dialog layouts
│   │   │   ├── values/       - Colors, strings, styles
│   │   │   └── xml/          - Network security config
│   │   └── AndroidManifest.xml
│   └── build.gradle
├── build.gradle
└── settings.gradle
```

## Recent Changes (December 15, 2025)
- Restructured main layout header to horizontal compact design with avatar on left
- Moved app version badge (v1.0) to header right side
- Reduced margins and padding across all cards for better space utilization
- Removed bottom version text (now in header)
- All elements now visible on screen without excessive scrolling
- Added btn_history and btn_about buttons to activity_main.xml
- Fixed "variable must be final" error in showHistoryDialog()
- Added AndroidX core dependency for NotificationCompat

## Dependencies
- androidx.core:core:1.9.0
- androidx.appcompat:appcompat:1.6.1
- okhttp-4.9.3.jar (in libs folder)
- okio-2.8.0.jar (in libs folder)

## Build Instructions
This is an Android project. To build:
1. Open project in Android Studio or AIDE
2. Sync Gradle files
3. Build APK (Build > Build Bundle(s) / APK(s) > Build APK(s))

## Features
- Phone number input with validation
- Quick select buttons (1, 3, 5, Random)
- SMS/WhatsApp provider selection
- Progress tracking with success/fail stats
- Send history with clear option
- Privacy policy dialog
- Notifications on completion
