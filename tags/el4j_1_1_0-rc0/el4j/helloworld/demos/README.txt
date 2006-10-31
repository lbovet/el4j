---+ Abstract
A minimalistic calculator that currently only supports division do show the
usage of maven together with the maven exec plugin.

---+ Usage
   * cd EL4J/external/helloworld
   * mvn install (to compile and install everything needed)
   * cd demos/example/
   * mvn exec:java (to execute ch.elca.helloworld.demos.MainCalculator without 
     arguments, see the pom.xml for details)
   * mvn exec:java -Dexec.args="div 1.9 9.5" (to execute with the 3 arguments)
      * div (for division)
      * 1.9 and 9.5 (for 1.9/9.5)
      * As a result something correct is expected