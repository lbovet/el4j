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
package ch.elca.el4j.services.search.criterias;

import ch.elca.el4j.util.codingsupport.Reject;


/**
 * Marker interface for a criteria.
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
public abstract class AbstractCriteria implements Criteria {
    /**
     * Is the field the criteria is made for.
     */
    private String m_field;
    
    /**
     * Is the value of this criteria.
     */
    private Object m_value;
    
    /**
     * Default constructor for remoting protocols like hessian and burlap added.
     */
    protected AbstractCriteria() { }
    
    /**
     * Constructor.
     * 
     * @param field Is the field the criteria is made for.
     * @param value Is the value of this criteria.
     */
    protected AbstractCriteria(String field, Object value) {
        Reject.ifEmpty(field);
        Reject.ifNull(value);
        m_field = field;
        m_value = value;
    }
    
    /**
     * @return Returns the field.
     */
    public final String getField() {
        return m_field;
    }

    /**
     * @return Returns the value.
     */
    public final Object getValue() {
        return m_value;
    }
    
    /**
     * @return Returns the string value of this criteria.
     */
    public final String getStringValue() {
        return (String) getValue();
    }
    
    /**
     * @return Returns the boolean value of this criteria.
     */
    public final Boolean getBooleanValue() {
        return (Boolean) getValue();
    }

    /**
     * @return Returns the integer value of this criteria.
     */
    public final Integer getIntegerValue() {
        return (Integer) getValue();
    }

    /**
     * @return Returns the long value of this criteria.
     */
    public final Long getLongValue() {
        return (Long) getValue();
    }

    /**
     * @return Returns the short value of this criteria.
     */
    public final Short getShortValue() {
        return (Short) getValue();
    }

    /**
     * @return Returns the byte value of this criteria.
     */
    public final Byte getByteValue() {
        return (Byte) getValue();
    }
}