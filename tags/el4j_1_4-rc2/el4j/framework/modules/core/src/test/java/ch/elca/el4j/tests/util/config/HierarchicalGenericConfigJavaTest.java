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
package ch.elca.el4j.tests.util.config;

import ch.elca.el4j.util.config.HierarchicalGenericConfig;


/**
 * This class tests {@link HierarchicalGenericConfigJavaTest}
 * using pure Java (no Spring).
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Stefan Wismer (SWI)
 */
public class HierarchicalGenericConfigJavaTest
    extends AbstractHierarchicalGenericConfigTest {
    
    /** {@inheritDoc} */
    @Override
    protected HierarchicalGenericConfig getDefaultConfig() {
        return new DefaultConfig();
    }
    
    /** {@inheritDoc} */
    @Override
    protected HierarchicalGenericConfig getSpecificConfig() {
        return new SpecificConfig();
    }
    /**
     * A default configuration class.
     */
    private class DefaultConfig extends HierarchicalGenericConfig {
        /**
         * Default constructor to insert default configuration.
         */
        public DefaultConfig() {
            add("ch.elca.el4j", "EL4J");
            add("ch.elca.el4j.default.a", "defaultA");
            add("ch.elca.el4j.default.b", "defaultB");
            add("ch.elca.el4j.default.c.d", "defaultCD");
            add("ch.elca.el4j.default.c.e", "defaultCE");
            add("ch.elca.el4j.tests.core", "core");
            add("ch.elca.el4j.tests.services", "services");
            add("ch.elca.el4j.tests.util", "util");
        }
    }
    
    /**
     * A specific configuration class.
     */
    private class SpecificConfig extends DefaultConfig {
        /**
         * Default constructor to insert specific configuration.
         */
        public SpecificConfig() {
            add("ch.elca", "elca");
            add("ch.elca.el4j.tests.core", "core2");
        }
    }
}
