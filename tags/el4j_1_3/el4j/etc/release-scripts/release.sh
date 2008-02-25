#!/bin/bash -e

#   * %box% =cd "D:/el4jFresh/external"=
#   * %box% =svn copy <nop>https://el4j.svn.sourceforge.net/svnroot/el4j/trunk <nop>https://el4j.svn.sourceforge.net/svnroot/el4j/tags/el4j_1_1_1 -r LATEST_SUCCESSFUL_RC_REVISION_EXTERNAL -m "Tagging the EL4J release 1.1.1"=
#      * Set the tag with name =el4j_1_1_1= means that you mark EL4J version 1.1.1. Adapt the tag to your version. Replace =LATEST_SUCCESSFUL_RC_REVISION_EXTERNAL= with the latest successful release candidate revision number of external repository. To find out this revision number, execute =svn update= in your working copy.
#   * %box% Switch to the release tag =svn switch <nop>https://el4j.svn.sourceforge.net/svnroot/el4j/tags/el4j_1_1_1/el4j .=
#   * %box% Make sure you have the proper version
#      * %box% =svn revert -R .=
#      * %box% =svn update=
#
#   * %box% =cd "D:/el4jFresh/internal"=
#   * %box% =svn copy <nop>https://cvs.elca.ch/subversion/el4j-internal/trunk <nop>https://cvs.elca.ch/subversion/el4j-internal/tags/el4j_1_1_1 -r LATEST_SUCCESSFUL_RC_REVISION_INTERNAL -m "Tagging the EL4J release 1.1.1."=
#      * Set the tag with name =el4j_1_1_1= means that you mark EL4J version 1.1.1 Adapt the tag to your version. Replace =LATEST_SUCCESSFUL_RC_REVISION_INTERNAL= with the latest successful release candidate revision number of external repository. To find out this revision number, execute =svn update= in your working copy.
#   * %box% Switch to the release tag =svn switch <nop>https://cvs.elca.ch/subversion/el4j-internal/tags/el4j_1_1_1 .=
#   * %box% Make sure you have the proper version
#      * %box% =svn revert -R .=
#      * %box% =svn update=

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
	echo "Step 1: Release $1"
	cd $1

	echo "Executing svn update..."
	svn update
	echo "Which revision was the latest release candidate?"
	read revision
	echo "Revision is $revision, OK?"
	read dummy

	echo "Which tag should be chosen? (like 1.1.1)"
	read tagDot
	tagScore=$(echo $tagDot | sed "s/\./_/g")
	echo "Tag is $tagDot, OK?"
	read dummy

	echo "Command is:"
	echo svn copy $2/trunk $2/tags/el4j_$tagScore -r $revision -m "Tagging the EL4J release $tagDot"
	echo "Continue?"
	read dummy

	svn copy $2/trunk $2/tags/el4j_$tagScore -r $revision -m "Tagging the EL4J release $tagDot"

	echo "Switch to tag..."
	svn switch $2/tags/el4j_$tagScore$3 .
	svn revert -R .
	svn update

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
echo "Hint: The next script to execute is update.sh"
