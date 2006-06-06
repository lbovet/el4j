
# compile and generate all needed jars recursively
ant jars.rec.module

# start the default class of this module (as defined in the module.xml)
ant start.module.eu

# Open http://localhost:9092/ in a web browser

# Browse to "key=lightStatisticsReporter" -> "Data"  to see the measures of the performance interceptors via JMX
 (URL: http://localhost:9092/ViewProperty/Data//Performance%3Akey%3DlightStatisticsReporter )