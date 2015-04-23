import time
from math import (pi, sin)
from mcpi.minecraft import Minecraft


def cube(size=5, typeId=1):
    "_mcp: create a cube"
    mc = Minecraft.create()
    pos = mc.player.getTilePos()
    s = int(size)
    t = int(typeId)
    mc.setBlocks(pos.x + 1, pos.y, pos.z,
        pos.x + s, pos.y + s - 1, pos.z + s - 1, t)
    

def flatten(size=50):
    """_mcp: flatten world around me.
    change one layer of blocks below me to sandstone,
    clear everything above.
    """
    mc = Minecraft.create()
    pos = mc.player.getTilePos()
    s = int(size)
    mc.setBlocks(pos.x - s, pos.y - 1, pos.z - s,
        pos.x + s, pos.y - 1, pos.z + s, 24)
    mc.setBlocks(pos.x - s, pos.y, pos.z - s,
        pos.x + s, pos.y + 64, pos.z + s, 0)


def falling_block():
    """_mcp
    A gold block is falling from the sky
    """
    mc = Minecraft.create()
    pos = mc.player.getTilePos()
    y = pos.y + 40
    for i in range(40):
        time.sleep(0.5)
        # if the block below is anything other than air
        # stop falling
        if mc.getBlock(pos.x, y-i-1, pos.z) != 0:
            break
        mc.setBlock(pos.x, y-i, pos.z, 0)
        mc.setBlock(pos.x, y-i-1, pos.z, 41)

    
def banner(txt, size=24, type1=41, type2=0):
    """_mcp
    Display a word banner made of blocks
    must have word2banner.py and word2banner.ini in the 
    same directory.
    see word2banner at github.com/wensheng/word2banner
    """
    mc = Minecraft.create()
    pos = mc.player.getTilePos()
    import pplugins.word2banner
    size = int(size)
    type1 = int(type1)
    type2 = int(type2)
    w2b = pplugins.word2banner.word2banner(txt, 1, size)
    y = pos.y + size
    for r in w2b:
        z = pos.z + 1
        for c in r:
            if c:
                mc.setBlock(pos.x, y, z, type1)
            else:
                mc.setBlock(pos.x, y, z, type2)
            z += 1
        y -= 1


def rainbow():
    """_mcp
    create a rainbow.
    The code is from:
    http://dev.bukkit.org/bukkit-plugins/raspberryjuice/
    """
    mc = Minecraft.create()
    pos = mc.player.getTilePos()
    colors = [14, 1, 4, 5, 3, 11, 10]
    height = 60
    mc.setBlocks(pos.x-64,0,0,pos.x+64,height + len(colors),0,0)
    for x in range(0, 128):
        for colourindex in range(0, len(colors)):
            y = sin((x / 128.0) * pi) * height + colourindex
            mc.setBlock(pos.x+x - 64, pos.y+y, pos.z,
                35, colors[len(colors) - 1 - colourindex])
