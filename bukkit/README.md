## Developing JuicyRaspberryPie Spigot Plugin

This guide set your programming environment for developing JuicyRaspberryPie plugin for Spigot.  Please note as an example I use version 1.16.5, which at the time of this writing is the latest version of Spigot. You should replace `1.16.5` with whatever version you want to use.  Also here I use JDK version 11, but you can use any Java version from 8 onward.

### Spigot Installation

In a command window (cmd.exe or powershell, or a Linux terminal): 

```
mkdir mc1.16.5
cd mc1.16.5
mkdir build
mkdir run
cd build
```

Download [BuildTools.jar](https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar) into build directory, then:
```
java -jar BuildTools.jar --rev 1.16.5
```
Copy `spigot-1.16.5.jar` to run directory and rename it to just `spigot.jar`

Create file `start-server-win.bat` with content:

```
echo off
java -Xms8G -Xmx8G -jar spigot.jar
pause
```

Run `start-server-win.bat`.  If it displays error message like `main ERROR Unable to locate appender "SysOut" for logger config "root"`, do `chcp 437`, then `start-server-win.bat`.


Change `eula.txt` run `start-server-win.bat` again, type `stop` to exit.

### IntelliJ IDEA Configuration

Open File -> Settings, select Plugins, install "Minecraft Development" plugin.

Clone or download this repo, then in IntelliJ Idea, open File -> Open..., open JuicyRaspberryPie/bukkit. The project is now opened.

Open File -> "Project Structure", in "Project Settings" -> Artifacts, add 'Jar (from modules with dependencies...), click "OK".  In "Output Directory", specify output jar location, for example: `E:\mcdev\mc1.16.5\run\plugins`, check `include in project build`.

Click Run -> "Edit Configurations...", add a "Jar Application" called "spigot", then in "Path to Jar", enter or find spigot.jar location, for example:
 `E:\mcdev\mc1.16.5\run\spigot.jar`.

Click build icon (hammer) or Build -> "Build Project".
