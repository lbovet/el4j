#!/bin/bash -e

# This build script needs the following three environment variables:
# PROFILE :      the profile specified in run.sh
# LOG_DIR :      the directory to put the surefire reports
#
# - current directory must contain subfolders external and/or internal, settings.xml and run.sh (use downloadScripts.sh)

PROFILE=$1
LOG_DIR=$2

if [ $# -ge 2 ] ; then
	# Log directory
	mkdir $LOG_DIR
fi

if [ $# -ge 3 ] && [ $3 == "Java6" ] ; then
	cat settings.xml | sed 's#/m2repository#/m2repositoryJava6#' > ~/.m2/settings.xml
	OPTS="-Del4j.java.version=1.6"
else
	cp settings.xml ~/.m2/settings.xml
	OPTS=""
fi

if [ $# -ge 2 ] ; then
	# perform the maven build and copy the result in case of errors
	if ./run.sh $PROFILE $OPTS ; then
		echo Build successful
	else
		echo Build failed
		find . -path "*target/surefire-reports*" -exec cp  {} $LOG_DIR/ \;
		ps -ely
		false
	fi
else
	# no log file -> just run tests
	./run.sh $PROFILE $OPTS
fi

# move old el4j-logs to ~/el4j/logs/el4j-logging/
for logFile in $(find /tmp/el4j-logging/ -mtime +1) ; do
	mv $logFile ~/el4j/logs/el4j-logging/
done
