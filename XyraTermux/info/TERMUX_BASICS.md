# Termux Basics

## Apa itu Termux?

Termux adalah terminal emulator Android yang memberikan akses ke Linux environment pada smartphone/tablet.

## Instalasi

1. Download dari F-Droid atau Play Store
2. Buka aplikasi
3. Tunggu inisialisasi pertama kali (setup environment)

## Perintah Dasar

### Package Management
```bash
pkg update              # Update package list
pkg upgrade             # Upgrade semua packages
pkg install <package>   # Install package
pkg remove <package>    # Hapus package
pkg list-all            # List semua packages
pkg search <keyword>    # Cari package
```

### File Navigation
```bash
ls                      # List file
ls -la                  # List dengan hidden files
pwd                     # Print working directory
cd <directory>          # Change directory
cd ~                    # Ke home directory
cd ..                   # Ke parent directory
```

### File Operations
```bash
touch <filename>        # Create file
mkdir <dirname>         # Create directory
cp <src> <dst>          # Copy file
mv <src> <dst>          # Move/rename file
rm <file>               # Delete file
rm -r <dir>             # Delete directory
cat <file>              # Display file content
nano <file>             # Edit file (nano editor)
```

### System Info
```bash
uname -a                # System information
whoami                  # Current user
date                    # Current date/time
df -h                   # Disk usage
free -h                 # Memory usage
```

## Storage Access

```bash
termux-setup-storage    # Setup external storage access
cd /sdcard              # Access external storage
cd /storage/emulated/0  # Alternative external storage path
```

## Keyboard Shortcuts

- `Ctrl+C` - Cancel current command
- `Ctrl+D` - Exit terminal
- `Ctrl+A` - Move to start of line
- `Ctrl+E` - Move to end of line
- `Ctrl+L` - Clear screen
- `Ctrl+Z` - Suspend process
- `Volume Up + Q` - Show extra keys
- `Volume Up + K` - Toggle keyboard

## Tips

1. Gunakan `clear` untuk membersihkan terminal
2. Gunakan `history` untuk melihat command history
3. Gunakan `man <command>` untuk melihat manual
4. Gunakan `apt` alternative untuk `pkg`
5. Termux menggunakan `apt` package manager (Debian-based)
