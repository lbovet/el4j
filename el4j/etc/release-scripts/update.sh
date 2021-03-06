#!/bin/bash -e

#* %box% Go through all the pom.xml and set the version number (if present). E.g. =1.1.1-SNAPSHOT= -> =1.1.1= and correct the version of the parent.
#   * %box% Update the version of all modules to indicate that they changed.
#      * Remove the =-SNAPSHOT= for modules and plugins that weren't changed. E.g. 1.6-SNAPSHOT -> 1.6

# make sure you are in right folder
if ! [ -e external ] ; then
	echo "Error: Folder 'external' not found. Go to its parent folder (el4j)!"
	exit
fi

performInternal=$(cat .performInternal)
performExternal=$(cat .performExternal)
el4jNext=$(cat .nextVersion)

echo "The script will sometimes ask you if something is correct."
echo "If it is, press Enter, if it is not, press Ctrl-C."
echo "Press Enter to continue"
read dummy

echo "You are preparing version $el4jNext with the following settings: performExternal=$performExternal, performInternal=$performInternal. OK?"
read dummy

echo "Update framework (external and internal)"
el4jCurrent=$(cat external/pom.xml | grep "<version.el4j-framework.current>" -A 1 | tail -n 1 | tr -d ' \t\r\n' | sed 's/-SNAPSHOT//')
if [ $# -eq 1 ] ; then
	el4jNext=$el4jCurrent-$1
	auto=true
else
	echo "Current version on the branch (i.e. the version to be adapted) seems to be $el4jCurrent-SNAPSHOT. Please type in the correct number (without -SNAPSHOT, e.g 1.6)."
	read el4jCurrent
	
	echo "Replacing '$el4jCurrent-SNAPSHOT' by '$el4jNext', OK?"
	read dummy
	auto=false
fi

el4jCurrent=$el4jCurrent-SNAPSHOT

echo "Searching for pom.xml files..."


# list pom files
if [ $performExternal == "y" ] && [ $performInternal == "y" ] ; then
	find ./ -name "pom.xml" > pom.files.txt
	find ./ -name "site.xml" >> pom.files.txt
else
	if  [ $performExternal == "y" ] ; then
		find external/ -name "pom.xml" > pom.files.txt
		find external/ -name "site.xml" >> pom.files.txt
	else
		if [ $performInternal == "y" ] ; then
			find internal/ -name "pom.xml" > pom.files.txt
			find internal/ -name "site.xml" >> pom.files.txt
		fi
	fi
fi

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
if  [ $performExternal == "y" ] ; then
	search="external/pom.xml"
fi

if [ -e internal ] && [ $performInternal == "y" ] ; then
	search="$search internal/pom.xml"
fi

if [ $auto == true ] ; then
	newSuffix="-$1"
else
	newSuffix=""
fi
for i in $search ; do
	cat $i | sed "s/-SNAPSHOT$/$newSuffix/" > $i.new
	mv $i.new $i
done

echo ""
echo "#############################################################################################"
echo "#Work is not finished yet! Search in all pom.xml and site.xml files for the String SNAPSHOT.#"
echo "#############################################################################################"
echo ""

echo "Searching for SNAPSHOT:"
cat pom.files.txt | xargs grep "SNAPSHOT"

echo ""
echo "Check and correct these files manually if necessary!"
rm pom.files.txt
