#!/bin/bash

# This script helps you to create and connect virtual interfaces to connect simulator to real network.
# You can create, destroy and connect virtual switches based on vde2. 

vde_dir="/var/run/vde2"

## Normal method to run tap interfaces: ---------------------------------------------------------------------------------------------------

function listAllIfaces() {
	echo "The list of all network interfaces:"
	echo `ifconfig  -a -s|tail -n +2 |cut -d' ' -f1`
}

# This will create vde_switch connected to tap interface.
function setupIface(){	# first parameter - iface name
	echo "This will create vde_switch connected to tap interface."
	if listAllIfaces |grep $1 > /dev/null ; then
		echo "Cannot create $1, $1 exists already. Exiting."
		exit 1;
	fi
	if [ ! -d $vde_dir ] ; then 
		mkdir $vde_dir
	fi
	vde_switch -s "$vde_dir/$1" -tap $1 -daemon
	sleep 1
	ifconfig $1 up
	ifconfig $1
}

# This connects 2 vde_switch.
function connect(){
	echo "This connects 2 vde_switch."
	dpipe vde_plug "$vde_dir/$1" = vde_plug "$vde_dir/$2" &
	echo "$1 a $2 are connected."
}

# This creates 2 vde_switches, 
function createAndConnect(){
	echo "This will create $1 and $2 and connect them."
	echo "Creating $1:"
	setupIface $1;
	echo "Creating $2:"
	setupIface $2;
	echo "Connecting $1 $2:"
	connect $1 $2;
}

# destroys all vde_switch
function destroyAll(){
	echo "Destroying all vde_switch."
	killall vde_switch
}

function existsIface(){
	listAllIfaces |grep $1 > /dev/null
	return $?
}

function destroyIface(){
	if command=`ps -e -o pid,command |grep vde_switch |grep $1 ` ; then
		PID=`echo $command |sed s/'^ '/''/|cut -d' ' -f1`
		echo "Destroying $1:"
		kill $PID
		echo "Killed: $PID"
	else
		echo "Iface $1 not found."
	fi
}


## Alternative method to run tap interfaces: ---------------------------------------------------------------------------------------------------

function createSwitch(){ 
	if [ ! -d $vde_dir ] ; then 
		mkdir $vde_dir
	fi
	vde_switch -s $1 -daemon
}

function plugIface() {	# 1. param: switch name, 2. param iface name
	vde_plug2tap -s $1 $2 &
	ifconfig $2 up
	ifconfig $2
}


# Another method to create interface pair: Creates one vde_swich and plug two vde_plug2tap into it.
function oneSwitchPair() {
	createSwitch switch0
	plugIface switch0 $1
	plugIface switch0 $2
}



## Running script: ---------------------------------------------------------------------------------------------------


function printHelp(){
	name="virt_iface"
	
	echo "This script helps you to create and connect virtual interfaces to connect simulator to real network."
	echo "You can create, destroy and connect virtual switches based on vde2. "
	echo ""
	echo "You must have installed vde2! You must be root to run this script!"
	echo ""
	echo "Usage:"
	echo "   $name list			lists all network interfaces"
	echo "   $name iface1 iface2		creates 2 connected interfaces"
	echo "   $name destroy iface		destroys iface"
	echo "   $name destroyall		destroys all vde_intefaces."
	echo "   $name create iface		creates iface"
	echo "   $name connect iface1 iface2	connect existing iface1 and iface2"
	echo ""
	echo "Examples:"
	echo "   $name pair sim0 tap0	creates interfaces sim0 and tap0 and connect them."
	echo
	exit 1;
}



case $1 in
	list )
		listAllIfaces ;;
	create )
		if [ $@ -ne 2 ] ; then
			printHelp
		fi
		setupIface $2 ;;
	connect )
		connect $2 $3 ;;
	pair )
		createAndConnect $2 $3 ;;
	destroyall )
		destroyAll ;;
	destroy )
		destroyIface $2 ;;
	oneSwitchPair )
		oneSwitchPair $2 $3 ;;
	
	* )
		printHelp ;;
esac
	
