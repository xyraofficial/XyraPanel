# XyraPanel - Android OTP Sender App

## Project Overview
XyraPanel adalah aplikasi Android untuk mengirim OTP melalui ACC.co.id API. Aplikasi ini memiliki tampilan modern bergaya iOS dengan tema light dan smooth.

## Project Type
**Android Native App** - Project ini BUKAN web application. Tidak bisa dijalankan di Replit karena memerlukan Android SDK. Build harus dilakukan di AIDE atau Android Studio.

## Package Structure
- Package: `com.xyra.panel`
- Main Activity: `MainActivity.java`
- Application ID: `com.xyra.panel`

## Key Files
- `XyraPanel/app/src/main/java/com/xyra/panel/MainActivity.java` - Main logic
- `XyraPanel/app/src/main/res/layout/activity_main.xml` - UI layout
- `XyraPanel/app/src/main/res/values/colors.xml` - Color scheme (iOS style)
- `XyraPanel/app/src/main/res/values/styles.xml` - App theme
- `XyraPanel/app/src/main/AndroidManifest.xml` - App manifest
- `XyraPanel/app/build.gradle` - Build configuration

## Features
1. Modern iOS-style UI with light theme
2. Quick select buttons (1, 3, 5, Random)
3. Toggle button (Mulai Kirim / Stop)
4. Live statistics (Berhasil, Gagal, Rata-rata)
5. Status indicator with color dot
6. Max send limit: 5 with warning popup
7. Cancel/Stop functionality

## UI Components
- Card-based layout with rounded corners
- Custom drawables for buttons
- Warning dialog for max limit
- Progress bar with custom style

## Build Instructions
1. Open project in AIDE or Android Studio
2. Clean project (if errors appear)
3. Rebuild to generate R.java
4. Install APK on Android device

## Recent Changes (Dec 15, 2025)
- Changed package from com.xyra.config to com.xyra.panel
- Added iOS-style UI design
- Added quick select buttons
- Added toggle Start/Stop button
- Added max send warning popup
- Fixed Rata-rata text size to match other stats
- Removed separate cancel button
