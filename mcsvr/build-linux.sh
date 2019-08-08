#!/bin/sh

if [ ! -e "BuildTools.jar" ]
then
    wget https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar
fi

if [ -n "$1" ]
then
    java -Xmx2G -jar BuildTools.jar --rev $1
else
    java -Xmx2G -jar BuildTools.jar
fi
