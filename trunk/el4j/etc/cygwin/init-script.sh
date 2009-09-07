#!/bin/bash

# if you are working on more than one project this might help:
#echo "**************************"
#echo "* Bash for $PROJECT_NAME *"
#echo "**************************"

alias debugmaven='export MAVEN_OPTS="-ea -Xmx1024M -Xss128k -XX:MaxPermSize=512M -Duser.language=en -Duser.region=US -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000 -Dcom.sun.management.jmxremote"'
alias runmaven='export MAVEN_OPTS="-ea -Xmx1024M -Xss128k -XX:MaxPermSize=512M -Duser.language=en -Duser.region=US -Dcom.sun.management.jmxremote"'
export CATALINA_OPTS="-Xmx1024M -Xss128k -XX:MaxPermSize=256M"

# workaround for Altiris problem
alias debugmaven='export MAVEN_OPTS="-ea -Xmx1024M -Xss128k -XX:MaxPermSize=256M -Duser.language=en -Duser.region=US -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000 -Dcom.sun.management.jmxremote"'
alias runmaven='export MAVEN_OPTS="-ea -Xmx1024M -Xss128k -XX:MaxPermSize=256M -Duser.language=en -Duser.region=US -Dcom.sun.management.jmxremote"'

runmaven

# it is strongly recommended to use the maven associated with the project (again useful if someone is working on more than one project)
export M2_HOME=$(cygpath -w "$PROJECT_DIRECTORY/tools/maven")
export PATH="$(cygpath -u $PROJECT_DIRECTORY)/tools/maven/bin:${PATH}"

# Searching patterns in files
function ff {
	if [ $# -eq 0 ] ; then
		echo "usage: ff <file-pattern> <search-string>"
		echo "or"
		echo "ff <search-string> to search in all files"
		echo ""
		echo "example: ff pom.xml spring"
		return
	fi
	
	if [ $# -eq 1 ] ; then
		find . | grep -v "\.svn" | xargs grep "$1"
	else
		find . -name "$1" | grep -v "\.svn" | xargs grep "$2"
	fi
}

alias l='ls -al'
alias ..='cd ..'
alias m=less
alias mci='mvn clean install'
alias mciskip='mvn -DskipTests=true -P-integrationTests clean install'
alias me='mvn eclipse:clean eclipse:eclipse'
alias mes='mvn eclipse:clean eclipse:eclipse -DdownloadSources=true'
alias mrr='mvn resources:resources'
alias mdf='mvn depgraph:fullgraph'
alias mep='mvn help:effective-pom'
alias mdt='mvn dependency:tree'
alias expl='explorer.exe /e,$(cygpath -w .)'
