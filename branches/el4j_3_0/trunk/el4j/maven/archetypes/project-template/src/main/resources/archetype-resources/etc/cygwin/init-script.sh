#!/bin/bash

# if you are working on more than one project this might help:
#echo "**************************"
#echo "* Bash for $PROJECT_NAME *"
#echo "**************************"

alias debugmaven='export MAVEN_OPTS="-Xmx1024m -Xss128k -XX:MaxPermSize=256M -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000"'
alias runmaven='export MAVEN_OPTS="-Xmx1024m -Xss128k -XX:MaxPermSize=256M"'
runmaven

echo $PROJECT_DIRECTORY
# it is strongly recommended to use the maven associated with the project (again useful if someone is working on more than one project)
#export PATH=/cygdrive/d/Projects/RhB/tools/maven/bin:$PATH

# define more
