/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * This program is published under the GNU General Public License (GPL) license.
 * http://www.gnu.org/licenses/gpl.txt
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.services.search.criterias;

/**
 * Criteria for the like pattern.
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
public class LikeCriteria extends AbstractCriteria {
    /**
     * Marks if the pattern is case sensitive.
     */
    private Boolean m_caseSensitive;
    
    /**
     * Constructor.
     * 
     * @param field Is the field the criteria is made for.
     * @param value Is the value of this criteria.
     * @param caseSensitive Marks if the pattern is case sensitive.
     */
    protected LikeCriteria(String field, String value, 
        boolean caseSensitive) {
        super(field, value);
        m_caseSensitive = Boolean.valueOf(caseSensitive);
    }

    /**
     * @param field Is the field the criteria is made for.
     * @param value Is the value of this criteria.
     * @return Returns a case insensitive pattern criteria.
     */
    public static LikeCriteria caseInsensitive(String field, String value) {
        return new LikeCriteria(field, value, false);
    }

    /**
     * @param field Is the field the criteria is made for.
     * @param value Is the value of this criteria.
     * @return Returns a case sensitive pattern criteria.
     */
    public static LikeCriteria caseSensitive(String field, String value) {
        return new LikeCriteria(field, value, true);
    }

    /**
     * @return Returns <code>true</code> if it is case sensitive.
     */
    public final Boolean isCaseSensitive() {
        return m_caseSensitive;
    }

    /**
     * {@inheritDoc}
     */
    public String getType() {
        return "like";
    }
}