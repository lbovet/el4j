Directory to hold multi-environment support. Setup properties in function of the
active environment. Example environments: tomcat and rmi.
These properties can be used by the build system and by your project. The usage
is the same like the ant properties (notation: ${my.variable}).

File "env.properties" is the entry point. It refers to other property files. A
module must have a dependency to "module-env" to be able to use the properties.
