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

package ch.elca.el4j.core.config;

import java.util.Properties;

import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyOverrideConfigurer;
import org.springframework.util.StringUtils;


/**
 * This class allows overriding of Spring configurations using JNDI to retrieve
 * values to override. The JNDI entries may be marked with a special prefix or
 * a distinct JNDI context is used, which contains only override properties.
 * This class allows configuring web or EJB applications at deploy time.
 * 
 * <p>This class falls back on the {@link
 * org.springframework.beans.factory.config.PropertyOverrideConfigurer}'s
 * behaviour, if there are any JNDI-related problems.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Andreas Bur (ABU)
 * @see ch.elca.el4j.core.config.JndiPropertyPlaceholderConfigurer
 */
public class JndiPropertyOverrideConfigurer extends PropertyOverrideConfigurer {

    /** The default key prefix that signals an override property. */
    public static final String DEFAULT_KEY_PREFIX = "springConfig.";
    
    /** The JNDI configuration helper. */
    private JndiConfigurationHelper m_jndiPropertyConfigurationgHelper;
    
    /** The prefix of JNDI resources we're interested in. */
    private String m_keyPrefix;
    
    /**
     * @return Returns the prefix of JNDI resources we're interested in.
     */
    public String getKeyPrefix() {
        if (!StringUtils.hasText(m_keyPrefix)) {
            m_keyPrefix = DEFAULT_KEY_PREFIX;
        }
        return m_keyPrefix;
    }

    /**
     * Sets the prefix of JNDI resources we're interested in. Default is
     * <code>springConfig.</code>.
     * 
     * @param keyPrefix
     *      The JNDI name prefix.
     */
    public void setKeyPrefix(String keyPrefix) {
        m_keyPrefix = keyPrefix;
    }
    
    /**
     * @return Returns the JNDI configuration helper. Default is
     *      {@link JndiConfigurationHelper}.
     */
    public JndiConfigurationHelper getJndiPropertyConfigurationgHelper() {
        if (m_jndiPropertyConfigurationgHelper == null) {
            m_jndiPropertyConfigurationgHelper
                = new JndiConfigurationHelper();
        } 
        return m_jndiPropertyConfigurationgHelper;
    }

    /**
     * Sets the JNDI configuration helper.
     * 
     * @param jndiPropertyConfigurationgHelper
     *      The JNDI configuration helper.
     */
    public void setJndiPropertyConfigurationgHelper(
            JndiConfigurationHelper jndiPropertyConfigurationgHelper) {
        m_jndiPropertyConfigurationgHelper = jndiPropertyConfigurationgHelper;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    protected void processProperties(
            ConfigurableListableBeanFactory beanFactory,
            Properties props) throws BeansException {
        
        logger.debug("process properties");
        Properties p = props;
        try {
            NamingEnumeration<NameClassPair> enumeration
                = getJndiPropertyConfigurationgHelper().getJndiTemplate().
                    list(getJndiPropertyConfigurationgHelper().getContext());
            p = createProperties(enumeration);
        } catch (NamingException nex) {
            logger.error("Unable to retrieve override properties through JNDI.",
                    nex);
        }
        
        super.processProperties(beanFactory, p);
    }
    
    /**
     * Iterates over all entries of the given JNDI context and extracts valid
     * configuration override properties.
     * 
     * @param enumeration
     *      The enumeration of all properties of the given JNDI context.
     *      
     * @return Returns Spring configuration override properties.
     * 
     * @throws NamingException
     *      If a naming exception is encountered.
     */
    protected Properties createProperties(NamingEnumeration<NameClassPair> enumeration)
        throws NamingException {
        
        Properties props = new Properties();
        JndiTemplate jndiTemplate
            = getJndiPropertyConfigurationgHelper().getJndiTemplate();
        while (enumeration.hasMore()) {
            NameClassPair next = (NameClassPair) enumeration.next();
            logger.debug("processing env-entry with name '"
                    + next.getName() + "'");
            
            if (isConfigProperty(next.getName())) {
                Object value = jndiTemplate.lookup(
                        getJndiPropertyConfigurationgHelper().
                            buildJndiResourceName(
                                    next.getName()));
                props.setProperty(
                        getPropertyName(next.getName()), value.toString());
                
                logger.info("using env-entry <" + next.getName()
                        + ", " + value + "> in override configuration");
            }
        }
        return props;
    }
    
    /**
     * Checks whether the given name represents a configuration property.
     * This is the case if the prefix of JNDI resources we're interested in is
     * empty (i.e. <code>null</code> or consisting of spaces only) or if the
     * name starts with teh given prefix.
     * 
     * @param name
     *      The name to check.
     *      
     * @return Returns <code>true</code> if it's a configuration property,
     *      <code>false</code> otherwise.
     */
    protected boolean isConfigProperty(String name) {
        return !StringUtils.hasText(getKeyPrefix()) 
            || name.startsWith(getKeyPrefix())
                && name.length() > getKeyPrefix().length();
    }
    
    /**
     * Computes the configuration property's name.
     * 
     * @param name
     *      A JNDI resource name that is a valid configuration property, i.e.
     *      that makes the {@link #isConfigProperty(String)} resulting in
     *      <code>true</code>.
     *      
     * @return Returns the name of the property used to override the Spring
     *      configuration.
     */
    protected String getPropertyName(String name) {
        String result;
        if (StringUtils.hasText(getKeyPrefix())) {
            result = name.substring(getKeyPrefix().length());
        } else {
            result = name;
        }
        return result;
    }
}
