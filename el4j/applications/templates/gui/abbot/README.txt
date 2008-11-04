Abbot tests for swing-demo-standalone-client (MDI).

Start the tests by executing mvn clean install in this folder.

Run
  mvn exec:java -Pcostello
or if you need a running database
  mvn db:prepare exec:java -Pcostello
to edit the tests (xml files in src/test/abbot)
