#Put your Python files here.


#In your Python file, define functions with doctring that starts with "_mcp", 
#these functions will then be available in Minecraft as "p command".

#For example, you define 2 functions: hi and blk as follow:

from pplugins import blocks
from pplugins.utils import block_by_name, exc_chat, playerPos

from math import sqrt

import time

# --------------------------------------------------
from mcpi.minecraft import Minecraft

def hi():
    "_mcp: just saying hello"
    mc = Minecraft.create()
    with exc_chat(mc):
        mc.postToChat("Hello!")
        raise ValueError('bad stuff')

def search(blk=None):
    "_mcp: search block by name"
    mc = Minecraft.create()
    try:
        bbn = block_by_name(blk)
    except ValueError:
        mc.postToChat('not found')
        return
    mc.postToChat('{} = {}'.format(blk, bbn))
    
def blk():
    "_mcp: place 10 blocks of diamond"
    mc = Minecraft.create()
    pos = mc.player.getTilePos()
    for i in range(10):
        mc.setBlock(pos.x+1, pos.y+i, pos.z, blocks.DIAMOND_BLOCK)

def joe():
    "_mcp: do something"
    mc = Minecraft.create()
    pos = mc.player.getTilePos()
    for i in range(10):
        mc.setBlock(pos.x + i, pos.y + i, pos.z, blocks.STONE)
        
def _sphere(x=None,y=None,z=None,r=5):
    mc = Minecraft.create()
    pos = mc.player.getTilePos()
    x = pos.x if x is None else int(x)
    y = pos.y if y is None else int(y)
    z = pos.z if z is None else int(z)
    r = int(r)
    results = []
    for i in range(r*2):
        for j in range(r*2):
            for k in range(r*2):
                ix = i - r
                jy = j - r
                kz = k - r
                s = sqrt(ix**2 + jy**2 + kz**2)
                results.append((x+ix, y+jy, z+kz, s))
    return results

def sphere(r=10, block=blocks.STONE):
    "_mcp: create sphere"
    mc = Minecraft.create()
    with exc_chat(mc):
        r = int(r)
        block = block_by_name(block)
        for x, y, z, s in _sphere(r=r):
            if s <= r:
                mc.setBlock(x, y, z, block)
    
def hole(r=10):
    "_mcp: create sphere"
    mc = Minecraft.create()
    r = int(r)
    sphere(r, blocks.AIR)

def holsphere(r=10, block=blocks.STONE):
    "_mcp: create hollow sphere"
    mc = Minecraft.create()
    r = int(r)
    sphere(r, block)
    mc.getBlock(1,1,1) # prevent concurrent mod
    sphere(r-1, blocks.AIR)

def dome(r=10, block=blocks.STONE):
    "_mcp: create dome"
    mc = Minecraft.create()
    with exc_chat(mc):
        pos = mc.player.getTilePos()    
        r = int(r)
        block = block_by_name(block)
        for x, y, z, s in _sphere(r=r):
            if s <= r + 1 and s >= r - 1:
                mc.addBlock(x, y, z, block)

import random
import time

def rwalk(length=100,blktype=blocks.STONE):
    "_mcp: random walk"
    length = int(length)
    blktype = int(blktype)
    mc = Minecraft.create()
    pos = mc.player.getTilePos()
    x, y, z = pos.x, pos.y, pos.z
    for _ in range(length):
        while True:
            dxs = [ 0, 0, 0, 0, 1,-1]
            dys = [ 0, 0, 1,-1, 0, 0]
            dzs = [ 1,-1, 0, 0, 0, 0]
            d = random.randint(0,5)
            x2 = x + dxs[d]
            y2 = y + dys[d]
            z2 = z + dzs[d]
            b = mc.getBlock(x2, y2, z2)
            if b == 0:
                mc.setBlock(x2, y2, z2, blktype)
                mc.setBlock(x, y, z, 0)
                x, y, z = x2, y2, z2
                time.sleep(0.05)
                break

def copy(r=5):
    "_mcp: copy blocks"
    r = int(r)
    mc = Minecraft.create()
    pos = mc.player.getTilePos()
    x, y, z = pos.x, pos.y, pos.z
    ox, oy, oz = 0, 0, r * 2
    for i in range(-r,r):
        for j in range(-r,r):
            for k in range(-r,r):
                mc.setBlock(x+i+ox,y+j+oy,z+k+oz,blocks.BEACON)
                b = mc.getBlock(x+i,y+j,z+k)
                mc.setBlock(x+i+ox,y+j+oy,z+k+oz,b)

def maze(size=10,blktype=1,data=0):
    "_mcp: create a maze"
    from pplugins.maze import maze as gen_maze
    size = int(size)
    blktype = int(blktype)
    data = int(data)
    mc = Minecraft.create()
    pos = mc.player.getTilePos()
    px, py, pz = pos.x-size, pos.y, pos.z-size
    W = gen_maze(size)
    size = W.shape[0]
    for x in range(size):
        for z in range(size):
            if W[x,z]:
                sett = 0
                d = 0
            else:
                sett = blktype
                d = data
            mc.setBlock(px+x,py,pz+z,sett,d)
            mc.setBlock(px+x,py+1,pz+z,sett,d)

def pow():
    "_mcp: create an explosion"
    mc = Minecraft.create()
    pos = mc.player.getTilePos()
    x, y, z = pos.x, pos.y, pos.z
    mc.setBlock(x, y, z, blocks.TNT)
    mc.setBlock(x,y+1,z, blocks.FIRE)
    
def boom(size=1):
    "_mcp: create a very large explosion"
    mc = Minecraft.create()
    pos = mc.player.getTilePos()
    sphere(size, blocks.TNT)
    sphere(1, 51)

def gb(size=5):
    "_mcp: get blocks"
    mc = Minecraft.create()
    with exc_chat(mc):
        size = int(size)
        x, y, z = playerPos(mc.player)
        x1 = x - size
        y1 = y - size
        z1 = z - size
        x2 = x + size
        y2 = y + size
        z2 = z + size
        b = list(mc.getBlocks(x1,y1,z1,x2,y2,z2))
        l = (size*2)+1
        ib = 0
        for j in range(l):
            for k in range(l):
                for i in range(l):
                    bx = x + k - size
                    by = y + j + size
                    bz = z + i - size
                    mc.setBlock(bx, by, bz, b[ib])
                    ib += 1

def line(steps=100, block=blocks.WOOL, sphere=0):
    "_mcp: straight line from player"
    mc = Minecraft.create()
    with exc_chat(mc):
        block = block_by_name(block)
        steps = int(steps)
        sphere = int(sphere)
        d = mc.player.getDirection()
        dx = float(getattr(d,'x'))
        dy = float(getattr(d,'y'))
        dz = float(getattr(d,'z'))
        x, y, z = playerPos(mc.player)
        for n2 in range(steps*2):
            n = n2 / 2.
            i = int(x) + int(dx * n)
            j = int(y) + int(dy * n)
            k = int(z) + int(dz * n)
            mc.setBlock(i, j, k, block)
            if sphere > 0:
                for sx, sy, sz, s in _sphere(i, j, k, sphere):
                    if s < sphere:
                        mc.setBlock(sx, sy, sz, block)

def flood(size=10, block=blocks.WATER):
    "_mcp: flood surrounding area"
    with exc_chat() as mc:
        size = int(size)
        block = block_by_name(block)        
        x, y, z = playerPos(mc.player)
        heights = []
        for i in range(x-size, x+size):
            mc.postToChat('flood: scanning ({})...'.format(len(heights)))
            for k in range(z-size, z+size):
                j = mc.getHeight(i, k)
                heights.append(j)
        mc.postToChat('flood: flooding')
        n = 0
        for i in range(x-size, x+size):
            for k in range(z-size, z+size):
                j = heights[n]
                mc.setBlock(i, j+1, k, block)
                n += 1
