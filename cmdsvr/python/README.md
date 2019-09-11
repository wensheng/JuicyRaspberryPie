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

From within Minecraft, issuing "/p shutdownserver" will also shutdown the server

## **Note**

Please note it's best to make a copy of the directory `\bukkit\src\main\resources`, then run command server within the copied directory -- just do `python cmdsvr/pycmdsvr.py`.

If using Spigot, don't edit directly with the generated `plugins/JuicyRaspberryPie` directory because the files there might be deleted or over-written inadvertently.
