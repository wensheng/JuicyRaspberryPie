from pplugins import blocks
from pplugins.utils import exc_chat, playerPos, mark_pos, marked_pos
from pplugins.placemarks import Placemarks

from mcpi.minecraft import Minecraft

import time
import random

def attempt():
    "_mcp: attempt"
    mc = Minecraft.create()
    while True:
        pos = mc.player.getTilePos()
        mc.postToChat('x={} z={}'.format(pos.x, pos.z))
        time.sleep(1)
    
def tprand(far=200):
    "_mcp: random teleport"
    mc = Minecraft.create()
    far = int(far)
    x = random.randint(0-far,far)
    z = random.randint(0-far,far)
    y = mc.getHeight(x,z)
    mc.player.setTilePos(x,y,z)

def high():
    """_mcp: set y to world height"""
    mc = Minecraft.create()
    with exc_chat(mc):
        x, y, z = playerPos(mc.player)
        y = mc.getHeight(x, z) + 1
        mc.player.setTilePos(x, y, z)

def up(up=10):
    "_mcp: zoom up"
    mc = Minecraft.create()
    with exc_chat(mc):
        up = int(up)
        x, y, z = playerPos(mc.player)
        y = mc.getHeight(x, z) + 1
        for i in range(up):
            mc.player.setTilePos(x, y+i, z)
            time.sleep(0.005) # zip!
    
def compass():
    """_mcp: get direction"""
    mc = Minecraft.create()
    with exc_chat(mc):
        d = mc.player.getDirection()
        mc.postToChat('{}'.format(d))

def zoom(steps=100):
    "_mcp: zoom"
    mc = Minecraft.create()
    with exc_chat(mc):
        steps = int(steps)
        d = mc.player.getDirection()
        dx = float(getattr(d,'x'))
        dy = float(getattr(d,'y'))
        dz = float(getattr(d,'z'))
        x, y, z = playerPos(mc.player)
        for n in range(steps):
            i = int(x) + int(dx * n)
            j = int(y) + int(dy * n)
            k = int(z) + int(dz * n)
            mc.player.setTilePos(i, j, k)
            time.sleep(0.005)

def ray(steps=100):
    "_mcp: zoom"
    mc = Minecraft.create()
    with exc_chat(mc):
        steps = int(steps)
        d = mc.player.getDirection()
        dx = float(getattr(d,'x'))
        dy = float(getattr(d,'y'))
        dz = float(getattr(d,'z'))
        x, y, z = playerPos(mc.player)
        for n in range(steps):
            i = int(x) + int(dx * n)
            j = int(y) + int(dy * n) + 1
            k = int(z) + int(dz * n)
            if mc.getBlock(i, j, k) != blocks.AIR:
                mc.setBlock(i, j, k, blocks.TNT)
                mc.setBlock(i, j+1, k, blocks.FIRE)
                return
            mc.setBlock(i, j, k, blocks.TNT)
            mc.getBlock(i, j, k)
            mc.setBlock(i, j, k, blocks.AIR)

def mark(name='mark'):
    "_mcp: mark position for later teleport"
    with exc_chat() as mc:
        x, y, z = playerPos(mc.player)
        Placemarks().add(name, [x,y,z]).save()
        mc.postToChat('marked {},{},{} as {}'.format(x, y, z, name))

def tp(name='mark'):
    "_mcp: teleport to marked location"
    with exc_chat() as mc:
        try:
            x, y, z = Placemarks().location[name]
            mc.player.setTilePos(x, y, z)
            mc.postToChat('teleported to {}'.format(name))
        except KeyError:
            mc.postToChat('no marked location called {}'.format(name))

def tplist():
    "_mcp: list teleport locations by name"
    with exc_chat() as mc:
        msg = ' '.join(sorted(Placemarks().location))
        mc.postToChat('locations: {}'.format(msg))

def tpxyz(x=0,y=0,z=0):
    "_mcp: teleport to x,y,z"
    with exc_chat() as mc:
        x, y, z = int(x), int(y), int(z)
        mc.player.setTilePos(x, y, z)
        
def tporigin():
    "_mcp: teleport to 0,0,0"
    with exc_chat() as mc:
        mc.player.setTilePos(0, 0, 0)
        mc.postToChat('teleported to origin')
