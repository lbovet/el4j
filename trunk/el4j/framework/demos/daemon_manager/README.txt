# compile & install all required target files:
mvn install

# start the controller (in console 1):
cd controller
mvn exec:java

# get information about the running daemons via the console application (in console 2):
cd console
mvn exec:java -Dexec.args=information

# reconfigure the daemons via the console application (in console 2):
mvn exec:java -Dexec.args=reconfigure

# stop the daemons via the console application (in console 2):
mvn exec:java -Dexec.args=stop
