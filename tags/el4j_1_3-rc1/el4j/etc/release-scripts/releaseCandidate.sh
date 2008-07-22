#!/bin/bash -e

# make sure you are in right folder
if ! [ -e external ] ; then
	echo "Error: Folder 'external' not found. Go to its parent folder (el4j)!"
	exit
fi

echo "The script will sometimes ask you if something is correct."
echo "If it is, press Enter, if it is not, press Ctrl-C."
echo "Press Enter to continue"
read dummy

function makeRelease() {
	echo "Step 1: Make a release candidate for $1"
	cd $1

	echo "Executing svn update..."
	svn update
	echo "Which revision is it?"
	read revision
	echo "Revision is $revision, OK?"
	read dummy

	echo "Which tag should be chosen? (like 1.1.1, without -rc0)"
	read tagDot
	tagScore=$(echo $tagDot | sed "s/\./_/g")
	echo "Which release candiate should be chosen? (like 0 or 1, without -rc)"
	read rc
	echo "Tag is $tagDot-rc$rc, OK?"
	read dummy

	echo "Command is:"
	echo svn commit -m "Changes for release candidate $rc of EL4J $tagDot"
	echo "Continue?"
	read dummy

	svn commit -m "Changes for release candidate $rc of EL4J $tagDot"

	echo "Command is:"
	echo svn copy $2/trunk $2/tags/el4j_$tagScore-rc$rc -r $revision -m "Tagging the release candidate $rc for EL4J $tagDot"
	echo "Continue?"
	read dummy

	svn copy $2/trunk $2/tags/el4j_$tagScore-rc$rc -r $revision -m "Tagging the release candidate $rc for EL4J $tagDot"

	svn switch $2/tags/el4j_$tagScore-rc$rc$3 .

	echo "$2 done."
	cd ..
}

makeRelease "external" "https://el4j.svn.sourceforge.net/svnroot/el4j" "/el4j"

echo "Process internal? (y/n)"
read performInternal

if [ $performInternal != "y" ] ; then
	exit
fi

makeRelease "internal" "https://cvs.elca.ch/subversion/el4j-internal" ""
echo "Hint: The next step is to test the release candidate on leaffy"