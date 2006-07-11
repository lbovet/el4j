/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.services.persistence.hibernate.validation;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.validator.Validator;


/**
 * 
 * This class validates a bean with the help of a validation method defined by
 * the bean.
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
public class ValidateValidator implements Validator<Validate>, Serializable {
    
    /**
     * Private logger.
     */
    private static Log s_logger 
        = LogFactory.getLog(ValidateValidator.class);
    
    /**
     * The validation method defined by the annotated bean.
     */
    private String m_validationMethod;
    
    /**
     * {@inheritDoc}
     */
    public void initialize(Validate parameters) {
        m_validationMethod = parameters.validationMethod();    
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isValid(Object value) {
        try {
            if (value == null) {
                return true;   
            }
            // Invoke the validation method of the annotated bean on the 
            // annotated bean.
            Class annotatedClass = value.getClass();
            Method[] declaredMethods = annotatedClass.getMethods();
            Method currentMethod = null;
            for (int i = 0; i < declaredMethods.length; i++) {
                currentMethod = declaredMethods[i];
                s_logger.debug(currentMethod.getName());
                if (currentMethod.getName().equals(m_validationMethod)) {
                    Object result = currentMethod.invoke(value, new Object[]{});
                    s_logger.debug(result.getClass());
                    if (result instanceof Boolean) {
                        return ((Boolean) result).booleanValue();
                    } else {
                        return false;
                    }
                }
            }        
        } catch (IllegalAccessException iae) {
            s_logger.warn("Illegal access exception occurred: ");
            iae.printStackTrace();
        } catch (InvocationTargetException ite) {
            s_logger.warn("Invocation target exception occurred: ");
            ite.printStackTrace();
        }
        return false;
    }
    
}

