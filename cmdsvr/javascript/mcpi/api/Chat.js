const assert = require('assert');

class Chat {
  /**
   * Constructs a new Chat object.
   *
   * @param {Minecraft} mc the Minecraft connection
   */
  constructor(mc) {
    assert(mc, 'mc is required');

    this.mc = mc;
  }

  /**
   * Displays a message in the chat.
   *
   * @param {string} message the message
   * @returns {Promise}
   */
  post(message) {
    assert(message, 'message is required');
    return this.mc.send(`chat.post(${message})`);
  }

}

module.exports = Chat;
