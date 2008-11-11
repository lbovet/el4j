Abbot tests for swing-demo-standalone-client (MDI).

Note: mvn clean install does skip the tests!
Start the tests by executing mvn clean install -Pmanualtest in this folder.

Run
  mvn exec:java -Pmanualtest;costello
or if you need a running database
  mvn db:prepare exec:java -Pmanualtest;costello
to edit the tests (xml files in src/test/abbot)
