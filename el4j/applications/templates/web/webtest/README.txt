--------------------------
 Canoo webtest integration
--------------------------

Purpose:
 - Show the Canoo webtest integration


Authors:
 FKE (Florian Keusch)
 

How to use and install:
  read in the wiki:
  http://wiki.elca.ch/twiki/el4j/bin/view/EL4J/CanooWebtestIntegration


Usage:
  After the correct setup you can execute in this folder:
	mvn clean install

  This will setup the database, start the tomcat container and run the webtests.

  When the application is already running you can use:
	webtest.sh

  to execute the Canoo webtest directly in this folder.