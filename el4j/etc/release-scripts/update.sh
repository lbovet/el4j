#!/bin/bash -e

#* %box% Go through all the pom.xml and set the version number (if present). E.g. =1.1.1-SNAPSHOT= -> =1.1.1= and correct the version of the parent.
#   * %box% Update the version of all modules to indicate that they changed.
#      * Remove the =-SNAPSHOT= for modules and plugins that weren't changed. E.g. 1.6-SNAPSHOT -> 1.6

# make sure you are in right folder
if ! [ -e external ] ; then
	echo "Error: Folder 'external' not found. Go to its parent folder (el4j)!"
	exit
fi

echo "Update framework (external and internal)"
el4jCurrent=$(cat external/pom.xml | grep "<version.el4j-framework.current>" -A 1 | tail -n 1 | tr -d ' \r\n' | sed 's/-SNAPSHOT//')
echo "Current version is $el4jCurrent-SNAPSHOT, OK?"
read dummy
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
