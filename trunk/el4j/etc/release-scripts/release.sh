#!/bin/bash -e

#   * %box% =cd "D:/el4jFresh/external"=
#   * %box% =svn copy <nop>https://el4j.svn.sourceforge.net/svnroot/el4j/tags/el4j_X_Y-rcZ <nop>https://el4j.svn.sourceforge.net/svnroot/el4j/tags/el4j_X_Y_Z -m "Tagging the EL4J release 1.1.1"=
#      * Set the tag with name =el4j_1_1_1= means that you mark EL4J version 1.1.1. Adapt the tag to your version. Replace =LATEST_SUCCESSFUL_RC_REVISION_EXTERNAL= with the latest successful release candidate revision number of external repository. To find out this revision number, execute =svn update= in your working copy.
#   * %box% Switch to the release tag =svn switch <nop>https://el4j.svn.sourceforge.net/svnroot/el4j/tags/el4j_X_Y_Z/el4j .=
#   * %box% Make sure you have the proper version
#      * %box% =svn revert -R .=
#      * %box% =svn update=
#
#   * %box% =cd "D:/el4jFresh/internal"=
#   * %box% =svn copy <nop>https://cvs.elca.ch/subversion/el4j-internal/tags/el4j_X_Y-rcZ <nop>https://cvs.elca.ch/subversion/el4j-internal/tags/el4j_X_Y_Z -m "Tagging the EL4J release 1.1.1."=
#      * Set the tag with name =el4j_1_1_1= means that you mark EL4J version 1.1.1 Adapt the tag to your version. Replace =LATEST_SUCCESSFUL_RC_REVISION_INTERNAL= with the latest successful release candidate revision number of external repository. To find out this revision number, execute =svn update= in your working copy.
#   * %box% Switch to the release tag =svn switch <nop>https://cvs.elca.ch/subversion/el4j-internal/tags/el4j_X_Y_Z .=
#   * %box% Make sure you have the proper version
#      * %box% =svn revert -R .=
#      * %box% =svn update=

#configuration of SVN urls
ExternalURL="https://el4j.svn.sourceforge.net/svnroot/el4j"
InternalURL="https://cvs.elca.ch/subversion/el4j-internal"

# make sure you are in right folder
if ! [ -e external ] ; then
	echo "Error: Folder 'external' not found. Go to its parent folder (el4j)!"
	exit
fi

echo "The script will sometimes ask you if something is correct."
echo "If it is, press Enter, if it is not, press Ctrl-C."
echo "Press Enter to continue"
read dummy

lastRC=$(cat .lastRC)
performInternal=$(cat .performInternal)
performExternal=$(cat .performExternal)
tagDot=$(cat .nextVersion)

echo "You are preparing version $tagDot with the following settings: performExternal=$performExternal, performInternal=$performInternal. OK?"
read dummy

echo "The release is created from the last release candidate tag."
echo "The last release candidate tag was el4j_$lastRC. Is this correct?"
read dummy

tagScore=$(echo $tagDot | sed "s/\./_/g")

function makeRelease() {
	echo "Step 1: Release $1"
	cd $1

	echo "Command is:"
	echo svn copy $2/tags/el4j_$lastRC $2/tags/el4j_$tagScore -m "Tagging the EL4J release $tagDot"
	echo "Continue?"
	read dummy

	svn copy $2/tags/el4j_$lastRC $2/tags/el4j_$tagScore -m "Tagging the EL4J release $tagDot"

	echo "Switch to tag..."
	svn switch $2/tags/el4j_$tagScore$3 .
	svn revert -R .
	svn update

	echo "$2 done."
	cd ..
}
if  [ $performExternal == "y" ] ; then
	makeRelease "external" $ExternalURL "/el4j"
fi


if [ $performInternal == "y" ] ; then
	makeRelease "internal" $InternalURL ""
fi
