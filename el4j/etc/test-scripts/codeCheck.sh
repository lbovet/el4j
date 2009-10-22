#!/bin/bash

if [ $# -lt 1 ] ; then
	echo "Please specify whether you want to check external or internal (external/internal)"
	exit 1
fi

cd $1

#################
# Configuration #
#################
types="java xml xsd wsdl html xhtml css"
# xsl not included
excludeFolders="\./sandbox/ \./maven/demos/svn-m2repo/m2repository"


echo "Searching for all included files ($types)..."

find ./ -type f | grep -v "/\.svn" | grep -v "/target/" | grep -v "/\.settings/" > all_files.tmp
echo "" > files.tmp
for i in $types ; do
	cat all_files.tmp | grep "\.$i$" >> files.tmp
done
cat all_files.tmp | grep "pom.xml" >> pomXmlFiles.tmp
rm all_files.tmp

for i in $excludeFolders ; do
	cat files.tmp | grep -v "$i" >> files.tmp2
	mv files.tmp2 files.tmp
done

result=0

# check if all java files contain "@svnLink"
echo "Checking for valid @svnLink..."
echo "" > java_files.tmp
cat files.tmp | grep  ".java$" >> java_files.tmp
for i in $(grep -v "/maven/archetypes/" java_files.tmp) ; do
	
	# dummy command to make it unix style
	sed "s/ABC/ABC/" < $i > current.tmp
	
	egrep "@svnLink" current.tmp > /dev/null
	if [ $? -ne 0 ] ; then
		echo "@svnLink missing: $i"
		result=1
	fi
done
rm java_files.tmp
echo "done"

# search for illegal whitespaces
echo "Checking for illegal whitespaces..."
for i in $(cat files.tmp) ; do
	
	# dummy command to make it unix style
	sed "s/ABC/ABC/" < $i > current.tmp
	
	egrep "^[[:blank:]]* [^*^-]" current.tmp > /dev/null
	if [ $? -eq 0 ] ; then
		echo "Spaces at beginning of line found in: $i"
		egrep -n "^[[:blank:]]* [^*^-]" $i
		result=1
	fi
#	egrep "[^[:blank:]^*][[:blank:]][[:blank:]]+$" current.tmp > /dev/null
#	if [ $? -eq 0 ] ; then
#		echo "$i: Spaces at end of line found."
#		egrep -n "[^[:blank:]^*][[:blank:]][[:blank:]]+$" $i
#		result=1
#	fi
done
echo "done"

echo "Checking for headers..."
if [ $1 == "external" ] ; then
	# search for ELCA internal header
	for i in $(cat files.tmp) ; do
		grep "confidential and proprietary information" $i > /dev/null
		if [ $? -eq 0 ] ; then
			echo "ELCA internal header found in $i"
			result=1
		fi
	done
fi
if [ $1 == "internal" ] ; then
	# search for LGPL header
	for i in $(cat files.tmp) ; do
		grep "LGPL" $i > /dev/null
		if [ $? -eq 0 ] ; then
			echo "LGPL header found in $i"
			result=1
		fi
	done
fi
echo "done"

echo "Checking for commons-logging (use slf4j instead)..."
	mvn dependency:tree | grep "commons-logging" > /dev/null
	if [ $? -eq 0 ] ; then
		echo "commons-logging found! Use 'mvn dependency:tree' to find out where."
		result=1
	fi
echo "done"

echo "Checking for unknown repositories..."
allowedRepoList=external/etc/test-scripts/allowedRepositories.txt
for i in $(cat pomXmlFiles.tmp) ; do
	xml2 < "$i" | grep "/project/repositories/repository/url" | cut -d= -f 2 > repos.tmp
	xml2 < "$i" | grep "/project/pluginRepositories/pluginRepository/url" | cut -d= -f 2 >> repos.tmp
	for repo in $(cat repos.tmp) ; do
		count=$(grep "^$repo" ../$allowedRepoList | wc -l)
		if [ $count == "0" ] ; then
			echo "Unknown repository found: $repo (in $i)"
			echo "Please add this file to nexus and register it in " $allowedRepoList
			echo ""
			result=1
		fi
	done
done
rm repos.tmp
rm pomXmlFiles.tmp
echo "done"

rm current.tmp
rm files.tmp

exit $result