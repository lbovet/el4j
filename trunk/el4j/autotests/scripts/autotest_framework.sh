#!/bin/bash

# $URL$
# $Revision$
# $Date$
# $Author$

DOTASK="${1}"
shift 1

# Setup the environment for project "framework"
## Home of EL4J autotest configuration and EL4J projects
export BASEDIR=$HOME/el4j/external
## Directory where to copy the generated website
export REPORTDIR=/home/java/el4j/snapshot/website
## URL for the REPORTDIR above
export REPORTURL=http://leaffy/java/el4j/snapshot/website
## Base directory to write log files
export LOGBASEDIR="${BASEDIR}/autotests/logs"


if [ -z "$LOGBASEDIR" ] || ! [ -d "$LOGBASEDIR" ] ; then
    echo "Env variable LOGBASEDIR must be set and point "
    echo "to the directory where log files can be written!"
    exit 1
fi

STARTTIME="`date +%Y%m%d_%H%M%S`"
LOGFILE="${DOTASK}-website_framework-${STARTTIME}.log"
SPECLOGDIR="${LOGBASEDIR}/scripts/framework"
SPECLOGPATH="${SPECLOGDIR}/${LOGFILE}"

mkdir -p "${SPECLOGDIR}"

#Delete old log files
./delete_old_log_files.sh "${SPECLOGDIR}"

echo "Executing task ${DOTASK} for external framework..."
./autotest_project.sh "${DOTASK}" "framework" "framework" "framework-tests" "weblogic-weblogic tomcat-jboss" &> ${SPECLOGPATH}
cat ${SPECLOGPATH}
