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

package ch.elca.el4j.tests.core.io.support;

import ch.elca.el4j.core.io.support.ManifestOrderedConfigLocationProvider;

/**
 * This test checks whether the set of given configuration files is found
 * by the configuration location provider.
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
public class ManifestOrderedConfigLocationProviderTest
    extends AbstractOrderTestCase {

    /** Some configuration files. */
    public static final String[] LOCATIONS = {
        "scenarios/core/io/support/mandatory/1.xml",
        "scenarios/core/io/support/mandatory/2.xml",
        "scenarios/core/io/support/mandatory/3.xml",
        "scenarios/core/io/support/optional/a.xml",
        "scenarios/core/io/support/a.xml",
        "scenarios/core/io/support/ab.xml",
        "scenarios/core/io/support/b.xml"
    };
    
    /**
     * Tests whether all of the above configuration files are found.
     */
    public void testFindConfigFiles() {
        ManifestOrderedConfigLocationProvider provider
            = new ManifestOrderedConfigLocationProvider();
        String[] configLocations = provider.getConfigLocations();
        
        for (int i = 0; i < LOCATIONS.length; i++) {
            assertTrue("Missing '" + LOCATIONS[i] + "'",
                    containsStringEndingWith(LOCATIONS[i], configLocations));
        }
    }
}
