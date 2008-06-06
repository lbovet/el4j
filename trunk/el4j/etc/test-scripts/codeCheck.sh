#!/bin/bash

cd external

# search for illegal whitespaces
echo "Searching for all xml files..."
find ./ -name "*.xml" | grep -v "/target/" | grep -v "/.settings/" | grep -v "/sandbox/" > files.tmp

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
	egrep "[^[:blank:]][[:blank:]]+$" current.tmp > /dev/null
	if [ $? -eq 0 ] ; then
		echo "$i: Spaces at end of line found."
		egrep -n "[^[:blank:]][[:blank:]]+$" $i
		result=1
	fi
done

echo "done"

rm current.tmp
rm files.tmp

exit $result