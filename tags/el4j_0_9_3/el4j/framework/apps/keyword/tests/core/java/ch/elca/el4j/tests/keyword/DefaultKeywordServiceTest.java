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
package ch.elca.el4j.tests.keyword;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.OptimisticLockingFailureException;

import ch.elca.el4j.apps.keyword.dao.KeywordDao;
import ch.elca.el4j.apps.keyword.dto.KeywordDto;
import ch.elca.el4j.services.search.QueryObject;
import ch.elca.el4j.services.search.criterias.LikeCriteria;

// Checkstyle: MagicNumber off

/**
 * Test case for <code>SqlMapKeywordDao</code>.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public class DefaultKeywordServiceTest extends AbstractTestCaseBase {
    /**
     * Private logger.
     */
    private static Log s_logger 
        = LogFactory.getLog(DefaultKeywordServiceTest.class);

    /**
     * Keyword dao. Created by application context.
     */
    private KeywordDao m_keywordDao;

    /**
     * @return Returns the keywordDao.
     */
    protected KeywordDao getKeywordDao() {
        if (m_keywordDao == null) {
            m_keywordDao 
                = (KeywordDao) getApplicationContext().getBean("keywordDao");
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
        keyword = dao.saveKeyword(keyword);

        KeywordDto keyword2 = new KeywordDto();
        keyword2.setName("This is a too large keyword");
        keyword2.setDescription("I'm too long, I'm too long"
                + "I'm too long, I'm too long" + "I'm too long, I'm too long"
                + "I'm too long, I'm too long" + "I'm too long, I'm too long"
                + "I'm too long, I'm too long" + "I'm too long, I'm too long"
                + "I'm too long, I'm too long" + "I'm too long, I'm too long"
                + "I'm too long, I'm too long");
        try {
            keyword2 = dao.saveKeyword(keyword2);
            fail("A keyword with more than max length could be saved.");
        } catch (Exception e) {
            s_logger.debug("Expected exception catched.", e);
        }

        KeywordDto keyword3 = new KeywordDto();
        keyword3.setName("Java");
        keyword3.setDescription("Once again the same name");
        try {
            keyword3 = dao.saveKeyword(keyword3);
            fail("Keyword names need not to be unique.");
        } catch (DataIntegrityViolationException e) {
            s_logger.debug("Expected exception catched.", e);
        }
        
        KeywordDto keyword4 = new KeywordDto();
        keyword4.setName("KeywordWithoutDescription");
        keyword4.setDescription(null);
        dao.saveKeyword(keyword4);

        KeywordDto keyword5 = new KeywordDto();
        keyword5.setName(null);
        keyword5.setDescription(null);
        try {
            dao.saveKeyword(keyword5);
            fail("Possible to insert a keyword without a name and a "
                + "description.");
        } catch (DataIntegrityViolationException e) {
            s_logger.debug("Expected exception catched.", e);
        }

        KeywordDto keyword6 = new KeywordDto();
        keyword6.setName(null);
        keyword6.setDescription("A description");
        try {
            dao.saveKeyword(keyword6);
            fail("Possible to insert a keyword without a name.");
        } catch (DataIntegrityViolationException e) {
            s_logger.debug("Expected exception catched.", e);
        }
    }

    /**
     * This test inserts a keyword and looks up for it by keyword's primary key.
     */
    public void testInsertGetKeywordByKey() {
        KeywordDao dao = getKeywordDao();
        KeywordDto keyword = new KeywordDto();
        keyword.setName("Xml");
        keyword.setDescription("Xml related documentation");
        KeywordDto keyword2 = dao.saveKeyword(keyword);
        KeywordDto keyword3 = dao.getKeywordByKey(keyword2.getKey());
        assertEquals("The inserted and read Dtos are not equal", keyword3,
                keyword2);
    }

    /**
     * This test inserts a keyword and looks up for it by keyword's name.
     */
    public void testInsertGetKeywordByName() {
        KeywordDao dao = getKeywordDao();
        KeywordDto keyword = new KeywordDto();
        keyword.setName("Xml");
        keyword.setDescription("Xml related documentation");
        KeywordDto keyword2 = dao.saveKeyword(keyword);
        KeywordDto keyword3 = dao.getKeywordByName(keyword2.getName());
        assertEquals("The inserted and read Dtos are not equal", keyword3,
                keyword2);
    }

    /**
     * This test inserts two keywords and looks up for all. Tested will be the
     * number of keywords, should be two, and if they really are these which it
     * has added.
     */
    public void testInsertGetAllKeywords() {
        KeywordDao dao = getKeywordDao();
        KeywordDto keyword1 = new KeywordDto();
        keyword1.setName("Xml");
        keyword1.setDescription("Xml related documentation");
        KeywordDto keyword2 = new KeywordDto();
        keyword2.setName("Java");
        keyword2.setDescription("Java related documentation");
        dao.saveKeyword(keyword1);
        dao.saveKeyword(keyword2);
        List list = dao.getAllKeywords();
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
        KeywordDao dao = getKeywordDao();
        KeywordDto keyword = new KeywordDto();
        keyword.setName("Java");
        keyword.setDescription("Java related documentation");
        KeywordDto keyword2 = dao.saveKeyword(keyword);
        dao.removeKeyword(keyword2.getKey());
        try {
            dao.getKeywordByKey(keyword2.getKey());
            fail("The removed keyword is still in the DB.");
        } catch (DataRetrievalFailureException e) {
            s_logger.debug("Expected exception catched.", e);
        }
    }

    /**
     * This test inserts one keyword. Afterwards it will be looked up by two
     * persons. Now, these persons edit the same keyword and would like to save
     * changes. The person, which save as second must get a
     * <code>KeywordModificationException</code>. But this person should be
     * able to remove this keyword, because removing is not under optimistic
     * locking control.
     */
    public void testInsertModificateRemoveKeywordByTwoPersons() {
        KeywordDao dao = getKeywordDao();
        KeywordDto keyword = new KeywordDto();
        keyword.setName("Java");
        keyword.setDescription("Java related documentation");
        dao.saveKeyword(keyword);
        KeywordDto keyword2 = dao.getKeywordByName("Java");
        KeywordDto keyword3 = dao.getKeywordByName("Java");
        keyword2.setDescription("Java API");
        dao.saveKeyword(keyword2);
        keyword3.setDescription("Java API of version 1.5");
        try {
            dao.saveKeyword(keyword3);
            fail("The current keyword could be modificated "
                    + "by two persons on the same time.");
        } catch (OptimisticLockingFailureException e) {
            s_logger.debug("Expected exception catched.", e);
        }
        dao.removeKeyword(keyword3.getKey());
    }

    /**
     * This test inserts five keywords and does different searchs on it.
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
        keyword = dao.saveKeyword(keyword);
        keyword2 = dao.saveKeyword(keyword2);
        keyword3 = dao.saveKeyword(keyword3);
        keyword4 = dao.saveKeyword(keyword4);
        keyword5 = dao.saveKeyword(keyword5);

        QueryObject query = new QueryObject();
        query.addCriteria(LikeCriteria.caseInsensitive("description", "%Doc%"));
        List list = dao.searchKeywords(query);
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
        list = dao.searchKeywords(query);
        assertEquals("Search for name like 'host' results not in one keyword.",
            1, list.size());
        it = list.iterator();
        while (it.hasNext()) {
            KeywordDto k = (KeywordDto) it.next();
            if (!k.equals(keyword3)) {
                fail("Not expected keyword on search for name like 'host'.");
            }
        }

        list = dao.searchKeywords(new QueryObject());
        assertEquals("Search for empty name and description results not in "
            + "five keywords.", 5, list.size());

        query = new QueryObject();
        query.addCriteria(LikeCriteria.caseInsensitive(
            "description", "%log4j%"));
        list = dao.searchKeywords(query);
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
