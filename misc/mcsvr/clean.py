#!/usr/bin/python3
import os

keeps = [
    'start-server-linux.sh',
    'start-server-win.bat',
    'build-linux.sh',
    'clean.py']
    
confirm = input("This directory will be cleaned, are you sure? (y/n) ")
if confirm.upper() != "Y":
    exit("Aborted.")

files = os.listdir('.')
for f in files:
    if f not in keeps:
        os.system("rm -fr {}".format(f))
