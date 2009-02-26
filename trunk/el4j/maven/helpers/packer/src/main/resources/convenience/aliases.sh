# EL4J aliases (do not delete this marker comment)
alias debugmaven='export MAVEN_OPTS="-ea -Xmx1024M -Xss128k -XX:MaxPermSize=512M -Duser.language=en -Duser.region=US -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000 -Dcom.sun.management.jmxremote"'
alias runmaven='export MAVEN_OPTS="-ea -Xmx1024M -Xss128k -XX:MaxPermSize=512M -Duser.language=en -Duser.region=US -Dcom.sun.management.jmxremote"'
runmaven

alias l='ls -al'
alias ..='cd ..'
alias m=less
alias mci='mvn clean install'
alias mciskip='mvn -Dmaven.test.skip=true clean install'
alias me='mvn eclipse:clean eclipse:eclipse'
alias mes='mvn eclipse:clean eclipse:eclipse -DdownloadSources=true'
alias mdf='mvn depgraph:fullgraph'

# Searching patterns in files
# usage: "ff <file-pattern> <search-string>" or "ff <search-string>" to search in all files
# example: "ff pom.xml spring"
function ff {
	if [ $# -eq 1 ] ; then
		find . | xargs grep "$1"
	else
		find . -name "$1" | xargs grep "$2"
	fi
}
