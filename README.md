# JuicyRaspberryPie
Programming Minecraft with Python.

JuicyRaspberryPie is *RaspberryJuice* with a Python command server

RaspberryJuice is a Bukkit (a Minecraft server mod) plugin that implements the Minecraft Pi API.

https://github.com/zhuowei/RaspberryJuice

http://dev.bukkit.org/bukkit-plugins/raspberryjuice/

Most code of JuicyRaspberryPie are from RaspberryJuice, I just changed Java code a little bit and added a Python command server.

## Pre-requisites:

1. Spigot or CraftBukkit 1.8.3 is installed
2. Python 3 is installed

## How to use
Download [JuicyRaspberryPie.jar](https://github.com/wensheng/JuicyRaspberryPie/raw/master/jars/JuicyRaspberryPie.jar) and put it in plugins folder.  Start/Restart the Spigot/CraftBukkit server once and stop. A **JuicyRaspberryPie** folder should be created.

Inside the **JuicyRaspberryPie** folder, change config.yml to point pyexe and pypath to the correction location on your system.  If you use Windows and you installed python3 into "C:\Python34", you don't need to do anything.  If you use Linux, or if you use a Mac (and you installed python3 with "brew install python3"), then your pyexe would be "python3" and your pypath would mostly likely be "/usr/local/bin".

Put your Python files in **pplugins** folder and you're ready to do.  Your Python functions just need to have doctrings that starts with "_mcp", see pplugins/README.txt for details.

## How to develop
Clone this repo, copy spigot-1.8.3.jar to the lib folder, import JuicyRaspberryPie/java as root directory into Eclipse.

I only used JDK 1.8, 1.7 will not work. (or will if you change the code a little bit, the only Java 8 feature I use is "String.join")
