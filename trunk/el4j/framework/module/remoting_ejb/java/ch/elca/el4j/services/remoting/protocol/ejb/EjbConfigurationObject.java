/*
 * EL4J, the Enterprise Library for Java, complementing Spring http://EL4J.sf.net
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

package ch.elca.el4j.services.remoting.protocol.ejb;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import ch.elca.el4j.services.remoting.ProtocolSpecificConfiguration;

/**
 * This configuration objects contains additional informations used in order to
 * setup the EJB remoting solution.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Andreas Bur (ABU)
 */
public class EjbConfigurationObject implements ProtocolSpecificConfiguration {

    /** Whether the described EJB session bean is stateful. */
    protected boolean m_stateful = false;

    /** 
     * The service bean's method name where the EJB session bean's
     * <code>ejbCreate(Object[])</code> method is delegated to.
     */
    protected String m_create;
    
    /** 
     * The list of objects passed to the services <code>create(Object[])</code>
     * method.
     */
    protected List m_createArgument;
    
    /** 
     * The service bean's method name where the EJB session bean's
     * <code>ejbRemove()</code> method is delegated to.
     */
    protected String m_remove;

    /** 
     * The service bean's method name where the EJB session bean's
     * <code>ejbActivate()</code> method is delegated to.
     */
    protected String m_activate;
    
    /** 
     * The service bean's method name where the EJB session bean's
     * <code>ejbPassivate()</code> method is delegated to.
     */
    protected String m_passivate;

    /** 
     * The service bean's method name where the EJB session bean's
     * <code>setSessionContext()</code> method is delegated to.
     * <p/><b>Notice</b>: Since 
     * <code>setSessionContext(SessionContext ctx)</code>
     * is called before <code>ejbCreate</code> we'll set the session context
     * on the service bean in the EJB session bean's <code>ejbCreate</code>,
     * just before it is delegated to the service bean's create method as
     * it is specified with {@link #setCreate(String)}.
     */
    protected String m_sessionContext;

    // SessionSynchronization methods
    /** 
     * The service bean's method name where the EJB session bean's
     * <code>afterBegin()</code> method is delegated to.
     */
    protected String m_afterBegin;
    
    /** 
     * The service bean's method name where the EJB session bean's
     * <code>beforeCompletion</code> method is delegated to.
     */
    protected String m_beforeCompletion;
    
    /** 
     * The service bean's method name where the EJB session bean's
     * <code>afterCompletion(boolean committed)</code> method is delegated to.
     */
    protected String m_afterCompletion;

    /** Map containing additional tags passed to the XDoclet engine. */
    protected Map m_docletTags;
    
    /** 
     * Whether to wrap runtime exceptions on server side to push them to the
     * client. Default is <code>true</code>.
     */
    protected boolean m_wrapRTExceptions = true;
    
    /**
     * @return Returns whether the service has to be realized as a stateful
     *      session bean.
     */
    public boolean isStateful() {
        return m_stateful;
    }

    /**
     * Sets whether the service should be realized as a stateful or stateless
     * session bean.
     * 
     * @param isStateful
     *      <code>true</code> for a stateful session bean, <code>false</code>
     *      otherwise.
     */
    public void setStateful(boolean isStateful) {
        this.m_stateful = isStateful;
    }

    /**
     * @return Returns the name of the method where <code>ejbActivate</code>
     *      calls are delegated to.
     */
    public String getActivate() {
        return m_activate;
    }

    /**
     * Sets the name of the method where <code>ejbActivate</code> invocations
     * are delegated to.
     * 
     * @param activate
     *      The name of the method to invoke on a <code>ejbActivate</code> call.
     */
    public void setActivate(String activate) {
        m_activate = activate;
    }

    /**
     * @return Returns the name of the method where
     *      <code>afterCommpletion</code> calls are delegated to.
     */
    public String getAfterCompletion() {
        return m_afterCompletion;
    }

    /**
     * Sets the name of the method where <code>afterCommpletion</code>
     * invocations are delegated to.
     * 
     * @param afertCompletion
     *      The name of the method to invoke on a <code>afterCommpletion</code>
     *      call.
     */
    public void setAfterCompletion(String afertCompletion) {
        m_afterCompletion = afertCompletion;
    }

    /**
     * @return Returns the name of the method where <code>afterBegin</code>
     *      calls are delegated to.
     */
    public String getAfterBegin() {
        return m_afterBegin;
    }

    /**
     * Sets the name of the method where <code>afterBegin</code> invocations
     * are delegated to.
     * 
     * @param afterBegin
     *      The name of the method to invoke on a <code>afterBegin</code> call.
     */
    public void setAfterBegin(String afterBegin) {
        m_afterBegin = afterBegin;
    }

    /**
     * @return Returns the name of the method where
     *      <code>beforeCompletion</code> calls are delegated to.
     */
    public String getBeforeCompletion() {
        return m_beforeCompletion;
    }

    /**
     * Sets the name of the method where <code>ejbActivate</code> invocations
     * are delegated to.
     * 
     * @param beforeComplete
     *      The name of the method to invoke on a <code>ejbActivate</code> call.
     */
    public void setBeforeCompletion(String beforeComplete) {
        m_beforeCompletion = beforeComplete;
    }

    /**
     * @return Returns the name of the method where
     *      <code>create(Object[])</code> calls are delegated to.
     */
    public String getCreate() {
        return m_create;
    }

    /**
     * Sets the name of the method where <code>create(Object[])</code>
     * invocations are delegated to.
     * 
     * @param create
     *      The name of the method to invoke on a <code>create(Object[])</code>
     *      call.
     */
    public void setCreate(String create) {
        m_create = create;
    }

    /**
     * @return Returns the name of the method where <code>ejbPassivate</code>
     *      calls are delegated to.
     */
    public String getPassivate() {
        return m_passivate;
    }

    /**
     * Sets the name of the method where <code>ejbPassivate</code> invocations
     * are delegated to.
     * 
     * @param passivate
     *      The name of the method to invoke on a <code>ejbPassivate</code>
     *      call.
     */
    public void setPassivate(String passivate) {
        m_passivate = passivate;
    }

    /**
     * @return Returns the list of objects used in the session bean's
     *      <code>create(Object[])</code> method.
     */
    public List getCreateArgument() {
        return m_createArgument;
    }

    /**
     * Sets the list of objects used for initialization in the session bean.
     * 
     * @param createArguments
     *      The list with create argument objects.
     */
    public void setCreateArgument(List createArguments) {
        m_createArgument = createArguments;
    }

    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        if (m_createArgument == null) {
            m_createArgument = Collections.EMPTY_LIST;
        }
    }

    /**
     * @return Returns <code>true</code> if a mapping for one of the three
     *      session synchronization methods has been defined. 
     */
    public boolean implementsSessionSynchronization() {
        return StringUtils.hasText(getAfterBegin())
                || StringUtils.hasText(getBeforeCompletion())
                || StringUtils.hasText(getAfterBegin());
    }

    /**
     * @return Returns the name of the method where
     *      <code>setSessionContext(SessionContext ctx)</code> calls are
     *      delegated to.
     */
    public String getSessionContext() {
        return m_sessionContext;
    }

    /**
     * Sets the name of the method where
     * <code>setSessionContext(SessionContext ctx)</code> invocations are
     * delegated to.
     * 
     * @param sessionContext
     *      The name of the method to invoke on a
     *      <code>setSessionContext(SessionContext ctx)</code> call.
     */
    public void setSessionContext(String sessionContext) {
        m_sessionContext = sessionContext;
    }

    /**
     * @return Returns the name of the method where <code>ejbRemove</code>
     *      calls are delegated to.
     */
    public String getRemove() {
        return m_remove;
    }

    /**
     * Sets the name of the method where <code>ejbRemove</code> invocations are
     * delegated to.
     * 
     * @param remove
     *      The name of the method to invoke on a <code>ejbRemove</code> call.
     */
    public void setRemove(String remove) {
        m_remove = remove;
    }
    
    /**
     * @return Returns a map with XDoclet tags. The key of an entry is
     *      <ul>
     *          <li><b>class</b> to set XDoclet tags on class level.</li>
     *          <li><b>null</b> to set XDoclet tags that are applied to all
     *              methods</li>
     *          <li><b>&lt;method name&gt;</b> to set XDoclet tags that are
     *              applied to methods with the given name only.</li>
     *          <li><b>&lt;method_name(fqn_type_1, ..., fqn_type_n)</b> to set
     *              XDoclet tags that are applied on the method with the given
     *              signature only.</li>
     *      </ul>
     *      'class'
     */
    public Map getDocletTags() {
        return m_docletTags;
    }

    /**
     * Sets the map with XDoclet tags. The key of an entry is
     * <ul>
     *     <li><b>class</b> to set XDoclet tags on class level.</li>
     *     <li><b>null</b> to set XDoclet tags that are applied to all
     *         methods</li>
     *     <li><b>&lt;method name&gt;</b> to set XDoclet tags that are
     *         applied to methods with the given name only.</li>
     *     <li><b>&lt;method_name(fqn_type_1, ..., fqn_type_n)</b> to set
     *         XDoclet tags that are applied on the method with the given
     *         signature only.</li>
     * </ul>
     * 
     * @param xDocletTags
     *      The map to set.
     */
    public void setDocletTags(Map xDocletTags) {
        m_docletTags = xDocletTags;
    }

    /**
     * @return Returns the wrapRTExceptions.
     */
    public boolean isWrapRTExceptions() {
        return m_wrapRTExceptions;
    }

    /**
     * @param wrapRTExceptions The wrapRTExceptions to set.
     */
    public void setWrapRTExceptions(boolean wrapRTExceptions) {
        m_wrapRTExceptions = wrapRTExceptions;
    }
}
