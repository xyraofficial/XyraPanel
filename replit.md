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
│   │   │   ├── LoginActivity.java  - Face ID Login Screen
│   │   │   └── MainActivity.java   - Main Application
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

## Recent Changes

### New Feature: Face ID Login (December 18, 2025)
- **NEW**: Face ID / Biometric Login Screen
  - LoginActivity sebagai launcher activity baru
  - Fitur pendaftaran Face ID menggunakan Android BiometricPrompt API
  - Autentikasi wajah untuk membuka XyraPanel
  - Dialog pendaftaran wajah dengan panduan lengkap
  - Fallback untuk perangkat tanpa biometrik
  - Icon Face ID dan animasi scanning
  - Penyimpanan status pendaftaran di SharedPreferences

- **Files Added**:
  - `LoginActivity.java` - Activity login dengan Face ID
  - `activity_login.xml` - Layout halaman login
  - `dialog_register_face.xml` - Dialog pendaftaran wajah
  - `dialog_no_biometric.xml` - Dialog jika biometrik tidak tersedia
  - `ic_face_id.xml` - Icon Face ID
  - `ic_face_scan.xml` - Icon scanning wajah
  - `ic_warning_biometric.xml` - Icon peringatan

- **Permissions Added**:
  - `USE_BIOMETRIC` - Untuk biometrik API baru
  - `USE_FINGERPRINT` - Untuk kompatibilitas API lama

### Previous Changes (December 15, 2025)

### Latest Fixes (December 15, 2025)
- **FIXED**: Privacy Policy checkbox - now checked automatically when opened from navigation (if already accepted)
  - When user opens Privacy Policy from sidebar after initial acceptance, checkbox is pre-checked
  - Continue button is enabled automatically
  
- **IMPROVED**: History dialog with smooth table layout
  - Replaced card-style layout with clean table format
  - Added column headers: No, Nomor, Via, Hasil
  - Alternating row colors for better readability
  - Compact display showing success/total count
  - Single line text (no wrapping) for clean appearance
  - Provider shows "SMS" or "WA" (abbreviated)

- **REMOVED**: History and About buttons from main screen
  - Bottom buttons removed for cleaner UI
  - Features now accessible only through navigation drawer

- **FIXED**: All dialog layouts - dialogs were too narrow causing text to wrap vertically
  - Set all dialog widths to 90% of screen width using WindowManager.LayoutParams
  - Affected dialogs: About, History, Privacy, Report Problem, Failure Info
  
- **NEW**: Swipe gesture to open sidebar
  - Swipe from left edge of screen to right to open navigation drawer
  - Swipe left anywhere to close drawer when open
  - Uses GestureDetector with dispatchTouchEvent for smooth handling

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
- **Face ID Login** - Biometric authentication untuk keamanan
- **Daftar Face ID** - Pendaftaran wajah pengguna
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
