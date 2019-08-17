const mcpi = require('../mcpi')

const flatten = function(size=50){
  const mc = new mcpi.Minecraft()
  const s = parseInt(size)
  mc.player.getTilePos()
    .then(pos => {
      mc.world.setBlocks(pos.x + 1, pos.y, pos.z,
                         pos.x + size, pos.y + size -1, pos.z + size -1,
                         'SANDSTONE') 
      mc.world.setBlocks(pos.x - s, pos.y, pos.z - s,
                         pos.x + s, pos.y + 64, pos.z + s,
                         'AIR') 
    })
    .then(() => {
      mc.close()
    })
}

module.exports = flatten
