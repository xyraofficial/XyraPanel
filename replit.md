# XyraPanel

## Overview
XyraPanel adalah aplikasi Android untuk pengiriman OTP ACC.co.id. Aplikasi ini mendukung pengiriman via SMS dan WhatsApp.

**CATATAN PENTING**: Ini adalah proyek Android yang harus di-build dengan Android Studio atau AIDE, bukan proyek web. Tidak ada workflow/server yang bisa dijalankan di Replit.

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
- **REMOVED**: SSL Protection/Pinning system (user request)
- **ADDED**: Failure notification icon di header (sebelah kiri version badge)
- **ADDED**: Dialog info kegagalan dengan template inbox bagus
- **ADDED**: Deteksi VPN aktif, HTTP Capture app, Network status
- **ADDED**: layout/dialog_failure_info.xml - Dialog untuk menampilkan info kegagalan
- **ADDED**: layout/item_failure_reason.xml - Template item untuk setiap alasan kegagalan
- **UPDATED**: activity_main.xml - Tambah btn_failure_info icon
- **UPDATED**: MainActivity.java - Implementasi sistem notifikasi kegagalan

### Failure Detection Features
- VPN Detection (tun/ppp/pptp interfaces + NetworkCapabilities.TRANSPORT_VPN)
- HTTP Capture App Detection (list package names)
- Network Availability Check (isNetworkAvailable())
- Request failure reasons (timeout, no host, IO error, server error)

## Dependencies
- No external dependencies (pure Android SDK)
- build.gradle includes: implementation fileTree(dir: 'libs', include: ['*.jar'])

## Build Instructions
This is an Android project. To build:
1. Open project in Android Studio or AIDE
2. Sync Gradle files
3. Build APK (Build > Build Bundle(s) / APK(s) > Build APK(s))
4. Install APK on Android device

## Features
- Phone number input with validation
- Quick select buttons (1, 3, 5, Random)
- SMS/WhatsApp provider selection
- Progress tracking with success/fail stats
- Send history with clear option
- Privacy policy dialog
- Notifications on completion
- **Failure Info Icon**: Icon di header yang muncul saat ada kegagalan, klik untuk lihat detail
