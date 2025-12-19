# Useful Termux Commands

## Text Processing

```bash
grep <pattern> <file>       # Search pattern in file
grep -r <pattern> <dir>     # Recursive search
sed 's/old/new/g' <file>    # Replace text
awk '{print $1}' <file>     # Extract columns
wc -l <file>                # Count lines
sort <file>                 # Sort lines
uniq <file>                 # Remove duplicates
```

## Network

```bash
ping -c 4 <host>            # Ping host
wget <url>                  # Download file
curl <url>                  # Fetch URL
curl -O <url>               # Download with curl
ssh <user>@<host>           # SSH connection
scp <file> <user>@<host>:   # Copy via SSH
```

## System Management

```bash
ps aux                      # List processes
kill <pid>                  # Kill process
bg                          # Background job
fg                          # Foreground job
top                         # Process monitor
systemctl status            # Service status
```

## Archive

```bash
tar -czf <archive> <dir>    # Create tar.gz
tar -xzf <archive>          # Extract tar.gz
zip -r <archive> <dir>      # Create zip
unzip <archive>             # Extract zip
7z a <archive> <file>       # 7z compression
```

## Python One-liners

```bash
python -m http.server 8000  # Simple HTTP server
python -m json.tool < file.json  # Pretty print JSON
python -c "print('Hello')"   # Run Python code
python -m pip list          # List installed packages
```

## Development

```bash
git clone <url>             # Clone repository
git add .                   # Stage changes
git commit -m "msg"         # Commit
git push                    # Push to remote
git pull                    # Pull from remote
npm init                    # Initialize Node project
npm install <package>       # Install npm package
```

## Useful Aliases

Add to ~/.bashrc:

```bash
alias ll='ls -lah'
alias cd..='cd ..'
alias c='clear'
alias update='pkg update && pkg upgrade'
alias sinfo='neofetch'
```

## Tips & Tricks

1. **Long command?** Press `Ctrl+A` + `Ctrl+E` to navigate line ends
2. **Repeat command?** Use `!!` to repeat last command
3. **Run in background?** Add `&` at end: `command &`
4. **Redirect output?** Use `>` for file: `command > file.txt`
5. **Chain commands?** Use `;` or `&&`: `cmd1; cmd2`
