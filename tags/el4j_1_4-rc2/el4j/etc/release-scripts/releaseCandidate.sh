#!/bin/bash -e

#---++++ EL4J external 
#The first release candidate number is 0. If tests fail and you do not execute these steps the first time you have to increase the release candidate number.
#
#   * %box% =cd "D:/el4jFresh/external"=
#   * %box% =svn copy <nop>https://el4j.svn.sourceforge.net/svnroot/el4j/trunk <nop>https://el4j.svn.sourceforge.net/svnroot/el4j/tags/el4j_1_1_1-rc0 -r REVISION_OF_PREVIOUS_COMMIT -m "Tagging the release candidate 0 for EL4J 1.1.1."=
#      * Set the tag with name =el4j_1_1_1-rc0= means that you mark first release candidate for EL4J version 1.1.1. Adapt the tag to your version and release candidate number. Replace =REVISION_OF_PREVIOUS_COMMIT= with the revision number of previous commit step. If nothing had to be commited, execute =svn update= to find out the current revision number.
#   * %box% =svn switch <nop>https://el4j.svn.sourceforge.net/svnroot/el4j/tags/el4j_1_1_1-rc0/el4j .=
#      * Switches the working copy to the tag repository. Adapt the path to the one used above.
#
#---++++ EL4J internal
#
#Also create an release candidate for internal, along the way it's been done for external
#
#   * %box% =cd "D:/el4jFresh/internal"=
#      * Adapt version number and release candidate number in commit message.
#   * %box% =svn copy <nop>https://cvs.elca.ch/subversion/el4j-internal/trunk <nop>https://cvs.elca.ch/subversion/el4j-internal/tags/el4j_1_1_1-rc0 -r REVISION_OF_PREVIOUS_COMMIT -m "Tagging the release candidate 0 for EL4J 1.1.1."=
#   * %box% =svn switch <nop>https://cvs.elca.ch/subversion/el4j-internal/tags/el4j_1_1_1-rc0 .=
#      * Switches the working copy to the tag repository. Adapt the path to the used above.

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

	echo "Switch to tag..."
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
echo "Hint: The next step is to update the version numbers"
