This Hello World project is a EL4J project template. It contains a own project
root pom file that inherits all from the root pom of the EL4J. By having this
dependency all plugins and modules of the EL4J are implicitly available, so you
can directly begin with your new project. 

The following steps are necessary to start up with a new project:

    * Read in "GettingStarted.txt" (saved in directory "etc")
      how to set up the develop environment. It's recommended to get knowledge
      about the buildsystem maven.

    * Replace over all files with name "pom.xml" and pattern "*.txt" the string 
      "helloworld" and "Hello World" with your project name.

    * Choose an appropriate name of the module example, e.g. myproject-core.
      Rename it in the pom.xml files and the filesystem (change folder names).
      Do the same for the test modules and the demo modules.
   
    * Open a commandline and change to the directory of your new project.
      This should be the directory where this file is saved.
      Execute the command "mvn clean install" to totally clean, build, test and
      install your project in the local repository. If you have already imported
      the artifacts in Eclipse as Eclipse projects you have to refresh these
      Eclipse projects afterwards.
