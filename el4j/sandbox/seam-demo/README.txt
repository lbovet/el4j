------------------------
 Seam Demo Application
------------------------

Purpose:
 - Test integration of Seam with EL4J/Spring and the Maven build system
 - Test Seam's POJO functionality; run without EJB Container on Plain Tomcat
 - Provide the foundations for generic master/detail views
 - Demonstrate usage of JBoss Seam, Richfaces and JSF / Facelets in general.

Authors:
 CBA (Christoph Baeni)
 FBI (Frank Bitzer)
 SWI (Stefan Wismer)
 
Requirements: 
 - Java 1.5.0_14 or newer

Build Instructions:
 - First compile it, using mvn install.
 - To execute it, change to the war directory an run the following command:
      mvn jetty:run
   or if you want to use tomcat
      mvn db:prepare cargo:undeploy cargo:deploy cargo:start
 - After the application has started you can access it at
      http://localhost:8080/seam-demo/

Minor Problems:

- There is a non-fatal error that shows up each request:
  "ERROR [org.jboss.seam.contexts.Contexts] could not discover transaction status".
  This is most likely a seam bug, because <core:init transaction-management-enabled="false"/> has
  been set in components.xml which is supposed turn seam transaction management off althogether.


Missing Features / TODO:

- A sorting feature is missing completely. Adding this would include two tasks:
 - Add support for sorting in ObjectManager.getEntities().
 - Find and use a JSF Table component that supports "sortable column headers" instead of
   the RichFaces rich:dataTable.

- A generic searching feature is missing completely. Adding this would include several tasks:
 - Include a fulltext search engine like Apache Lucene.
 - Add searching support to ObjectManager.
 - Add a "searchBox" Facelets tag to the el4j facelet tag library.
 - Enhance the master view to include this searchBox on demand (or automatically?).
 - Most likely a lot of glue code, especially if this is supposed to work in combination w/ sorting & filtering.

- Suboptimal Navigation
 - Navigation is currently rather poor in this version of the Seam Demo.
   Example: Clicking on "Done" in the Employee detail view always returns
   to the employee master view, no matter where we came from (maybe Offer
   detail view). Furthermore pagination and filter states are not remembered.

