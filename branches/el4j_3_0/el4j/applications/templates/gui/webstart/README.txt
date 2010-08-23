Webstart Demo Application
=========================

How to start the swing-demo-webstart?
-------------------------------------

The swing-demo-webstart is a preconfigured web-application, which consists of
one simple index.html, which in turn contains a link to a jnlp-file located in 
the webstart-subfolder. Furthermore, the webstart-subfolder contains the 
jar-file of the swing-demo-thin-client and all the jars it depends on. 
Webstart reads the jnlp-file, downloads all necessary jars from the host 
and executes the swing-demo-thin-client.

Invoke the following commands to install and deploy the demo-application.
Note that you need a database up and running for the webstart to work
(you may start a database, e.g. by executing
mvn db:prepare db:block
in the folder of standalone-client).

mvn clean install
mvn cargo:undeploy cargo:deploy cargo:start

You can safely ignore the error message (ClassNotFoundException) when
executing the command above.

Now, open your browser and enter the following url:
http://localhost:8080/swing-demo/

Click on the "Launch"-link to download and run the demo client (standalone version).
If webstart asks you to accept the "EL4J Webstart Demo"-certificate, click 
"accept/run". 

For more information see the following twiki page (elca internal only):
http://wiki/twiki/el4j/bin/view/EL4J/MavenWebstartPlugin
