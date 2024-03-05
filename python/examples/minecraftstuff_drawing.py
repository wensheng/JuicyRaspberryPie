import path_helper
from minecraftstuff import Vec3, MinecraftDrawing, ShapeBlock
from mcpi.minecraft import Minecraft

#connect to minecraft
mc = Minecraft.create()

#test MinecraftDrawing

#clear area
mc.setBlocks(-25, 0, -25, 25, 25, 25, "air")

#create drawing object
mcDrawing = MinecraftDrawing(mc)

#line
mcDrawing.drawLine(0,0,-10,-10,10,-5,"stone")

#circle
mcDrawing.drawCircle(-15,15,-15,10,"WOOD")

#sphere
mcDrawing.drawSphere(-15,15,-15,5,"OBSIDIAN")

#face - solid triangle
faceVertices = []
faceVertices.append(Vec3(0,0,0))
faceVertices.append(Vec3(5,10,0))
faceVertices.append(Vec3(10,0,0))
mcDrawing.drawFace(faceVertices, True, "snow_block")

faceVertices = []
faceVertices.append(Vec3(0,0,5))
faceVertices.append(Vec3(10,0,5))
faceVertices.append(Vec3(10,10,5))
faceVertices.append(Vec3(0,10,5))
mcDrawing.drawFace(faceVertices, False, "diamond_block")

#face - 5 sided shape
faceVertices = []
faceVertices.append(Vec3(0,15,0))
faceVertices.append(Vec3(5,15,5))
faceVertices.append(Vec3(3,15,10))
faceVertices.append(Vec3(-3,15,10))
faceVertices.append(Vec3(-5,15,5))
mcDrawing.drawFace(faceVertices, True, "gold_block")
