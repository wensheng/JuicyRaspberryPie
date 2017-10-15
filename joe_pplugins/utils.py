from contextlib import contextmanager
from traceback import format_exc
import re

from pplugins import blocks

from mcpi.minecraft import Minecraft

def block_by_name(name):
    try:
        return int(name)
    except ValueError:
        pass
    name = name.upper()
    for n in [name, '{}_BLOCK'.format(name)]:
        try:
            return getattr(blocks, n)
        except AttributeError:
            pass
    raise ValueError('no such block')

def playerPos(player):
    pos = player.getTilePos()
    x = int(getattr(pos,'x'))
    y = int(getattr(pos,'y'))
    z = int(getattr(pos,'z'))
    return x, y, z

@contextmanager
def exc_chat(mc=None):
    if mc is None:
        mc = Minecraft.create()
    try:
        yield mc
    except:
        for line in re.split(r'\n',format_exc())[:-1]:
            mc.postToChat(line)

MARK_FILE = '/tmp/markpos.txt'

def mark_pos(x, y, z):
    with open(MARK_FILE,'w') as fout:
        print('{},{},{}'.format(x, y, z), file=fout)

def marked_pos():
    with open(MARK_FILE) as fin:
        for l in fin.readlines():
            break
    l = l.rstrip()
    x, y, z = re.split(r',', l)
    return int(x), int(y), int(z)
