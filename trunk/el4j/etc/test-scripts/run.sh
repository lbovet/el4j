#!/bin/bash -e

# Override username for oracle builds.
ORACLE_SETTINGS="-Doverride.db.username=leaf4"

if [ $# -lt 1 ] ; then
	echo "No profile specified."
	exit 0
fi

# Delete the db2 database prior to building.
echo Cleaning db2 ...
rm -rf ~/el4j/hudson_build_workspaces/tools/derby/derby-databases/*

case $1 in
	"external_nightly")
		cd external
		svn up
		mvn clean install -fae -B -Pauto,weblogic10x,oracle,integrationTests ${ORACLE_SETTINGS} $2
		mvn -f site/pom.xml site-deploy $2
		;;
	"external_svn")
		cd external
		mvn clean install -fae -B -U $2
		;;
	"internal_nightly")
		cd internal
		svn up
		mvn clean install -fae -B -Pauto,weblogic10x,oracle,integrationTests ${ORACLE_SETTINGS} $2
		mvn -f site/pom.xml site-deploy $2
		cd ..
		
		# test templates
		./internal/etc/release-scripts/createTemplates.sh clean
		./internal/etc/release-scripts/createTemplates.sh
		./internal/etc/release-scripts/createTemplates.sh clean
		;;
	"internal_svn")
		cd internal
		mvn clean install -fae -B -U $2
		;;
	"release_tomcat")
		mvn -f external/pom.xml clean install -fae -B -Pauto,tomcat6x,db2,integrationTests $2
		mvn -f internal/pom.xml clean install -fae -B -Pauto,tomcat6x,db2,integrationTests $2
		
		# test templates
		./internal/etc/release-scripts/createTemplates.sh clean
		./internal/etc/release-scripts/createTemplates.sh -Pauto,tomcat6x,db2,integrationTests $2
		./internal/etc/release-scripts/createTemplates.sh clean
		;;
	"release_weblogic")
		mvn -f external/pom.xml clean install -fae -B -Pauto,weblogic10x,oracle,integrationTests ${ORACLE_SETTINGS} $2
		mvn -f internal/pom.xml clean install -fae -B -Pauto,weblogic10x,oracle,integrationTests ${ORACLE_SETTINGS} $2
		
		# test templates
		./internal/etc/release-scripts/createTemplates.sh clean
		./internal/etc/release-scripts/createTemplates.sh -Pauto,tomcat6x,db2,integrationTests ${ORACLE_SETTINGS} $2
		./internal/etc/release-scripts/createTemplates.sh clean
		;;
	"release_website")
		rm -r /home/users2/tester/java/el4j/snapshot/website/external/* > /dev/null
		cd external/site
		./site.sh
		cd ../..

		## internal is not necessary
		#cd internal/site
		#./site.sh
		#cd ../..
		;;
	"clean_checkout")
		rm -rf external
		rm -rf internal

		if [ $# -ge 3 ] ; then
			SVN_EXTERNAL=$2
			SVN_INTERNAL=$3
		else
			SVN_EXTERNAL=https://el4j.svn.sourceforge.net/svnroot/el4j/trunk/el4j
			SVN_INTERNAL=https://cvs.elca.ch/subversion/el4j-internal/trunk
		fi
		# Checkout the external sourcecode
		svn co -q $SVN_EXTERNAL external

		# Checkout the internal sourcecode
		svn co -q $SVN_INTERNAL internal
		;;
	"weekly")
		mvn -f external/pom.xml clean install -fae -B -Pauto,tomcat6x,oracle,integrationTests ${ORACLE_SETTINGS} $2
		mvn -f internal/pom.xml clean install -fae -B -Pauto,tomcat6x,oracle,integrationTests ${ORACLE_SETTINGS} $2
		mvn -f external/pom.xml clean install -fae -B -Pauto,weblogic10x,db2,integrationTests $2
		mvn -f internal/pom.xml clean install -fae -B -Pauto,weblogic10x,db2,integrationTests $2
		;;
	"archetype")
		version=$(cat external/maven/archetypes/module-template/pom.xml | grep "<version>" | tail -n 1 | tr -d ' \t\r\n<>version/' | sed 's/-SNAPSHOT//')

		#mvn archetype:create -DarchetypeGroupId=ch.elca.el4j
		mvn org.apache.maven.plugins:maven-archetype-plugin:1.0-alpha-7:create -DarchetypeGroupId=ch.elca.el4j  \
			 -DarchetypeArtifactId=EL4JArchetypeCore -DarchetypeVersion=$version \
			 -DgroupId=ch.elca.test -DartifactId=testarchetype  \
			 -DremoteRepositories=http://el4.elca-services.ch/el4j/maven2repository

		mvn -f testarchetype/pom.xml clean install $2
		rm -rf testarchetype
		;;
esac
