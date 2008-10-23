#!/bin/bash
tags="URL Date Author Revision Id"

svn ls -R > files.tmp
echo "" > updateProp.txt

for i in $(cat files.tmp | egrep "(\.java)|(\.sql)|(\.txt)|(\.vm)|(\.htm)|(\.apt)|(\.twiki)|(\.css)|(\.dtd)|(\.properties)") ; do
	echo "svn propset svn:keywords \"$tags\" $i" >> updateProp.txt
	echo "svn propset svn:eol-style native $i" >> updateProp.txt
done
for i in $(cat files.tmp | egrep "(\.xml)|(\.xsl)|(\.xsd)") ; do
	echo "svn propset svn:keywords \"$tags\" $i" >> updateProp.txt
	echo "svn propset svn:eol-style native $i" >> updateProp.txt
	echo "svn propset svn:mime-type text/xml $i" >> updateProp.txt
done
for i in $(cat files.tmp | egrep "\.png") ; do
	echo "svn propset svn:mime-type image/png $i" >> updateProp.txt
done
for i in $(cat files.tmp | egrep "\.jpg") ; do
	echo "svn propset svn:mime-type image/jpeg $i" >> updateProp.txt
done
for i in $(cat files.tmp | egrep "\.gif") ; do
	echo "svn propset svn:mime-type image/gif $i" >> updateProp.txt
done
for i in $(cat files.tmp | egrep "\.pdf") ; do
	echo "svn propset svn:mime-type application/pdf $i" >> updateProp.txt
done
for i in $(cat files.tmp | egrep "\.sh") ; do
	echo "svn propset svn:eol-style LF $i" >> updateProp.txt
	echo "svn propset svn:keywords \"$tags\" $i" >> updateProp.txt
	echo "svn propset svn:mime-type application/x-sh $i" >> updateProp.txt
	echo "svn propset svn:executable ON $i" >> updateProp.txt
done
cat files.tmp
rm files.tmp

echo "Copy everthing from updateProp.txt to cygwin shell"
