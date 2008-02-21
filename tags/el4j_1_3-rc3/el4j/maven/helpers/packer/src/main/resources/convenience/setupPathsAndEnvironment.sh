#!/bin/sh 

#### setup each time:
alias debugmaven='export MAVEN_OPTS="-Xmx1024M -Xss128k -XX:MaxPermSize=512M -Duser.language=en -Duser.region=US -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000"'
alias runmaven='export MAVEN_OPTS="-Xmx1024M -Xss128k -XX:MaxPermSize=512M -Duser.language=en -Duser.region=US"'

case "`uname`" in
CYGWIN*)
	export M2_HOME="D:\Projects\EL4J\tools\maven"
	export PATH="/cygdrive/d/Projects/EL4J/tools/maven/bin:${PATH}"
	;;
*)
	export M2_HOME="/data/Projects/EL4J/tools/maven"
	export PATH="${M2_HOME}/bin:${PATH}"
	;;
esac

runmaven
