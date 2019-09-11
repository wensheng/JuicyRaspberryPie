# JuicyRaspberryPie Python API

## class Minecraft

`Minecraft` is the main class for interacting with the Minecraft world, includes functions for creating a connection, modifying player, create/edit/delete blocks, entities and capturing events.

Every program should start with the line:

    from mcpi.minecraft import Minecraft

**.create(address = "localhost", port = 4711)**
Create connection to Minecraft (address, port) => Minecraft object

    mc = Minecraft.create()
    #specify ip address and port
    mc = Minecraft.create("192.168.1.50", 4712)

**.player.getTilePos()**
Get the position of the tile the player is on.  (see .player for more player functions)
    pos = mc.player.getTilePos()

**.getBlock(x0, y0, z0)**
retrieves the block type for the block at x, y, z

**.getBlocks(x0, y0, z0, x1, y1, z1)**
Get a cuboid of blocks (x0,y0,z0,x1,y1,z1) => [id:int]

**.getBlockWithData(x,y,z)**
Get block with data (x,y,z) => Block

**.setBlock(x,y,z)**
Set block (x, y, z, block_type, [data])

see [block_types.md](block_types.md) for a list of block types.

**.setBlocks(x0,y0,z0,x1,y1,z1,blockType, blockData)**
Set a cuboid of blocks (x0,y0,z0,x1,y1,z1,block_type,[data])

**.getHeight(x,z)**
Get the height of the world (x,z) => int

find the y (vertical) of an x, z co-ordinate which represents the 'highest' (non-air) block

**.getPlayerEntityIds()**
Get the entity ids of the connected players => [id:int]


**.getPlayerEntityId(playerName)**
Get the entity id for a named player => [id:int]


## Minecraft.player

**.getPos()**
get players position as floatsplayerPos = mc.player.getPos()

**.setPos(x,y,z)**
Moves the player to a position in the world by passing co-ordinates ([x,y,z])

**.setTilePos(x,y,z)**
Move the player to a tile position in the world by passing co-ordinates ([x,y,z])

**.getRotation()**
Get the rotational angle (0 to 360) for the player => [angle:float]

**.getPitch()**
Get the pitch angle (-90 to 90) for the player => [pitch:float]

**.getDirection()**
Get unit vector of x,y,z for the player's direction => [Vec3]

## Minecraft.entity

The entity functions are used in conjunction with the .spawnEntity() and .getNearbyEntities() functions to interact with the entity in a game.

**.getPos(entityId)**
get entity position as float

**.setPos(entityId, x, y, z)**
set entity position

**.getRotation(entityId)**
Get the rotational angle (0 to 360) for an entity => [angle:float]"

**.getPitch(entityId)**
Get the pitch angle (-90 to 90) for an entity => [pitch:float]


**.getDirection(entityId)**
get entity direction => Vec3


**.pollBlockHits()**
Block Hits (Only triggered by sword) => [BlockEvent]

**.pollChatPosts()**
Chat posts => [ChatEvent]

**.clearAll()**
Clear all old events

### BlockEvent

"The definition of a BlockEvent in Minecraft, used to describe an event in Minecraft affecting blocks; returned by the Minecraft.events.pollBlockHits() method."
blockEvent = mc.events.pollBlockHits()


blockEventType = blockEvent.type


**.pos**"The position of the block where the event occured, i.e. the block which was hit.  .pos returns a Vec3 object of x,y,z co-ordinates"

blockEventPos = BlockEvent.pos
**.face**"The face of the block where the event occured"

blockEventFace = BlockEvent.face
**.entityId**"entityId of the player who caused the block event, i.e. the player who hit the block"

blockEventPlayer - BlockEvent.entityId
"The definition of a ChatEvent in Minecraft, used to describe an event when a message is posted to the chat bar in Minecraft, returned by Minecraft.events.pollBlockHits() method."

**.type**"Type of block event; there is only 1 event currently implemented ChatEvent.POST"

chatEventType = chatEvent.type
*ChatEvent types:*0: ChatEvent.POST


chatEventMessage = ChatEvent.message


blockEventPlayer - BlockEvent.entityId

## Vec3

A 3-part vector in Minecraft, i.e. a set of x, y, z co-ordinates; x and z are the horizontal positions, y the vertical"
