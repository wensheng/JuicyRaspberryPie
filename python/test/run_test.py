#!/usr/bin/python3

import os
import sys
import importlib
import argparse
from os.path import join, dirname, abspath

if sys.version_info[0] != 3:
    exit("Python 3 require")

parser = argparse.ArgumentParser()
parser.add_argument("name", help="test name")
parser.add_argument('--host', default="127.0.0.1", help="MCPI host")
parser.add_argument('--port', type=int, default=4711, help="MCPI port")
args = parser.parse_args()
name = args.name
os.environ["JRP_API_HOST"] = args.host
os.environ["JRP_API_PORT"] = str(args.port)

cmdsvr_pydir = join(dirname(dirname(abspath(__file__))), "bukkit", "src", "main", "resources")
sys.path.insert(0, cmdsvr_pydir)

try:
    module = importlib.import_module("test_" + name)
except ModuleNotFoundError:
    exit("test_{}.py can not be imported".format(name))

if "main" in module.__dict__ and callable(module.__dict__["main"]):
    module.__dict__["main"]()
else:
    print("test not runnable")
