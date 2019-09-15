import path_helper
from minecraftstuff import MinecraftShape, ShapeBlock
from mcpi.minecraft import Minecraft
import time

#connect to minecraft
mc = Minecraft.create()

#test MinecraftShape
playerPos = mc.player.getTilePos()

#create the shape object
bt = "diamond_block"
shapeBlocks = [ShapeBlock(0,0,0,bt),
               ShapeBlock(1,0,0,bt),
               ShapeBlock(1,0,1,bt),
               ShapeBlock(0,0,1,bt),
               ShapeBlock(0,1,0,bt),
               ShapeBlock(1,1,0,bt),
               ShapeBlock(1,1,1,bt),
               ShapeBlock(0,1,1,bt)]

#move the shape about
myShape = MinecraftShape(mc, playerPos, shapeBlocks)
print("drawn shape")
time.sleep(10)
myShape.moveBy(-1,1,-1)
time.sleep(1)
myShape.moveBy(1,0,1)
time.sleep(1)
myShape.moveBy(1,1,0)
time.sleep(1)

#rotate the shape
myShape.rotate(90,0,0)

#clear the shape
myShape.clear()
