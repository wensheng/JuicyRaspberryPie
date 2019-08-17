const assert = require('assert');
const net = require('net');
const util = require('util');

const debuglog = util.debuglog('mcpi');

/**
 * A promisified connection to a server
 */
class Connection {
  /**
   * Creates a connection with the given host and port
   *
   * @param {string} host the host
   * @param {number} port the port
   */
  constructor(host, port) {
    assert(host, 'host is required');
    assert(port, 'port is required');

    this.callbacks = [];
    this.isClosed = false;

    const self = this;
    this.promise = new Promise((resolve, reject) => {
      self.callbacks.push((err, data) => { // eslint-disable-line no-unused-vars
        if (err) {
          reject(err);
        }

        // ignore data
      });

      const socket = net.connect({ port, host }, () => {
        self.callbacks.shift();
        resolve(socket);
      });

      this.setupSocket(socket);
    });
  }

  write(data) {
    assert(data, 'data is required');
    assert(!this.closed, 'connection is closed');

    const self = this;
    return this.promise.then(socket =>
      new Promise((resolve, reject) => {
        self.pushPromiseCallback(resolve, reject);
        debuglog('Writing data:', data);
        socket.write(data, err => {
          const callback = self.callbacks.shift();
          if (err) {
            callback(err);
            return;
          }
          resolve();
        });
      }));
  }

  writeAndRead(data) {
    assert(data, 'command is required');
    assert(!this.closed, 'connection is closed');

    const self = this;
    return this.promise.then(socket =>
      new Promise((resolve, reject) => {
        self.pushPromiseCallback(resolve, reject);
        debuglog('Writing data for read:', data);
        socket.write(data, err => {
          const callback = self.callbacks[0];
          if (err) {
            callback(err);
          }
        });
      }));
  }

  pushPromiseCallback(resolve, reject) {
    this.callbacks.push((err, data) => {
      if (err) {
        reject(err);
        return;
      }

      resolve(data.toString());
    });
  }

  setupSocket(socket) {
    assert(socket, 'socket is required');

    const self = this;
    socket.on('error', err => {
      debuglog('Socket error:', err);

      const callback = self.callbacks.shift();
      if (callback) {
        callback(err);
      }
    });

    socket.on('data', data => {
      debuglog(`Got data: ${data}`);

      const callback = self.callbacks.shift();
      if (callback) {
        callback(null, data);
      }
    });

    socket.on('end', () => {
      debuglog('Server disconnected.');
    });
  }

  get closed() {
    return this.isClosed;
  }

  close() {
    if (this.closed) {
      return Promise.resolve();
    }

    const self = this;
    return this.promise.then(socket => {
      self.isClosed = true;
      socket.end();
    });
  }
}

module.exports = Connection;
