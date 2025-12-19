#!/bin/bash
# XyraTermux Setup Script
# Update dan setup Termux environment

echo "========================================"
echo "  XyraTermux - Setup Script"
echo "========================================"
echo ""

# Update packages
echo "[1] Updating packages..."
pkg update && pkg upgrade -y

# Setup storage
echo "[2] Setting up external storage..."
termux-setup-storage

# Install essential tools
echo "[3] Installing essential tools..."
pkg install -y \
    curl \
    wget \
    git \
    nano \
    vim \
    htop \
    neofetch \
    python \
    nodejs \
    openssh

echo ""
echo "========================================"
echo "  ✓ Setup complete!"
echo "========================================"
echo ""
echo "Next steps:"
echo "  1. Check installed packages: pkg list-installed"
echo "  2. View system info: neofetch"
echo "  3. Check storage: ls /sdcard"
echo ""
