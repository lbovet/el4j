#!/bin/bash -e

# This build script needs the following three environment variables:
# PROFILE :      the profile specified in execProfile.sh
# WORKING_DIR :  must contain subfolders external and internal, parent folder must contain passwords.sh
# LOG_DIR :      the directory to put the surefire reports

PROFILE=$1
WORKING_DIR=$2
LOG_DIR=$3

# Log directory
mkdir $LOG_DIR

# Change to working directory
cd $WORKING_DIR

source ../passwords.sh setPasswords
cat external/etc/m2/settings.xml \
	| sed 's#~/.m2/repository#/home/users2/tester/el4j/m2repository#' \
	| sed 's#<!--proxy>#<proxy>#' | sed 's#</proxy-->#</proxy>#' \
	| sed 's#<!--server>#<server>#' | sed 's#</server-->#</server>#' \
	| sed 's#<!--mirror>#<mirror>#' | sed 's#</mirror-->#</mirror>#' \
	| sed 's#<!--project-server.path>#<project-server.path>#' | sed 's#</project-server.path-->#</project-server.path>#' \
	| sed "s#__USR_SRV__#$USR_SRV#" | sed "s#__PWD_SRV__#$PWD_SRV#" \
	| sed "s#__USR_LFY__#$USR_LFY#" | sed "s#__PWD_LFY__#$PWD_LFY#" \
	| sed "s#~/myproject#$WORKING_DIR#" \
	> ~/.m2/settings.xml 
source ../passwords.sh unsetPasswords


# perform the maven build and copy the result in any case
( ./external/etc/test-scripts/run.sh $PROFILE ) || ( ( \
find . -path "*target/surefire-reports*" -exec cp  {} $LOG_DIR/ \; ) && (( ps -ely )) && (( 0 )) )
