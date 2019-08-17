const assert = require('assert');

class Camera {
  /**
   * Constructs a new Camera object.
   *
   * @param {Minecraft} mc the Minecraft connection
   */
  constructor(mc) {
    assert(mc, 'mc is required');

    this.mc = mc;
  }

  /**
   * Sets the player's camera mode.
   *
   * @param {CameraModes} cameraMode
   *
   * @returns {Promise}
   */
  setMode(cameraMode) {
    assert(cameraMode, 'cameraMode is required');
    return this.mc.send(`camera.mode.set${cameraMode}()`);
  }

  /**
   * Sets the camera's position at the selected coordinates.
   *
   * @param {number} x
   * @param {number} y
   * @param {number} z
   *
   * @returns {Promise}
   */
  setPos(x, y, z) {
    assert(x !== undefined && x !== null, 'x is required');
    assert(y !== undefined && y !== null, 'y is required');
    assert(z !== undefined && z !== null, 'z is required');

    return this.mc.send(`camera.mode.setPos(${x},${y},${z})`);
  }
}

module.exports = Camera;
