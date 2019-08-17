const mcpi = require('../mcpi')

module.exports = function(size=5, typeName='stone'){
  const mc = new mcpi.Minecraft()
  size = parseInt(size)
  mc.player.getTilePos()
    .then(pos => {
      console.log("player at " + JSON.stringify(pos))
      mc.world.setBlocks(pos.x + 1, pos.y, pos.z,
                         pos.x + size, pos.y + size -1, pos.z + size -1,
                         typeName) 
    })
    .then(() => {
      mc.close()
    })
}
