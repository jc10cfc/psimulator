#!/bin/bash

if [ "$1" == "nahod" ]; then
	vde_switch -s "/var/run/vde.ctl/$2" -tap $2 -daemon
	ifconfig $2 up
	ifconfig $2
	exit 0
fi
if [ "$1" == "spoj" ]; then
	dpipe vde_plug "/var/run/vde.ctl/$2" = vde_plug "/var/run/vde.ctl/$3" &
	echo $2 a $3 jsou propojeny.
	exit 0
fi

echo Spatna syntaxe, nic jsem neprovedl, volby jsou: nahod <rozhrani> nebo spoj <rozhrani1> <rozhrani2>
exit 1

