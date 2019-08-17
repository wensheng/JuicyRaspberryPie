const assert = require('assert');

class World {
  /**
   * Constructs a new World object.
   *
   * @param {Minecraft} mc the Minecraft connection
   */
  constructor(mc) {
    assert(mc, 'mc is required');

    this.mc = mc;
  }

  /**
   * Returns the block ID and data at the selected coordinates.
   *
   * @param {number} x
   * @param {number} y
   * @param {number} z
   *
   * @returns {Promise.<number>} a promise with block ID
   */
  getBlock(x, y, z) {
    assert(x !== undefined && x !== null, 'x is required');
    assert(y !== undefined && y !== null, 'y is required');
    assert(z !== undefined && z !== null, 'z is required');

    return this.mc.sendReceive(`world.getBlock(${x},${y},${z})`)
      .then(data => Number(data));
  }

  /**
   * Returns the block ID and data at the selected coordinates.
   *
   * @param {number} x
   * @param {number} y
   * @param {number} z
   *
   * @returns {Promise.<{blockId,dataValue}>} a promise with data in the form <code>{blockId,dataValue}</code>
   */
  getBlockWithData(x, y, z) {
    assert(x !== undefined && x !== null, 'x is required');
    assert(y !== undefined && y !== null, 'y is required');
    assert(z !== undefined && z !== null, 'z is required');

    return this.mc.sendReceive(`world.getBlockWithData(${x},${y},${z})`)
      .then(data => {
        const parts = data.split(',');
        return {
          id: Number(parts[0]),
          data: Number(parts[1]),
        };
      });
  }

  /**
   * Places a block with the ID of `id` at the selected coordinates, plus data if it is appended.
   * You can use `mcpi.Blocks.BLOCK_NAME` instead of the actual ID.
   *
   * @param {number} x
   * @param {number} y
   * @param {number} z
   * @param {number} id block ID
   * @param {number} [data] block data (optional)
   *
   * @returns {Promise}
   */
  setBlock(x, y, z, id, data) {
    assert(x !== undefined && x !== null, 'x is required');
    assert(y !== undefined && y !== null, 'y is required');
    assert(z !== undefined && z !== null, 'z is required');
    assert(id !== undefined && id !== null, 'id is required');

    if (data) {
      return this.mc.send(`world.setBlock(${x},${y},${z},${id},${data})`);
    }

    return this.mc.send(`world.setBlock(${x},${y},${z},${id})`);
  }

  /**
   * Places a cuboid of blocks with the coordinate set using the specified id and data.
   * You can use `mcpi.Blocks.BLOCK_NAME` instead of the actual ID.
   *
   * @param {number} x1 x start
   * @param {number} y1 y start
   * @param {number} z1 z start
   * @param {number} x2 x end (inclusive)
   * @param {number} y2 y end (inclusive)
   * @param {number} z2 z end (inclusive)
   * @param {number} id block ID
   * @param {number} [data] block data (optional)
   *
   * @returns {Promise}
   */
  setBlocks(x1, y1, z1, x2, y2, z2, id, data) {
    assert(x1 !== undefined && x1 !== null, 'x1 is required');
    assert(y1 !== undefined && y1 !== null, 'y1 is required');
    assert(z1 !== undefined && z1 !== null, 'z1 is required');
    assert(x2 !== undefined && x2 !== null, 'x2 is required');
    assert(y2 !== undefined && y2 !== null, 'y2 is required');
    assert(z2 !== undefined && z2 !== null, 'z2 is required');
    assert(id !== undefined && id !== null, 'id is required');

    if (data) {
      return this.mc.send(`world.setBlocks(${x1},${y1},${z1},${x2},${y2},${z2},${id},${data})`);
    }

    return this.mc.send(`world.setBlocks(${x1},${y1},${z1},${x2},${y2},${z2},${id})`);
  }

  /**
   * Returns the Y coordinate of the last block that isn't solid from the top-down in the coordinate pair.
   *
   * @param {number} x
   * @param {number} z
   *
   * @returns {Promise.<number>} a promise with block ID
   */
  getHeight(x, z) {
    assert(x !== undefined && x !== null, 'x is required');
    assert(z !== undefined && z !== null, 'z is required');

    return this.mc.sendReceive(`world.getHeight(${x},${z})`)
      .then(data => Number(data));
  }

  /**
   * Saves a checkpoint that can be used to restore the world.
   *
   * @return {Promise} promise indicating when save is requested
   */
  saveCheckpoint() {
    return this.mc.send('world.checkpoint.save()');
  }

  /**
   * Restores to the last checkpoint.
   *
   * @return {Promise} promise indicating when save is requested
   */
  restoreCheckpoint() {
    return this.mc.send('world.checkpoint.restore()');
  }

  /**
   * Sets a world property.
   *
   * Values are boolean, 0 or 1.
   *
   * @param {WorldSettings} key
   * @param {*} value
   *
   * @returns {Promise}
   */
  setProperty(key, value) {
    assert(key, 'key is required');
    return this.mc.send(`world.setting(${key},${value})`);
  }

  /**
   * Returns the entity IDs of the players online.
   *
   * @returns {Promise.<Array<Number>>} array of player IDs
   */
  getPlayerIds() {
    return this.mc.sendReceive('world.getPlayerIds()')
      .then(data => data.split(',').map(idValue => Number(idValue)));
  }
}

module.exports = World;
