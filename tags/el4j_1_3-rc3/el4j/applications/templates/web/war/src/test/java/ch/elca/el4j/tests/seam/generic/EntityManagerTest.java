/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU Lesser General Public License (LGPL)
 * Version 2.1. See http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.tests.seam.generic;

import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;
import org.testng.annotations.Test;

import ch.elca.el4j.seam.demo.entities.Client;
import ch.elca.el4j.seam.generic.EntityManager;
import ch.elca.el4j.tests.seam.AbstractEntityManagerTest;



// Checkstyle: MagicNumber off

/**
 * 
 * TestNG testclass for EntityManager. Contains one exemplarily testcase for a
 * Seam integration test.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Frank Bitzer (FBI)
 */

public class EntityManagerTest
    extends AbstractEntityManagerTest {
    
    
    //values for testClient object
    static final String ENTERPRISE = "Cool Test Enterprise";
    static final String ADRESS = "Rue du test";
    static final String ACTIVITY = "Testing bad software";
    
    static Client s_testClient;
    
    /**
     * number of entities in db before test.
     */
    static int s_entityCount;
    
    
    
    /**
     * This testcase serves as an example for a Seam integration test.
     * It simulates three requests.
     * At first, a Client object is created and stored in the database. 
     * Next, the insertion is checked and the object is deleted again.
     * The third request checks whether the deletion was successfull.
     * 
     * @throws Exception
     */
    @Test
    public void testPersistEntity() throws Exception {

        assert !super.isSessionInvalid();

        //first request
        new FacesRequest() {

            @Override
            protected void updateModelValues() throws Exception {
                //store entityClassName in Session context
                //for further use
                Contexts.getSessionContext().set("entityClassName",
                    "ch.elca.el4j.seam.demo.entities.Client");

                //make sure that beans are successfully loaded
                assert (getValue("#daoRegistry") != null);
            }

            @Override
            protected void invokeApplication() throws Exception {
                //create object to be stored
                s_testClient = new Client();

                s_testClient.setEnterprise(ENTERPRISE);
                s_testClient.setAddress(ADRESS);
                s_testClient.setActivity(ACTIVITY);

                //get object reference of seam-managed entityManager 
                //component
                EntityManager em = (EntityManager) Component
                    .getInstance("entityManager");

                s_entityCount = em.getEntityCount();

                //save client and end conversation 
                em.saveOrUpdateAndRedirect(s_testClient, null);

            }

        }.run();

        //second request
        new FacesRequest() {

            @Override
            protected void invokeApplication() throws Exception {

                EntityManager em = (EntityManager) Component
                    .getInstance("entityManager");

                int size = em.getEntityCount();
                //the currently saved client should be there 
                assert (size == s_entityCount + 1);

                //...and delete it again
                em.deleteAndRedirect(s_testClient, null);

            }

        }.run();

        //third request
        new FacesRequest() {

            @Override
            protected void invokeApplication() throws Exception {
                EntityManager em = (EntityManager) Component
                    .getInstance("entityManager");

                int size = em.getEntityCount();
                //table has old size again, so deletion was successfull
                assert (size == s_entityCount);

            }

        }.run();

    }

}
//Checkstyle: MagicNumber on
