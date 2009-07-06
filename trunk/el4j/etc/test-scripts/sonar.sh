#!/bin/bash -e

# check if sonar server is running
if ! /home/tester/el4j/tools/sonar/bin/linux-x86-32/sonar.sh status ; then
	# start sonar and wait for some minutes to be sure server is up
	startsonar
	sleep 300
fi

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
