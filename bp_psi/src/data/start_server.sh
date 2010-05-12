#!/bin/bash

help() {
    echo "This is help for starting a server."
    echo "First arg is configuration file."
    echo "Second arg is a port."
    echo "For reading only skelet of computers and interfaces (without settings) use optional arg '-n'"
}

if [ -z "$1" -o "$1" == "-h" ]; then
    help
    exit 1
fi

java -jar bp_psi.jar $@