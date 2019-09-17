# How to use JuicyRaspberryPie API on Spigot

(**Please note as an example I use version 1.14.4, which at the time of this writing is the latest version of Spigot. You should replace `1.14.4` with whatever version you want to use**)

Installation video: https://youtu.be/6-bqPcMoZ8M

## Spigot Installation
1. Download [BuildTools.jar](https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar) from [this page](https://hub.spigotmc.org/jenkins/job/BuildTools/)
2. Open a command window, run `java -jar BuildTools.jar --rev 1.14.4`
3. Some files will be created, among them we are interested in spigot-1.14.4.jar, copy it to a designated folder, rename it to just `spigot.jar`
4. Create a script/batch file, for example `run.bat`, with following content:
   `java -Xms4G -Xmx4G -XX:+UseConcMarkSweepGC -jar spigot.jar`
5. Type `run` in command window. Spigot will try to start and shutdown.
6. Open `eula.txt` with a text editor, change `eula=false` to `eula=true`
7. Re-run script, Spigot should start normally.

reference: https://www.spigotmc.org/wiki/spigot-installation/

## JuicyRaspberryPie Plugin Installation
1. Download juicyraspberrypie-1.14.4.jar from https://github.com/wensheng/JuicyRaspberryPie/releases
2. Move the file to `plugins` folder, the folder is at the same place where Spigot.jar is.
3. Start or reload Spigot. (To reload, just type `reload` in Spigot command window)

## Using Python API Interactively
In the plugins folder, a `JuicyRaspberryPie` folder is created.  Open command window, navigate to JuicyRaspberryPie, then start python. (Please note only Python3 is supported)
Open Minecraft if you haven't already, choose "Multiplayer", then select the Spigot server.

Navigate away from Minecraft.  On Windows, press Alt-Tab, or Win-Tab. If you game menu is on whenever you navigate away from Minecraft, see [tips](#tips).

In Python window, issue following code:

    from mcpi.minecraft import Minecraft
    mc = Minecraft.create()

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
3. If you don't use nether and the_end", set "allow-nether=false" in server.properties, set "allow-end: false" in bukkit.yml, so server starts up quickly.
4. On windows, sometime you have error message when you start Spigot.  This happen when your command window(cmd) was set to UTF-8 encoding. The error can be ignored.  Or, do a `chcp` to check if it says 65001.  Doing a `chcp 437` then starting Spigot again will get rid of the error.
