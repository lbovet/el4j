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

package ch.elca.el4j.services.remoting.protocol.ejb.xdoclet;


import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.elca.el4j.services.remoting.RemotingServiceExporter;
import ch.elca.el4j.services.remoting.protocol.Ejb;
import ch.elca.el4j.services.remoting.protocol.ejb.EjbConfigurationObject;
import ch.elca.el4j.util.codingsupport.ClassUtils;

/**
 * This class parses XDoclet definitions declared in an
 * {@link ch.elca.el4j.services.remoting.protocol.ejb.EjbConfigurationObject}.
 * 
 * <p/>Declaring additional XDoclet tags for a class is done using the
 * <code>class</code> key. Additional tags can be added to methods using their
 * (short) name or the (short) name and the methods parameter list. Using
 * <code>null</code> as key adds XDoclet tags to all methods.
 * 
 * <p/>Example:
 * <pre>
 * &lt;property name="docletTags"&gt;
 *     &lt;map&gt;
 *         &lt;entry key="class"&gt;
 *             &lt;value&gt;@ejb.util generate="logical"&lt;/value&gt;
 *         &lt;/entry&gt;
 *         &lt;entry&gt;
 *             &lt;key&gt;&lt;null /&gt;&lt;/key&gt;
 *             &lt;value&gt;@ejb.dao call="helloWorld"&lt;/value&gt;
 *         &lt;/entry&gt; 
 *         &lt;entry key="activate"&gt;
 *             &lt;value&gt;@jboss.persistence datasource="foo" read-only="bar"&lt;/value&gt;
 *         &lt;/entry&gt;
 *         &lt;entry key="beanAfterCompletion(boolean)"&gt;
 *             &lt;value&gt;@ejb.do-whatever foo="bar"&lt;/value&gt;
 *         &lt;/entry&gt;
 *     &lt;/map&gt;
 * &lt;/property&gt;
 * </pre>
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
public class XDocletTagGenerator {

    /** XDoclet's ejb bean tag. */
    public static final String EJB_BEAN_TAG = "@ejb.bean";
    
    /** XDoclet's ejb interface-method tag. */
    public static final String EJB_INTERFACE_METHOD_TAG
        = "@ejb.interface-method";
    
    /** XDoclet's view-type attribute. */
    public static final String EJB_METHOD_VIEW_TYPE = "view-type";
    
    /** XDoclet's view-type value. */
    public static final String EJB_METHOD_VIEW_TYPE_REMOTE = "remote";
    
    /** XDoclet's name parameter. */
    public static final String EJB_NAME_PARAMETER = "name";
    
    /** XDoclet's jndi-name parameter. */
    public static final String EJB_JNDINAME_PARAMETER = "jndi-name";
    
    /** XDoclet's type parameter. */
    public static final String EJB_TYPE_PARAMETER = "type";
    
    /** XDoclet's type parameter value. */
    public static final String EJB_SESSION_TYPE_STATEFUL = "Stateful";
    
    /** XDoclet's type parameter value. */
    public static final String EJB_SESSION_TUPE_STATELESS = "Stateless";
    
    /** Suffix added to EJB names. */
    public static final String EJB_NAME_SUFFIX = "Name";
    
    /** Designates a method. */
    public static final boolean IS_METHOD = true;
    
    /** Class file suffix. */
    public static final String CLASS_DESIGNATOR = "class";
    
    /** The static logger. */
    private static Log s_logger = LogFactory.getLog(
            XDocletTagGenerator.class);
    
    /** 
     * The remoting service exporter that is responsible for the service
     * described by the XDoclet tags.
     */
    private RemotingServiceExporter m_exporter;
    
    /**
     * Creates a new XDoclet tag parser instance.
     * 
     * @param exporter
     *      The remoting service exporter.
     */
    public XDocletTagGenerator(RemotingServiceExporter exporter) {
        m_exporter = exporter;
    }
    
    /**
     * @return Returns a tag set containing all XDoclet tags for the EJB bean
     *      referencing this tag generator.
     *      
     * @throws XDocletException
     *      Thrown if a XDoclet tag cannot be parsed.
     */
    public XDocletTagSet getTagsForClass() throws XDocletException {
        XDocletTagSet tagSet = new XDocletTagSet(!IS_METHOD);
        tagSet.add(createSessionBeanDefaultTag());
        addAdditionalTags(tagSet, CLASS_DESIGNATOR);
        return tagSet;
    }
    
    /**
     * Computes a tag set for the given method containing all XDoclet tags.
     * 
     * @param method
     *      The method which XDoclet tags have to be computed.
     *      
     * @return Returns a tag set with all XDoclet tags for the given method.
     * 
     * @throws XDocletException
     *      A XDoclet tag definition 
     */
    public XDocletTagSet getTagsForMethod(Method method)
        throws XDocletException {
        XDocletTagSet tagSet = new XDocletTagSet(IS_METHOD);
        tagSet.add(createMethodDefaultTag());
        // first add tags that are defined for all methods (key == null)
        addAdditionalTags(tagSet, null);
        // then, add tags defined for all methods having the same methods name
        addAdditionalTags(tagSet, method.getName());
        
        // get the vanilla method if context passing is activated
        // i.e. remove the map representing the context
        Ejb protocol = (Ejb) m_exporter.getRemoteProtocol();
        if (protocol.getImplicitContextPassingRegistry() != null) {
            Class inter = m_exporter.getServiceInterface();
            Class[] paramTypes = method.getParameterTypes();
            Class[] vanillaParamTypes = new Class[paramTypes.length - 1];
            for (int i = 0; i < vanillaParamTypes.length; i++) {
                vanillaParamTypes[i] = paramTypes[i];
            }
            try {
                method = inter.getMethod(method.getName(), vanillaParamTypes);
            } catch (Exception e) {
                s_logger.warn("Unable to get vanilla method.", e);
            }
        }
        
        // and last, add those tags that exactly match the given method
        addAdditionalTags(tagSet, ClassUtils.getMethodSignature(method));
        
        return tagSet;
    }
    
    /**
     * @return Returns the remoting exporter's configuration object.
     */
    protected EjbConfigurationObject getConfigurationObject() {
        return (EjbConfigurationObject) m_exporter.
                getProtocolSpecificConfiguration();
    }
    
    /**
     * Adds additional tags to the given tag set.
     * 
     * @param tagSet
     *      The tag set to add additional XDoclet tags.
     *      
     * @param key
     *      The additional tag's key.
     *      
     * @throws XDocletException
     *    Whenever the XDoclet string for the given key can not be parsed.  
     */
    protected void addAdditionalTags(XDocletTagSet tagSet, String key)
        throws XDocletException {
        s_logger.debug("Adding additional XDoclet tags for key " + key);
        Map tags = getConfigurationObject().getDocletTags();
        if (tags != null) {
            Object obj = tags.get(key);
            if (obj == null) {
                return;
                
            } else if (obj instanceof List) {
                List list = (List) obj;
                for (int i = 0; i < list.size(); i++) {
                    tagSet.add(new XDocletTag(list.get(i).toString()));
                }
                
            } else {
                tagSet.add(new XDocletTag(obj.toString()));
            }
        }
    }
    
    /**
     * @return Returns the default session bean XDoclet tag.
     */
    protected XDocletTag createSessionBeanDefaultTag() {
        XDocletTag tag = new XDocletTag();
        String serviceName = m_exporter.getServiceName();
        tag.setTagName(EJB_BEAN_TAG);
        tag.addParameter(EJB_NAME_PARAMETER,
                serviceName + EJB_NAME_SUFFIX);
        tag.addParameter(EJB_JNDINAME_PARAMETER, serviceName);
        tag.addParameter(EJB_TYPE_PARAMETER, 
                getConfigurationObject().isStateful() 
                    ? EJB_SESSION_TYPE_STATEFUL : EJB_SESSION_TUPE_STATELESS);
        return tag;
    }
    
    /**
     * @return Returns the default method XDoclet tag.
     */
    protected XDocletTag createMethodDefaultTag() {
        XDocletTag tag = new XDocletTag();
        tag.setTagName(EJB_INTERFACE_METHOD_TAG);
        tag.addParameter(EJB_METHOD_VIEW_TYPE, EJB_METHOD_VIEW_TYPE_REMOTE);
        return tag;
    }
}
