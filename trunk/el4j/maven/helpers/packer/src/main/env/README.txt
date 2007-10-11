This is the convenience zip for EL4J ${project.version}

The scripts work under unix and windows/cygwin!
For a more complete list of (manual) steps to follow, please refer to 
http://wiki.elca.ch/twiki/el4j/bin/view/EL4J/GettingStarted or 
docs/pdf/GettingStarted.pdf. In case of disagreement of this automatic and the 
manual set up in those files, the manual set up takes precedence. 

Prerequisites:
 * If under Windows: Cygwin (http://www.cygwin.com/) otherwise bash suffices.
 * JDK 1.5 or higher

To set up:
 * create directory d:/Projects (Windows) or /data/Projects (Linux)
 * unzip this zip in the previously created directory
 * rename directory el4j-xy to EL4J
 * open a cygwin or bash shell, cd EL4J
 * execute  chmod 755 *sh
 * chmod 755 /data/el4j/tools/maven/bin/mvn
 * execute  ./finishInstallation.sh 
      This finalizes the installation. You need to do this only once.
 * execute  source ./setupPathsAndEnvironment.sh
      This sets up your environment. This is required each time you open a new shell.
 * execute  ./checkInstallation.sh
      This checks your installation. Please look through the output of the 
      command.
      
To start with a trivial program (1 class and 1 test)
 * open a shell, cd EL4J
 * execute source ./setupPathsAndEnvironment.sh
 * mvn archetype:create -DarchetypeGroupId=ch.elca.el4j -DarchetypeArtifactId=EL4JArchetypeCore -DarchetypeVersion=1.4 -DgroupId=ch.elca.el4j -DartifactId=myFirstProject -DremoteRepositories=http://el4.elca-services.ch/el4j/maven2repository 
 * cd myFirstProject
 * mvn install
 * mvn exec:java

To start working with the EL4J sources
 * open a shell, cd EL4J
 * execute source ./setupPathsAndEnvironment.sh
 * svn co https://el4j.svn.sourceforge.net/svnroot/el4j/trunk/el4j external
 * cd external
 * mvn clean install
 * if you're outside of ELCA, this will fail due to missing database drivers
    (we don't have the right to publish them). In this case, do the following (you could also remove the oracle or db2 dependencies):
     * Download the Oracle driver (ojdbc14.jar) from "http://www.oracle.com/technology/software/tech/java/sqlj_jdbc/". Note: You can also use the 10g driver for version 9 databases.
     * Download the DB2/Derby JDBC driver (db2jcc.jar and db2jcc_license_c.jar) from "http://www.ibm.com/developerworks/db2/downloads/jcc/".
     * Open a console and change to the directory where you have saved the downloaded jar files (but within the EL4J/external directory).
     * Execute the following commands:
       o mvn install:install-file -DgroupId=com.oracle -DartifactId=ojdbc14_g -Dversion=10.2.0.1.0 -Dpackaging=jar -Dfile=ojdbc14_g.jar
       o mvn install:install-file -DgroupId=com.ibm -DartifactId=db2jcc -Dversion=20040819 -Dpackaging=jar -Dfile=db2jcc.jar
       o mvn install:install-file -DgroupId=com.ibm -DartifactId=db2jcc_license_c -Dversion=20040819 -Dpackaging=jar -Dfile=db2jcc_license_c.jar
     * mvn clean install  
       
To start with the gui application template (for the moment the convenience zip is only available within ELCA)
* Follow the instructions under http://wiki.elca.ch/twiki/el4j/bin/view/EL4J/SpringRcpApplicationTemplate or 
  set the application up yourself by downloading the source from svn under
   https://svn.sourceforge.net/svnroot/el4j/trunk/el4j/applications/templates/
 
To start with the web application template (due to proprietary technology, this is only available within ELCA)
 * Download http://leaffy.elca.ch/java/el4j/templates/web-template-1.1.3.zip and unzip it to el4j/web-template
 * Open a cygwin console and go to el4j/web-template, where the template is located.
 * Type mvn clean install to install the template.
 * cd web/war
 * Type in the command mvn db:prepareDB cargo:undeploy cargo:deploy cargo:start to prepare the database and deploy the template to tomcat
 * Open your browser and go to the webpage http://localhost:8080/web-template-web/
 * More info: http://wiki.elca.ch/twiki/el4j/bin/view/EL4J/WebApplicationTemplate
 
To start with the seam application template do the following (this is not yet tested a lot)
 * Download el4j-seam.zip application template from sf.net or 
    http://wiki.elca.ch/twiki/el4j/bin/view/EL4J/JbossSeam and unzip it to the 
    EL4J directory
 * Open a shell and go to EL4J/el4j-seam, where the template is located.
 * Execute  source ../setupPathsAndEnvironment.sh 
 * Execute  mvn clean install -DinitDB=true
 * cd  war/ 
 * Run   mvn db:start jetty:run   to start the Derby network server and run jetty
 * You can access the application now at http://localhost:8080/
 
More information:
 * The local ./docs directory
 * http://el4j.sourceforge.net/
 * http://wiki.elca.ch/twiki/el4j/bin/view/EL4J
