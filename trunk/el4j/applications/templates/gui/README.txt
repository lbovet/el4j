How to launch the gui demo application
======================================

standalone mode
===============

 cd ..; mvn clean install
 
 cd standalone-client
 mvn db:prepare exec:java


distributed mode
================

  start server:

   cd server/war
   mvn db:prepare cargo:undeploy cargo:deploy cargo:start 
   # the server should now be available under http://localhost:8080/swing-demo-server


  start gui (in a separate console): 
 
   cd thin-client
   mvn exec:java


Deploy the application as a single jar
=======================================
 
 cd standalone-client

 # Create a JAR with all dependencies (see maven-assembly-plugin configuration in the pom.xml).
 mvn assembly:assembly
 
 # Open a new console and start the database.
 mvn db:prepare db:block

 # Start the JAR in the first console or make a double-click on the JAR file.
 java -jar target/swing-demo-standalone-client-<version>-jar-with-dependencies.jar

Remark: The generation of the single jar only works in all cases, when there are no
duplicate configuration files in the jar files to merge (this is due to the fact that the order maven uses
to unpack the jar files is only approximately the same as the one we use when reading the configuration files).
This should typically not be an issue.
If we create a single jar for a project A with a dependecy to project B and this has a dependecy to project C, 
Maven starts unpacking at project B followed by project C and finishes at project A. Maven follows up the 
dependencies and unpacks successively the jar files but it always finishes with your own project.

