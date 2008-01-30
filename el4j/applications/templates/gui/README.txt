How to launch the gui demo application 
======================================

standalone mode
===============

 cd ..; mvn clean install
 
 cd standalone-client
 mvn db:prepareDB exec:java


distributed mode
================

  start server:

   cd server/war
   mvn db:prepareDB cargo:undeploy cargo:deploy cargo:start 
   # the server should now be available under http://localhost:8080/swing-demo-server


  start gui (in a separate console): 
 
   cd thin-client
   mvn exec:java


JAR mode
========
 
 cd standalone-client

 # create a JAR with all dependencies (see maven-assembly-plugin in the pom.xml)
 mvn assembly:assembly

 # Start the JAR in the console or make a double-click on the JAR file
 java -jar target/swing-demo-standalone-client-2.5-SNAPSHOT-jar-with-dependencies.jar