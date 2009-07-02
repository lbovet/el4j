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
rm all_files.tmp

for i in $excludeFolders ; do
	cat files.tmp | grep -v "$i" >> files.tmp2
	mv files.tmp2 files.tmp
done

result=0

# check if all java files contain "printFileStatus" and "URL: "
echo "Checking for valid printFileStatus..."
echo "" > java_files.tmp
cat files.tmp | grep  ".java$" >> java_files.tmp
for i in $(grep -v "/maven/archetypes/" java_files.tmp) ; do
	
	# dummy command to make it unix style
	sed "s/ABC/ABC/" < $i > current.tmp
	
	egrep "printFileStatus" current.tmp > /dev/null
	if [ $? -ne 0 ] ; then
		echo "printFileStatus missing: $i"
		result=1
	else
		egrep "URL: http" current.tmp > /dev/null
		if [ $? -ne 0 ] ; then
			echo "'URL:' has to be followed by a space: $i"
			egrep -n "URL: http" $i
			result=1
		fi
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

rm current.tmp
rm files.tmp

exit $result