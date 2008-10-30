#!/bin/bash -e

#Create a clean environment to see if a freshly checked out EL4J works. I assume it's =D:/el4jFresh=
#
#   * %box% =cd "D:\el4jFresh"=
#   * %box% =svn checkout <nop>https://el4j.svn.sourceforge.net/svnroot/el4j/trunk/el4j external=
#   * %box% =svn checkout <nop>https://cvs.elca.ch/subversion/el4j-internal/trunk internal=
#   * %box% Copy settings.xml from etc/m2/ to ~/.m2/ (Backup your settings before)

# Use "start.sh yes 3" to create an alpha version 3 of external AND internal (because of "yes")

echo "Do you want to prepare a release for external? (y/n)"
read performExternal
echo "Do you want to prepare a release for internal? (y/n)"
read performInternal
echo "What will be the next version of EL4J? That is the number of the new release or release candidate (without -rc). Example: 1.5.1"
read nextVersion
echo "You will prepare version $nextversion with the following settings: performExternal=$performExternal, performInternal=$performInternal. OK?"
read dummy

echo $performInternal > .performInternal
echo $performExternal > .performExternal
echo $nextVersion > .nextVersion


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
	#auto=false
fi


mkdir $workingDir
cd $workingDir

mkdir tools

# Copy maven
echo "Take the following maven version, OK?"
mvn --version
read dummy
echo "ok, I'll now work for quite a long time. Please be patient."

cp -r $(which mvn)/../../../maven tools

svn checkout https://el4j.svn.sourceforge.net/svnroot/el4j/trunk/el4j external

cp external/etc/release-scripts/*.sh .

if [ ${performInternal:0:1} == "y" ] ; then
	svn checkout https://cvs.elca.ch/subversion/el4j-internal/trunk internal
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
echo "Hint: The next step is to tag a release candidate"
