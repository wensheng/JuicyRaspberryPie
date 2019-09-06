#!/usr/bin/python3

import sys
import importlib
from os.path import join, dirname, abspath

if sys.version_info[0] != 3:
    exit("Python 3 require")

if len(sys.argv) != 2:
    exit("test what?")
name = sys.argv[1]

cmdsvr_pydir = join(dirname(dirname(abspath(__file__))), "cmdsvr", "python")
sys.path.insert(0, cmdsvr_pydir)

module = importlib.import_module("test_" + name)
if "main" in module.__dict__ and callable(module.__dict__["main"]):
    module.__dict__["main"]()
