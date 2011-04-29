#!/bin/bash
# skript pro buildeni binarek ze zdrojaku
# autor Stanislav Řehák

# umisteni projektu
LOC=/home/haldyr/NetBeansProjects/psimulator

# kde je cygwin
CYGWIN=/home/haldyr/NetBeansProjects/psimulator/cygwin

# verze aplikace - tak se bude jmenovat soubor zip archiv
VERSION=v1.0


CURR_PATH=`pwd`

DATE=`date +"%F"`
LINUX="psimulator_$VERSION"_"$DATE"_linux".zip" 
WINDOWS="psimulator_$VERSION"_"$DATE"_windows".zip" 

TEMP="temp.$$"

cd $LOC
ant

if [ $? -ne 0 ]; then
    echo Nepodarilo se buildnout projekt.. koncim..
    exit 1
fi

mkdir $TEMP
cd $TEMP

cp ../dist/psimulator.jar .
cp ../src/data/{cisco,linux,start_server}.sh .
cp ../src/data/laborka.xml .
cp ../src/data/psimulator.dtd .
cp ../src/data/doplnovani_{cisco,linux}.txt .
cp ../src/poznamky/{install,readme}.txt .
zip $CURR_PATH/$LINUX *

echo

if [ -f $CURR_PATH/$LINUX ]; then
    echo Verze pro linux: $LINUX
fi

cp * $CYGWIN/etc/skel/
cd $CYGWIN
zip -rq $CURR_PATH/$WINDOWS .

if [ -f $CURR_PATH/$WINDOWS ]; then
    echo Verze pro windows: $WINDOWS
fi

cd "$CURR_PATH"

rm -r $LOC/$TEMP
rm $CYGWIN/etc/skel/*
