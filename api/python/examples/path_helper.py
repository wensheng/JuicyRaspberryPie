"""For setting up PYTHONPATH for mcpi
   put "import path_helper" at the beginning of example file
"""
import sys
from os.path import dirname, abspath

parent_dir = dirname(abspath(dirname(__file__)))
sys.path.insert(0, parent_dir)
