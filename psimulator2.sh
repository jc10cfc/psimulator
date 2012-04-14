#!/bin/bash
# script for creating downloads for psimulator2 project

# psimulator2 directory containing: bp_psi, branches, tags, trunk and wiki
DIR=/home/haldyr/NetBeansProjects/diplomka
TEMP=$DIR/"temp"
NAME=psimulator2
DATE=`date +"%F"`
FINAL_NAME=$NAME"_"$DATE.zip

mkdir $TEMP; cd $TEMP

cp ../trunk/psimulator2/dist/psimulator2.jar $NAME"_backend.jar"
cp ../branches/vizualizace/dist/PSimulatorUI.jar $NAME"_frontend.jar"
cp -r ../trunk/psimulator2/dist/lib .
cp ../branches/vizualizace/dist/lib/* lib/
cp ../branches/real_network_connection/virt_iface.sh .


# names of wiki pages from directory wiki
WIKI_FILES="psimulator2_readme.wiki"

for FILE in $WIKI_FILES; do
	cat ../wiki/$FILE | sed 's/{{{//g; s/}}}//g' > ${FILE/%.wiki/.txt}
done


# names of XML configuration files
CONFIG_FILES="example.xml laborka.xml"

for FILE in $CONFIG_FILES; do
	cp ../trunk/psimulator2/src/xml/$FILE .
done


zip -r $FINAL_NAME *
cd - >/dev/null
mv $TEMP/$FINAL_NAME .
rm -r $TEMP

echo "ZIP archive saved to: " `pwd`/$FINAL_NAME
