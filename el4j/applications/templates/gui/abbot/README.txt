Abbot tests for swing-demo-standalone-client (MDI).

You can launch the tests in this folder with "mvn test". Optionally,
pass "-Dabbot.runTests=" a comma-spearated list of xml file names from the
src/test/resources/abbot directories to select which tests to run. Setting this
to "all" (default) runs all tests, "none" runs none - but abbot considers 
no tests at all to run an error.

Each test focuses on one of the demos and displays an informative message when the
test is complete, stating what it has done.

Eclipse, for reasons as yet unknown, produces errors when trying to launch these 
tests. They are all expected to work under plain maven.
 