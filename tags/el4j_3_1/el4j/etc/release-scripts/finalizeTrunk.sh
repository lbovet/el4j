#!/bin/bash -e

#   * %box% Update the development version in the framework (e.g. from 1.1.1-SNAPSHOT to 1.1.2-SNAPSHOT)
#   * %box% Update the version of all modules to a new development version.
#   * %box% Update the archetype to use the new modules.

function update() {
	awk -F= '\
    {if ($1 ~ /-SNAPSHOT$/) {
        sub (/-SNAPSHOT$/, "", $1)
        major = substr($1 , 0, length($1) - 1)
        minor = substr($1 , length($1))
        minor++
        print major minor "-SNAPSHOT"
    } else
        print}' $1 > $1.new
    mv $1.new $1
}

performInternal=$(cat .performInternal)
performExternal=$(cat .performExternal)
el4jNext=$(cat .nextVersion)

# make sure you are in right folder
if ! [ -e external ] ; then
	echo "Error: Folder 'external' not found. Go to its parent folder (el4j)!"
	exit
fi

echo "This script will update the version of the trunk to the upcoming major / minor release (X.Y)"
echo ""

echo "You are preparing version $el4jNext with the following settings: performExternal=$performExternal, performInternal=$performInternal. OK?"
read dummy

echo "Enter current (old) el4j version number (without -SNAPSHOT)"
read el4jCurrent
echo "Enter next / upcoming el4j snapshot version number for the trunk (without -SNAPSHOT). This is supposed to be a major / minor version (X.Y)."
read el4jNext


#check version
if [[ "$el4jNext" =~ [0-9]+\.[0-9]+\.[0-9]+ ]] ; then
	echo "You entered a built version number (X.Y.Z)."
	echo "This implies that this step must be skipped! (See checklist on EL4J Wiki...)"
	exit 1;
else 
	echo "Entered version numbers seem to be ok"
fi

el4jCurrent=$el4jCurrent-SNAPSHOT
el4jNext=$el4jNext-SNAPSHOT

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

#####################################
# el4j a.b-SNAPSHOT -> x.y-SNAPSHOT #
#####################################
for i in $(cat pom.files.txt) ; do
	if [ $(grep -c "<version>$el4jCurrent</version>" $i) -eq 1 ] ; then
		cat $i | sed "s/$el4jCurrent/$el4jNext/" > $i.new
		mv $i.new $i
		echo "    OK $i"
	else
		echo "CHECK! $i"
	fi
done

################################
# el4j modules: increase minor #
################################

if [ $performExternal == "y" ] ; then
	update external/pom.xml
fi

if [ $performInternal == "y" ] ; then
	update internal/pom.xml
fi


echo ""
echo "#####################################################################################"
echo "#Work is not finished yet! Check all pom.xml and site.xml files if they are correct #"
echo "#Look at the files that have a CHECK! tag.                                          #"
echo "#####################################################################################"
echo "#The archetype version (maven/archetype) and <version.el4j-framework.current>       #"
echo "#in pom.xml are definitely wrong and have to be corrected!                           #"
echo "#####################################################################################"
echo ""

rm pom.files.txt

echo "Hint: The next step is to commit the new version to the trunk."
echo "... and don't forget to restore your maven settings (mv ~/.m2/settings.xml.backup ~/.m2/settings.xml)"

rm .nextVersion .performInternal .performExternal

exit
