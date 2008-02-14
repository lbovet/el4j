#!/bin/bash -e

if [ ! -e ../../external/site ] ; then
	echo "This script has to be run from external/site."
	exit 1
fi

# clean generated docs
mvn clean
cd ..

# install root pom
mvn clean install -N

# install framework pom
cd framework
mvn clean install -N

# generate doc for modules
mvn -N -Pgenerate.doc.set.framework-modules collector:aggregate-files
mvn -Pgenerate.doc.set.framework-modules javadoc:javadoc jxr:jxr

# generate doc for applications
#mvn -N -Pgenerate.doc.set.framework-applications collector:aggregate-files
#mvn -Pgenerate.doc.set.framework-applications javadoc:javadoc jxr:jxr

# generate doc for tests
mvn -N -Pgenerate.doc.set.framework-tests collector:aggregate-files
mvn -Pgenerate.doc.set.framework-tests javadoc:javadoc jxr:jxr

# generate doc for demos
#mvn -N -Pgenerate.doc.set.framework-demos collector:aggregate-files
#mvn -Pgenerate.doc.set.framework-demos javadoc:javadoc jxr:jxr

cd ..

# generate doc for plugins
cd maven/plugins
mvn clean install site
plugins=$(find -maxdepth 1 -name "maven-*-plugin"  -exec basename {} \;)
cd ../..

cd framework/plugins
mvn clean install site
plugins="$plugins $(find -maxdepth 1 -name "maven-*-plugin"  -exec basename {} \;)"
cd ../..

# generate test reports (no -Ptest.quiet)
mvn install -Pauto,tomcat6x,db2
mvn antrun:run -f site/pom.xml -Pcopy.surefire-report.tomcat-derby

mvn install -Pauto,weblogic10x,oracle
mvn antrun:run -f site/pom.xml -Pcopy.surefire-report.weblogic-oracle

# aggregate files
cd site
mvn collector:aggregate-files -Pcollect-doc
for p in $plugins ; do
	mvn collector:aggregate-files -Pcollect-plugin-doc -Dplugin=$p
done

# hack due to bug in surefire repor plugin
#mv site/target/site/framework-tests/xref/ site/target/site/xref-test

# make site
mvn site -Psurefire-report.tomcat-derby
mvn site -Psurefire-report.weblogic-oracle

# deploy site
mvn site-deploy
