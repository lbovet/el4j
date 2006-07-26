To be able to use XmlMerge as a command-line tool, you have to execute the 
following steps:

- Go to <EL4J_HOME>/external/framework

- Create an executable distribution of the xml_merge module:
  ant create.distribution.module.module-xml_merge
  
- The executable distribution can be found in the module-xml_merge-default 
  folder under <EL4J_HOME>/external/framework/dist/distribution. You can copy 
  this folder to any location you want.
  
- To be able to execute the command-line tool from your desired location, you
  have to add the location containing the executable distribution your PATH
  environment variable:
  
  * Windows: add <YOUR_LOCATION>\module-xml_merge-default to the right end of
    your PATH environment variable, where <YOUR_LOCATION> denotes the folder
    into which you have copied the module-xml_merge-default folder.
  
  * Unix: launch the following command to set the PATH environment variable,
    where <YOUR_LOCATION> denotes the folder into which you have copied the
    module-xml_merge-default folder:
    export PATH=$PATH:"<YOUR_LOCATION>/module-xml_merge-default" 
  
- You can now launch the command-line tool from any location by launching the
  xmlmerge script:
  xmlmerge [-config <config-file>] file1 file2 [file3 ...]
  
  In this command, config-file denotes an optional configuration file and
  file1, file2, file3 etc are the xml files to merge.