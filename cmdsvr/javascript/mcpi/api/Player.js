const assert = require('assert');

class Player {
  /**
   * Constructs a new Player object.
   *
   * @param {Minecraft} mc the Minecraft connection
   */
  constructor(mc) {
    assert(mc, 'mc is required');

    this.mc = mc;
  }

  /**
   * Gets the player's coordinates to the nearest block.
   *
   * @returns {Promise.<{x,y,z}>} returns players x,y,z coordinates
   */
  getTilePos() {
    return this.mc.sendReceive('player.getTile()')
      .then(data => {
        const parts = data.split(',');
        return {
          x: Number(parts[0]),
          y: Number(parts[1]),
          z: Number(parts[2]),
        };
      });
  }

  /**
   * Sets the player's coordinates to the specified block.
   *
   * @param x
   * @param y
   * @param z
   *
   * @returns {Promise}
   */
  setTilePos(x, y, z) {
    assert(x !== undefined && x !== null, 'x is required');
    assert(y !== undefined && y !== null, 'y is required');
    assert(z !== undefined && z !== null, 'z is required');

    return this.mc.send(`player.setTile(${x},${y},${z})`);
  }

  /**
   * Gets the precise position of the player.
   *
   * @returns {Promise.<{x,y,z}>} returns players x,y,z position
   */
  getPos() {
    return this.mc.sendReceive('player.getPos()')
      .then(data => {
        const parts = data.split(',');
        return {
          x: Number(parts[0]),
          y: Number(parts[1]),
          z: Number(parts[2]),
        };
      });
  }

  /**
   * Sets the position of the player precisely.
   *
   * @param x
   * @param y
   * @param z
   *
   * @returns {Promise}
   */
  setPos(x, y, z) {
    assert(x !== undefined && x !== null, 'x is required');
    assert(y !== undefined && y !== null, 'y is required');
    assert(z !== undefined && z !== null, 'z is required');

    return this.mc.send(`player.setPos(${x},${y},${z})`);
  }

  /**
   * Sets a player property.
   *
   * Values are boolean, 0 or 1.
   *
   * @param {PlayerSettings} key
   * @param {*} value
   *
   * @returns {Promise}
   */
  setProperty(key, value) {
    assert(key, 'key is required');
    return this.mc.send(`player.setting(${key},${value}`);
  }
}

module.exports = Player;
