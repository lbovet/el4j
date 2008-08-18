#!/bin/bash -e

# This build script needs the following three environment variables:
# PROFILE :      the profile specified in run.sh
# LOG_DIR :      the directory to put the surefire reports
#
# - current directory must contain subfolders external and/or internal, settings.xml and run.sh (use downloadScripts.sh)

PROFILE=$1
LOG_DIR=$2

# Log directory
mkdir $LOG_DIR

if [ $# -ge 3 ] && [ $3 == "Java6" ] ; then
	cat settings.xml \
		| sed 's#jrockit-R27.4.0-jdk1.5.0_12#jrockit-R27.5.0-jdk1.6.0_03#' \
		| sed 's#/m2repository#/m2repositoryJava6#' > ~/.m2/settings.xml
	OPTS="-Del4j.java.version=1.6"
else
	cp settings.xml ~/.m2/settings.xml
	OPTS=""
fi

# perform the maven build and copy the result in case of errors
./run.sh $PROFILE $OPTS || ( ( \
find . -path "*target/surefire-reports*" -exec cp  {} $LOG_DIR/ \; ) && (( ps -ely )) && (( 0 )) )