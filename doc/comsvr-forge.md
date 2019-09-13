# How to program command server on Forge
JuicyRaspberryPie mod provides a "/p" command. When a player issue a "/p" command inside Minecraft, the mod will try to send that command to a command server.   the command server interprets incoming commands, then send API request back to Minecraft for it to execute.

## Setting up a command server
JuicyRaspberryPie Forge mod, unlike JuicyRaspberryPie Spigot plugin, does not have a embedded command server. To set up a command server, clone or download https://github.com/wensheng/JuicyRaspberryPie. In JuicyRaspberryPie folder, navigate to bukkit/src/main, copy `resources` folder to anywhere you like, maybe rename it to something you can remember.  Open command window, `cd` to the folder.

Now you can do interactive programming as described in [How to use API on Forge](using-api-forge.md) page, you can also start the command server:

    python cmdsvr/pycmdsvr.py

With the command server turned on, now when you issue "/p list" inside Minecraft, you should see the functions that can be executed.  These functions are listed:

```
cube, flatten, falling_block, rainbow, sphere
```

Try issue command: "/p cube 3 diamond_block".  A 3x3x3 cube of diamond will be created close you in Minecraft.  Issuing "/p flatten" will flatten every structure nearby and put a giant square floor under your feet.

Where are these functions come from?  Please take a look at `examples.py` file in side `pplugins` folder, all functions are defined there.

`pplugins` is a special folder.   The files in this folders are scanned by the command server.  Any functions that are decorated with `@mcpy` or has docstring that starts with "_mcp" will be registered as functions that can be executed by "/p" command.  

Go ahead add your own files in this folder then issue "/p update" in Minecraft so your functions in the new files can be picked up and added to "/p" command.

In addition to Python, a javascript command server is provided in JuicyRaspberryPie source, under cmdsvr/javascript folder.  To start the Javascript command server, shutdow the python one to free up the port, do a `npm install`, then:

    node index.js

Just as python command server, any files you add to *pplugins* folder will be picked up by the command server.  But unlike python one, each file can only have one exported function.

