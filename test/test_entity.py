"""
api tested:
    mc.spawnEntity
    mc.getNearbyEntities
    mc.entity.getTilePos
    mc.entity.setTilePos
    mc.entity.getPos
    mc.entity.setPos
    mc.entity.getRotation
    mc.entity.setRotation
    mc.entity.getPitch
    mc.entity.setPitch
    mc.entity.getDirection
"""
import os
import time
from math import (pi, sin, cos)
from mcpi.minecraft import Minecraft

try:
    port = int(os.environ["mcpi_port"])
except (KeyError, ValueError):
    port = 4711
mc = Minecraft.create(port=port)
pos = mc.player.getTilePos()


def test_pos():
    # in game make player face south to observe
    entityId = mc.spawnEntity(pos.x, pos.y, pos.z + 4, "pig")
    print("ID of the pig:", entityId)
    time.sleep(1)
    print("pic direction:", mc.entity.getDirection(entityId))
    entities = mc.getNearbyEntities(pos.x, pos.y, pos.z)
    # should print out player and pig
    for entity in entities:
        print(entity)
    time.sleep(2)
    # kill the pig by free-falling it
    mc.entity.setTilePos(entityId, pos.x, pos.y + 50, pos.z + 4)
    time.sleep(3)

    entityId = mc.spawnEntity(pos.x, pos.y, pos.z + 20, "panda")
    time.sleep(1)
    mc.entity.setTilePos(entityId, pos.x, pos.y, pos.z + 15) 
    time.sleep(1)
    mc.entity.setTilePos(entityId, pos.x, pos.y, pos.z + 10) 
    time.sleep(1)
    mc.entity.setTilePos(entityId, pos.x, pos.y, pos.z + 5) 
    mc.postToChat("panda will disapear in 5 seconds")
    time.sleep(5)
    mc.entity.remove(entityId)


def test_rotation():
    entityId = mc.spawnEntity(pos.x, pos.y, pos.z + 4, "boat")
    time.sleep(2)
    print("last boat rotation:", mc.entity.getRotation(entityId))
    for i in range(50):
        angle = (i % 8) * 45
        mc.entity.setRotation(entityId, angle)
        time.sleep(0.2)

    print("current boat rotation:", mc.entity.getRotation(entityId))
    time.sleep(3)
    mc.entity.remove(entityId)


def test_pitch():
    # don't know which entity can bend head -90-90 degree
    entityId = mc.spawnEntity(pos.x, pos.y, pos.z + 3, "llama")
    time.sleep(2)
    print("last witch pitch:", mc.entity.getPitch(entityId))
    for i in range(24):
        angle = (i % 12) * 15 - 90
        mc.entity.setPitch(entityId, angle)
        time.sleep(0.2)
    print("current witch pitch:", mc.entity.getPitch(entityId))
    time.sleep(3)
    mc.entity.remove(entityId)
    

def main():
    test_pos()
    test_rotation()
    test_pitch()
