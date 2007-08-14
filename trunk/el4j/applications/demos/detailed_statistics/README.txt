Description:
This is a demo for the use of the detailed statistics modules of EL4J.

Detailed statistics means that for every method duration, caller, callee and 
other information is stored. 
This is done with help of a IntelligentExclusiveBeanNameAutoProxyCreator, which
adds an interceptor to ALL (!) objects, except of 1) proxies and 2) the ones 
used by the detailed statistics service itself.

This information is processed and made accessible through a reporter which JMX
interface (currently on http://localhost:9092/).


Use:
# package the demo
mvn package

# start the default class of this module (as defined in the pom.xml)
mvn exec:java

# Open http://localhost:9092/ in a web browser

# Browse to "Performance - key=detailedStatisticsReporter" 
  Click on "showMeasureIdTable" on the bottom to choose a measureId
  Then, either create a CSV or a GIF File (providing filename and measureId).