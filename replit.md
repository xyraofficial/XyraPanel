# XyraTermux ToolBox

## Overview
XyraTermux ToolBox adalah koleksi lengkap informasi, script, dan tools untuk Termux. Berisi dokumentasi, script siap pakai, dan tips berguna.

## Struktur Project
```
XyraTermux/
├── info/
│   ├── TERMUX_BASICS.md           - Pengenalan Termux
│   └── USEFUL-COMMANDS.md         - Perintah berguna
├── scripts/
│   ├── 00-SETUP.sh                - Setup environment
│   ├── 01-SYSTEM-INFO.sh          - Informasi sistem
│   ├── 02-INSTALL-TOOLS.sh        - Install tools development
│   ├── 03-BACKUP.sh               - Backup configuration
│   └── 04-PYTHON-SETUP.sh         - Setup Python environment
├── docs/
│   └── TROUBLESHOOTING.md         - Solusi masalah umum
└── README.md
```

## Konten

### Info Files
- **TERMUX_BASICS.md** - Pengenalan Termux, instalasi, perintah dasar, storage access
- **USEFUL-COMMANDS.md** - Text processing, network, system, archive, development commands

### Scripts
1. **00-SETUP.sh** - Update packages, setup storage, install essential tools
2. **01-SYSTEM-INFO.sh** - Display sistem info (OS, disk, memory, versions)
3. **02-INSTALL-TOOLS.sh** - Interactive tool installer (Python, Node, Git, SSH, FFmpeg)
4. **03-BACKUP.sh** - Backup config files dan packages list
5. **04-PYTHON-SETUP.sh** - Setup Python development environment

### Docs
- **TROUBLESHOOTING.md** - Solusi untuk common issues, performance tips

## Cara Menggunakan

### Baca Dokumentasi
```bash
cat info/TERMUX_BASICS.md
cat info/USEFUL-COMMANDS.md
cat docs/TROUBLESHOOTING.md
```

### Jalankan Script (di Termux)
```bash
bash scripts/00-SETUP.sh          # Setup
bash scripts/01-SYSTEM-INFO.sh    # Cek info
bash scripts/02-INSTALL-TOOLS.sh  # Install tools
bash scripts/03-BACKUP.sh         # Backup
bash scripts/04-PYTHON-SETUP.sh   # Setup Python
```

## Fitur

✅ Dokumentasi lengkap tentang Termux
✅ 5+ ready-to-use scripts
✅ Troubleshooting guide
✅ Command reference
✅ Setup automation

## Target Users

- Termux beginners - Dokumentasi lengkap
- Termux power users - Useful scripts & commands
- Developers - Development tools setup
- System administrators - Backup & maintenance scripts

## Status
✅ Complete & Ready to Use
