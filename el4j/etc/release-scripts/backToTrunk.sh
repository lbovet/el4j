#!/bin/bash -e

#   * %box% Switch the working copy back to trunk
#      * %box% In external =svn switch <nop>https://el4j.svn.sourceforge.net/svnroot/el4j/trunk/el4j=
#      * %box% In internal =svn switch <nop>https://cvs.elca.ch/subversion/el4j-internal/trunk=

#configuration of SVN urls
ExternalURL="https://el4j.svn.sourceforge.net/svnroot/el4j"
InternalURL="https://svn.elca.ch/subversion/el4j-internal"

performInternal=$(cat .performInternal)
performExternal=$(cat .performExternal)
el4jNext=$(cat .nextVersion)

# make sure you are in right folder
if ! [ -e external ] ; then
	echo "Error: Folder 'external' not found. Go to its parent folder (el4j)!"
	exit
fi

echo "You are preparing version $el4jNext with the following settings: performExternal=$performExternal, performInternal=$performInternal. OK?"
read dummy


if [ $performExternal == "y" ] ; then
	cd external
	echo "Cleaning external..."
	mvn clean
	svn revert -R ./
	echo "Switching repository back to trunk"
	svn switch $ExternalURL/trunk/el4j
fi

if [ $performInternal == "y" ] ; then
	cd ../internal
	echo "Cleaning internal..."
	mvn clean
	svn revert -R ./
	echo "Switching repository back to trunk"
	svn switch $InternalURL/trunk
	cd ..
fi
echo "Switched back to trunk."
