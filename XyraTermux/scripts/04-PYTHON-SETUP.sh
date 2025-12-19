#!/bin/bash
# Setup Python Development Environment

echo "========================================"
echo "  Python Development Setup"
echo "========================================"
echo ""

# Check if Python is installed
if ! command -v python &> /dev/null; then
    echo "Python not found. Installing..."
    pkg install -y python python-pip
fi

echo "Python Version:"
python --version
echo ""

echo "Installing popular Python packages..."
echo ""

# pip packages
read -p "Install requests library? (y/n) " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    pip install requests
fi

echo ""
read -p "Install Flask web framework? (y/n) " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    pip install flask
fi

echo ""
read -p "Install Django? (y/n) " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    pip install django
fi

echo ""
read -p "Install Jupyter Notebook? (y/n) " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    pip install jupyter
fi

echo ""
echo "========================================"
echo "✓ Python setup complete!"
echo "========================================"
