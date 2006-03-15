#!/bin/bash

# $URL$
# $Revision$
# $Date$
# $Author$

cd "${1}"
shift 1

TODAY="`date +%D`"
LASTMONTH="`date --date \"${TODAY} -1month\"`"
touch -d "${LASTMONTH}" file_of_last_month
find ! -newer file_of_last_month -exec rm {} \;
