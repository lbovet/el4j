#!/bin/bash

# This script will update the svn properties 
# svn:keywords
# svn:eol-style
# svn:mime-type 
# svn:executable
# if they are not set correctly.
#
# For the given Tags / svn:keywords, the corresponding value entries
# inside the file are also reseted / deleted. 



#check for echoonly mode
if [ "$1" == "echoonly" ] ; then
	echoonly=1
	echo "echoonly mode active"
	echo "commands will only be printed, NOT executed"
else
	echoonly=0
	echo "echoonly mode inactive"
	echo "commands will be executed"
	echo "if you want the script only to echo all commands, use 'echoonly' as argument"
fi

echo "Press any key to continue..."
read dummy

#function defintion to delete property values in files
#parameters (1) pathtofile
function deltagsinfile() {
	#check for echo only mode
	if [ $echoonly == 1 ] ; then
		echo "mv $1 $1.tmp"
		echo "sed -e 's/\$Revision:[^\$]\+\$/\$Revision\$/g' -e 's/\$URL:[^\$]\+\$/\$URL\$/g' -e 's/\$Date:[^\$]\+\$/\$Date\$/g' -e 's/\$Author:[^\$]\+\$/\$Author\$/g' $1.tmp > $1"
		echo "rm $1.tmp"
	else
		mv $1 $1.tmp
		sed -e 's/\$Revision:[^\$]\+\$/\$Revision\$/g' -e 's/\$URL:[^\$]\+\$/\$URL\$/g' -e 's/\$Date:[^\$]\+\$/\$Date\$/g' -e 's/\$Author:[^\$]\+\$/\$Author\$/g' $1.tmp > $1
		rm $1.tmp
	fi
}

#ensure that given property is set
#parameters (1) property to check, (2) property value, (3) path to file
function ensurePropSet() {
	if ! svn propget "$1" "$3" | grep "$2" > /dev/null ; then
		if [ $echoonly == 1 ] ; then
			echo "svn propset $1 \"$2\" $3"
		else
			svn propset $1 "$2" $3
		fi
	fi
}

echo "Fetching complete filelist from SVN. This can take a while..."
svn ls -R > files.tmp
dos2unix files.tmp
echo "Filelist completed. Processing now..."

for i in $(cat files.tmp | egrep "(\.java)|(\.sql)|(\.txt)|(\.vm)|(\.htm)|(\.apt)|(\.twiki)|(\.css)|(\.dtd)|(\.properties)") ; do
	ensurePropSet "svn:keywords" "URL Date Author Revision Id" $i
	ensurePropSet "svn:eol-style" "native" $i
done
for i in $(cat files.tmp | egrep "(\.xml)|(\.xsl)|(\.xsd)") ; do
	ensurePropSet "svn:keywords" "URL Date Author Revision Id" $i
	ensurePropSet "svn:eol-style" "native" $i
	ensurePropSet "svn:mime-type" "text/xml" $i
done
for i in $(cat files.tmp | egrep "\.png") ; do
	ensurePropSet "svn:mime-type" "image/png" $i
done
for i in $(cat files.tmp | egrep "\.jpg") ; do
	ensurePropSet "svn:mime-type" "image/jpeg" $i
done
for i in $(cat files.tmp | egrep "\.gif") ; do
	ensurePropSet "svn:mime-type" "image/gif" $i
done
for i in $(cat files.tmp | egrep "\.pdf") ; do
	ensurePropSet "svn:mime-type" "application/pdf" $i
done
for i in $(cat files.tmp | egrep "\.sh") ; do
	ensurePropSet "svn:eol-style" "LF" $i
	ensurePropSet "svn:keywords" "URL Date Author Revision Id" $i
	ensurePropSet "svn:mime-type" "application/x-sh" $i
	ensurePropSet "svn:executable" "ON" $i
done

rm files.tmp

echo "DONE!"
