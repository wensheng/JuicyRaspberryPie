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
import time
from math import (pi, sin, cos)
from mcpi.minecraft import Minecraft

mc = Minecraft.create()
pos = mc.player.getTilePos()


def test_pos():
    # in game make player face south to observe
    pig = mc.spawnEntity(pos.x, pos.y, pos.z + 4, "pig")
    print("ID of the pig:", pig.id)
    time.sleep(1)
    print("pic direction:", pig.getDirection())
    entities = mc.getNearbyEntities(pos.x, pos.y, pos.z)
    # should print out player and pig
    for entity in entities:
        print(entity.id)
    time.sleep(2)
    # kill the pig by free-falling it
    pig.setTilePos(pos.x, pos.y + 50, pos.z + 4)
    time.sleep(3)

    panda = mc.spawnEntity(pos.x, pos.y, pos.z + 20, "panda")
    time.sleep(1)
    panda.setTilePos(pos.x, pos.y, pos.z + 15) 
    time.sleep(1)
    panda.setTilePos(pos.x, pos.y, pos.z + 10) 
    time.sleep(1)
    panda.setTilePos(pos.x, pos.y, pos.z + 5) 
    mc.postToChat("panda will disapear in 5 seconds")
    time.sleep(5)
    panda.remove()


def test_rotation():
    boat = mc.spawnEntity(pos.x, pos.y, pos.z + 4, "boat")
    time.sleep(2)
    print("last boat rotation:", boat.getRotation())
    for i in range(50):
        angle = (i % 8) * 45
        boat.setRotation(angle)
        time.sleep(0.2)

    print("current boat rotation:", boat.getRotation())
    time.sleep(3)
    boat.remove()


def test_pitch():
    # use mc.entity.xx instead of entity.xx
    # don't know which entity can bend head -90-90 degree
    llama = mc.spawnEntity(pos.x, pos.y, pos.z + 3, "llama")
    time.sleep(2)
    print("last witch pitch:", mc.entity.getPitch(llama.id))
    for i in range(24):
        angle = (i % 12) * 15 - 90
        mc.entity.setPitch(llama.id, angle)
        time.sleep(0.2)
    print("current witch pitch:", mc.entity.getPitch(llama.id))
    time.sleep(3)
    mc.entity.remove(llama.id)
    

def main():
    test_pos()
    test_rotation()
    test_pitch()
