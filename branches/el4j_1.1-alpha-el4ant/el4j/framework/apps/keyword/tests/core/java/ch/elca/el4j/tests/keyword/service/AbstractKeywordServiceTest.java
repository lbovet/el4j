
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
package ch.elca.el4j.tests.keyword.service;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.OptimisticLockingFailureException;

import ch.elca.el4j.apps.keyword.dto.KeywordDto;
import ch.elca.el4j.apps.keyword.service.KeywordService;
import ch.elca.el4j.services.search.QueryObject;
import ch.elca.el4j.services.search.criterias.LikeCriteria;
import ch.elca.el4j.tests.keyword.AbstractTestCaseBase;

// Checkstyle: MagicNumber off

/**
 * Abstract test case for <code>KeywordService</code>.
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
public abstract class AbstractKeywordServiceTest extends AbstractTestCaseBase {
    /**
     * Private logger.
     */
    private static Log s_logger 
        = LogFactory.getLog(AbstractKeywordServiceTest.class);

    /**
     * Keyword service. Created by application context.
     */
    private KeywordService m_keywordService;

    /**
     * Hide default constructor.
     */
    protected AbstractKeywordServiceTest() { }
    
    /**
     * @return Returns the keywordService.
     */
    protected KeywordService getKeywordService() {
        if (m_keywordService == null) {
            m_keywordService 
                = (KeywordService) getApplicationContext().getBean("keywordService");
        }
        return m_keywordService;
    }

    /**
     * This test inserts different keywords.
     */
    public void testInsertKeywords() {
        KeywordService service = getKeywordService();
        KeywordDto keyword = new KeywordDto();
        keyword.setName("Java");
        keyword.setDescription("Java related documentation");
        keyword = service.saveKeyword(keyword);

        KeywordDto keyword2 = new KeywordDto();
        keyword2.setName("This is a too large keyword");
        keyword2.setDescription("I'm too long, I'm too long"
                + "I'm too long, I'm too long" + "I'm too long, I'm too long"
                + "I'm too long, I'm too long" + "I'm too long, I'm too long"
                + "I'm too long, I'm too long" + "I'm too long, I'm too long"
                + "I'm too long, I'm too long" + "I'm too long, I'm too long"
                + "I'm too long, I'm too long");
        try {
            keyword2 = service.saveKeyword(keyword2);
            fail("A keyword with more than max length could be saved.");
        } catch (Exception e) {
            s_logger.debug("Expected exception catched.", e);
        }

        KeywordDto keyword3 = new KeywordDto();
        keyword3.setName("Java");
        keyword3.setDescription("Once again the same name");
        try {
            keyword3 = service.saveKeyword(keyword3);
            fail("Keyword names need not to be unique.");
        } catch (DataIntegrityViolationException e) {
            s_logger.debug("Expected exception catched.", e);
        }
        
        KeywordDto keyword4 = new KeywordDto();
        keyword4.setName("KeywordWithoutDescription");
        keyword4.setDescription(null);
        service.saveKeyword(keyword4);

        KeywordDto keyword5 = new KeywordDto();
        keyword5.setName(null);
        keyword5.setDescription(null);
        try {
            service.saveKeyword(keyword5);
            fail("Possible to insert a keyword without a name and a "
                + "description.");
        } catch (DataIntegrityViolationException e) {
            s_logger.debug("Expected exception catched.", e);
        }

        KeywordDto keyword6 = new KeywordDto();
        keyword6.setName(null);
        keyword6.setDescription("A description");
        try {
            service.saveKeyword(keyword6);
            fail("Possible to insert a keyword without a name.");
        } catch (DataIntegrityViolationException e) {
            s_logger.debug("Expected exception catched.", e);
        }
    }

    /**
     * This test inserts a keyword and looks up for it by keyword's primary key.
     */
    public void testInsertGetKeywordByKey() {
        KeywordService service = getKeywordService();
        KeywordDto keyword = new KeywordDto();
        keyword.setName("Xml");
        keyword.setDescription("Xml related documentation");
        KeywordDto keyword2 = service.saveKeyword(keyword);
        KeywordDto keyword3 = service.getKeywordByKey(keyword2.getKey());
        assertEquals("The inserted and read Dtos are not equal", keyword3,
                keyword2);
    }

    /**
     * This test inserts a keyword and looks up for it by keyword's name.
     */
    public void testInsertGetKeywordByName() {
        KeywordService service = getKeywordService();
        KeywordDto keyword = new KeywordDto();
        keyword.setName("Xml");
        keyword.setDescription("Xml related documentation");
        KeywordDto keyword2 = service.saveKeyword(keyword);
        KeywordDto keyword3 = service.getKeywordByName(keyword2.getName());
        assertEquals("The inserted and read Dtos are not equal", keyword3,
                keyword2);
    }

    /**
     * This test inserts two keywords and looks up for all. Tested will be the
     * number of keywords, should be two, and if they really are these which it
     * has added.
     */
    public void testInsertGetAllKeywords() {
        KeywordService service = getKeywordService();
        KeywordDto keyword1 = new KeywordDto();
        keyword1.setName("Xml");
        keyword1.setDescription("Xml related documentation");
        KeywordDto keyword2 = new KeywordDto();
        keyword2.setName("Java");
        keyword2.setDescription("Java related documentation");
        service.saveKeyword(keyword1);
        service.saveKeyword(keyword2);
        List list = service.getAllKeywords();
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
    public void testInsertRemoveKeyword() {
        KeywordService service = getKeywordService();
        KeywordDto keyword = new KeywordDto();
        keyword.setName("Java");
        keyword.setDescription("Java related documentation");
        KeywordDto keyword2 = service.saveKeyword(keyword);
        service.removeKeyword(keyword2.getKey());
        try {
            service.getKeywordByKey(keyword2.getKey());
            fail("The removed keyword is still in the DB.");
        } catch (DataRetrievalFailureException e) {
            s_logger.debug("Expected exception catched.", e);
        }
    }
    
    /**
     * This test inserts two keywords and removes them using the removeKeywords
     * method. Afterwards, they should not be reachable any more.
     */
    public void testInsertRemoveKeywords() {
        KeywordService service = getKeywordService();
        KeywordDto keyword = new KeywordDto();
        keyword.setName("Java");
        keyword.setDescription("Java related documentation");
        KeywordDto keyword2 = service.saveKeyword(keyword);
        
        KeywordDto keyword3 = new KeywordDto();
        keyword3.setName("C");
        keyword3.setDescription("C related documentation");
        KeywordDto keyword4 = service.saveKeyword(keyword3);
        
        HashSet<Integer> keywords = new HashSet<Integer>();
        keywords.add(keyword2.getKey());
        keywords.add(keyword4.getKey());
        
        service.removeKeywords(keywords);
        
        try {
            service.getKeywordByKey(keyword2.getKey());
            service.getKeywordByKey(keyword4.getKey());
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
    public void testInsertModificateRemoveKeywordByTwoPersons() {
        KeywordService service = getKeywordService();
        KeywordDto keyword = new KeywordDto();
        keyword.setName("Java");
        keyword.setDescription("Java related documentation");
        service.saveKeyword(keyword);
        KeywordDto keyword2 = service.getKeywordByName("Java");
        KeywordDto keyword3 = service.getKeywordByName("Java");
        keyword2.setDescription("Java API");
        service.saveKeyword(keyword2);
        keyword3.setDescription("Java API of version 1.5");
        try {
            service.saveKeyword(keyword3);
            fail("The current keyword could be modified "
                + "by two persons at the same time.");
        } catch (OptimisticLockingFailureException e) {
            s_logger.debug("Expected exception catched.", e);
        }
        // this suceeds, as the service never throws an 
        // OptimisticLockingException on delete. The
        // new repository interface, which will superseed this interface
        // at a later time, throws the expected excaption.
        service.removeKeyword(keyword3.getKey());
    }

    /**
     * This test inserts five keywords and does different searchs on it.
     */
    public void testSearchKeywords() {
        KeywordService service = getKeywordService();
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
        keyword = service.saveKeyword(keyword);
        keyword2 = service.saveKeyword(keyword2);
        keyword3 = service.saveKeyword(keyword3);
        keyword4 = service.saveKeyword(keyword4);
        keyword5 = service.saveKeyword(keyword5);

        QueryObject query = new QueryObject();
        query.addCriteria(LikeCriteria.caseInsensitive("description", "%Doc%"));
        List list = service.searchKeywords(query);
        assertEquals(
            "Search for description like 'Doc' results not in two keywords.",
            2, list.size());
        Iterator it = list.iterator();
        while (it.hasNext()) {
            KeywordDto k = (KeywordDto) it.next();
            if (!(k.equals(keyword) || k.equals(keyword2))) {
                fail("Not expected keyword on search for description "
                    + "like 'Doc'.");
            }
        }

        query = new QueryObject();
        query.addCriteria(LikeCriteria.caseInsensitive("name", "%host%"));
        list = service.searchKeywords(query);
        assertEquals("Search for name like 'host' results not in one keyword.",
            1, list.size());
        it = list.iterator();
        while (it.hasNext()) {
            KeywordDto k = (KeywordDto) it.next();
            if (!k.equals(keyword3)) {
                fail("Not expected keyword on search for name like 'host'.");
            }
        }

        list = service.searchKeywords(new QueryObject());
        assertEquals("Search for empty name and description results not in "
            + "five keywords.", 5, list.size());

        query = new QueryObject();
        query.addCriteria(LikeCriteria.caseInsensitive(
            "description", "%log4j%"));
        list = service.searchKeywords(query);
        assertEquals(
            "Search for description like 'log4j' results not in one keyword.",
            1, list.size());
        it = list.iterator();
        while (it.hasNext()) {
            KeywordDto k = (KeywordDto) it.next();
            if (!k.equals(keyword4)) {
                fail("Not expected keyword on search for description "
                    + "like 'log4j'.");
            }
        }
    }
    
}
//Checkstyle: MagicNumber on
