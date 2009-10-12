#!/bin/bash -e

# check if sonar server is up running

sonarstatus > son-status

status=$(cat son-status)

if [ "$status" == "sonar is not running" ] ; then
	echo "Sonar is not running yet!"
	# start sonar and wait for some minutes to be sure server is up
	startsonar
	sleep 300
fi

rm son-status

# run sonar in external

cd external/
svn up
mvn clean install

mvn sonar:sonar -Dsonar.skippedModules=el4j-demos-remoting-jaxws,el4j-framework-tests-remoting-jaxws -P+sonar

# run sonar in internal

cd ../internal/
svn up
mvn clean install

mvn sonar:sonar -Dsonar.skippedModules=el4j-demos-remoting-jaxws,el4j-framework-tests-remoting-jaxws -P+sonar
