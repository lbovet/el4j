#!/bin/bash -e


performInternal=$(cat .performInternal)
performExternal=$(cat .performExternal)
el4jNext=$(cat .nextVersion)

echo "You are preparing version $el4jNext with the following settings: performExternal=$performExternal, performInternal=$performInternal. OK?"
read dummy


echo "Since we are going to modify files we have to switch to the trunk."
echo "Press any key to continue or Ctrl-C to stop."
read dummy

if  [ $performExternal == "y" ] ; then
	cd external
	echo "Executing: svn switch https://el4j.svn.sourceforge.net/svnroot/el4j/trunk/el4j"
	svn switch https://el4j.svn.sourceforge.net/svnroot/el4j/trunk/el4j
	cd ..
	
	echo "Which tag/version should be chosen for the new release? (like 1.1.1)"
	read version
	version=$(echo $version | sed "s/\./_/g")
	echo "Version is $version, OK?"
	read dummy
	
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
else
	echo "Nothing to do... You decided not to perform external and there is no documentation to prepare for internal."
fi
