#!/bin/bash -e

# This build script needs the following three environment variables:
# PROFILE :      the profile specified in execProfile.sh
# LOG_DIR :      the directory to put the surefire reports
#
# - current directory must contain subfolders external and internal, settings.xml and run.sh (use downloadScripts.sh)
# - parent folder must contain passwords.sh

PROFILE=$1
LOG_DIR=$2

# Log directory
mkdir $LOG_DIR

cp settings.xml ~/.m2/settings.xml 

# perform the maven build and copy the result in any case
( ./run.sh $PROFILE ) || ( ( \
find . -path "*target/surefire-reports*" -exec cp  {} $LOG_DIR/ \; ) && (( ps -ely )) && (( 0 )) )
