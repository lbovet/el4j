#!/bin/bash -e

currentVersion=$(cat pom.xml | grep "<version>" | head -n 2 | tail -n 1 | tr -c -d '.[:digit:]')

if [ -z "$currentVersion" ] ; then
	echo "Error: Version number has to placed on the same line as the version tags (e.g. <version>1.0</version>)"
	exit
fi
echo "Current version is $currentVersion"
echo "Press ENTER to continue or Ctrl-C to stop."
read dummy

echo "Enter next version number"
read nextVersion

# list pom files, add more files if neccessary
find . -name 'src' -prune -o -name "pom.xml" | grep -v "/src$" > pom.files.txt
find . -name "site.xml" >> pom.files.txt

for i in $(cat pom.files.txt) ; do
	if [ $(grep -c "<version>$currentVersion</version>" $i) -eq 1 ] ; then
		cat $i | sed "s/$currentVersion/$nextVersion/" > $i.new
		mv $i.new $i
		echo "    Found: $i"
	else
		echo "!!! Not found/more than one match: $i"
	fi
done

rm pom.files.txt

echo "Done. Please review all changes carefully."

