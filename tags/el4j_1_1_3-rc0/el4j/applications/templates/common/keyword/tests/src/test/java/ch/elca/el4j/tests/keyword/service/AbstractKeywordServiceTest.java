
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
package ch.elca.el4j.tests.keyword.service;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.OptimisticLockingFailureException;

import ch.elca.el4j.apps.keyword.dao.KeywordDao;
import ch.elca.el4j.apps.keyword.dom.Keyword;
import ch.elca.el4j.apps.keyword.service.KeywordService;
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
     * Keyword DAO. Created by application context.
     */
    private KeywordDao m_keywordDao;
    
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
                = (KeywordService) getApplicationContext()
                    .getBean("keywordService");
        }
        return m_keywordService;
    }
    
    /**
     * @return Returns the keywordDao.
     */
    protected KeywordDao getKeywordDao() {
        if (m_keywordDao == null) {
            m_keywordDao 
                = (KeywordDao) getApplicationContext()
                    .getBean("keywordDao");
        }
        return m_keywordDao;
    }

    
    /**
     * This test inserts two keywords and removes them using the removeKeywords
     * method. Afterwards, they should not be reachable any more.
     */
    public void testInsertRemoveKeywords() {
        KeywordService service = getKeywordService();
        KeywordDao dao = getKeywordDao();
        
        Keyword keywordJava = dao.saveOrUpdate(createNewJavaKeyword());
        Keyword keywordC = dao.saveOrUpdate(createNewCKeyword());
        
        List<Keyword> keywordList = dao.getAll();
        assertEquals(2, keywordList.size());
        for (Keyword keyword : keywordList) {
            if (!keywordJava.equals(keyword)
                && !keywordC.equals(keyword)) {
                fail("Contains unexpected keyword '" 
                    + keyword.getName() + "'.");
            }
        }
        
        Set<Integer> keywordKeys = new HashSet<Integer>();
        keywordKeys.add(keywordJava.getKey());
        keywordKeys.add(keywordC.getKey());
        
        service.deleteKeywords(keywordKeys);
        
        try {
            dao.findById(keywordJava.getKey());
            fail("Keyword Java is still in database!");
        } catch (DataRetrievalFailureException e) {
            s_logger.debug("Expected exception catched.", e);
        }

        try {
            dao.findById(keywordC.getKey());
            fail("Keyword C is still in database!");
        } catch (DataRetrievalFailureException e) {
            s_logger.debug("Expected exception catched.", e);
        }
    }
    
    public void testInsertRemoveKeywordsFailing() {
        KeywordService service = getKeywordService();
        KeywordDao dao = getKeywordDao();
        
        Keyword keywordJava = dao.saveOrUpdate(createNewJavaKeyword());
        Keyword keywordC = dao.saveOrUpdate(createNewCKeyword());
        
        List<Keyword> keywordList = dao.getAll();
        assertEquals(2, keywordList.size());
        for (Keyword keyword : keywordList) {
            if (!keywordJava.equals(keyword)
                && !keywordC.equals(keyword)) {
                fail("Contains unexpected keyword '" 
                    + keyword.getName() + "'.");
            }
        }
        
        dao.delete(keywordC);
        
        try {
            dao.findById(keywordC.getKey());
            fail("Keyword C is still in database!");
        } catch (DataRetrievalFailureException e) {
            s_logger.debug("Expected exception catched.", e);
        }
        
        Set<Integer> keywordKeys = new LinkedHashSet<Integer>();
        keywordKeys.add(keywordJava.getKey());
        keywordKeys.add(keywordC.getKey());
        
        try {
            service.deleteKeywords(keywordKeys);
            fail("Removing keywords should fail!");
        } catch (OptimisticLockingFailureException e) {
            s_logger.debug("Expected exception catched.", e);
        }
        
        keywordList = dao.getAll();
        assertEquals(1, keywordList.size());
        for (Keyword keyword : keywordList) {
            if (!keywordJava.equals(keyword)) {
                fail("Contains unexpected keyword '" 
                    + keyword.getName() + "'.");
            }
        }
    }

    /**
     * @return Returns a newly create keyword with java program language as 
     *         content.
     */
    protected Keyword createNewJavaKeyword() {
        Keyword keywordJava = new Keyword();
        keywordJava.setName("Java");
        keywordJava.setDescription("Java related documentation");
        return keywordJava;
    }
    
    /**
     * @return Returns a newly create keyword with c program language as 
     *         content.
     */
    protected Keyword createNewCKeyword() {
        Keyword keywordC = new Keyword();
        keywordC.setName("C");
        keywordC.setDescription("C related documentation");
        return keywordC;
    }
}
//Checkstyle: MagicNumber on
