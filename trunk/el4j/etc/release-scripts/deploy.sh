#!/bin/bash -e

# make sure you are in right folder
if ! [ -e external ] ; then
	echo "Error: Folder 'external' not found. Go to its parent folder (el4j)!"
	exit
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

cd ../..

cd maven
mvn deploy -N

cd archetypes
for i in $( ls ); do
	cd $i
	mvn deploy
	cd ..
done
cd ..

cd plugins
mvn deploy
cd ../..

cd ..

echo "Process internal? (y/n)"
read performInternal

if [ $performInternal != "y" ] ; then
	exit
fi

cd internal
mvn deploy -N

cd framework
mvn deploy -N

cd modules
mvn deploy
cd ../..
