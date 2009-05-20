#!/bin/bash -e

# check if sonar server is up running

if [ ! sonarstatus ] ; then
	echo "Sonar is not running yet!"
	# start sonar and wait for some minutes to be sure server is up
	startsonar
	sleep 300
fi

# run sonar in external

cd external/

mvn clean install

mvn sonar:sonar -Dsonar.skippedModules=el4j-demos-remoting-jaxws,el4j-framework-tests-remoting-jaxws -P+sonar

# run sonar in internal

cd ../internal/

mvn clean install

mvn sonar:sonar -Dsonar.skippedModules=el4j-demos-remoting-jaxws,el4j-framework-tests-remoting-jaxws -P+sonar
