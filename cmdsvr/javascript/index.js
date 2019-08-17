const process = require("process")
const child_process = require("child_process")
const fs = require("fs")

var server = child_process.fork("server.js")
console.log("Server Started")

fs.watch("./pplugins/", function(event, filename){
    console.log("change detected, restart server")
    server.kill()
    server = child_process.fork("server.js")
})
