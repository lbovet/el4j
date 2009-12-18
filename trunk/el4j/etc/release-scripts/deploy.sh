#!/bin/bash -e

#Now we want to deploy the following modules to the corresponding repositories:
#   * external
#      * applications (only - no subdirectories. This is the entry point for the templates)
#      * framework
#         * modules
#      * maven
#         * archetypes (*)
#         * plugins
#   * internal
#      * framework
#         * modules
#
#(*) The archetypes have to be deployed seperatly. To do so enter each directory of the archetypes and execute =mvn deploy= there.
#      
#Note that all folders here that have a subfolder need the option =-N= when =mvn deploy= is run in them, to prevent recursive invocations on subfolders we don't want to be deployed.
#So our invocations look like the following
#
#   * %box% =cd "D:/el4jFresh/external"=
#   * %box% Deploy the toplevel pom =mvn deploy -N=
#   * %box% Deploy the applications entrypoint
#      * =cd applications=
#         * =mvn deploy -N=
#         * =cd ..=
#   * %box% Deploy the framework
#      * =cd framework=
#         * =mvn deploy -N=
#         * =cd modules=
#            * =mvn deploy=
#            * =cd ../..=
#   * %box% Deploy the maven tools
#      * =cd maven/=
#         * =mvn deploy -N=
#         * =cd archetypes/module-x= (Repeat this for each archetype)
#            * =mvn deploy=
#            * =cd ../..=
#         * =cd plugins=
#            * =mvn deploy=
#            * =cd ../..=
#
#   * %box% =cd "D:/el4jFresh/internal"=
#   * %box% Deploy the toplevel pom =mvn deploy -N=
#   * %box% Deploy the framework
#      * =cd framework=
#         * =mvn deploy -N=
#         * =cd modules=
#            * =mvn deploy=
#            * =cd ../..=

# make sure you are in right folder

performInternal=$(cat .performInternal)
performExternal=$(cat .performExternal)
el4jNext=$(cat .nextVersion)


echo "You are preparing version $el4jNext with the following settings: performExternal=$performExternal, performInternal=$performInternal. OK?"
read dummy

if ! [ -e external ] ; then
	echo "Error: Folder 'external' not found. Go to its parent folder (el4j)!"
	exit
fi

#define function to execute statements and check for BUILD ERROR in return
function executewithcheck {
	builderrorfound=0;
	while read line
	do
		echo $line
		if [[ $line == *BUILD\ ERROR* ]] ; then
			builderrorfound=1;
		fi
	done < <($1)
	if [ $builderrorfound == 1 ] ; then 
		echo "[SCRIPT ERROR] Build Error detected - execution of script aborted";
		exit 1;
	fi
}

if  [ $performExternal == "y" ] ; then

	cd external
	executewithcheck "mvn deploy -N"
	
	cd applications
	executewithcheck "mvn deploy -N"
	cd ..
	
	cd framework
	executewithcheck "mvn deploy -N"
	
	cd modules
	executewithcheck "mvn deploy"
	cd ..
	
	cd plugins
	# maven-metadata.xml is not updated in 2.0.9 if mvn deploy is not called for every plugin explicitly
	executewithcheck "mvn deploy -N"
	for i in $(ls | grep "^maven-") ; do
		cd $i
		executewithcheck "mvn deploy"
		cd ..
	done
	
	# deploy maven utils
	cd utils
	executewithcheck "mvn deploy"
	cd ..
	
	cd ../..
	
	cd maven
	executewithcheck "mvn deploy -N"
	
	cd archetypes
	for i in $(ls) ; do
		cd $i
		executewithcheck "mvn deploy"
		cd ..
	done
	cd ..
	
	cd plugins
	# maven-metadata.xml is not updated in 2.0.9 if mvn deploy is not called for every plugin explicitly
	executewithcheck "mvn deploy -N"
	for i in $(ls | grep "^maven-") ; do
		cd $i
		executewithcheck "mvn deploy"
		cd ..
	done
	cd ..
	
	cd extensions/taglet
	executewithcheck "mvn deploy"
	cd ../..
	
	cd ..
	
	cd skin
	executewithcheck "mvn deploy"
	cd ..
	
	cd ..
fi

if  [ $performInternal == "y" ] ; then
	cd internal
	executewithcheck "mvn deploy -N"
	
	cd applications
	executewithcheck "mvn deploy -N"
	cd ..
	
	cd framework
	executewithcheck "mvn deploy -N"
	
	cd modules
	executewithcheck "mvn deploy"
	cd ../..
	
	cd maven
	executewithcheck "mvn deploy -N"
	
	cd plugins
	executewithcheck "mvn deploy"
	cd ../..
fi
