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
package ch.elca.el4j.demos.gui.events;

/**
 * This event informs about the search of a reference from refDB.
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
public class SearchRefDBEvent {
    /**
     * The field where to search.
     */
    private String m_field;
    /**
     * The value that the {@link #m_field} should match.
     */
    private String m_value;
    
    /**
     * @param field    the field where to search
     * @param value    the value that the field should match
     */
    public SearchRefDBEvent(String field, String value) {
        m_field = field;
        m_value = value;
    }
    /**
     * @return    the field where to search
     */
    public String getField() {
        return m_field;
    }
    
    /**
     * @param field    the field where to search
     */
    public void setField(String field) {
        this.m_field = field;
    }
    
    /**
     * @return    the value that the {@link #m_field} should match
     */
    public String getValue() {
        return m_value;
    }
    
    /**
     * @param value    the value that the {@link #m_field} should match
     */
    public void setValue(String value) {
        this.m_value = value;
    }

}
