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
package ch.elca.el4j.demos.rcp.helpers;

import org.hibernate.validator.ClassValidator;
import org.hibernate.validator.InvalidValue;
import org.springframework.core.closure.Constraint;

/**
 * 
 * This class checks constraints given as Hibernate Annotation in the 
 * domain objects.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 *
 * @param <T> Generic parameter of Class Type this Constraint is for.
 * @author David Stefan (DST)
 */
public class HibernateConstraint<T> implements Constraint {

    /**
     * The Hibernate Class Validator.
     */
    private ClassValidator m_validator;
    
    /**
     * Property name to check.
     */
    private String m_propertyName;
    
    /**
     * Constructor.
     * 
     * @param propName Property to check with this constraint
     * @param domainObjectClass The domain object
     */
    public HibernateConstraint(Class<T> domainObjectClass, String propName) {
        m_propertyName = propName;
        m_validator = new ClassValidator<T>(domainObjectClass);
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean test(Object argument) {
        Object toValidate = null;
        // Use toString() on argument because regexs need Strings to validate
        if (argument != null) {
            toValidate = argument.toString();
        }
        InvalidValue[] invalid = m_validator.getPotentialInvalidValues(
            m_propertyName, toValidate);

        return (invalid.length == 0);
    }

}
