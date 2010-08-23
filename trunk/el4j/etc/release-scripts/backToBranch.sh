#!/bin/bash -e

#   * %box% Switch the working copy back to branch
#      * %box% In external =svn switch <nop>https://el4j.svn.sourceforge.net/svnroot/el4j/branches/el4j_X_Y/el4j=
#      * %box% In internal =svn switch <nop>https://cvs.elca.ch/subversion/el4j-internal/branches/el4j_X_Y=

#configuration of SVN urls
ExternalURL="https://el4j.svn.sourceforge.net/svnroot/el4j"
InternalURL="https://svn.elca.ch/subversion/el4j-internal"

echo "The script will sometimes ask you if something is correct."
echo "If it is, press Enter, if it is not, press Ctrl-C."
echo "Press Enter to continue"
read dummy

# make sure you are in right folder
if ! [ -e external ] ; then
	echo "Error: Folder 'external' not found. Go to its parent folder (el4j)!"
	exit
fi

performInternal=$(cat .performInternal)
performExternal=$(cat .performExternal)
tagDot=$(cat .nextVersion)

builtrelease=0
if [[ "$tagDot" =~ [0-9]+\.[0-9]+\.[0-9]+ ]] ; then
	echo "The next version $tagDot seems to be a built release (X.Y.Z). Is this correct?"
	read dummy
	builtrelease=1
else
	echo "The next version $tagDot seems to be a major or minor release (X.0 or X.Y). Is this correct?"
	read dummy
	builtrelease=0
fi

#get minor version out of built version
if [ $builtrelease == 1 ] ; then
	$tagDot=$(echo $tagDot | sed "s/\.[0-9]*$//g")
fi

tagScore=$(echo $tagDot | sed "s/\./_/g")

echo "We are going back to the branch el4j_$tagScore (version $tagDot) with the following settings: performExternal=$performExternal, performInternal=$performInternal."
echo "This implies some bugs were found in the last release candidate during testing which need to be fixed."
echo "Is this OK?"
read dummy

if [ $performExternal == "y" ] ; then
	cd external
	echo "Cleaning external..."
	mvn clean
	svn revert -R ./
	echo "Switching repository back to branch"
	svn switch $ExternalURL/branches/el4j_$tagScore/el4j
	cd ..
fi

if [ $performInternal == "y" ] ; then
	cd internal
	echo "Cleaning internal..."
	mvn clean
	svn revert -R ./
	echo "Switching repository back to branch"
	svn switch $InternalURL/branches/el4j_$tagScore
	cd ..
fi

echo "Switched back to branch(es)."
echo "All versions are back to -SNAPSHOT now."
