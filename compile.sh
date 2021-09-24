#!/bin/bash

if [[ ! -e "build/classes" ]] ; then mkdir -p build/classes ; fi
if [[ ! -e "build/output" ]] ; then mkdir -p build/output ; fi

javac -d build/classes/ src/main/java/*.java

cd build/classes
jar -cvmf ../../src/main/manifest.txt ../../deadwood.jar *.class
