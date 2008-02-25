#!/bin/bash -e

echo "Since we are going to modify files we have to switch to the trunk."
echo "Press any key to continue or Ctrl-C to stop."
read dummy

cd external
echo "Executing: svn switch https://el4j.svn.sourceforge.net/svnroot/el4j/trunk/el4j"
svn switch https://el4j.svn.sourceforge.net/svnroot/el4j/trunk/el4j
cd ..

version=$(cat external/pom.xml | grep "<version.el4j-framework.current>" -A 1 | tail -n 1 | tr -d ' \r\n')

cd external/site/src/site/resources/docs/pdf
./wikiPdfDownloadScript.sh

svn status
problems=$(svn status | grep "^?" | wc -l)
if [ $problems -ne 0 ] ; then
	echo "Attention: new files were added. Commit has to be done manually"
	echo "Snippet: svn commit -m \"Update documentation for release of EL4J $version\""
else
	echo "Executing: svn commit -m \"Update documentation for release of EL4J $version\""
	echo "Press any key to continue or Ctrl-C to stop."
	read dummy
	svn commit -m "Update documentation for release of EL4J $version"
fi

echo ""
echo "Keep in mind that external now points to the trunk!"
