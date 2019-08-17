const assert = require('assert');

class Events {
  /**
   * Constructs a new Events object.
   *
   * @param {Minecraft} mc the Minecraft connection
   */
  constructor(mc) {
    assert(mc, 'mc is required');

    this.mc = mc;
  }

  // These are in need of proper documentation. If you know about these, please send a pull request! :-)
  blockHits() {
    return this.mc.sendReceive('events.block.hits()');
  }

  clear() {
    return this.mc.send('events.clear()');
  }
}

module.exports = Events;

