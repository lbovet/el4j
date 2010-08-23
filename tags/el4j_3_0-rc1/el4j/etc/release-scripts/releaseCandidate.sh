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

#configuration of SVN urls
ExternalURL="https://el4j.svn.sourceforge.net/svnroot/el4j"
InternalURL="https://svn.elca.ch/subversion/el4j-internal"

# make sure you are in right folder
if ! [ -e external ] ; then
	echo "Error: Folder 'external' not found. Go to its parent folder (el4j)!"
	exit
fi
echo "The script will sometimes ask you if something is correct."
echo "If it is, press Enter, if it is not, press Ctrl-C."
echo "Press Enter to continue"
read dummy

performInternal=$(cat .performInternal)
performExternal=$(cat .performExternal)
tagDot=$(cat .nextVersion)

echo "You are preparing version $tagDot with the following settings: performExternal=$performExternal, performInternal=$performInternal. OK?"
read dummy

tagScore=$(echo $tagDot | sed "s/\./_/g")
echo "Which release candiate should be chosen? (like 0 or 1, without -rc)"
read rc
echo "New tag is $tagDot-rc$rc, OK?"
read dummy

function makeRelease() {
	echo "Make a release candidate for $1 from (local) working copy"
	cd $1

	echo "Press Enter to continue..."
	read dummy

	echo "Command is:"
	echo svn copy . $2/tags/el4j_$tagScore-rc$rc$3 -m "Tagging the release candidate $rc for EL4J $tagDot"
	echo "This can take a while. Continue?"
	read dummy
	
	#make subdir first...
	if [ "$3" != "" ] ; then
		svn mkdir $2/tags/el4j_$tagScore-rc$rc -m "Creation of $3 subdir inside tag for release candidate $rc for EL4J $tagDot"
	fi
	svn copy . $2/tags/el4j_$tagScore-rc$rc$3 -m "Tagging the release candidate $rc for EL4J $tagDot"

	echo "Switching to newly created tag el4j_$tagScore-rc$rc ..."
	svn switch $2/tags/el4j_$tagScore-rc$rc$3 .

	echo "$2 done."
	cd ..
}

if [ $performExternal == "y" ] ; then
	makeRelease "external" $ExternalURL "/el4j"
fi

#echo "Process internal? (y/n)"
#read performInternal

if [ $performInternal == "y" ] ; then
	makeRelease "internal" $InternalURL ""
fi

#save the last release candidate tag in file
echo "$tagScore-rc$rc" > .lastRC

echo "Hint: The next step is to run tests."
