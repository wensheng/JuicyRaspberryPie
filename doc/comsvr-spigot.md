# How to program command server on Spigot

Instructional video: https://youtu.be/gUl2TidJaSk

JuicyRaspberryPie plugin provides a "/p" command. When a player issue a "/p" command inside Minecraft, the plugin will try to send that command to a command server.   the command server interprets incoming commands, then send API request back to Minecraft for it to execute.

JuicyRaspberryPie ships with an embedded command server, by default, it's not turned on.  To turn it on, edit the file plugins/JuicyRaspberryPie/config.yml.  Changed `start_cmdsvr: false` to `start_cmdsvr: true`; also make sure `pyexe` point to correct python executable in your system.  Reload Spigot, the embedded command server will be turned on. 

(Note JuicyRaspberryPie Forge mod, unlike JuicyRaspberryPie Spigot plugin, does not have a embedded command server).

With the command server turned on, now when you issue "/p list", you should see the functions that can be executed.  These functions are just examples JuicyRaspberryPie provides, they are:

    cube, flatten, falling_block, rainbow, sphere

Try issue command: "/p cube 3 diamond_block".  A 3x3x3 cube of diamond will be created close you in Minecraft.  Issuing "/p flatten" will flatten every structure nearby and put a giant square floor under your feet.

Where are these functions come from?  Please take a look at `examples.py` file in side `pplugins` folder, all functions are defined there.

`pplugins` is a special folder.   The files in this folders are scanned by the command server.  Any functions that are decorated with `@mcpy` or has docstring that starts with "_mcp" will be registered as functions that can be executed by "/p" command.  Add your own files in this folder then issue "/p update" in Minecraft so your functions in the new files can be picked up and added to "/p" command.

You don't have to use the embedded command server.  You can shut it down by issue "/p shutdownserver".  You can create your own command server anywhere else.  Make a copy of `plugins/JuicyRaspberryPie` folder and paste to anywhere you like.  Then open a command window, navigate to the folder, start your command server by:

    python cmdsvr/pycmdsvr.py

The command server is started again.
