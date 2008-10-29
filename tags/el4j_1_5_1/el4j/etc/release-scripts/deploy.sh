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
if ! [ -e external ] ; then
	echo "Error: Folder 'external' not found. Go to its parent folder (el4j)!"
	exit
fi

if [ $# -eq 1 ] ; then
	performInternal=$1
	auto=true
else
	echo "Process internal? (y/n)"
	read performInternal
	auto=false
fi


cd external
mvn deploy -N

cd applications
mvn deploy -N
cd ..

cd framework
mvn deploy -N

cd modules
mvn deploy
cd ..

cd plugins
# maven-metadata.xml is not updated in 2.0.9 if mvn deploy is not called for every plugin explicitly
mvn deploy -N
for i in $(ls | grep "^maven-") ; do
	cd $i
	mvn deploy
	cd ..
done
cd ../..

cd maven
mvn deploy -N

cd utils
mvn deploy
cd ..

cd archetypes
for i in $(ls) ; do
	cd $i
	mvn deploy
	cd ..
done
cd ..

cd plugins
# maven-metadata.xml is not updated in 2.0.9 if mvn deploy is not called for every plugin explicitly
for i in $(ls | grep "^maven-") ; do
	cd $i
	mvn deploy
	cd ..
done
cd ../..

cd ..


if [ ${performInternal:0:1} != "y" ] ; then
	exit
fi

cd internal
mvn deploy -N

cd applications
mvn deploy -N
cd ..

cd framework
mvn deploy -N

cd modules
mvn deploy
cd ../..

cd maven
mvn deploy -N

cd plugins
mvn deploy
cd ../..
