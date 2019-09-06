"""
api tested:
    mc.setBlock
    mc.getBlock
    mc.getBlockWithData
    mc.setBlocks
    mc.getBlocks
    mc.getHeight
    mc.getPlayerEntityIds
    mc.postToChat
"""
import time
from math import (pi, sin, cos)
from mcpi.minecraft import Minecraft

mc = Minecraft.create(port=4711)
pos = mc.player.getTilePos()


def test_block():
    mc.setBlock(pos.x + 1, pos.y, pos.z, "gOLd_bLocK")
    time.sleep(0.2)
    print(mc.getBlock(pos.x + 1, pos.y, pos.z))
    print(mc.getBlockWithData(pos.x + 1, pos.y, pos.z))


def test_blocks():
    mc.setBlocks(pos.x + 1, pos.y, pos.z,
                 pos.x + 5, pos.y + 3, pos.z + 4,
                 "gOLd_bLocK")
    time.sleep(0.2)
    blocks = mc.getBlocks(pos.x + 1, pos.y, pos.z,
                          pos.x + 5, pos.y + 3, pos.z + 5)
    for block in blocks:
        print(block)


def test_misc():
    print("getHeight:", mc.getHeight(0, 0))
    print("getPlayerEntityIds:", mc.getPlayerEntityIds())
    print("getPlayerEntityId for wensheng:", mc.getPlayerEntityId("wensheng"))


def main():
    test_block()
    #test_blocks()
    test_misc()
