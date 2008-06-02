package ch.elca.el4j.services.monitoring.jmx.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

/**
 * Inspects a PropertyDescriptor.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author David Bernhard (DBD)
 */
public class PropertyReflector {
    
    /**
     * The wrapped descriptor. 
     */
    private PropertyDescriptor m_d;
    
    /**
     * Create a PropertyReflector from a {@link PropertyDescriptor}. 
     * @param d The descriptor to wrap.
     */
    public PropertyReflector(PropertyDescriptor d) {
        m_d = d;
    }
    
    /**
     * @return Whether this property is readable.
     */
    public boolean isReadable() {
        return (m_d.getReadMethod() != null);
    }
    
    /**
     * @return The name of the read method - 
     * usually "get-" or "is-" + Name. 
     */
    public String getReadMethod() {
        return m_d.getReadMethod().getName();
    }
    
    /**
     * @return Whether this property is writable.
     */
    public boolean isWritable() {
        return (m_d.getWriteMethod() != null);
    }
    
    /**
     * @return The name of the write method.
     */
    public String getWriteMethod() {
        return m_d.getWriteMethod().getName();
    }
    
    /**
     * @return The name of this property.
     */
    public String getName() {
        return m_d.getName();
    }
    
    /**
     * @return The type of this property.
     */
    public String getType() {
        String type = "";
        if (isReadable()) {
            type = m_d.getReadMethod().getReturnType().toString();
        }
        if (isWritable() && type.equals("")) {
            Method m = m_d.getWriteMethod();
            if (m.getParameterTypes().length == 1) {
                type = m.getParameterTypes()[0].toString();
            }
        }
        return type;
    }
    
    /**
     * @return A string (R|-)(W|-) indicating what access this property has.
     */
    public String getRW() {
        return (isReadable() ? "R" : "-") + (isWritable() ? "W" : "-"); 
    }
}