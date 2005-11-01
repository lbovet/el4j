/*
 * EL4J, the Enterprise Library for Java, complementing Spring http://el4j.sf.net
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

package ch.elca.el4j.util.interfaceenrichment;

/**
 * Class to describe a method. This is used to decorate methods.
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
public class MethodDescriptor {
    /**
     * Name of the method.
     */
    protected String m_methodName;
    
    /**
     * Parameter names.
     */
    protected String[] m_parameterNames;
    
    /**
     * Parameter types.
     */
    protected Class[] m_parameterTypes;
    
    /**
     * Exceptions which this method can throw.
     */
    protected Class[] m_thrownExceptions;
    
    /**
     * The return type of this method.
     */
    protected Class m_returnType;

    /**
     * @return Returns the m_methodName.
     */
    public String getMethodName() {
        return m_methodName;
    }

    /**
     * @param name
     *            The m_methodName to set.
     */
    public void setMethodName(String name) {
        m_methodName = name;
    }

    /**
     * @return Returns the m_parameterTypes.
     */
    public Class[] getParameterTypes() {
        return m_parameterTypes;
    }

    /**
     * @param types
     *            The m_parameterTypes to set.
     */
    public void setParameterTypes(Class[] types) {
        m_parameterTypes = types;
    }

    /**
     * @return Returns the m_returnType.
     */
    public Class getReturnType() {
        return m_returnType;
    }

    /**
     * @param type
     *            The m_returnType to set.
     */
    public void setReturnType(Class type) {
        m_returnType = type;
    }

    /**
     * @return Returns the m_thrownExceptions.
     */
    public Class[] getThrownExceptions() {
        return m_thrownExceptions;
    }

    /**
     * @param exceptions
     *            The m_thrownExceptions to set.
     */
    public void setThrownExceptions(Class[] exceptions) {
        m_thrownExceptions = exceptions;
    }

    /**
     * This may not be the real names (e.g. when we use reflection)
     * 
     * @return Returns the m_parameterNames.
     */
    public String[] getParameterNames() {
        return m_parameterNames;
    }

    /**
     * This may not be the real names (e.g. when we use reflection)
     * 
     * @param names
     *            The m_parameterNames to set.
     */
    public void setParameterNames(String[] names) {
        m_parameterNames = names;
    }
}
