# XyraPanel

## Overview
XyraPanel adalah aplikasi Android untuk pengiriman OTP ACC.co.id. Aplikasi ini mendukung pengiriman via SMS dan WhatsApp.

**CATATAN PENTING**: Ini adalah proyek Android Native (Java) yang harus di-build dengan Android Studio atau AIDE. Ini BUKAN proyek web atau Expo React Native, sehingga tidak bisa dijalankan di Replit.

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
│   │   │   ├── menu/         - Navigation drawer menu
│   │   │   ├── values/       - Colors, strings, styles
│   │   │   └── xml/          - Network security config
│   │   └── AndroidManifest.xml
│   └── build.gradle
├── build.gradle
└── settings.gradle
```

## Recent Changes (December 15, 2025)

### New Features Added
- **Navigation Bar (Toolbar)**: Header dengan tombol menu hamburger
- **Sidebar (Navigation Drawer)**: Menu geser dari kiri dengan opsi:
  - Beranda
  - Riwayat
  - Tentang
  - Laporkan Masalah
  - Kebijakan Privasi
- **Laporkan Masalah**: Fitur untuk mengirim email ke developer
  - Menggunakan Intent Email (aman, tidak perlu password)
  - Otomatis mengisi info perangkat
  - Membuka aplikasi email bawaan pengguna

### Previous Fixes
- **FIXED**: SSL Pin verification failed error (removed invalid cert pinning)
- **FIXED**: Duplicate "Tidak Ada Jaringan" count issue
- **FIXED**: Time display now shows correctly in failure info dialog
- **ADDED**: Auto-disable "Mulai Kirim" button when input invalid
- **ADDED**: Vibration feedback on success/failure
- **ADDED**: Hide keyboard when starting send
- **ADDED**: Contact buttons (Email & WhatsApp) in About dialog
- **ADDED**: Changelog section in About dialog
- **ADDED**: Disclaimer section in About dialog
- **UPDATED**: Developer name to "XyraOfficial"
- **UPDATED**: Stats panel label "Gagal" -> "Total Kirim"

### Key Features
- Phone number input with real-time validation
- Quick select buttons (1, 3, 5, Random)
- SMS/WhatsApp provider selection
- Progress tracking with success/total/avg stats
- Send history with clear option
- Privacy policy dialog
- Notifications on completion
- Failure Info Icon with detailed error list
- Vibration feedback (ringan saat sukses/gagal)
- Navigation Drawer dengan menu lengkap
- Fitur Laporkan Masalah via Email Intent

### Failure Detection Features
- VPN Detection
- HTTP Capture App Detection
- Network Availability Check
- Request failure reasons with timestamps

## Developer Info
- Developer: XyraOfficial
- Email: xyraofficialsup@gmail.com
- WhatsApp: +62895325844493

## Dependencies
- AndroidX AppCompat 1.6.1
- AndroidX Core 1.12.0
- AndroidX DrawerLayout 1.2.0
- Google Material Design 1.11.0
- VIBRATE permission

## Build Instructions
This is a native Android project. To build:
1. Open project in Android Studio or AIDE
2. Sync Gradle files
3. Build APK (Build > Build Bundle(s) / APK(s) > Build APK(s))
4. Install APK on Android device

## Note About Replit
Proyek Android native TIDAK BISA dijalankan di Replit karena:
- Tidak ada Android SDK/Emulator
- Memerlukan lingkungan build Android (Gradle)
- APK harus di-install di device Android

Gunakan Android Studio atau AIDE untuk build dan test aplikasi.
