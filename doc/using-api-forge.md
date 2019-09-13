# How to use JuicyRaspberryPie API on Forge

(**Please note as an example I use version 1.14.4, which at the time of this writing is the latest version of Forge. You should replace `1.14.4` with whatever version you want to use**)

## Forge Installation
1. Go to https://files.minecraftforge.net/, select 1.14.4 from left menu pane.
2. Click `Installer` to download forge installer.
3. Double click the installer.jar file, select `Install client` from the menu.
4. Open Minecraft, select "forge (...)", then `PLAY`.

## JuicyRaspberryPie Mod Installation
1. Download juicyraspberrypie-forge-1.14.4.jar from https://github.com/wensheng/JuicyRaspberryPie/releases
2. Open Explorer, in the address bar, type `%appdata%`, then enter.
3. Click `.minecraft`, the `mods`, move juicyraspberrypie-forge-1.14.4.jar file here.
4. Start or restart Minecraft.

## Using Python API Interactively
Clone or download https://github.com/wensheng/JuicyRaspberryPie. In JuicyRaspberryPie folder, navigate to bukkit/src/main, copy `resources` folder to anywhere you like, maybe rename it to something you can remember.  Open command window, navigate to the folder, then start python. (Please note only Python3 is supported)
Open Minecraft (Forge option) if you haven't already, choose **Singleplayer**, then select or generate a world.

Navigate away from Minecraft.  On Windows, press Alt-Tab, or Win-Tab. If you game menu is on whenever you navigate away from Minecraft, see [tips](#tips).

In Python window, issue following code:

    from mcpi.minecraft import Minecraft
    mc = Minecraft.create(port=4712)

`mc` is the Minecraft connection object, you interact with Minecraft through `mc`.

Find you place in Minecraft by:

    pos = mc.player.getTilePos()
    x, y, z = pos.x, pos.y, pos.z

`pos` is player position in Minecraft, x, y, z are just for convenience.  Now we want to spawn an animal, make sure you face east Minecraft (press F3 to find out your facing and more), then in Python:

    mc.spawnEntity(x+2, y, z, 'panda')

A panda appears right in front of your eyes.

Explore [API doc](python-api.md) to find out more what you can do with JuicyRaspberryPie API.  

## Using Python API programmably
(todo)

## Using JavaScript API
(todo)

### Tips

1. in %appdata%/.minecraft/options.txt change "pauseOnLostFocus"  to false so Minecraft doesn't pause when you move away from Minecraft.
2. issue `/gamerule doDaylightCycle false` to disable day/night cycle
