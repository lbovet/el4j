#!/bin/bash -e

#Create a clean environment to see if a freshly checked out EL4J works. I assume it's =D:/el4jFresh=
#
#   * %box% =cd "D:\el4jFresh"=
#   * %box% =svn checkout <nop>https://el4j.svn.sourceforge.net/svnroot/el4j/trunk/el4j external=
#   * %box% =svn checkout <nop>https://cvs.elca.ch/subversion/el4j-internal/trunk internal=
#   * %box% Copy settings.xml from etc/m2/ to ~/.m2/ (Backup your settings before)

freshDir="el4jFresh"
cd d:
mkdir $freshDir
cd $freshDir

mkdir tools

# Copy maven
echo "Take the following maven version, OK?"
mvn --version
cp -r $(which mvn)/../../../maven tools

svn checkout https://el4j.svn.sourceforge.net/svnroot/el4j/trunk/el4j external

cp external/etc/release-scripts/*.sh .

echo "Checkout internal? (y/n)"
read performInternal

if [ $performInternal == "y" ] ; then
	svn checkout https://cvs.elca.ch/subversion/el4j-internal/trunk internal
	cp internal/etc/release-scripts/*.sh .
fi

# process settings.xml
mv ~/.m2/settings.xml ~/.m2/settings.xml.backup
cat external/etc/m2/settings.xml \
	| sed "s#~/.m2/repository#D:/$freshDir/m2repository#" \
	| sed "s#~/myproject#D:/$freshDir#" \
	> ~/.m2/settings.xml

echo "Open ~/.m2/settings.xml and fill in all passwords that will be needed for deployment."
echo ""
echo "Hint: The next step is to tag a release candidate"