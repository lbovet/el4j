#!/bin/bash -e

#   * %box% External Demos
#      * %box% Remoting Benchmark
#         * Run the benchmark with =mvn -Pexecute,db2,tomcat6x= an check that all test ran cleanly
#      * %box% Light Statistics
#         * Run the demo as described in the Readme to see if it works as expected
#      * %box% Detailed Statistics
#         * Run the demo as described in the Readme to see if it works as expected

# make sure you are in right folder
if ! [ -e external ] ; then
	echo "Error: Folder 'external' not found. Go to its parent folder (el4j)!"
	exit
fi

echo "Remoting Benchmark"
cd external/applications/demos/remoting
mvn clean install
cd benchmark
mvn -Pauto,execute,db2,tomcat6x

cd ../../remoting_jaxws
mvn clean install
cd benchmark
mvn -Pauto,execute,db2,tomcat6x

echo "Light Statistics"
cd ../../light_statistics
mvn package
echo "Enter 'mvn exec:java' in the window that will appear next. Press Enter to open window..."
read dummy
cygstart bash -i

echo ""
echo "Open http://localhost:9092/ in a web browser"
echo "Browse to 'Performance - key=lightStatisticsReporter'"
echo "-> 'view the values of Data'  to see the measures of the"
echo "performance interceptors via JMX"
echo "(URL: http://localhost:9092/ViewProperty/Data//Performance%3Akey%3DlightStatisticsReporter)"
echo ""
echo "Close opened window and press Enter to continue"
read dummy

cd ../detailed_statistics
echo "Detailed Statistics"

mvn package
echo "Enter 'mvn exec:java' in the window that will appear next. Press Enter to open window..."
read dummy
cygstart bash -i

echo ""
echo "Open http://localhost:9092/ in a web browser"
echo "Browse to 'Performance - key=detailedStatisticsReporter'"
echo "Click on 'showMeasureIdTable' on the bottom to choose a measureId"
echo "Then, either create a CSV or a GIF File (providing filename and measureId)."
echo ""
echo "Close opened window and press Enter to continue"
read dummy

echo "Execute internal demo tests? (y/n)"
read performInternal

if [ $performInternal != "y" ] ; then
	exit
fi

cd ../../../../internal/applications/demos/daemon_manager
echo "Daemon Manager"
mvn install
cd controller
echo "Enter 'mvn exec:java' in the window that will appear next. Press Enter to open window..."
read dummy
cygstart bash -i
cd ..

echo "Press Enter when controller has started..."
read dummy

# get information about the running daemons via the console application (in console 2):
cd console
mvn exec:java -Dexec.args=information

# reconfigure the daemons via the console application (in console 2):
mvn exec:java -Dexec.args=reconfigure

echo "Press Enter to continue"
read dummy

# stop the daemons via the console application (in console 2):
mvn exec:java -Dexec.args=stop

echo "Scheduler"
echo "TODO not yet supported"
