/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU General Public License (GPL) Version 2.0.
 * http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.tests.services.persistence.generic.dao;

import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ch.elca.el4j.services.persistence.generic.dao.DaoRegistry;
import ch.elca.el4j.services.persistence.generic.dao.GenericDao;

public class DaoAutocollectionTests extends TestCase {

    public void testDaoAutocollection() {
        ApplicationContext ac = 
            new ClassPathXmlApplicationContext(new String[] {"scenarios/core/dao/springConfig.xml"});
        
        //System.out.println("beans: "+StringUtils.arrayToCommaDelimitedString(ac.getBeanDefinitionNames()));
        
        DaoRegistry registry = (DaoRegistry)ac.getBean("registry");
        
        GenericDao<?> dao = registry.getFor(String.class);
        //System.out.println("registry: "+DataDumper.dump(((DefaultDaoRegistry)registry).getDaos()));
        
        dao = registry.getFor(String.class);
        assertTrue(dao != null);
        assertTrue(dao instanceof Dao1);
        
        dao = registry.getFor(Long.class);
        assertTrue(dao != null);
        assertTrue(dao instanceof Dao2);        
        
        assertTrue(ac.getBeanNamesForType(GenericDao.class).length == 2);
    }
    
}
