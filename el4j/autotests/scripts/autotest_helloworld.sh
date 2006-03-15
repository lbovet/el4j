#!/bin/bash

# $URL$
# $Revision$
# $Date$
# $Author$

DOTASK="${1}"
shift 1

if [ -z "$LOGBASEDIR" ] || ! [ -d "$LOGBASEDIR" ] ; then
    echo "Env variable LOGBASEDIR must be set and point "
    echo "to the directory where log files can be written!"
    exit 1
fi

STARTTIME="`date +%Y%m%d_%H%M%S`"
LOGFILE="${DOTASK}-website_helloworld-${STARTTIME}.log"
SPECLOGDIR="${LOGBASEDIR}/scripts/helloworld"
SPECLOGPATH="${SPECLOGDIR}/${LOGFILE}"

mkdir -p "${SPECLOGDIR}"

#Delete old log files
./delete_old_log_files.sh "${SPECLOGDIR}"

echo "Executing task ${DOTASK} for external helloworld..."
./website_project.sh "${DOTASK}" "external/helloworld" "helloworld" "helloworld-tests" "tomcat" &> ${SPECLOGPATH}
cat ${SPECLOGPATH}
