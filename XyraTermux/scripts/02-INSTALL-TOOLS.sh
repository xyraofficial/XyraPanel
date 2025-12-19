#!/bin/bash
# Install Popular Development Tools

echo "========================================"
echo "  Install Development Tools"
echo "========================================"
echo ""

read -p "Install Python & pip? (y/n) " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    pkg install -y python python-pip
    pip install --upgrade pip
    echo "✓ Python installed"
fi

echo ""
read -p "Install Node.js & npm? (y/n) " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    pkg install -y nodejs
    echo "✓ Node.js installed"
fi

echo ""
read -p "Install Git? (y/n) " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    pkg install -y git
    git config --global user.email "termux@example.com"
    git config --global user.name "Termux User"
    echo "✓ Git installed"
fi

echo ""
read -p "Install OpenSSH? (y/n) " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    pkg install -y openssh
    echo "✓ OpenSSH installed"
    echo "Start SSH server: sshd"
fi

echo ""
read -p "Install FFmpeg? (y/n) " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    pkg install -y ffmpeg
    echo "✓ FFmpeg installed"
fi

echo ""
echo "========================================"
echo "✓ Tool installation complete!"
echo "========================================"
