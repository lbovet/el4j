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
package ch.elca.el4j.tests.services.persistence.generic;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;
import ch.elca.el4j.services.persistence.generic.primarykey.PrimaryKeyGenerator;
import ch.elca.el4j.services.persistence.generic.primarykey.UuidPrimaryKeyGenerator;

/**
 * This is the unit test for <code>UuidPrimaryKeyGenerator</code>.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Jacques-Olivier Haenni (JOH)
 */
public class UuidPrimaryKeyGeneratorTest extends TestCase {

    /**
     * Tests the key size.
     */
    public void testKeySize() {
        String key = getPrimaryKeyGenerator().getPrimaryKey();
        assertEquals("The size of the PK is not correct.", 32, key.length());
    }

    /**
     * Tests whether generated keys are unique.
     */
    public void testKeyUnicity() {
        PrimaryKeyGenerator pkg = getPrimaryKeyGenerator();
        Set set = new HashSet();
        int count = 2000;
        for (int i = 0; i < count; i++) {
            set.add(pkg.getPrimaryKey());
        }
        assertEquals("Some generated keys were equals.", count, set.size());
    }

    /**
     * Checks the keys' format.
     *
     */
    public void testKeyFormat() {
        PrimaryKeyGenerator pkg = getPrimaryKeyGenerator();
        int count = 2000;
        for (int i = 0; i < count; i++) {
            String key = pkg.getPrimaryKey();
            assertEquals("The size of the PK is not correct.",
                    32, key.length());
            assertTrue("The key contains spaces.", key.indexOf(' ') == -1);
        }
    }

    /**
     * @return Returns a new key generator instance.
     */
    private PrimaryKeyGenerator getPrimaryKeyGenerator() {
        return new UuidPrimaryKeyGenerator();
    }
}