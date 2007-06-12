For usage instruction, visit the corresponding wiki page at 
http://wiki.elca.ch/twiki/el4j/bin/view/EL4J/JbossSeam

The template is referenced there as "Maven template"

Here's a short description of how to use it:
    * in this directory, execute    mvn clean install -DinitDB=true
    * cd war 
    * Run   mvn db:start jetty:run   to start the Derby network server and run jetty
    * You can access the application now at http://localhost:8080/

