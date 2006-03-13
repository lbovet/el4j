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

package ch.elca.el4j.buildsystem.tools.env;

import java.util.Properties;

import ch.elca.el4ant.propgen.AbstractPropertyGenerator;

/**
 * This class merges a {@link java.util.Properties} object with the project
 * properties.
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
public class PropertiesMergePropertyGenerator
    extends AbstractPropertyGenerator {

    /** The properties to merge with the project properties. */
    private Properties m_properties;
    
    /**
     * Sets the properties which have to be merged with the project properties.
     * 
     * @param properties
     *      The properties to merge.
     */
    public void setProperties(Properties properties) {
        m_properties = properties;
    }

    /**
     * {@inheritDoc}
     */
    public String getGeneratorName() {
        return "Properties merger";
    }

    /**
     * {@inheritDoc}
     */
    public boolean generate(Properties p) {
        if (m_properties == null) {
            return false;
        } else {
            p.putAll(m_properties);
            return true;
        }
    }
}
