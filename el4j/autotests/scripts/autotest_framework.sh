#!/bin/bash

# $URL$
# $Revision$
# $Date$
# $Author$

DOTASK="${1}"
shift 1

# Setup the environment for project "framework"
./setup_environment_framework.sh

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
./website_project.sh "${DOTASK}" "external/framework" "framework" "framework-tests" "weblogic-weblogic tomcat-jboss" &> ${SPECLOGPATH}
cat ${SPECLOGPATH}
