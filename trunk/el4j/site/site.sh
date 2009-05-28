#!/bin/bash -e

if [ ! -e ../../external/site ] ; then
	echo "This script has to be run from external/site."
	exit 1
fi

# clean generated docs
mvn clean
cd ..

# install root pom
mvn clean install -N $1

# install framework pom
cd framework
mvn clean install -N $1

# generate doc for modules
mvn -N -Pgenerate.doc.set.framework-modules collector:aggregate-files $1
mvn -N -Pgenerate.doc.set.framework-modules jxr:jxr $1 &
mvn -Pgenerate.doc.set.framework-modules javadoc:javadoc $1 &

# generate doc for applications
mvn -N -Pgenerate.doc.set.framework-applications collector:aggregate-files $1
mvn -N -Pgenerate.doc.set.framework-applications jxr:jxr $1 &
mvn -Pgenerate.doc.set.framework-applications javadoc:javadoc $1 &

# generate doc for tests
mvn -N -Pgenerate.doc.set.framework-tests collector:aggregate-files $1
mvn -N -Pgenerate.doc.set.framework-tests jxr:jxr $1 &
mvn -Pgenerate.doc.set.framework-tests javadoc:test-javadoc $1 &

# generate doc for demos
#mvn -N -Pgenerate.doc.set.framework-demos collector:aggregate-files
#mvn -N -Pgenerate.doc.set.framework-demos jxr:jxr &
#mvn -Pgenerate.doc.set.framework-demos javadoc:javadoc &

# wait until all parallel processes are done
wait

cd ..

# generate doc for plugins
cd maven/plugins
mvn clean install $1
mvn site $1
plugins=$(find -maxdepth 1 -name "maven-*-plugin"  -exec basename {} \;)
cd ../..

cd framework/plugins
mvn clean install $1
mvn site $1
frameworkPlugins=$(find -maxdepth 1 -name "maven-*-plugin"  -exec basename {} \;)
cd ../..

# generate test reports (no -Ptest.quiet)
mvn install -Pauto,tomcat6x,db2 $1
mvn antrun:run -f site/pom.xml -Pcopy.surefire-report.tomcat-derby $1

mvn install -Pauto,weblogic10x,oracle $1
mvn antrun:run -f site/pom.xml -Pcopy.surefire-report.weblogic-oracle $1

# aggregate files
cd site
mvn collector:aggregate-files -Pcollect-doc $1
for p in $plugins ; do
	mvn collector:aggregate-files -Pcollect-plugin-doc -Dplugin=$p $1
done
for p in $frameworkPlugins ; do
	mvn collector:aggregate-files -Pcollect-framework-plugin-doc -Dplugin=$p $1
done

# make site
mvn site -Psurefire-report.tomcat-derby $1
mvn site -Psurefire-report.weblogic-oracle $1

# deploy site
mvn site-deploy $1
