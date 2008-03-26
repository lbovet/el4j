/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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

import ch.elca.el4j.util.config.GenericConfig;

import junit.framework.TestCase;

// Checkstyle: MagicNumber off
// Checkstyle: EmptyBlock off


/**
 * This class tests {@link GenericConfig} using pure Java (no Spring).
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
public class GenericConfigJavaTest extends TestCase {

    /***/
    public void testGenericConfig() {
        GenericConfig config = new DefaultConfig();
        assertTrue(config.get("class").equals("DefaultConfig"));
        assertTrue(config.get("DefaultConfig").equals("DefaultConfig"));
    }
    
    /***/
    public void testSpecificConfig() {
        GenericConfig config = new SpecificConfig();
        assertTrue(config.get("class").equals("SpecificConfig"));
        assertTrue(config.get("SpecificConfig").equals("SpecificConfig"));
    }
    
    /***/
    public void testMoreSpecificConfig() {
        GenericConfig config = new MoreSpecificConfig();
        assertTrue(config.get("class").equals("MoreSpecificConfig"));
        assertTrue(config.get("MoreSpecificConfig")
            .equals("MoreSpecificConfig"));
        assertTrue(config.get("SpecificConfig").equals("SpecificConfig"));
    }
    
    /**
     * A default configuration class.
     */
    private class DefaultConfig extends GenericConfig {
        /**
         * Default constructor to insert default configuration.
         */
        public DefaultConfig() {
            add("class", "DefaultConfig");
            add("DefaultConfig", "DefaultConfig");
        }
    }
    
    /**
     * A specific configuration class.
     */
    private class SpecificConfig extends GenericConfig {
        /**
         * Default constructor to insert specific configuration.
         */
        public SpecificConfig() {
            add("class", "SpecificConfig");
            add("SpecificConfig", "SpecificConfig");
        }
    }
    
    /**
     * A even more specific configuration class.
     */
    private class MoreSpecificConfig extends SpecificConfig {
        /**
         * Default constructor to insert even more specific configuration.
         */
        public MoreSpecificConfig() {
            add("class", "MoreSpecificConfig");
            add("MoreSpecificConfig", "MoreSpecificConfig");
        }
    }
}
// Checkstyle: EmptyBlock on
// Checkstyle: MagicNumber on