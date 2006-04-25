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

package ch.elca.el4j.core.config;

import org.springframework.util.StringUtils;

/**
 * This class simplifies the setup of JNDI.
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
public class JndiConfigurationHelper {
    
    /** The default JNDI context <code>java:comp/env</code>. */
    public static final String ENV_CONTEXT = "java:comp/env"; 
    
    /** The JNDI template. */
    private JndiTemplate m_jndiTemplate;
    
    /** The JNDI context. */
    private String m_context;

    /**
     * @return Returns the JNDI context.
     */
    public String getContext() {
        if (!StringUtils.hasText(m_context)) {
            return ENV_CONTEXT;
        }
        return m_context;
    }

    /**
     * Sets the JNDI context. Default is <code>java:comp/env</code>.
     * 
     * @param context
     *      The JNDI context.
     */
    public void setContext(String context) {
        m_context = context;
    }

    /**
     * @return Returns the JNDI template.
     */
    public JndiTemplate getJndiTemplate() {
        if (m_jndiTemplate == null) {
            m_jndiTemplate = new JndiTemplate();
        }
        return m_jndiTemplate;
    }

    /**
     * Sets the JNDI template. Default ist {@link JndiTemplate}.
     * 
     * @param jndiTemplate
     *      The JNDI template.
     */
    public void setJndiTemplate(JndiTemplate jndiTemplate) {
        m_jndiTemplate = jndiTemplate;
    }
    
    /**
     * Makes a context-relative JNDI resource name absolute.
     * 
     * @param name
     *      A relative JNDI resource name.
     *      
     * @return Returns the absolute representation of the given relative
     *      JNDI resource name.
     */
    public String buildJndiResourceName(String name) {
        String result;
        if (StringUtils.hasText(getContext())) {
            result = getContext() + "/" + name;
        } else {
            result = name;
        }
        return result;
    }
}
