
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataRetrievalFailureException;

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
        Keyword keyword = new Keyword();
        keyword.setName("Java");
        keyword.setDescription("Java related documentation");
        Keyword keyword2 = dao.saveOrUpdate(keyword);
        
        Keyword keyword3 = new Keyword();
        keyword3.setName("C");
        keyword3.setDescription("C related documentation");
        Keyword keyword4 = dao.saveOrUpdate(keyword3);
        
        HashSet<Integer> keywords = new HashSet<Integer>();
        keywords.add(keyword2.getKey());
        keywords.add(keyword4.getKey());
        
        service.removeKeywords(keywords);
        
        try {
            dao.findById(keyword2.getKey());
            dao.findById(keyword4.getKey());
        } catch (DataRetrievalFailureException e) {
            s_logger.debug("Expected exception catched.", e);
        }
    }

}
//Checkstyle: MagicNumber on
