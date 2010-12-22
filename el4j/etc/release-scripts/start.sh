#!/bin/bash -e

#Create a new branch on svn if the upcoming release will be a major (X.0) or minor (X.Y) version.
#Check out the corresponding branch of the parent minor version if it will be a built release (X.Y.Z).
#Create a clean environment to see if a freshly checked out EL4J works. I assume it's =D:/el4jFresh=
#
#   * %box% =cd "D:\el4jFresh"=
#   * %box% =svn copy <nop>https://el4j.svn.sourceforge.net/svnroot/el4j/trunk/el4j <nop>https://el4j.svn.sourceforge.net/svnroot/el4j/branches/el4j_X_Y/el4j=
#   * %box% =svn checkout <nop>https://el4j.svn.sourceforge.net/svnroot/el4j/branches/el4j_X_Y/el4j external=
#   * %box% =svn copy <nop>https://cvs.elca.ch/subversion/el4j-internal/trunk <nop>https://cvs.elca.ch/subversion/el4j-internal/branches/el4j_X_Y=
#   * %box% =svn checkout <nop>https://cvs.elca.ch/subversion/el4j-internal/branches/el4j_X_Y internal=
#   * %box% Copy settings.xml from etc/m2/ to ~/.m2/ (Backup your settings before)

# Use "start.sh yes 3" to create an alpha version 3 of external AND internal (because of "yes")

#configuration of SVN urls
ExternalURL="https://el4j.svn.sourceforge.net/svnroot/el4j"
InternalURL="https://svn.elca.ch/subversion/el4j-internal"

echo "The script will sometimes ask you if something is correct."
echo "If it is, press Enter, if it is not, press Ctrl-C."
echo "Press Enter to continue"
read dummy

echo "Do you want to prepare a release for external? (y/n)"
read performExternal
echo "Do you want to prepare a release for internal? (y/n)"
read performInternal
echo "What will be the next version of EL4J? That is the number of the new release or release candidate (without -rc). Example: 1.5.1 or 1.7"
read nextVersion
echo "You will prepare version $nextVersion with the following settings: performExternal=$performExternal, performInternal=$performInternal. OK?"
read dummy

echo $performInternal > .performInternal
echo $performExternal > .performExternal
echo $nextVersion > .nextVersion

#check if it's a major / minor or built release
builtrelease=0
if [[ "$nextVersion" =~ [0-9]+\.[0-9]+\.[0-9]+ ]] ; then
	echo "The next version $nextVersion seems to be a built release (X.Y.Z). Is this correct?"
	read dummy
	builtrelease=1
else
	echo "The next version $nextVersion seems to be a major or minor release (X.0 or X.Y). Is this correct?"
	read dummy
	builtrelease=0
fi


if [ $# -eq 2 ] ; then
	echo "This script will download el4j from trunk, modify version and deploy it without user interaction!"
	echo "Be sure to have all passwords that will be needed for deployment stored in ~/.m2/settings.xml"
	workingDir="el4jAlpha"
	performInternal=$1
	auto=true
else
	workingDir="d:/el4jFresh"
	
	#echo "Checkout internal? (y/n)"
	#read performInternal
	auto=false
fi

mkdir $workingDir
cd $workingDir

mkdir tools

# Copy maven
echo "Take the following maven version, OK?"
mvn --version
read dummy

cp -r $(which mvn)/../../../maven tools

tagScore=$(echo $nextVersion | sed "s/\./_/g")

# create new branch from trunk if it will NOT be a built release
# else, checkout the branch of the corresponding minor version
if [ $builtrelease == 0 ] ; then
	echo "The release seems to be a major / minor version."
	echo "I'm going to create a new branch for version $nextVersion (el4j_$tagScore) from trunk for external."
	echo "The command will be:"
	echo svn copy $ExternalURL/trunk $ExternalURL/branches/el4j_$tagScore -m "Creation of new branch for EL4J $nextVersion"
	echo "Is this ok?"
	read dummy
	svn copy $ExternalURL/trunk $ExternalURL/branches/el4j_$tagScore -m "Creation of new branch for EL4J $nextVersion"
	echo "New branch was created. I'm going to checkout the newly created branch el4j_$tagScore for external now. Is this ok?"
	read dummy
	svn checkout $ExternalURL/branches/el4j_$tagScore/el4j external
else 
	#get minor version out of built version
	minorScore=$(echo $tagScore | sed "s/_[0-9]*$//g")
	echo "The release seems to be a built version."
	echo "The last minor / major version was $minorScore."
	echo "I'm going to checkout the branch el4j_$minorScore for external. Is this ok?"
	read dummy
	svn checkout $ExternalURL/branches/el4j_$minorScore/el4j external
fi

cp external/etc/release-scripts/*.sh .

if [ ${performInternal:0:1} == "y" ] ; then
	# create new branch from trunk if it will NOT be a built release
	if [ $builtrelease == 0 ] ; then
		echo "The release seems to be a major / minor version."
		echo "I'm going to create a new branch for version $nextVersion (el4j_$tagScore) from trunk for internal."
		echo "The command will be:"
		echo svn copy $InternalURL/trunk $InternalURL/branches/el4j_$tagScore -m "Creation of new branch for EL4J $nextVersion"
		echo "Is this ok?"
		read dummy
		svn copy $InternalURL/trunk $InternalURL/branches/el4j_$tagScore -m "Creation of new branch for EL4J $nextVersion"
		echo "New branch was created. I'm going to checkout the newly created branch el4j_$tagScore for internal now. Is this ok?"
		read dummy
		svn checkout $InternalURL/branches/el4j_$tagScore internal
	else 
		#get minor version out of built version
		minorScore=$(echo $tagScore | sed "s/_[0-9]*$//g")
		echo "The release seems to be a built version."
		echo "The last minor / major version was $minorScore."
		echo "I'm going to checkout the branch el4j_$minorScore for internal. Is this ok?"
		read dummy
		svn checkout $InternalURL/branches/el4j_$minorScore internal
	fi
	
	cp internal/etc/release-scripts/*.sh .
fi

if [ $auto == true ] ; then
	./update.sh alpha$2
	./deploy.sh $performInternal
	exit
fi

# process settings.xml
echo "Copy current settings.xml to $workingDir/settings.xml.backup"
mv ~/.m2/settings.xml settings.xml.backup
cat external/etc/m2/settings.xml \
	| sed "s#~/.m2/repository#$workingDir/m2repository#" \
	| sed "s#~/myproject#$workingDir#" \
	> ~/.m2/settings.xml

echo "Open ~/.m2/settings.xml and fill in all passwords that will be needed for deployment."
echo ""
echo "Hint: The next step is to update the framework (script update.sh) and create / tag a release candidate (script releaseCandidate.sh)."
