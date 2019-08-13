#!/bin/sh

if [ ! -n "$1" ]
then
    echo 'Must provide Spigot Version number such as "build.sh 1.13.2"'
else
    mvn -DSpigotVersion=$1 clean package
fi
