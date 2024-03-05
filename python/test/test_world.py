"""
api tested:
    mc.setBlock
    mc.getBlock
    mc.getBlockWithData
    mc.setBlocks
    mc.getBlocks
    mc.getHeight
    mc.getPlayerEntityId
    mc.getPlayerEntityIds
    mc.postToChat
    mc.setSign
"""
import time
from math import (pi, sin, cos)
from mcpi.minecraft import Minecraft

mc = Minecraft.create()
pos = mc.player.getTilePos()


def test_block():
    print(pos.x, pos.y, pos.z)
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
    # note we should get most gold and a few air
    for block in blocks:
        print(block)


def test_sign():
    print("player direction:", mc.player.getDirection())
    mc.setSign(pos.x - 3, pos.y, pos.z - 3, "acasia_sign", 0,
               "ACASIA SIGN", "试试中文", "JuicyRaspberryPie", "0-north")
    mc.setSign(pos.x - 2, pos.y, pos.z - 2, "oak_sign", 1,
               "OAK SIGN", "1-east",
               "dark_oak is too dark",
               "This is a long sentence")
    mc.setSign(pos.x - 3, pos.y, pos.z - 1, "jungle_sign", 2,
               "JUNGLE SIGN", "2-south")
    mc.setSign(pos.x - 4, pos.y, pos.z - 2, "spruce_sign", 3,
               "SPRUCE SIGN", "3-west")

    mc.setBlock(pos.x - 3, pos.y + 1, pos.z - 2, "diamond_block")
    mc.setSign(pos.x - 3, pos.y + 1, pos.z - 3, "jungle_wall_sign", 0,
               "jungle wall sign", "This sign is", "at the north", "of a block")
    mc.setSign(pos.x - 2, pos.y + 1, pos.z - 2, "birch_wall_sign", 1,
               "birch wall sign", "facing east", "こんにちは", "Japanese hello/你好")
    mc.setSign(pos.x - 3, pos.y + 1, pos.z - 1, "OAK_wall_sign", 2,
               "dark oak wall sign", "2 facing south")
    mc.setSign(pos.x - 4, pos.y + 1, pos.z - 2, "spruce_wall_sign", 3,
               "sprunce wall sign", "3 facing west")


def test_misc():
    mc.postToChat("Hello, 世界！")
    # explosion doesn't work in bukkit, it should be explosion_normal, explosion_large
    # mc.spawnParticle(pos.x + 3, pos.y + 2, pos.z + 3, "explosion", 1000)
    # time.sleep(2)
    mc.spawnParticle(pos.x + 3, pos.y + 2, pos.z + 3, "smoke", 1000, 0.1)
    time.sleep(2)
    mc.spawnParticle(pos.x + 3, pos.y + 2, pos.z + 3, "heart", 100, 0.05)
    print("getHeight:", mc.getHeight(0, 0))
    print("getPlayerEntityIds:", mc.getPlayerEntityIds())
    print("getPlayerEntityId for wensheng:", mc.getPlayerEntityId("wensheng"))


def main():
    test_block()
    test_blocks()
    test_sign()
    test_misc()
