# compile & install all required targets files:
mvn install

# start the controller: (in console 1):
cd controller
mvn exec:java

# get information about the running daemons via the console application (in console 2):
cd console
mvn exec:java -Pinformation

# reconfigure the daemons via the console application (in console 2):
mvn exec:java -Preconfigure

# stop the daemons via the console application (in console 2):
mvn exec:java -Pstop
