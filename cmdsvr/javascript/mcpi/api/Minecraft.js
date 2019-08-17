const assert = require('assert');

const Connection = require('../server/Connection');

const Camera = require('./Camera');
const Chat = require('./Chat');
const Events = require('./Events');
const Player = require('./Player');
const World = require('./World');

class Minecraft {
  /**
   * Constructs a new Minecraft object.
   *
   * @param {string} host the host
   * @param {number} port the port
   */
  constructor(host="localhost", port=4712) {
    assert(host, 'host is required');
    assert(port, 'port is required');

    this.host = host;
    this.port = port;

    // When a new `Minecraft` is created, it connects to the port and host given.
    this.connection = new Connection(host, port);
  }

  get camera() {
    if (!this.cameraObj) {
      this.cameraObj = new Camera(this);
    }
    return this.cameraObj;
  }

  get chat() {
    if (!this.chatObj) {
      this.chatObj = new Chat(this);
    }
    return this.chatObj;
  }

  get events() {
    if (!this.eventsObj) {
      this.eventsObj = new Events(this);
    }
    return this.eventsObj;
  }

  get player() {
    if (!this.playerObj) {
      this.playerObj = new Player(this);
    }
    return this.playerObj;
  }

  get world() {
    if (!this.worldObj) {
      this.worldObj = new World(this);
    }
    return this.worldObj;
  }

  // low-level
  /**
   * Sends a message to the Minecraft server.
   *
   * @param {string} command the command to send
   *
   * @return {Promise} promise indicating when the message is written
   */
  send(command) {
    assert(command, 'command is required');

    // If you're wanting to write your own API, this makes sure commands are written end with a new line.
    // Without it, the commands will not work!
    return this.connection.write(`${command}\n`);
  }

  /**
   * Sends a command to the Minecraft server.
   *
   * @param {string} command the command to send
   *
   * @return {Promise<string>} promise with the response from the server
   */
  sendReceive(command) {
    assert(command, 'command is required');
    return this.connection.writeAndRead(`${command}\n`)
      .then(data => {
        if (data === 'Fail\n') {
          throw Error(`Got Fail response for command: ${command}`);
        }

        return data;
      });
  }

  /**
   * Close the connection to the Minecraft server.
   *
   * @returns {Promise} promise indicating when the connection is closed.
   */
  close() {
    return this.connection.close();
  }
}

module.exports = Minecraft;
