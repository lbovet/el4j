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

package ch.elca.el4j.util.codingsupport;

import java.util.Iterator;
import java.util.Map;

/**
 * This class simplifies to use properties as first class configuration objects.
 * It allows to set a number of properties through spring's configuration
 * features:
 * <pre>
 * &lt;bean name="beanName" class="ch.elca.el4j.util.codingsupport.Properties"&gt;
 *     &lt;property name="properties"&gt;
 *         &lt;props&gt;
 *             &lt;prop key="java.naming.factory.initial"&gt;org.jnp.interfaces.NamingContextFactory&lt;/prop&gt;
 *             &lt;!-- more properties --&gt;
 *         &lt;/props&gt;
 *     &lt;/property&gt;
 * &lt;/bean&gt;
 * </pre>
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
public class Properties extends java.util.Properties {

    /**
     * Crates a new Properties instance.
     */
    public Properties() {
        super();
    }

    /**
     * Creates a new Properties instance and sets the default properties.
     * 
     * @param defaults
     *      The default properties to set.
     */
    public Properties(java.util.Properties defaults) {
        super(defaults);
    }

    /**
     * Copies all properties of the provided {@link java.util.Properties} object
     * into this instance.
     * 
     * @param props
     *      The properties to copy.
     */
    public void setProperties(java.util.Properties props) {
        for (Iterator iter = props.entrySet().iterator(); iter.hasNext();) {
            Map.Entry next = (Map.Entry) iter.next();
            put(next.getKey(), next.getValue());
        }
    }
}
