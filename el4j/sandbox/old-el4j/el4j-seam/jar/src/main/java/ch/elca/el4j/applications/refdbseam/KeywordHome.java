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

import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.RequestParameter;
import org.springframework.dao.DataRetrievalFailureException;

import ch.elca.el4j.apps.keyword.dao.KeywordDao;
import ch.elca.el4j.apps.keyword.dom.Keyword;

/**
 * 
 * This is a EntityHome for Keywords.
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
@Name("keywordHome")
public class KeywordHome extends AbstractEntityHome<Keyword> { 
    /**
     * The Id passed with the url.
     */
    @RequestParameter("keywordId")
    private Integer m_keywordId;
                        
    /**
     * Outject the current instance.
     * @return The current instance.
     */
    @Factory("keyword")
    public Keyword initKeyword() {
        return getInstance();
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public String persist() {       
        if (validKeyword()) {
            return super.persist();
        } else {
            return "keywordExists";
        }
    }
    
    /**
     * Check if a keyword is valid.
     * It checks if the name of the keyword already exists, and if, whether the
     * found Keyword has the same id.
     * @return True, if the keyword can be saveld like this
     */
    private boolean validKeyword() {
        m_log.info("Check if #0 already exists", getInstance().getName());
        KeywordDao dao = (KeywordDao)  getDao();
        boolean keywordValid;
        try {
            Keyword k = dao.getKeywordByName(getInstance().getName());
            if (k.getKey() != getInstance().getKey()) {
                // The Keyword already exists
                m_log.info("Keyword with this name already exists");
                keywordValid =  false;
            } else {
                m_log.info("Keyword exists, but has the same ID");
                keywordValid = true;
            }
        } catch (DataRetrievalFailureException e) {
            // The Keyword with does not exist, therefore it's save to create it
            m_log.info("The Keyword does not exist yet");
            keywordValid = true;
        }
        return keywordValid;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    protected void handleEntityNotFound(DataRetrievalFailureException drfe) {
        throw drfe;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    protected Integer getId() {
        if (m_keywordId == null) {
            return super.getId();
        } else {
            return m_keywordId;
        }
    }
}
