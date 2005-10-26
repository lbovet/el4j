/*
 * EL4J, the Enterprise Library for Java, complementing Spring http://EL4J.sf.net
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

package ch.elca.el4j.tests.core.io.support;

import ch.elca.el4j.core.io.support.ManifestOrderedConfigLocationProvider;

/**
 * This test checks whether the set of given configuration files is found
 * by the configuration location provider.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
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
        "core/io/support/mandatory/1.xml",
        "core/io/support/mandatory/2.xml",
        "core/io/support/mandatory/3.xml",
        "core/io/support/optional/a.xml",
        "core/io/support/a.xml",
        "core/io/support/ab.xml",
        "core/io/support/b.xml"
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
