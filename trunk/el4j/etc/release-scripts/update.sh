#!/bin/bash -e

# make sure you are in right folder
if ! [ -e external ] ; then
	echo "Error: Folder 'external' not found. Go to its parent folder (el4j)!"
	exit
fi

echo "Update framework (external and internal)"
echo "Enter current el4j version number (without -SNAPSHOT)"
read el4jCurrent
echo "Enter next el4j version number"
read el4jNext

el4jCurrent=$el4jCurrent-SNAPSHOT

echo "Replacing '$el4jCurrent' by '$el4jNext', OK?"
read dummy

echo "Searching for pom.xml files..."


# list pom files
find ./ -name "pom.xml" > pom.files.txt
find ./ -name "site.xml" >> pom.files.txt

################################
# el4j x.y-SNAPSHOT -> x.y #
################################
for i in $(cat pom.files.txt) ; do
	if [ $(grep -c "<version>$el4jCurrent</version>" $i) -eq 1 ] ; then
		cat $i | sed "s/$el4jCurrent/$el4jNext/" > $i.new
		mv $i.new $i
		echo "FOUND $i"
	else
		echo "----- $i"
	fi
done

##################################
# el4j modules: remove -SNAPSHOT #
##################################
search="external/pom.xml"
if [ -e internal ] ; then
	search="$search internal/pom.xml"
fi
for i in $search ; do
	cat $i | sed "s/-SNAPSHOT$//" > $i.new
	mv $i.new $i
done

echo ""
echo "#############################################################################################"
echo "#Work is not finished yet! Search in all pom.xml and site.xml files for the String SNAPSHOT.#"
echo "#It's also a good practice to go through the changes this script made (use svn diff).       #"
echo "#############################################################################################"
echo ""

echo "Searching for SNAPSHOT:"
cat pom.files.txt | xargs grep "SNAPSHOT"

echo ""
echo "Correct these files manually if necessary!"
rm pom.files.txt
