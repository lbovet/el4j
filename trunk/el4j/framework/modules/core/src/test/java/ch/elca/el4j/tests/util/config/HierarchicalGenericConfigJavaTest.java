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

import junit.framework.TestCase;

//Checkstyle: MagicNumber off
//Checkstyle: EmptyBlock off


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
public class HierarchicalGenericConfigJavaTest extends TestCase {
    /***/
    public void testGenericConfig() {
        HierarchicalGenericConfig config = new DefaultConfig();
        
        assertTrue(config.get("ch.elca.el4j").equals("EL4J"));
        assertTrue(config.get("ch.elca.el4j.tests.core").equals("core"));
        assertTrue(config.getMap().size() == 6);
        
        HierarchicalGenericConfig subConfig
            = config.getSubConfig("ch.elca.el4j.default");
        assertTrue(subConfig.getMap().size() == 2);
        assertTrue(subConfig.get("a").equals("defaultA"));
        assertTrue(subConfig.get("b").equals("defaultB"));
        
        subConfig = config.getSubConfig("ch.elca.el4j.tests");
        
        assertTrue(subConfig.getMap().size() == 3);
        assertTrue(subConfig.get("core").equals("core"));
        assertTrue(subConfig.get("services").equals("services"));
        assertTrue(subConfig.get("util").equals("util"));
    }
    
    /***/
    public void testSpecificConfig() {
        HierarchicalGenericConfig config = new SpecificConfig();
        assertTrue(config.get("ch.elca.el4j").equals("EL4J"));
        assertTrue(config.get("ch.elca.el4j.tests.core").equals("core2"));
        assertTrue(config.getMap().size() == 7);
        
        HierarchicalGenericConfig subConfig
            = config.getSubConfig("ch.elca.el4j.default");
        assertTrue(subConfig.getMap().size() == 2);
        assertTrue(subConfig.get("a").equals("defaultA"));
        assertTrue(subConfig.get("b").equals("defaultB"));
        
        subConfig = config.getSubConfig("ch.elca.el4j.tests");
        
        assertTrue(subConfig.getMap().size() == 3);
        assertTrue(subConfig.get("core").equals("core2"));
        assertTrue(subConfig.get("services").equals("services"));
        assertTrue(subConfig.get("util").equals("util"));
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
//Checkstyle: EmptyBlock on
//Checkstyle: MagicNumber on