#!/bin/sh

echo java -version:
java -version
echo -----------------------------------------

echo javac -version:
javac -version
echo -----------------------------------------

echo mvn -version
mvn -version
echo -----------------------------------------

echo echo \$MAVEN_OPTS
echo $MAVEN_OPTS
echo -----------------------------------------
echo -----------------------------------------

echo expected output:
echo Must print out the version number of a Java 5 JDK or newer.
echo Must print out the same version number as above.
echo Must print out the version "2.0.7" or newer.
echo Must print something like -Xmx1024M -Xss128k -XX:MaxPermSize=512M -Duser.language=en -Duser.region=US
 
