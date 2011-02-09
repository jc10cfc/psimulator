#!/bin/bash
LOC=/home/haldyr/NetBeansProjects/psimulator
CYGWIN=/home/haldyr/NetBeansProjects/psimulator/cygwin
VERSION=v1.0

DATE=`date +"%F"`
LINUX="../dist/psimulator_$VERSION"_"$DATE"_linux".zip" 
WINDOWS="../dist/psimulator_$VERSION"_"$DATE"_windows".zip" 

cd $LOC
ant

if [ $? -ne 0 ]; then
    echo Nepodarilo se buildnout projekt.. koncim..
    exit 1
fi

mkdir temp
cd temp

cp ../dist/psimulator.jar .
cp ../src/data/{cisco,linux,start_server}.sh .
cp ../src/data/laborka.xml .
cp ../src/data/psimulator.dtd .
cp ../src/data/doplnovani_{cisco,linux}.txt .
cp ../src/poznamky/{install,readme}.txt .
zip $LINUX *

if [ -f $LINUX ]; then
    echo Verze pro linux: $LINUX
fi

cp * $CYGWIN/etc/skel/
zip -rq $WINDOWS $CYGWIN

if [ -f $WINDOWS ]; then
    echo Verze pro windows: $WINDOWS
fi

cd ..

rm -r temp
rm $CYGWIN/etc/skel/*
