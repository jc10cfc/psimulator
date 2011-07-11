#!/bin/bash
# script for connecting client to a cisco router
# author Stanislav Řehák

VERSION=`rlwrap -v | cut -d" " -f2 | sed 's/\./,/g'`
COLOR=cyan
COMPLETION=doplnovani_cisco.txt

if [[ $VERSION -ge 0,32 ]]; then
    OPT="-I -p$COLOR -w-3"
fi

if [[ $VERSION -ge 0,29 && $VERSION -lt 0,32 ]]; then
    OPT="-p$COLOR"
fi

if [[ $VERSION -lt 0,29 ]]; then
    OPT=""
fi

if [ -z "$1" ]; then
    echo "Specify a port!"
    exit 1
fi

OPT="$OPT -b'(){}[],+=&^%0@;|\' -f $COMPLETION"

# echo using OPT=$OPT

rlwrap $OPT telnet localhost $@
