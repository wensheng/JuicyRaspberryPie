import time
from math import (pi, sin)

import path_helper
from mcpi.minecraft import Minecraft


mc = Minecraft.create()
pos = mc.player.getTilePos()

def spawnN(size=10, entityType='cow'):
    s = int(size)
    entities = []
    for i in range(s):
        entities.append(mc.spawnEntity(pos.x + 1 + i , pos.y, pos.z, entityType))
    return entities

cow = spawnN(1)[0]
time.sleep(3)
cow.setTilePos(pos.x + 10, pos.y, pos.z)
time.sleep(3)
cow.remove()
time.sleep(3)

villagers = spawnN(5,'villager')
for v in villagers:
    print(v.id)

time.sleep(5)
nrby = mc.getNearbyEntities(pos.x, pos.y, pos.z)
print("nearby villagers, should be the same as above")
for e in nrby:
    print(e.id)
    e.remove()
    time.sleep(1)
