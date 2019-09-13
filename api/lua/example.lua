Minecraft = require "mcpi.minecraft"
mc = Minecraft:new{}
-- for Forge, use port 4712
-- mc = Minecraft:new{port=4712}

x, y, z = mc:getPlayerPos()
if x == nil then
    os.exit()
end

mc:setBlock(x+3, y, z, "gold_block")
mc:sleep(1)
mc:spawnEntity(x+2, y, z, "turtle")
mc:sleep(1)
for i = 1, 10 do
    mc:spawnParticle(x+2, y+1, z+2, {"heart"})
    mc:sleep(0.3)
end
mc:spawnParticle(x+2, y+1, z+2, {"smoke", 1000, 0.01})
