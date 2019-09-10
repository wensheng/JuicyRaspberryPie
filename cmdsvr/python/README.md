# Running JuicyRaspberryPie Python command server

## Start

If using JuicyRaspberryPie Spigot plugin:

    python startsvr.py

If using JuicyRaspberryPie Forge mod:

    python startsvr.py --apiport=4712 --svrport=4732

On Linux, you can omit "python", just do "./startsvr.py".

If you changed the default host/port number, just specify the correct --apihost/--apiport/--svrhost/--svrport options.
 
## shutdown 

Press Ctrl-C will shutdown the command server.

From within Minecraft, issuing "/p shutdownserver" or "/py shutdownserver" will also shutdown the server
