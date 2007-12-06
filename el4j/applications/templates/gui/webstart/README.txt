Webstart Demo Application
=========================

How to start the webstart-demo-application?
-------------------------------------------

The webstart-demo-application is a preconfigured web-application, which consists of one simple index.html, which in turn contains a link to a jnlp-file located in the webstart-subfolder. Furthermore, the webstart-subfolder contains the jar-file of the swing-demo-thin-client and all the jars it depends on. Webstart reads the jnlp-file, downloads all necessary jars from the host and executes the swing-demo-thin-client.
Invoke the following commands to install and deploy the demo-application:

mvn clean install
mvn cargo:undeploy cargo:deploy cargo:start

Now, open your browser and enter the following url: http://localhost:8080/webstart-demo/. Click on the "Launch"-link to download and run the swing-demo-thin-client. If webstart asks you to accept the "EL4J Webstart Demo"-certificate, click "accept/run". 


TWiki: http://wiki/twiki/el4j/bin/view/EL4J/MavenWebstartPlugin
