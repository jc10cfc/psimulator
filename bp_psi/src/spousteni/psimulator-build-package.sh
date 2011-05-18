#!/bin/bash
# skript pro buildeni binarek ze zdrojaku
# autor Stanislav Řehák

# umisteni projektu
LOC="/home/haldyr/NetBeansProjects/psimulator"

# kde je cygwin
CYGWIN="/home/haldyr/NetBeansProjects/psimulator/cygwin_orezany"

# verze aplikace - tak se bude jmenovat soubor zip archiv
VERSION="v1.0"


CURR_PATH=`pwd`

DATE=`date +"%F"`


NAME="psimulator_$VERSION"_"$DATE.zip"
TEMP="temp.$$"

cd "$LOC"
ant

if [ $? -ne 0 ]; then
    echo "Nepodarilo se buildnout projekt.. koncim.."
    exit 1
fi

cd "$CURR_PATH"

mkdir "$TEMP"
cd "$TEMP"

# nakopirovat vsechny soubory:
cp "$LOC"/dist/psimulator.jar .

for f in cisco.bat cisco.sh doplnovani_cisco.txt doplnovani_linux.txt linux.bat linux.sh start_server.bat start_server.sh INSTALL.txt RUN.txt; do
	cp "$LOC"/src/spousteni/$f .
done

for f in psimulator.dtd laborka.xml; do
	cp "$LOC"/src/data/$f .
done

cp "$LOC"/images/laborka.png .


cp -r "$CYGWIN" .
mv cygwin_orezany cygwin

# vytvorit zip
zip -rqv ../$NAME *


cd ..
rm -r "$TEMP"

echo "Archiv ulozen do $CURR_PATH/$NAME"
