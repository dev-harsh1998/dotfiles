#!/usr/bin/expect -f
spawn ssh host.IP -l uname
expect "uname@hosts's password:"
send "sendpassword\r"
send "\r"
interact
# Simple expect script to connect to remote ssh server
# Will only work if you have expect installed (sudo apt install expect)
