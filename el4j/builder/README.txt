This directory contains all stuff to execute tasks like testing, creating
website, upload website automatically. The scripts for it can be found in 
directory "scripts". For example to execute tasks for project "helloworld" just
adapt environment variables in script "build_helloworld.sh" and execute it.
You don't have to change the "build_project.sh" script.

The directory "env" contains the environment property files. Each project has a 
directory with its report name where inside are files with name
"env-mytest.properties" where "mytest" is the name of the test. For each test
environment the corresponding file will be copied to "env/env.properties" of the
project to test.

The directory "etc" contains currently only the "mail.properties" file where
you have to adapt the email to report to.

Log files will be written into directory "logs".

Directory "mailer" contains the ant build file to check the state of executed
JUnit tests and a directory "ant" that contains a version of Ant which has
the Ant-Contrib-Library (see http://ant-contrib.sourceforge.net/) in classpath.
