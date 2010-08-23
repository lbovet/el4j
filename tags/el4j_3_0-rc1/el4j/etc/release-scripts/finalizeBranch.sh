#!/bin/bash -e

#   * %box% Update the development version on the branch (e.g. from 1.1.1-SNAPSHOT to 1.1.2-SNAPSHOT or from 1.0-SNAPSHOT to 1.0.1-SNAPSHOT)
#   * %box% Update the version of all modules to a new development version.
#   * %box% Update the archetype to use the new modules.


function update() {
	awk -F= '\
    {if ($1 ~ /-SNAPSHOT$/) {
        sub (/-SNAPSHOT$/, "", $1)
        if (match($1,"[0-9]+\\.[0-9]+\\.[0-9]+")) {
			majorminor = substr($1 , 0, length($1) - 1)
			built = substr($1 , length($1))
			built++
			print majorminor built "-SNAPSHOT"
		} else {
			print $1 ".1-SNAPSHOT"
		}
    } else {
        print
	}
	}' $1 > $1.new
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

echo "This script will update the version of the branch to the upcoming built release (X.Y.Z)"
echo ""

echo "You have done a release for version version $el4jNext with the following settings: performExternal=$performExternal, performInternal=$performInternal."
echo "Your local working copy is on the branch (you ran backToBranch.sh before)."
echo "Is this correct?"
read dummy

echo "Current version on the branch (i.e. the version to be adapted) seems to be $el4jNext-SNAPSHOT. Please type in the correct number (without -SNAPSHOT, e.g 1.6 or 1.7.1)."
read el4jCurrent

echo "Enter next / upcoming el4j snapshot version number for the branch (without -SNAPSHOT). This is supposed to be a built version (X.Y.Z)."
read el4jNext

#check version
if [[ "$el4jNext" =~ [0-9]+\.[0-9]+\.[0-9]+ ]] ; then
	echo "Entered version numbers seem to be ok"
else 
	echo "The next / upcoming el4j snapshot version for the branch must be a built version (X.Y.Z)"
	echo "You entered a minor version number (X.Y) which can not be correct in this step!"
	exit 1;
fi

echo "Processing version replacement now..."

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
# el4j a.b(.c)-SNAPSHOT -> x.y.z-SNAPSHOT #
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

echo "Hint: The next step is to commit this update back to the branch."

exit
