# JuicyRaspberryPie
Programming Minecraft with Python!

JuicyRaspberryPie implements MinecraftPi modding API for Bukkit server or Minecraft Forge mod .

An integrated Python command server is added to make Python programming much easier.

JuicyRaspberryPie is based on [*RaspberryJuice*](https://github.com/zhuowei/RaspberryJuice)

youtube demo (note this video is somewhat outdated, for example cube command should be "/p cube 5 gold_block", it now use block name instead of ID):

<a href="http://www.youtube.com/watch?feature=player_embedded&v=qhDLa2muivY&t=15m" target="_blank"><img src="http://img.youtube.com/vi/qhDLa2muivY/0.jpg" alt="youtube" width="240" height="180" border="10" /></a>

## Pre-requisites:

1. Spigot or CraftBukkit server is installed.  Download Spigot from https://getbukkit.org/download/spigot
2. Python 3 is installed

## How to use

1. Download juicyraspberrypie-1.x.x.jar from [releases](https://github.com/wensheng/JuicyRaspberryPie/releases) and put it in plugins folder.  Start the Spigot/CraftBukkit server. A **juicyraspberrypie** folder should be created.
2. Inside the **juicyraspberrypie** folder, change config.yml to point pyexe and pypath to the correction location on your system.  See config.yml below.
3. Put your Python files in **pplugins** folder and you're ready to do.  Your Python functions just need to have doctrings that starts with "_mcpy", or decorated with "@mcpy", see pplugins/README.txt for details.

## config.yml

1. On Linux, the default should be fine. 
2. On Windows, your "pyexe" should be "python.exe", and your pypath should be ""C:\\Users\\yourusername\\Anaconda3" or "C:\\Python37" depending on where you installed Python.  Note the two backslashes!
3. If you use Mac and you installed python3 with "brew install python3", then your pyexe would be "python3" and your pypath would mostly likely be "/usr/local/bin".

## How to develop

**Bukkit Server**

You need maven and jdk8.

In bukkit folder, do "./buld.sh 1.14.4", "1.14.4" is the Bukkit version number, specify whatever version you need.

Tip: set "allow-nether=false" in server.properties, set "allow-end: false" in bukkit.yml, so server starts up quickly.

**Forge Mod**

Import forge folder import IntelliJ.

![juicy](misc/images/juicy.png)
