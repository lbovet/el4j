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
package ch.elca.el4j.applications.refdbseam;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.framework.Controller;
import org.jboss.seam.log.Log;
import org.springframework.dao.DataIntegrityViolationException;

import ch.elca.el4j.apps.keyword.dao.KeywordDao;
import ch.elca.el4j.apps.keyword.dom.Keyword;
import ch.elca.el4j.services.persistence.generic.dao.DaoRegistry;

/**
 * 
 * This class can be used to work with Keywords, based on a list of keywords.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Philippe Jacot (PJA)
 */
@Scope(ScopeType.CONVERSATION)
@Name("keywordController")
public class KeywordController extends Controller {
    /**
     * The KeywordService injected from Spring.
     * Not really used, just as an example.
     */
//    @In("#{keywordService}")
//    private KeywordService m_service;
    
    /**
     * The dao registry to load the dao from.
     */
    @In("#{daoRegistry}")
    private DaoRegistry m_registry;
        
    /**
     * The logger.
     */
    @Logger
    private Log m_log;
    
    /**
     * The Keyword DAO.
     */
    private KeywordDao m_keywordDao;
    
    /**
     * Keyword "Example" to create a new Keyword.
     */
    @In(create = true, value = "newKeyword") @Out("newKeyword")
    private Keyword m_newKeyword;
        
    /**
     * A list of all available keywords.
     */
    @DataModel("keywords")
    private List<Keyword> m_keywords;
    
    /**
     * The currently selected Keyword.
     */
    @DataModelSelection("keywords")
    private Keyword m_selectedKeyword;
        
    /**
     * Create a new Keyword.
     * @return Creation string if successful. Null otherwise
     */
    public String createKeyword() {
        m_log.info("Trying to create a new Keyword");
        try {
            m_keywordDao.saveOrUpdate(m_newKeyword);
        } catch (DataIntegrityViolationException e) {
            m_log.info("Error creating keyword " + m_newKeyword);
            FacesMessages.instance().add("Unable to create Keyword");
            return null;
        }
        m_keywords.add(m_newKeyword);
        m_newKeyword = new Keyword();
        return "created";
    }
    
    /**
     * Delete the selected keyword.
     * @return Deletion string
     */
    public String deleteKeyword() {
        m_log.info("Trying to delete a keyword");
        if (m_selectedKeyword != null) {
            m_log.info("Delete #0", m_selectedKeyword.getName());
            m_keywordDao.delete(m_selectedKeyword);
            m_keywords.remove(m_selectedKeyword);
        } else {
            m_log.info("No keyword selected");
        }
        return "deleted";
    }
    
    
    /* Factories */
    
    /**
     * Factory to create a new Keyword.
     * @return A new Keyword
     */
    @Factory("newKeyword")
    public Keyword getNewKeyword() {
        return new Keyword();
    }
    
    /**
     * Factory to instantiate the available keywords.
     *
     */
    @Factory("keywords")
    public void findKeywords() {
        m_keywords = getKeywordDao().getAll(); 
    }
    
    /**
     * Get the keyword dao.
     * @return The Keyword Dao
     */
    private KeywordDao getKeywordDao() {
        if (m_keywordDao == null) {
            m_keywordDao = (KeywordDao) m_registry.getFor(Keyword.class);
        }
        return m_keywordDao;
    }
    
}
