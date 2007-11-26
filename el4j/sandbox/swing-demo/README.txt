How to run the demo:

Ensure that all essential el4j modules are either in your local or on the elca-services repository. A "mvn install" in
your "external" directory ensure the former.

Execute:

cd ../swing
mvn clean install

cd ../swing-demo
mvn clean install
mvn exec:java


To use the refDB demos execute:

start server:
    cd ../../applications/templates/common/refdb/tests
    mvn db:prepare db:start

start GUI:
    mvn exec:java