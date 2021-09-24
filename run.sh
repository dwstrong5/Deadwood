#!/bin/bash

if [[ $# != 3 ]]; then
    echo "Usage: ./run.sh BoardXML CardXML numberOfPlayers"
    exit 1
fi

java -jar deadwood.jar $1 $2 $3
