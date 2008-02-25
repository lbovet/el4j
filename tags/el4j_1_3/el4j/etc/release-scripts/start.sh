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

svn checkout https://el4j.svn.sourceforge.net/svnroot/el4j/trunk/el4j external

cp external/etc/release-scripts/*.sh .

echo "Checkout internal? (y/n)"
read performInternal

if [ $performInternal == "y" ] ; then
	svn checkout https://cvs.elca.ch/subversion/el4j-internal/trunk internal
	cp internal/etc/release-scripts/*.sh .
fi


mv ~/.m2/settings.xml ~/.m2/settings.xml.backup
cp external/etc/m2/settings.xml ~/.m2/settings.xml

echo "Please edit ~/.m2/settings.xml according to checklist (your version is backuped -> settings.xml.backup)"
echo "These snippets might help you:"
echo "<localRepository>D:/$freshDir/m2repository</localRepository>"
echo ""
echo "<!-- EL4J developer specific properties -->"
echo "<el4j.root>D:/$freshDir</el4j.root>"
echo "<el4j.external>\${el4j.root}/external</el4j.external>"
echo "<el4j.internal>\${el4j.root}/internal</el4j.internal>"
echo ""
echo "<!-- Project properties -->"
echo "<el4j.project.home>D:/$freshDir</el4j.project.home>"
echo "<el4j.project.tools>\${el4j.project.home}/tools</el4j.project.tools>"
echo ""
echo "Hint: The next step after editing settings.xml is to tag a release candidate"