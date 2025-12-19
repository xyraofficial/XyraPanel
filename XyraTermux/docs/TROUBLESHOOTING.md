# Termux Troubleshooting

## Common Issues & Solutions

### Issue: "Command not found"
**Solution:**
```bash
pkg update
pkg install <package>
```

### Issue: Storage permission denied
**Solution:**
```bash
termux-setup-storage
ls /sdcard
```

### Issue: Git credential issues
**Solution:**
```bash
git config --global user.email "email@example.com"
git config --global user.name "Your Name"
```

### Issue: SSH connection refused
**Solution:**
```bash
pkg install openssh
sshd  # Start SSH server
```

### Issue: Python import errors
**Solution:**
```bash
pip install --upgrade pip
pip install <missing-package>
```

### Issue: Node.js not found
**Solution:**
```bash
pkg install nodejs
node --version
```

### Issue: Low storage space
**Solution:**
```bash
# Check disk usage
df -h

# Clean package cache
pkg clean

# Remove unused packages
pkg remove <package>
```

### Issue: Battery drain
**Solution:**
- Close unnecessary apps
- Use `top` to check heavy processes
- Reduce update frequency
- Disable auto-sync

### Issue: Termux crashes
**Solution:**
- Clear cache: `rm -rf ~/.termux/cache`
- Reinstall: `pkg install --reinstall termux-app`
- Check logs: `logcat`

### Issue: Cannot edit file with nano
**Solution:**
```bash
# Try vim instead
vim <file>

# Or set default editor
export EDITOR=nano
```

## Performance Tips

1. **Disable startup commands**
   - Remove auto-run scripts from ~/.profile

2. **Reduce visual effects**
   - Use simpler terminal colors

3. **Optimize storage**
   - Remove old backups regularly

4. **Monitor resources**
   ```bash
   watch -n 1 'free -h'  # Monitor memory
   watch -n 1 'df -h'    # Monitor disk
   ```

5. **Use efficient commands**
   - Use `grep` instead of `cat | grep`
   - Use aliases for frequently used commands

## Getting Help

```bash
man <command>           # Manual for command
<command> --help        # Quick help
<command> -h            # Alternative help
termux-info             # Termux information
apt-cache search <keyword>  # Search packages
```
