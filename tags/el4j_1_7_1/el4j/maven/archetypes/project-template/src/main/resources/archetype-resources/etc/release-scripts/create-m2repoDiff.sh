#!/bin/bash -e

# configuration
M2REPO="/home/rhb/m2repository"
REPO_EXCLUSION_PATTERN="/ch/rhb/"
REPO_DIFF_FOLDER=$(pwd)/m2repodiff
DATE_NOW_STRING=$(date +%Y%m%d%H%M)

# create folder if it does not exists
if ! [ -e $REPO_DIFF_FOLDER ] ; then
	mkdir $REPO_DIFF_FOLDER
	touch -t 197001010000 $REPO_DIFF_FOLDER/incrM2repoLog.log
fi
# go to parent folder of M2REPO to get m2repository as folder in the zip file
cd $M2REPO/..
find ${M2REPO##*/} -newer $REPO_DIFF_FOLDER/incrM2repoLog.log | grep -v $REPO_EXCLUSION_PATTERN | zip $REPO_DIFF_FOLDER/incremental-m2repo$DATE_NOW_STRING.zip -@
echo $(date) >> $REPO_DIFF_FOLDER/incrM2repoLog.log
