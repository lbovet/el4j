
# package the demo
mvn package

# start the default class of this module (as defined in the pom.xml)
mvn exec:java

# Open http://localhost:9092/ in a web browser

# Browse to "Performance - key=lightStatisticsReporter" 
-> "view the values of Data"  to see the measures of the 
performance interceptors via JMX (URL: http://localhost:9092/ViewProperty/Data//Performance%3Akey%3DlightStatisticsReporter)