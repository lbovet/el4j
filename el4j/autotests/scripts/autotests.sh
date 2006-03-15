#!/bin/bash

DOTASK="${1}"
shift 1

# Specific env variables for ELCA internal test server "leaffy"
export BASH_ENV=$HOME/.bashrc
export JAVA_HOME=/opt/bea/weblogic90/jdk1.5
export PATH=$PATH:$HOME/bin:$JAVA_HOME/bin
export ANT_OPTS="-Duser.language=en -Duser.region=US"

# Home of EL4J autotest configuration and EL4J projects
export BASEDIR=$HOME/el4j/external
# Directory where to copy the generated website
export REPORTDIR=/home/java/el4j/snapshot/website
# URL for the REPORTDIR above
export REPORTURL=http://leaffy/java/el4j/snapshot/website
# Base directory to write log files
export LOGBASEDIR="${BASEDIR}/autotests/logs"

#Execute tests with different profiles, generate reports and create a website
./website_framework.sh "${DOTASK}"
./website_framework.sh CleanOnly
echo ""
echo ""
./website_helloworld.sh "${DOTASK}"
./website_helloworld.sh CleanOnly
