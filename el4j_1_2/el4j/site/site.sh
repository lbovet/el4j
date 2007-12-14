#!/bin/bash -e

if [ $# -ne 1 ] ; then
	echo "Parameter must be external or internal."
	exit 1
fi
cd $1

# clean generated docs
cd site
mvn clean
cd ..

# install root pom
mvn clean install -N

# install framework pom
cd framework
mvn clean install -N

# generate doc for modules
mvn -Pgenerate.doc.set.framework-modules collector:aggregate-files javadoc:javadoc jxr:jxr

# Excluded applications from site generation

# generate doc for tests
mvn -Pgenerate.doc.set.framework-tests collector:aggregate-files javadoc:javadoc jxr:jxr

# Excluded Demos from site generation
cd ..

# generate doc for plugins
cd maven/plugins
mvn clean install site
plugins=$(find -maxdepth 1 -name "maven-*-plugin"  -exec basename {} \;)
cd ../..

# generate test reports (no -Ptest.quiet)
mvn install -Pauto,tomcat6x,db2
mvn antrun:run -f site/pom.xml -Pcopy.surefire-report.tomcat-derby

mvn install -Pauto,weblogic10,oracle
mvn antrun:run -f site/pom.xml -Pcopy.surefire-report.weblogic-oracle

# aggregate files
mvn collector:aggregate-files -f site/pom.xml -Pcollect-doc
for p in $plugins ; do
	mvn collector:aggregate-files -f site/pom.xml -Pcollect-plugin-doc -Dplugin=$p
done

# hack due to bug in surefire repor plugin
mv site/target/site/framework-tests/xref/ site/target/site/xref-test

# make site
mvn site -f site/pom.xml -Psurefire-report.tomcat-derby
mvn site -f site/pom.xml -Psurefire-report.weblogic-oracle

# deploy site
mvn site-deploy -f site/pom.xml
