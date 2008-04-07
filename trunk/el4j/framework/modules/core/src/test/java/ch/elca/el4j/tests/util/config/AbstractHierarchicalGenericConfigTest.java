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
 * This class tests {@link HierarchicalGenericConfig}.
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
public abstract class AbstractHierarchicalGenericConfigTest extends TestCase {
    /***/
    public void testGenericConfig() {
        HierarchicalGenericConfig config = getDefaultConfig();
        
        assertTrue(config.get("ch.elca.el4j").equals("EL4J"));
        assertTrue(config.get("ch.elca.el4j.tests.core").equals("core"));
        assertTrue(config.getMap().size() == 8);
        
        HierarchicalGenericConfig subConfig
            = config.getSubConfig("ch.elca.el4j.default");
        assertTrue(subConfig.getMap().size() == 4);
        assertTrue(subConfig.getChildren().size() == 3);
        assertTrue(subConfig.get("a").equals("defaultA"));
        assertTrue(subConfig.get("b").equals("defaultB"));
        
        HierarchicalGenericConfig subsubConfig
            = (HierarchicalGenericConfig) subConfig.getChildren().get("c");
        assertTrue(subsubConfig.get("d").equals("defaultCD"));
        
        subConfig = config.getSubConfig("ch.elca.el4j.tests");
        
        assertTrue(subConfig.getMap().size() == 3);
        assertTrue(subConfig.get("core").equals("core"));
        assertTrue(subConfig.get("services").equals("services"));
        assertTrue(subConfig.get("util").equals("util"));
    }
    
    /***/
    public void testSpecificConfig() {
        HierarchicalGenericConfig config = getSpecificConfig();
        assertTrue(config.get("ch.elca.el4j").equals("EL4J"));
        assertTrue(config.get("ch.elca.el4j.tests.core").equals("core2"));
        assertTrue(config.getMap().size() == 9);
        
        HierarchicalGenericConfig subConfig
            = config.getSubConfig("ch.elca.el4j.default");
        assertTrue(subConfig.getMap().size() == 4);
        assertTrue(subConfig.get("a").equals("defaultA"));
        assertTrue(subConfig.get("b").equals("defaultB"));
        
        subConfig = config.getSubConfig("ch.elca.el4j.tests");
        
        assertTrue(subConfig.getMap().size() == 3);
        assertTrue(subConfig.get("core").equals("core2"));
        assertTrue(subConfig.get("services").equals("services"));
        assertTrue(subConfig.get("util").equals("util"));
    }
    
    /**
     * @return    the default configuration
     */
    protected abstract HierarchicalGenericConfig getDefaultConfig();
    
    /**
     * @return    the specific configuration
     */
    protected abstract HierarchicalGenericConfig getSpecificConfig();
}

//Checkstyle: EmptyBlock on
//Checkstyle: MagicNumber on
