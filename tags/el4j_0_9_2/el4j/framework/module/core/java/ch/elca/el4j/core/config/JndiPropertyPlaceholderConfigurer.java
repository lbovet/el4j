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

package ch.elca.el4j.core.config;

import java.util.Properties;

import javax.naming.NamingException;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;



/**
 * This subclass of {@link
 * org.springframework.beans.factory.config.PropertyPlaceholderConfigurer}
 * acts a bit like {@link
 * org.springframework.web.context.support.ServletContextPropertyPlaceholderConfigurer}
 * but searches for JNDI entries instead. This allows to configure an
 * application more easily at deployment time through the container's admin 
 * console for instance.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *   "$Revision$", "$Date$", "$Author$"
 * )</script>
 *
 * @author Sylvain Laurent (SLA)
 * @author Andreas Bur (ABU)
 * @see ch.elca.el4j.core.config.JndiPropertyOverrideConfigurer
 */
public class JndiPropertyPlaceholderConfigurer extends
    PropertyPlaceholderConfigurer {

    /** The JNDI configuration helper. */
    private JndiConfigurationHelper m_jndiPropertyConfigurationHelper;

    /**
     * @return Returns the JNDI configuration helper.
     */
    public JndiConfigurationHelper getJndiPropertyConfigurationHelper() {
        if (m_jndiPropertyConfigurationHelper == null) {
            m_jndiPropertyConfigurationHelper
                = new JndiConfigurationHelper();
        }
        return m_jndiPropertyConfigurationHelper;
    }
    
    /**
     * Sets the JNDI configuration helper. Default is
     * {@link JndiConfigurationHelper}.
     * 
     * @param jndiPropertyConfigurationHelper
     *      A JNDI configuration helper.
     */
    public void setJndiPropertyConfigurationHelper(
            JndiConfigurationHelper jndiPropertyConfigurationHelper) {
        m_jndiPropertyConfigurationHelper = jndiPropertyConfigurationHelper;
    }
    
    /**
     * {@inheritDoc}
     */
    protected String resolvePlaceholder(String placeholder, Properties props) {
        String result = null;
        try {
            result = getJndiPropertyConfigurationHelper().getJndiTemplate().
                lookup(getJndiPropertyConfigurationHelper().
                        buildJndiResourceName(placeholder)).toString();
        } catch (NamingException nex) {
            logger.error("Unable to resolve the placeholder '"
                    + placeholder + "' using JNDI. Fall back to property file."
                    , nex);
        }

        if (result == null) {
            result = super.resolvePlaceholder(placeholder, props);
        }
        return result;
    }
}
