from contextlib import contextmanager
from traceback import format_exc
import re

from pplugins import blocks

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
def exc_chat(mc):
    try:
        yield
    except:
        for line in re.split(r'\n',format_exc())[:-1]:
            mc.postToChat(line)
