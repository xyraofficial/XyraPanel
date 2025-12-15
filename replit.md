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
- **REMOVED**: Sistem deteksi VPN, PacketCapture, HttpCapture
- **ADDED**: Protect Request Send Service dengan SSL Pinning menggunakan OkHttp
- **ADDED**: Certificate Pinning untuk acc.co.id domain
- **ADDED**: Request ID generation untuk setiap request
- **UPDATED**: Network security config untuk enforce HTTPS only
- **UPDATED**: Dialog security warning menjadi dialog status protected
- Mengganti HttpURLConnection dengan OkHttp untuk proteksi SSL bypass

### Security Features
- SSL Certificate Pinning dengan OkHttp CertificatePinner
- Request ID unik untuk setiap transaksi
- HTTPS only (cleartext traffic disabled)
- Anti MITM (Man-in-the-Middle) attack protection

## Dependencies
- androidx.core:core:1.9.0
- androidx.appcompat:appcompat:1.6.1
- okhttp-4.9.3.jar (in libs folder) - untuk SSL Pinning
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
- **Protected Request**: SSL Pinning anti bypass capture
