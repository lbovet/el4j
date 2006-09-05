/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.tests.keyword.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.OptimisticLockingFailureException;

import ch.elca.el4j.apps.keyword.dao.KeywordDao;
import ch.elca.el4j.apps.keyword.dao.impl.hibernate.HibernateKeywordDaoRegistry;
import ch.elca.el4j.apps.keyword.dto.KeywordDto;
import ch.elca.el4j.services.search.QueryObject;
import ch.elca.el4j.services.search.criterias.LikeCriteria;
import ch.elca.el4j.tests.keyword.AbstractTestCaseBase;


// Checkstyle: MagicNumber off

/**
 * Abstract test case for <code>KeywordDao</code>.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Alex Mathey (AMA)
 */
public abstract class AbstractKeywordDaoTest
    extends AbstractTestCaseBase {
    
    /**
     * Private logger.
     */
    private static Log s_logger 
        = LogFactory.getLog(AbstractKeywordDaoTest.class);
    
    /**
     * Keyword DAO. Created by application context.
     */
    private KeywordDao m_keywordDao;

    /**
     * Hide default constructor.
     */
    protected AbstractKeywordDaoTest() { }
    
    /**
     * @return Returns the keyword DAO.
     */
    protected KeywordDao getKeywordDao() {
        if (m_keywordDao == null) {
            HibernateKeywordDaoRegistry daoFactory 
                = (HibernateKeywordDaoRegistry) getApplicationContext()
                    .getBean("daoRegistry");
            m_keywordDao = daoFactory.getForKeyword();
        }
        return m_keywordDao;
    }

    /**
     * This test inserts different keywords.
     */
    public void testInsertKeywords() {
        KeywordDao dao = getKeywordDao();
        KeywordDto keyword = new KeywordDto();
        keyword.setName("Java");
        keyword.setDescription("Java related documentation");
        keyword = dao.saveOrUpdate(keyword);

        KeywordDto keyword2 = new KeywordDto();
        keyword2.setName("This is a too large keyword");
        keyword2.setDescription("I'm too long, I'm too long"
                + "I'm too long, I'm too long" + "I'm too long, I'm too long"
                + "I'm too long, I'm too long" + "I'm too long, I'm too long"
                + "I'm too long, I'm too long" + "I'm too long, I'm too long"
                + "I'm too long, I'm too long" + "I'm too long, I'm too long"
                + "I'm too long, I'm too long");
        try {
            keyword2 = dao.saveOrUpdate(keyword2);
            fail("A keyword with more than max length could be saved.");
        } catch (Exception e) {
            s_logger.debug("Expected exception catched.", e);
        }
        
        KeywordDto keyword3 = new KeywordDto();
        keyword3.setName("Java");
        keyword3.setDescription("Once again the same name");
        try {
            keyword3 = dao.saveOrUpdate(keyword3);
            fail("Keyword names need not to be unique.");
        } catch (DataIntegrityViolationException e) {
            s_logger.debug("Expected exception catched.", e);
        }
        
        KeywordDto keyword4 = new KeywordDto();
        keyword4.setName("KeywordWithoutDescription");
        keyword4.setDescription(null);
        dao.saveOrUpdate(keyword4);
        
        KeywordDto keyword5 = new KeywordDto();
        keyword5.setName(null);
        keyword5.setDescription(null);
        try {
            dao.saveOrUpdate(keyword5);
            fail("Possible to insert a keyword without a name and a "
                + "description.");
        } catch (DataIntegrityViolationException e) {
            s_logger.debug("Expected exception catched.", e);
        }
        
        KeywordDto keyword6 = new KeywordDto();
        keyword6.setName(null);
        keyword6.setDescription("A description");
        
        try {
            dao.saveOrUpdate(keyword6);
            fail("Possible to insert a keyword without a name.");
        } catch (DataIntegrityViolationException e) {
            s_logger.debug("Expected exception catched.", e);
        }
    }
    
    /**
     * This test inserts a keyword and looks up for it by keyword's primary key.
     */
    public void testGetKeywordById() {
        KeywordDao dao = getKeywordDao();
        KeywordDto keyword = new KeywordDto();
        keyword.setName("Xml");
        keyword.setDescription("Xml related documentation");
        KeywordDto keyword2 = dao.saveOrUpdate(keyword);
        KeywordDto keyword3 = dao.findById(keyword2.getKey(), false);
        assertEquals("The inserted and read Dtos are not equal", keyword2,
            keyword3);
    }
    
    /**
     * This test inserts a keyword and looks up for it by its name.
     */
    public void testGetKeywordByName() {
        KeywordDao dao = getKeywordDao();
        KeywordDto keyword = new KeywordDto();
        keyword.setName("Xml");
        keyword.setDescription("Xml related documentation");
        KeywordDto keyword2 = dao.saveOrUpdate(keyword);
        KeywordDto keyword3 = dao.getKeywordByName(keyword2.getName());
        assertEquals("The inserted and read Dtos are not equal", keyword3,
            keyword2);        
    }
    
    /**
     * This test inserts two keywords and looks up for all the keywords in the
     * database. We test the number of keywords, which should be two, and if
     * they really are the keywords which have been added.
     */
    public void testFindAllKeywords() {
        KeywordDao dao = getKeywordDao();
        KeywordDto keyword1 = new KeywordDto();
        keyword1.setName("Xml");
        keyword1.setDescription("Xml related documentation");
        
        KeywordDto keyword2 = new KeywordDto();
        keyword2.setName("Java");
        keyword2.setDescription("Java related documentation");
        
        dao.saveOrUpdate(keyword1);
        dao.saveOrUpdate(keyword2);
        
        List<KeywordDto> list = dao.findAll();
        
        assertEquals("Wrong number of keywords in DB", 2, list.size());
        assertTrue("First keyword has not been found", 
            list.contains(keyword1));
        assertTrue("Second keyword has not been found", 
            list.contains(keyword2));
    }
    
    /**
     * This test inserts a keyword and removes it. Afterwards that, the keyword
     * should not be reachable.
     */
    public void testDeleteKeyword() {
        KeywordDao dao = getKeywordDao();
        KeywordDto keyword = new KeywordDto();
        keyword.setName("Java");
        keyword.setDescription("Java related documentation");
        KeywordDto keyword2 = dao.saveOrUpdate(keyword);
        dao.delete(keyword2);
        try {
            dao.findById(keyword2.getKey(), false);
            fail("The removed keyword is still in the DB.");
        } catch (DataRetrievalFailureException e) {
            s_logger.debug("Expected exception catched.", e);
        }
    }
    
    /**
     * This test inserts one keyword, which will afterwards be looked up by two
     * people. Now, these people edit the same keyword and would like to save
     * changes. The person that saves second must get a
     * <code>OptimisticLockingFailureException</code>. The same should
     * happen if she attempts to delete the keyword.
     */
    public void testInsertModifyDeleteKeywordByTwoPeople() {
        KeywordDao dao = getKeywordDao();
        KeywordDto keyword = new KeywordDto();
        keyword.setName("Java");
        keyword.setDescription("Java related documentation");
        dao.saveOrUpdate(keyword);
        KeywordDto keyword2 = dao.getKeywordByName("Java");
        KeywordDto keyword3 = dao.getKeywordByName("Java");
        keyword2.setDescription("Java API");
        dao.saveOrUpdate(keyword2);
        keyword3.setDescription("Java API of version 1.5");
        try {
            dao.saveOrUpdate(keyword3);
            fail("The current keyword could be modified "
                    + "by two persons at the same time.");
        } catch (OptimisticLockingFailureException e) {
            s_logger.debug("caught expected exception.", e);
        }
        try {
            dao.delete(keyword3);
            fail("A keyword could be deleted although it has been modified "
                + "concurrently.");
        } catch (OptimisticLockingFailureException e) {
            s_logger.debug("caught expected exception.", e);
        }
    }
    
    /**
     * This test inserts five keywords and performs different searchs on it.
     */
    public void testSearchKeywords() {
        KeywordDao dao = getKeywordDao();
        KeywordDto keyword = new KeywordDto();
        keyword.setName("Java");
        keyword.setDescription("Java related documentation");
        KeywordDto keyword2 = new KeywordDto();
        keyword2.setName("XML");
        keyword2.setDescription("Xml related documentation");
        KeywordDto keyword3 = new KeywordDto();
        keyword3.setName("Ghost");
        keyword3.setDescription("");
        KeywordDto keyword4 = new KeywordDto();
        keyword4.setName("Chainsaw");
        keyword4.setDescription("Tool of Log4J to filter logfiles");
        KeywordDto keyword5 = new KeywordDto();
        keyword5.setName("Zombie");
        keyword5.setDescription("");
        
        keyword = dao.saveOrUpdate(keyword);
        keyword2 = dao.saveOrUpdate(keyword2);
        keyword3 = dao.saveOrUpdate(keyword3);
        keyword4 = dao.saveOrUpdate(keyword4);
        keyword5 = dao.saveOrUpdate(keyword5);
        
        QueryObject query = new QueryObject();
        query.addCriteria(LikeCriteria.caseInsensitive("description", "%doc%"));
        
        List<KeywordDto> list = dao.findByQuery(query);
        assertEquals(
            "Search for description like 'doc' does not result in two"
            + " keywords.", 2, list.size());
        
        for (KeywordDto k : list) {
            if (!(k.equals(keyword) || k.equals(keyword2))) {
                fail("Not expected keyword on search for description "
                    + "like 'doc'.");
            }
        }
        
        query = new QueryObject();
        query.addCriteria(LikeCriteria.caseInsensitive("name", "%host%"));
        list = dao.findByQuery(query);
        assertEquals("Search for name like 'host' results not in one keyword.",
            1, list.size());
        
        for (KeywordDto k : list) {
            if (!k.equals(keyword3)) {
                fail("Not expected keyword on search for name like 'host'.");
            }
        }
        
        list = dao.findByQuery(new QueryObject());
        assertEquals("Search for empty name and description does not result in "
            + "five keywords.", 5, list.size());
        
        query = new QueryObject();
        query.addCriteria(LikeCriteria.caseInsensitive(
            "description", "%log4j%"));
        list = dao.findByQuery(query);
        assertEquals(
            "Search for description like 'log4j' results not in one keyword.",
            1, list.size());
        
        for (KeywordDto k : list) {
            if (!k.equals(keyword4)) {
                fail("Not expected keyword on search for description "
                    + "like 'log4j'.");
            }
        }
    }
    
    /**
     * This test inserts two keywords and executes a query based on an example
     * keyword on them.
     */
    public void testFindByExample() {
        KeywordDao dao = getKeywordDao();
        KeywordDto keyword = new KeywordDto();
        keyword.setName("Java");
        keyword.setDescription("Java related documentation");
        keyword = dao.saveOrUpdate(keyword);
        
        KeywordDto keyword2 = new KeywordDto();
        keyword2.setName("C");
        keyword2.setDescription("C related documentation");
        keyword2 = dao.saveOrUpdate(keyword2);
        
        List<KeywordDto> list = dao.findByExample(keyword);
        assertEquals(
            "Query by example with does not result in one keyword.",
            1, list.size());
        
        for (KeywordDto k : list) {
            if (!k.equals(keyword)) {
                fail("Not expected keyword on query by example.");
            }
        }
        
    }
  
}
//Checkstyle: MagicNumber on
