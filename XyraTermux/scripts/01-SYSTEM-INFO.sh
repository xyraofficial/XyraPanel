#!/bin/bash
# Display Termux System Information

echo "========================================"
echo "  Termux System Information"
echo "========================================"
echo ""

echo "→ OS & Device Info"
uname -a
echo ""

echo "→ Current User"
whoami
echo ""

echo "→ Home Directory"
echo $HOME
echo ""

echo "→ Shell"
echo $SHELL
echo ""

echo "→ Date & Time"
date
echo ""

echo "→ Disk Usage"
df -h
echo ""

echo "→ Memory Usage"
free -h
echo ""

echo "→ CPU Info"
nproc
echo ""

echo "→ Termux Path"
echo $PREFIX
echo ""

echo "→ Python Version"
python --version 2>/dev/null || echo "Python not installed"
echo ""

echo "→ Node Version"
node --version 2>/dev/null || echo "Node not installed"
echo ""

echo "========================================"
