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

package ch.elca.el4j.services.remoting.protocol.ejb;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.naming.NamingException;

import org.aopalliance.aop.AspectException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class creates stateful EJB remote proxies.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Andreas Bur (ABU)
 */
public class RemoteStatefulSessionProxyFactoryBean extends
        AbstractRemoteSessionProxyFactoryBean {

    /** The static logger. */
    private static Log s_logger = LogFactory.getLog(
            RemoteStatefulSessionProxyFactoryBean.class);
    
    /** Whether the crete(Object[]) method has been set. */
    private boolean m_createMethodSet;
    
    /** The create(Object[]) method's attributes. */
    private Object[] m_createArgument;
    
    /**
     * @return Returns whether a custom <code>create(Object[])</code> method
     *      has been set.
     */
    public boolean isCreateMethodSet() {
        return m_createMethodSet;
    }

    /**
     * Sets whether a custom <code>create(Object[])</code> method is used.
     * 
     * @param crateMethodSet
     *      <code>true</code> if a custom <code>create(Object[])</code> method
     *      is used.
     */
    public void setCreateMethodSet(boolean crateMethodSet) {
        if (this.m_createMethodSet == crateMethodSet) {
            return;
        }
        
        this.m_createMethodSet = crateMethodSet;
        try {
            refreshHome();
        } catch (NamingException ne) {
            s_logger.warn("failed refreshing home object");
        }
    }

    /**
     * {@inheritDoc}
     */
    protected Method getCreateMethod(Object home) throws AspectException {
        if (isCreateMethodSet()) {
            try {
                /* Cache the EJB create(Object[]) method that must be declared
                 * on the home interface.
                 */
                return home.getClass().getMethod("create", 
                        new Class[] {Object[].class});
            } catch (NoSuchMethodException ex) {
                throw new AspectException("EJB home [" + home + "] has no "
                        + "create(Object[]) method");
            }
        }
        return super.getCreateMethod(home);
    }
    
    /**
     * {@inheritDoc}
     */
    protected Object create() throws NamingException,
        InvocationTargetException {
        
        if (isCreateMethodSet()) {
            try {
                Object home = getHome();
                Method createMethodToUse = getCreateMethod(home);
                // Invoke create(Object[]) method on EJB home object.
                return createMethodToUse.invoke(home, 
                        new Object[] {getCreateArgument()});
            } catch (IllegalAccessException ex) {
                throw new AspectException("Could not access EJB home "
                        + "create(Object[]) method", ex);
            }
        } else {
            return super.create();
        }
    }

    /**
     * @return Returns the arguments used in a <code>create(Object[])</code>
     *      call.
     */
    public Object[] getCreateArgument() {
        return m_createArgument;
    }

    /**
     * Sets the arguments used in a <code>create(Object[])</code> method call.
     * 
     * @param createArgument
     *      The arguments to set.
     */
    public void setCreateArgument(Object[] createArgument) {
        this.m_createArgument = createArgument;
    }
}
