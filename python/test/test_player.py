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

mc = Minecraft.create()
pos = mc.player.getTilePos()


def test_pos():
    global pos
    print("current tilePos:", pos.x, pos.y, pos.z)
    time.sleep(1)
    mc.player.setTilePos(pos.x + 5, pos.y + 5, pos.z + 5)
    time.sleep(0.5)
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
    time.sleep(0.2)
    print("current rotation:", mc.player.getRotation())
    print("current direction:", mc.player.getDirection())
    time.sleep(0.5)
    mc.player.setRotation(45)
    time.sleep(0.5)
    mc.player.setRotation(90)
    time.sleep(0.5)
    mc.player.setRotation(135)
    time.sleep(0.5)
    mc.player.setRotation(180)
    time.sleep(0.2)
    print("current rotation:", mc.player.getRotation())
    print("current direction:", mc.player.getDirection())


def test_pitch():
    angle = mc.player.getPitch()
    print("current pitch:", angle)
    print("adjust player pitch in the next 10 seconds")
    time.sleep(5)
    print("current pitch:", mc.player.getPitch())
    time.sleep(5)
    print("current pitch:", mc.player.getPitch())
    mc.player.setPitch(-90)
    time.sleep(0.2)
    print("current pitch:", mc.player.getPitch())
    time.sleep(0.5)
    mc.player.setPitch(-45)
    time.sleep(0.5)
    mc.player.setPitch(0)
    time.sleep(0.5)
    mc.player.setPitch(45)
    time.sleep(0.5)
    mc.player.setPitch(90)
    time.sleep(0.2)
    print("current pitch:", mc.player.getPitch())


def main():
    test_pos()
    test_rotation()
    test_pitch()
