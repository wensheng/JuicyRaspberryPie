Put your Python files here.
In your Python file, define functions with doctring that starts with "_mcp", 
or, decorate them with "@mcpy", these functions will then be available in Minecraft as "p command".

For example, you define 2 functions: hi and blk as follow:

# --------------------------------------------------
from mcpi.minecraft import Minecraft, mcpy

def hi():
    "_mcp: just saying hello"
    mc = Minecraft.create()
    mc.postToChat("Hello!")

@mcpy
def blk():
    "place 10 blocks of diamond"
    mc = Minecraft.create()
    pos = mc.player.getTilePos()
    for i in range(10):
        mc.setBlock(pos.x+1, pos.y+i, pos.z, 57)
# --------------------------------------------------

Save this file as test.py in pplugins folder.

Restart the server, now you can use "/p hi" and "/p blk" in Minecraft.
        
        
