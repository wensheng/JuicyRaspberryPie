import time
from math import (pi, sin)
from mcpi.minecraft import mc, mcpy


@mcpy
def cube(size=5, typeName='SANDSTONE'):
    "create a cube"
    pos = mc.player.getTilePos()
    s = int(size)
    mc.setBlocks(pos.x + 1, pos.y, pos.z,
        pos.x + s, pos.y + s - 1, pos.z + s - 1, typeName)
    

def flatten(size=50):
    """_mcpy: flatten world around me.
    change one layer of blocks below me to sandstone,
    clear everything above.
    """
    pos = mc.player.getTilePos()
    s = int(size)
    mc.setBlocks(pos.x - s, pos.y - 1, pos.z - s,
        pos.x + s, pos.y - 1, pos.z + s, 'SANDSTONE')
    mc.setBlocks(pos.x - s, pos.y, pos.z - s,
        pos.x + s, pos.y + 64, pos.z + s, 'AIR')


def falling_block():
    """_mcpy
    A gold block is falling from the sky
    """
    pos = mc.player.getTilePos()
    y = pos.y + 40
    for i in range(40):
        time.sleep(0.5)
        # if the block below is anything other than air
        # stop falling
        if mc.getBlock(pos.x, y-i-1, pos.z) != 'AIR':
            break
        mc.setBlock(pos.x, y-i, pos.z, 'AIR')
        mc.setBlock(pos.x, y-i-1, pos.z, 'GOLD_BLOCK')

    
def rainbow():
    """_mcpy
    create a rainbow.
    The code is from: http://dev.bukkit.org/bukkit-plugins/raspberryjuice/
    """
    pos = mc.player.getTilePos()
    colors = ['RED_WOOL', 'ORANGE_WOOL', 'YELLOW_WOOL', 'LIME_WOOL', 'LIGHT_BLUE_WOOL', 'BLUE_WOOL', 'PURPLE_WOOL']
    height = 60
    mc.setBlocks(pos.x-64,0,0,pos.x+64,height + len(colors),0,'AIR')
    for x in range(0, 128):
        for colourindex in range(0, len(colors)):
            y = sin((x / 128.0) * pi) * height + colourindex
            mc.setBlock(pos.x+x - 64, pos.y+y, pos.z, colors[len(colors) - 1 - colourindex])
