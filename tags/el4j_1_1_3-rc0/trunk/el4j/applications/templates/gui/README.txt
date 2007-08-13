How to launch the gui demo application (standalone mode):

 cd ..; mvn clean install
 cd standalone-client
 mvn db:prepareDB exec:java


distributed mode:

  start server:

   cd server/war
   mvn db:prepareDB cargo:undeploy cargo:deploy cargo:start 
   # the server should now be available under http://localhost:8080/module-remoting-demos-web 


  start gui (in a separate console): 
 
   cd thin-client
   mvn exec:java
