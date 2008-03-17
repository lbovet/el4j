About the jBPM tryout
=====================

This is an example application using JBoss jBPM to manage both pageflow and processflow of a web application.

It is based on the El4J JSF web application template (however, on an old version from february 2008) and integrates the jBPM engine into Seam and the El4J/Spring environment. More information on the Seam-Spring-jBPM integration can be found at http://www.seamframework.org/Documentation/SeamSpringAndJBPMIntegrationHowTo .

The topic of this application is a holiday request, which passes some phases represented by states in a process. 
At first, the request is submited by a user, who provides some basic information. 
Next, an admin can pick up the request and evaluate it. He is able to enter a decision or to request more details. In latter case, it's the user's turn again to provide more details on the request.

The process definition can be found in war\src\main\webapp\WEB-INF\classes\processimage.jpg .

There are two pageflows, which each define a conversation. To view them, open the images available at war\src\main\webapp\WEB-INF\classes\pageflow.createRequest.jpdl.jpg and war\src\main\webapp\WEB-INF\classes\pageflow.jpdl.jpg .


Used technologies
=================

* EL4J JSF web application template, Seam 2.1.0
* JBoss jBPM jpdl 3.2.2


Author
======

* Frank Bitzer (FBI)


Installation
============

* mvn clean install

* To execute it, change to /war and run the following command:
    
      mvn jetty:run
      
  After the application has started you can access it at
      
      http://localhost:8080/jBPM-tryout-war
  
  Click on the "jBPM" item at the right of the menu bar to browse to the holiday request demo.
   

Known issues/limitations
========================

* currently, the whole HolidayRequest domain object is bound to BUSINESS_PROCESS scope. This is not the best way in practice. Instead, you could store the domain objects as entity beans and just bind the ID of the bean to the process instance. This example merely shows you that storing serializable objects in the process instance is possible.

* there is no validation implemented yet (i.e. to ensure proper dates)
