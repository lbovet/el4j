This is the convenience zip for EL4J ${project.version}

A list of known issues can be found at:
http://el4j.svn.sourceforge.net/viewvc/*checkout*/el4j/trunk/el4j/etc/KnownIssues.txt

The scripts work under unix and windows/cygwin!
(There are some .bat files for windows without cygwin, but they are less tested.)

If you are new to Maven and EL4J, please refer to 
http://wiki.elca.ch/twiki/el4j/bin/view/EL4J/GettingStarted or 
docs/pdf/GettingStarted.pdf and follow the step-by-step tutorial.
If you are an EL4J developer please refer additionally to
http://wiki.elca.ch/twiki/el4j/bin/view/EL4J/GettingStartedDeveloper or 
docs/pdf/GettingStartedDeveloper.pdf.
The following instructions describe the fast-track and are recommended 
for advanced users.

In case of disagreement of this automatic and the 
manual set up in those files, the manual set up takes precedence. 

Prerequisites:
 * If under Windows: Cygwin (http://www.cygwin.com/) otherwise bash suffices.
 * JDK 1.5.0_14 or higher

To set up:
 * create directory d:/Projects (Windows) or /data/Projects (Linux)
 * unzip this zip in the previously created directory
 * rename directory el4j-xy to EL4J
 * open a cygwin or bash shell, cd EL4J
 * execute  chmod 755 *sh
 * execute  chmod 755 tools/maven/bin/mvn*
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
      * mvn org.apache.maven.plugins:maven-archetype-plugin:1.0-alpha-7:create -DarchetypeGroupId=ch.elca.el4j.archetypes -DarchetypeArtifactId=EL4JCore -DarchetypeVersion=${project.version} -DgroupId=ch.elca.el4j -DartifactId=myFirstProject -DremoteRepositories=http://el4.elca-services.ch/el4j/maven2repository
      * cd myFirstProject
      * mvn install
      * mvn exec:java

To start working with the EL4J sources
 * open a shell, cd EL4J
 * execute source ./setupPathsAndEnvironment.sh
 * svn co https://el4j.svn.sourceforge.net/svnroot/el4j/trunk/el4j external
 * cd external
 * mvn clean install
 * if you're outside of ELCA, this will fail due to missing database driver
    (we don't have the right to publish it). In this case, do the following (you could also remove the oracle dependency):
     * Download the Oracle driver (ojdbc14_g.jar) from "http://www.oracle.com/technology/software/tech/java/sqlj_jdbc/". Note: You can also use the 10g driver for version 9 databases.
     * Open a console and change to the directory where you have saved the downloaded jar files (but within the EL4J/external directory).
     * Execute the following command:
       o mvn install:install-file -DgroupId=com.oracle -DartifactId=ojdbc14_g -Dversion=10.2.0.3.0 -Dpackaging=jar -Dfile=ojdbc14_g.jar
     * if you are using JDK 6 and want the jaxws support working, set up jaxws 2.1 like described here: 
         http://weblogs.java.net/blog/ramapulavarthi/archive/2007/01/problems_using.html
     * mvn clean install

To start a new GUI application 
 * Check out the GUI demo application under external/applications/templates/gui (assuming you have downloaded the
   source code of el4j). Within ELCA, you can get a pre-packaged zip with all code in it.
 * Set up your EL4J environment (see the examples above)
 * Follow the steps in the file external/applications/templates/gui/README.txt

To start a new JSF web application using JBoss Seam (remark: this application template is only available within ELCA)
 * Check out the web demo application under external/applications/templates/web (assuming you have downloaded the
   source code of el4j)
 * Set up your EL4J environment (see the examples above)
 * Follow the steps in the file external/applications/templates/web/README.txt
 
More information:
 * The local ./docs directory
 * http://el4j.sourceforge.net/
 * http://wiki.elca.ch/twiki/el4j/bin/view/EL4J
