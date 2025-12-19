#!/bin/bash
# Backup Termux Configuration

BACKUP_DIR="$HOME/backup-$(date +%Y%m%d-%H%M%S)"

echo "========================================"
echo "  Termux Backup Script"
echo "========================================"
echo ""
echo "Creating backup in: $BACKUP_DIR"
echo ""

mkdir -p "$BACKUP_DIR"

# Backup important directories
echo "[1] Backing up configuration files..."
mkdir -p "$BACKUP_DIR/config"
cp -r $HOME/.bashrc "$BACKUP_DIR/config/" 2>/dev/null || true
cp -r $HOME/.profile "$BACKUP_DIR/config/" 2>/dev/null || true
cp -r $HOME/.termuxrc "$BACKUP_DIR/config/" 2>/dev/null || true

# Backup installed packages list
echo "[2] Backing up packages list..."
pkg list-installed > "$BACKUP_DIR/packages-installed.txt"

# Backup environment
echo "[3] Backing up environment..."
echo "=== ENV ===" > "$BACKUP_DIR/environment.txt"
env >> "$BACKUP_DIR/environment.txt"

echo ""
echo "========================================"
echo "✓ Backup complete!"
echo "  Location: $BACKUP_DIR"
echo "========================================"
