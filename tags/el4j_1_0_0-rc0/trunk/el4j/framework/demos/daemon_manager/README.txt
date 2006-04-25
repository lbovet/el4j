# show all the available targets:
ant -p
# recursively compile all required targets files:
ant jars.rec.module.module-daemon_manager-demos

# start the eu execution unit (eu) with the controller: (in console 1):
ant start.module.eu.module-daemon_manager-demos.controller

# get information abou the running daemons via the console application (in console 2):
ant -Dargs="information" start.module.eu.module-daemon_manager-demos.console

# stop the daemons via the console application (in console 2):
ant -Dargs="information" start.module.eu.module-daemon_manager-demos.console

