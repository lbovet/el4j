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
package ch.elca.el4j.seam.generic.errorhandling;

/**
 * This class contains all information about conflicting entites. This is used
 * to resolve optimistic locking exceptions.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Stefan Wismer (SWI)
 */
public class EntityConflict {
    /**
     * The current (valid) object from the database.
     */
    private Object m_currentObject;
    
    /**
     * The modified (invalid) object.
     */
    private Object m_staleObject;
    
    /**
     * The page to redirect to when conflict is resolved.
     */
    private String m_redirectPage;

    /**
     * @return Returns the currentObject.
     */
    public Object getCurrentObject() {
        return m_currentObject;
    }

    /**
     * @param currentObject Is the currentObject to set.
     */
    public void setCurrentObject(Object currentObject) {
        m_currentObject = currentObject;
    }

    /**
     * @return Returns the staleObject.
     */
    public Object getStaleObject() {
        return m_staleObject;
    }

    /**
     * @param staleObject Is the staleObject to set.
     */
    public void setStaleObject(Object staleObject) {
        m_staleObject = staleObject;
    }

    /**
     * @return Returns the redirectPage.
     */
    public String getRedirectPage() {
        return m_redirectPage;
    }

    /**
     * @param redirectPage Is the redirectPage to set.
     */
    public void setRedirectPage(String redirectPage) {
        m_redirectPage = redirectPage;
    }
}
