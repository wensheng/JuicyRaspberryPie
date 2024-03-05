import path_helper

from mcpi.minecraft import Minecraft
from minecraftstuff import MinecraftTurtle

# connect to minecraft
mc = Minecraft.create()
pos = mc.player.getTilePos()
print(pos)
# create minecraft turtle
turtle = MinecraftTurtle(mc, pos)

# tests
# draw a pentagon at different speeds
turtle.forward(5)
turtle.right(72)
turtle.speed(8)
turtle.forward(5)
turtle.right(72)
turtle.speed(10)
turtle.forward(5)
turtle.right(72)
turtle.speed(0)
turtle.forward(5)
turtle.right(72)
turtle.forward(5)

# change pen
turtle.penblock("white_wool")

# backward
turtle.speed(6)
turtle.backward(10)

# change pen
turtle.penblock("red_wool")

# pen up/down
turtle.penup()
turtle.forward(20)
turtle.pendown()

# change pen
turtle.penblock("green_wool")

# up, down, left
turtle.up(30)
turtle.forward(5)
turtle.right(72)
turtle.forward(5)
turtle.down(30)
turtle.left(72)
turtle.forward(5)

# change pen
turtle.penblock("light_blue_wool")

# walking
turtle.walk()
turtle.forward(10)
