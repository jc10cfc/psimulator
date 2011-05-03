#!/bin/bash
# script for starting a simulator server
# author Stanislav Řehák

help() {
    echo "This is help for starting a server."
    echo "First arg is configuration file."
    echo "Second arg is a port."
    echo "For reading only skelet of computers and interfaces (without settings) use optional arg '-n'"
}

if [ ! -e psimulator.dtd ]; then
    echo "File with DTD (psimulator.dtd) must be in this directory: $PWD"
    echo "Exiting.."
    exit 2
fi

if [ -z "$1" -o "$1" == "-h" -o "$1" == "--help" ]; then
    help
    exit 1
fi

java -jar psimulator.jar $@