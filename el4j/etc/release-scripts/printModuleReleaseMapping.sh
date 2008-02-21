#!/bin/sh

version=$(cat external/pom.xml | grep "<version.el4j-framework.current>" -A 1 | tail -n 1 | tr -d ' \n')

function printList {
	cat $1 | grep "<version.module" -A 1 | grep -v "\-\-" | sed 's/version./| /g' | tr -d '\r\n' | sed 's/ *//g' | sed 's/\t*//g' | sed 's/>/ | /g' | sed 's/</ |\n/g' | grep -v "\-tests" | grep -v "\-demos"
}
echo "---++ EL4J $version"
echo " "
echo "---+++ Framework modules"
echo " "
echo "External modules"
echo " "
echo -n "| *Module* | *Version*"
printList external/pom.xml
echo " "

echo "Internal modules"
echo " "
echo -n "| *Module* | *Version*"
printList internal/pom.xml
