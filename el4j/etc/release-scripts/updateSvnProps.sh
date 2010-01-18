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


tags="URL Date Author Revision Id"

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

#function definition to run svn propset command
#parameters (1) command to run, (2) echomode
function runcommand() {
	#check for echo only mode
	if [ $2 == 1 ] ; then
		echo "$1"
	else
		$1
	fi
}

#function defintion to set the keywords for the configured tags
#parameters (1) pathtofile, (2) echomode
function svnsetkeywords() {
	#check for echo only mode
	if [ $2 == 1 ] ; then
		echo "svn propset svn:keywords \"$tags\" $1"
	else
		svn propset svn:keywords "$tags" $1
	fi
}

#function defintion to delete property values in files
#parameters (1) pathtofile, (2) echomode
function deltagsinfile() {
	#check for echo only mode
	if [ $2 == 1 ] ; then
		echo "mv $1 $1.tmp"
		echo "sed -e 's/\$Revision:[^\$]\+\$/\$Revision\$/g' -e 's/\$URL:[^\$]\+\$/\$URL\$/g' -e 's/\$Date:[^\$]\+\$/\$Date\$/g' -e 's/\$Author:[^\$]\+\$/\$Author\$/g' $1.tmp > $1"
		echo "rm $1.tmp"
	else
		mv $1 $1.tmp
		sed -e 's/\$Revision:[^\$]\+\$/\$Revision\$/g' -e 's/\$URL:[^\$]\+\$/\$URL\$/g' -e 's/\$Date:[^\$]\+\$/\$Date\$/g' -e 's/\$Author:[^\$]\+\$/\$Author\$/g' $1.tmp > $1
		rm $1.tmp
	fi
}

#function to check for given props
#parameters (1) property to check, (2) path to file
#returns 1 if a property value has been found, else 0
function checkforprops() {
	while read line
	do
		if [[ $line != "" ]] ; then
			return 1
		fi
	done < <(svn propget $1 $2)
	return 0
}

echo "Fetching complete filelist from SVN. This can take a while..."
svn ls -R > files.tmp
echo "Filelist completet. Processing now..."

for i in $(cat files.tmp | egrep "(\.java)|(\.sql)|(\.txt)|(\.vm)|(\.htm)|(\.apt)|(\.twiki)|(\.css)|(\.dtd)|(\.properties)") ; do
	checkforprops "svn:keywords" $i
	if [ $? == 0 ] ; then
		svnsetkeywords $i $echoonly
		deltagsinfile $i $echoonly
	fi
	checkforprops "svn:eol-style" $i
	if [ $? == 0 ] ; then
		runcommand "svn propset svn:eol-style native $i" $echoonly
	fi
done
for i in $(cat files.tmp | egrep "(\.xml)|(\.xsl)|(\.xsd)") ; do
	checkforprops "svn:keywords" $i
	if [ $? == 0 ] ; then
		svnsetkeywords $i $echoonly
		deltagsinfile $i $echoonly
	fi
	checkforprops "svn:eol-style" $i
	if [ $? == 0 ] ; then
		runcommand "svn propset svn:eol-style native $i" $echoonly
	fi
	checkforprops "svn:mime-type" $i
	if [ $? == 0 ] ; then
		runcommand "svn propset svn:mime-type text/xml $i" $echoonly
	fi
done
for i in $(cat files.tmp | egrep "\.png") ; do
	checkforprops "svn:mime-type" $i
	if [ $? == 0 ] ; then
		runcommand "svn propset svn:mime-type image/png $i" $echoonly
	fi
done
for i in $(cat files.tmp | egrep "\.jpg") ; do
	checkforprops "svn:mime-type" $i
	if [ $? == 0 ] ; then
		runcommand "svn propset svn:mime-type image/jpeg $i" $echoonly
	fi
done
for i in $(cat files.tmp | egrep "\.gif") ; do
	checkforprops "svn:mime-type" $i
	if [ $? == 0 ] ; then
		runcommand "svn propset svn:mime-type image/gif $i" $echoonly
	fi
done
for i in $(cat files.tmp | egrep "\.pdf") ; do
	checkforprops "svn:mime-type" $i
	if [ $? == 0 ] ; then
		runcommand "svn propset svn:mime-type application/pdf $i" $echoonly
	fi
done
for i in $(cat files.tmp | egrep "\.sh") ; do
	checkforprops "svn:eol-style" $i
	if [ $? == 0 ] ; then
		runcommand "svn propset svn:eol-style LF $i" $echoonly
	fi
	checkforprops "svn:keywords" $i
	if [ $? == 0 ] ; then
		svnsetkeywords $i $echoonly
	fi
	checkforprops "svn:mime-type" $i
	if [ $? == 0 ] ; then
		runcommand "svn propset svn:mime-type application/x-sh $i" $echoonly
	fi
	checkforprops "svn:executable" $i
	if [ $? == 0 ] ; then
		runcommand "svn propset svn:executable ON $i" $echoonly
	fi
done

rm files.tmp

echo "DONE!"
