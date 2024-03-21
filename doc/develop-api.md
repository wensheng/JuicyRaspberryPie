# How to develop JuicyRaspberryPie API in any language

When we say API here, we really means client API library, because a application programming interface (API) is on the server side.  We usually make API calls to server using a client library, and we often (mistakenly) just refer client library as API.

Minecraft with JuicyRaspberryPie exposes API through a socket server.  When one make API calls, she first acquires a socket to the server, then sends and receives information through the socket to/from the server.  A client library make it much easier to use the socket and hide a lot of complexity.

The API can be accessed without any library because it's a standard socket.  You can use a program called `Telnet` to access it.

## Using Telnet to access JuicyRaspberryPie API

The socket server in JuicyRaspberryPie Spigot plugin by default listen on port 4711, the one in Forge listen on port 4712.

On Linux or Mac, login to Minecraft, then open a terminal window, and type:

    telnet localhost 4711

The socket connection is established if you see:

    Connected to localhost.
    Escape character is '^]'.

Now type any API commands, for example:

    player.getTile()
    62,-6,-28
    world.setBlock(64,-6,-28,tnt)
    world.spawnEntity(62,-6,-26,pufferfish)
    world.spawnParticle(64,-5,-24,smoke,1000,0.01)
    
The first line get the player position.  The second line is the result  we received from the server. The third line create a block of TNT on your east side. The 4th line spawn a puffer fish on your south (your fish will die shortly if you spawn it on land).  The 5th line create a cloud of smoke on your south-east.

Press Ctrl+] (control and right-square-bracket), the `quit` to exit the Telnet program.

## Client API library

A client library make it easy to write command. In the above example, you must type the coornidate numbers precisely, it's hard to see the relationship between player coornidate and later numbers.    If you use Lua library, you can assign x,y,z coordinates to different variables and use them later in the program.

    x, y, z = mc.getPlayerPos()
    mc.setBlock(x + 2, y, z, "tnt")

You can write you own client library in any languages.  This source repo provides API libraries in 3 different languages: Python, JavaScript, and Lua.  You can study them and see if you want to create a new library in the same or different language, or if you can improve the current ones.
