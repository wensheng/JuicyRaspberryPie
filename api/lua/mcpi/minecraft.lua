local socket = require "socket"

local Minecraft = {}
function Minecraft:new(arg)
    o = {}
    setmetatable(o, self)
    self.__index = self
    arg.host = arg.host or 'localhost'
    arg.port = arg.port or 4711
    self.conn = socket.connect(arg.host, arg.port)
    if self.conn == nil then
        print("Could not connect to Minecraft")
        os.exit()
    end
    self.conn:settimeout(10)
    return o
end

function Minecraft:sleep(f)
    socket.sleep(f)
end

function Minecraft:send(m, d)
    local s = m .. "(" .. table.concat(d, ",") .. ")\n"
    self.conn:send(s)
end

function Minecraft:sendReceive(m, d)
    self:send(m, d)
    local s, status, partial
    while true do
        s, status, partial = self.conn:receive()
        break
    end
    return s
end

function Minecraft:getPlayerPos()
    local result = self:sendReceive("player.getTile",{})
    local x, y, z = string.match(result, "(.*),(.*),(.*)")
    if y == nil or z == nil then
        print(result)
        x, y = nil, nil
    else
        x, y, z = 0+x, 0+y, 0+z
    end
    return x, y, z
end

-- It probably makes more sense to return a table {x, y, z}
-- Then all following function ask for {x,y,z} 
-- for example: setBlock({x,y,z}, block_type)

function Minecraft:setBlock(x, y, z, block_type)
    self:send("world.setBlock", {x, y, z, block_type})
end

function Minecraft:spawnEntity(x, y, z, entity_type)
    self:send("world.spawnEntity", {x, y, z, entity_type})
end

function Minecraft:spawnParticle(x, y, z, particle)
    -- expect particle to be a table {name, count, speed}
    local args = {x, y, z}
    for _, v in ipairs(particle) do
        table.insert(args, v)
    end
    self:send("world.spawnParticle", args)
end

-- TODO: rest of API
-- should be pretty straight forward to implement

return Minecraft
