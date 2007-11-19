------------------------
 Seam Demo Application
------------------------

Purpose:
 - Test integration of Seam with EL4J/Spring and the Maven build system
 - Test Seam's POJO functionality; run without EJB Container on Plain Tomcat
 - Provide the foundations for generic master/detail views
 - Demonstrate usage of JBoss Seam, Richfaces and JSF / Facelets in general.

Author:
 CBA (Christoph Baeni)

Requirements: 
 - Maven 2.1-SNAPSHOT
 - Tomcat 5.5

Used Libraries:
 - JBoss Seam 2.0.0.CR1
 - JBoss RichFaces 3.1.1-SNAPSHOT (includes Ajax4JSF now)
 - Sun JSF reference implementation 1.2_04-b16-p02
 - Sun Facelets implementation 1.1.13

Build Instructions:
 - First compile it, using mvn install.
 - To execute it, change to the war directory an run the following command:
      mvn db:prepareDB cargo:undeploy cargo:deploy cargo:start
 - After the application has started you can access it at
      http://localhost:8080/seam-demo-war/

Severe Problems:

- BUG: Paginating Offer master view throws LazyInitializationException. This bug is triggered by
  any (Multi)EntityColumn in a master view. The LIE is thrown because the entities live within
  a long running conversation and are detached from the hibernate session after the first request
  is processed. The entities should either be reassociated with the new session in each request
  or the session should somehow be associated with the seam conversation rather than with the
  Spring OpenSessionInViewFilter. This is in fact an integration issue that was only introduced
  with the abandonment of seam managed persistence in favor of EL4J/Spring persistence. 

- Master view always loads _all_(!!) entities of a given type from the db, not just those
   being displayed on the current page. This renders the whole master view very unscalable.
 - Reason /problem:
  - The heavyily ajaxed rich:dataTable and rich:datascroller do not support a reasonable DataModel
    but instead require a list of all entries from the first request on. Only a better JSF table
    component could enable us to solve this issue. (For example Trinidad's DataTable).

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

Misc:

- This example does not really use Seam conversations a lot, because they are just not
   needed for such a simple data driven CRUD master/detail view sample application. Conversations
   are rather designed for interactions involving multiple steps like wizzards, shop checkouts,
   user registrations etc. Nevertheless you have to take care of conversations!!! If you
   forget to terminate a long running conversation in the right place, you may struggle with
   "stale" data popping up out of nowhere in forms that were supposed to be empty as well as
   many other nasty effects.

