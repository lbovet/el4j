Description:
This is a demo for the use of the detailed statistics modules of EL4J.

Detailed statistics means that for every method duration, caller, callee and 
other information is stored. 
This is done with help of a IntelligentExclusiveBeanNameAutoProxyCreator, which
adds an interceptor to ALL (!) objects, except of 1) proxies and 2) the ones 
used by the detailed statistics service itself.
This demo runs in two JVMs, which communicate through remote message invocation
(RMI).

This information is processed and made accessible through a reporter which JMX
interface (currently on http://localhost:9092/).


Use:
# Compile & install all required target files:
mvn install

# Start the service (in console 1):
cd service
mvn exec:java

# Start the client and invoke a RMI (in console 2):
cd client
mvn exec:java

# Open http://localhost:9092/ in a web browser

# Browse to "Performance - key=detailedStatisticsReporter" 
  Click on "showMeasureIdTable" on the bottom to choose and copy a measureId
  Then, either create a CSV or a GIF File (providing filename and pasting measureId).
  Generated CSV or GIF can then be found in the service folder.

# Stop the service (in console 1):
Ctrl+C