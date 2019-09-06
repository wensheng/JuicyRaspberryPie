"""
api tested:
    mc.player.getTilePos
    mc.player.setTilePos
    mc.player.getPos
    mc.player.setPos
    mc.player.getRotation
    mc.player.setRotation
    mc.player.getPitch
    mc.player.setPitch
"""
import time
from math import (pi, sin, cos)
from mcpi.minecraft import Minecraft

mc = Minecraft.create(port=4711)
pos = mc.player.getTilePos()


def test_pos():
    global pos
    print("current tilePos:", pos.x, pos.y, pos.z)
    time.sleep(1)
    mc.player.setTilePos(pos.x + 5, pos.y + 5, pos.z + 5)
    pos = mc.player.getTilePos()
    print("current tilePos:", pos.x, pos.y, pos.z)
    print("player.getPos:", mc.player.getPos())
    time.sleep(1)
    mc.player.setPos(pos.x + 5, pos.y + 5, pos.z + 5)
    print("current pos:", mc.player.getPos())


def test_rotation():
    angle = mc.player.getRotation()
    print("current rotation:", angle)
    print("adjust player in the next 10 seconds")
    time.sleep(5)
    print("current rotation:", mc.player.getRotation())
    time.sleep(5)
    print("current rotation:", mc.player.getRotation())
    mc.player.setRotation(0)
    print("current rotation:", mc.player.getRotation())


def main():
    test_pos()
    test_rotation()
