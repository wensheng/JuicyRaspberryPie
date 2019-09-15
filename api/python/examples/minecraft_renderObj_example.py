#www.stuffaboutcode.com
#Raspberry Pi, Minecraft - Create 3D Model from Obj file
# Version 2 - draws complete faces rather than wireframes and uses materials

# make it work with JuicyRaspberryPie
# replace [wool,id] with just "color_wool"
#  find color from following table
# 0	white
# 1	orange
# 2	magenta
# 3	light_blue
# 4	yellow
# 5	lime
# 6	pink
# 7	gray
# 8	silver
# 9	cyan
# 10 purple
# 11 blue
# 12 brown
# 13 green
# 14 red
# 15 black

import sys
import time
import datetime
from minecraft_renderObj import Vec3, MinecraftDrawing, load_obj, getVertexXYZ

import path_helper
from mcpi.minecraft import Minecraft

model_no = 0
if len(sys.argv) > 1 and sys.argv[1].isnumeric():
    model_no = int(sys.argv[1])

print(datetime.datetime.now())
print("Model #:", model_no)
mc = Minecraft.create()
mcDrawing = MinecraftDrawing(mc)
playerPos = mc.player.getTilePos()

materials = None

# COORDSSCALE = factor to scale the co-ords by
# STARTCOORD = where to start the model, the relative position 0
# CLEARAREA1/2 = 2 points the program should clear an area in between to put the model in
# SWAPYZ = True to sway the Y and Z dimension
# MATERIALS = a dictionary object which maps materials in the obj file to blocks in minecraft
# DEFAULTBLOCK = the default type of block to build the model in, used if a material cant be found

if model_no == 1:
    # Space Shuttle
    COORDSSCALE = 6
    STARTCOORD = Vec3(playerPos.x-60, playerPos.y, playerPos.z+20)
    CLEARAREA1 = Vec3(playerPos.x-30, playerPos.y+5, playerPos.z-30)
    CLEARAREA2 = Vec3(playerPos.x-90, playerPos.y+50, playerPos.z+30)
    DEFAULTBLOCK = "white_wool"
    MATERIALS = {"glass": "glass",
                 "bone": "white_wool",
                 "fldkdkgrey": "gray_wool",
                 "redbrick": "red_wool",
                 "black": "black_wool",
                 "brass": "orange_wool",
                 "dkdkgrey": "gray_wool"}
    SWAPYZ = True
    vertices,textures,normals,faces,materials = load_obj("shuttle.obj", DEFAULTBLOCK, MATERIALS)

elif model_no == 2:
    # donut
    COORDSSCALE = 2
    STARTCOORD = Vec3(0,0,0)
    CLEARAREA1 = Vec3(-100, -30, -100)
    CLEARAREA2 = Vec3(100, 70, 100)
    DEFAULTBLOCK = "gold_block"
    MATERIALS = {}
    SWAPYZ = False  # change to True to lay donut on side
    vertices,textures,normals,faces,materials = load_obj("Torus.zip", DEFAULTBLOCK, MATERIALS)

elif model_no == 3:
    # donut
    COORDSSCALE = 2
    STARTCOORD = Vec3(0,-30,0)
    CLEARAREA1 = Vec3(-100, 0, -100)
    CLEARAREA2 = Vec3(100, 70, 100)
    DEFAULTBLOCK = "diamond_block"
    MATERIALS = {"Turquoise_0": "diamond_block"}
    SWAPYZ = True
    vertices,textures,normals,faces,materials = load_obj("ChessSet.zip", DEFAULTBLOCK, MATERIALS)

elif model_no == 4:
    # Raspbery Pi
    COORDSSCALE = 1350
    STARTCOORD = Vec3(playerPos.x-50, playerPos.y, playerPos.z)
    CLEARAREA1 = Vec3(playerPos.x-60, playerPos.y, playerPos.z-80)
    CLEARAREA2 = Vec3(playerPos.x+100, playerPos.y+20, playerPos.z+40)
    DEFAULTBLOCK = "dirt"
    MATERIALS = {"Default_Material": "white_wool",
                 "Material1": "lime_wool",
                 "Goldenrod": "orange_wool",
                 "0136_Charcoal": "gray_wool",
                 "Gray61": "gray_wool",
                 "Charcoal": "gray_wool",
                 "Color_002": "light_gray_wool",
                 "Color_008": "yellow_wool",
                 "Plastic_Green": "lime_wool",
                 "MB_Pastic_White": "white_wool",
                 "IO_Shiny": "iron_block",
                 "Material4": "grass_block",
                 "Gainsboro3": "lime_wool",
                 "CorrogateShiny1": "iron_block",
                 "Gold": "gold_block",
                 "0129_WhiteSmoke": "white_wool",
                 "Color_005": "white_wool",
                 "USB_IO": "blue_wool",
                 "_Metal": "iron_block",
                 "0132_LightGray": "light_gray_wool"}
    SWAPYZ = False
    vertices,textures,normals,faces, materials = load_obj("RaspberryPi.zip", DEFAULTBLOCK, MATERIALS)

else:
    # Cube
    COORDSSCALE = 10
    # do a "/kill username" to find this cube
    STARTCOORD = Vec3(0,10,0)
    CLEARAREA1 = Vec3(-10, 0, -10)
    CLEARAREA2 = Vec3(10, 20, 10)
    DEFAULTBLOCK = "stone"
    MATERIALS = {}
    SWAPYZ = False
    vertices,textures,normals,faces,materials = load_obj("cube.obj", DEFAULTBLOCK, MATERIALS)

if not materials:
    exit("no obj file, will exit.")
else:
    print("obj file loaded")

# clear a suitably large area
mc.setBlocks(CLEARAREA1.x, CLEARAREA1.y, CLEARAREA1.z, CLEARAREA2.x, CLEARAREA2.y, CLEARAREA2.z, "air")
time.sleep(2)

faceCount = 0
# loop through faces
for face in faces:
    faceVertices = []
    
    # loop through vertex's in face and call drawFace function
    for vertex in face:
        #strip co-ords from vertex line
        vertexX, vertexY, vertexZ = getVertexXYZ(vertices[vertex[0]], COORDSSCALE, STARTCOORD, SWAPYZ)

        faceVertices.append(Vec3(vertexX,vertexY,vertexZ))
                   
    # draw the face
    mcDrawing.drawFace(faceVertices, materials[faceCount])
    faceCount = faceCount + 1

mc.postToChat("Model complete, www.stuffaboutcode.com")

print(datetime.datetime.now())
