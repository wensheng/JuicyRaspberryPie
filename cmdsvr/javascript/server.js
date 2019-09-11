const path = require("path")
const net = require("net")
const fs = require("fs")
const SVRHOST = process.env.svrhost || 'localhost'
const SVRPORT = process.env.svrport? parseInt(process.env.svrport): 4732

var pplugins_path = path.join(__dirname, "pplugins")

global.ppf = {}
fs.readdirSync(pplugins_path).forEach(file => {
    if(file.endsWith(".js")){
        var func_name = file.substring(0, file.length-3)
        global.ppf[func_name] = require(path.join(pplugins_path, file))
    }
});


var sockets = []
console.log(`Starting command server on ${SVRHOST}:${SVRPORT}`)
net.createServer(function(socket){
  sockets.push(socket)
  socket.setNoDelay()
  socket.on('data', chunk => {
    //chunk: {"type":"Buffer","data":[...]}
    // in Java client, we use BufferedReader.readLine(), so we must add '\n' to our reply
    var args = chunk.toString().substr(2).split(" ")
    var cmd = args[0]
    var s
    console.log("got command: " + cmd)
    if(cmd == 'list'){
      s = "Available commands: " + Object.keys(global.ppf).join() + '\n'
      socket.write(Buffer.from(s))
    }else if(cmd == 'help'){
      s = 'JuicyRaspberryPie: put your Python files in pplugins, then "/p cmd" to call your function, "/p list" to see list of commands\n'
      socket.write(Buffer.from(s))
    }else if (cmd == 'shutdownserver'){
      s = 'Shutdown request received, server will be shutdown.\n'
      socket.write(Buffer.from(s))
      socket.emit('destroy')
      socket.end()
      console.log("shutdown socket")
      socket.unref()
      process.exit()
    } else if(cmd in global.ppf){
      args.shift()
      socket.write(Buffer.from("ok\n"))
      global.ppf[cmd](...args)
    } else {
      socket.write(Buffer.from("Whaat?\n"))
    }
  })
  socket.on('error', err => {
    console.log(`Error: ${err}`)
  })
}).listen(SVRPORT, 'localhost')


//global.ppf['cube'](5, 'stone')
