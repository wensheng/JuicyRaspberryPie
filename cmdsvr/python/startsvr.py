#!/usr/bin/python3
# The python command server is actually located in bukkit/src/main/resources/cmdsvr
# Here we just start it with appropiate environment

import os
import sys
from os.path import abspath, dirname, join
from argparse import ArgumentParser
from subprocess import Popen

parent_dir = dirname(dirname(dirname(abspath(__file__))))
server_dir = join("bukkit", "src", "main", "resources", "cmdsvr")
server_path = join(parent_dir, server_dir, "pycmdsvr.py")
parser = ArgumentParser()
parser.add_argument("--apihost", default="localhost", help="api host")
parser.add_argument("--apiport", default="4711", help="api port")
parser.add_argument("--svrhost", default="localhost", help="server host")
parser.add_argument("--svrport", default="4731", help="server port")
args = parser.parse_args()
env = {"PYTHONHASHSEED": "6666",
       "JRP_API_HOST": args.apihost,
       "JRP_API_PORT": args.apiport,
       "JRP_CMDSVR_HOST": args.svrhost,
       "JRP_CMDSVR_PORT": args.svrport}
if "SYSTEMROOT" in os.environ:
    env['SYSTEMROOT'] = os.environ['SYSTEMROOT']

print("Python:", sys.executable)
print("cmdsvr:", server_path)
Popen([sys.executable, server_path], env=env)
