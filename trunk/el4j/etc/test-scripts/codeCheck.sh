#!/bin/bash

if [ $# -lt 1 ] ; then
	echo "Please specify whether you want to check external or internal (external/internal)"
	exit 1
fi

cd $1

types="java xml xsd wsdl html xhtml css"
# xsl not included

# search for illegal whitespaces
echo "Searching for all included files ($types)..."

echo "" > files.tmp
for i in $types ; do
	find ./ -name "*.$i" | grep -v "/target/" | grep -v "/.settings/" | grep -v "/sandbox/" >> files.tmp
done

echo -n "Checking for illegal whitespaces..."
result=0
for i in $(cat files.tmp) ; do
	
	# dummy command to make it unix style
	sed "s/ABC/ABC/" < $i > current.tmp
	
	egrep "^[[:blank:]]* [^*^-]" current.tmp > /dev/null
	if [ $? -eq 0 ] ; then
		echo "$i: Spaces at beginning of line found."
		egrep -n "^[[:blank:]]* [^*^-]" $i
		result=1
	fi
	egrep "[^[:blank:]^*][[:blank:]]+$" current.tmp > /dev/null
	if [ $? -eq 0 ] ; then
		echo "$i: Spaces at end of line found."
		egrep -n "[^[:blank:]^*][[:blank:]]+$" $i
		result=1
	fi
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