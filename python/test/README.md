# Simple functional tests

To test local spigot server:

    ./run_test.py world
    ./run_test.py player
    ./run_test.py event
    ./run_test.py entity

To test local windows forge mod:

    python run_test.py --port=4712 world
    python run_test.py --port=4712 player
    python run_test.py --port=4712 event
    python run_test.py --port=4712 entity

To test remote bukkit server(e.g. 192.168.1.10) from local windows PC:

    python run_test.py --host=192.168.1.10 world
    python run_test.py --host=192.168.1.10 player
    python run_test.py --host=192.168.1.10 event
    python run_test.py --host=192.168.1.10 entity

API tested:

* mc.getBlock - world.getBlock
* mc.setBlock - world.setBlock
* mc.getBlockWithData - world.getBlockWithData
* mc.getBlocks - world.getBlocks
* mc.setBlocks - world.setBlocks
* mc.getPlayerEntityId - world.getPlayerEntityId
* mc.getPlayerEntityIds - world.getPlayerEntityIds
* mc.getHeight - world.getHeight
* mc.spawnEntity - world.spawnEntity
* mc.spawnParticle - world.spawnParticle
* mc.getNearbyEntities - world.getNearbyEntities
* mc.postToChat - chat.post
* mc.setSign - world.setSign
* mc.player.getTilePos - player.getTile
* mc.player.setTilePos - player.setTile
* mc.player.getPos - player.getPos
* mc.player.setPos - player.setPos
* mc.player.getRotation - player.getRotation
* mc.player.setRotation - player.setRotation
* mc.player.getPitch - player.getPitch
* mc.player.setPitch - player.setPitch
* mc.player.getDirection - player.getDirection
* mc.entity.getTilePos - entity.getTile
* mc.entity.setTilePos - entity.setTile
* mc.entity.getPos - entity.getPos
* mc.entity.setPos - entity.setPos
* mc.entity.getRotation - entity.getRotation
* mc.entity.setRotation - entity.setRotation
* mc.entity.getPitch - entity.getPitch
* mc.entity.setPitch - entity.setPitch
* mc.entity.getDirection - entity.getDirection
* mc.event.pollBlockHits - events.block.hits
* mc.event.pollChatPosts - events.chat.posts
* mc.event.pollProjectileHits - events.projectile.hits (todo)
* mc.event.clearAll - events.clear
* mc.setPlayer - setPlayer (todo, Bukkit only)
