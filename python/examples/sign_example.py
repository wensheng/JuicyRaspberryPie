import path_helper
from mcpi.minecraft import mcpy, Minecraft


@mcpy
def sign(signType='BIRCH_SIGN', direction="2", *args):
    mc = Minecraft.create()
    pos = mc.player.getTilePos()
    mc.setSign(pos.x + 1 , pos.y, pos.z, signType, int(direction), *args) 

if "__main__" == __name__:
    sign("oak_sign", 1, "Hello World", "你好世界", "This is oak_sign")


