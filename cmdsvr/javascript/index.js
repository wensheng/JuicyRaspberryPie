const child_process = require("child_process")
const fs = require("fs")
const argv = require("yargs").argv

if(argv.apihost){
    process.env.apihost = argv.apihost
}
if(argv.apiport){
    process.env.apiport = argv.apiport
}
if(argv.apihost){
    process.env.svrhost = argv.svrhost
}
if(argv.apiport){
    process.env.svrport = argv.svrport
}

var server = child_process.fork("server.js")
console.log("Server Started")

fs.watch("./pplugins/", function(event, filename){
    console.log("change detected, restart server")
    server.kill()
    server = child_process.fork("server.js")
})
