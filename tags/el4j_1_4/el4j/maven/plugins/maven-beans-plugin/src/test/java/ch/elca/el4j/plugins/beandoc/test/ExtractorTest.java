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
package ch.elca.el4j.plugins.beandoc.test;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import ch.elca.el4j.plugins.beans.ConfigurationExtractor;

import junit.framework.TestCase;

/**
 * Tests for extracting configuration from files.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author David Bernhard (DBD)
 */
public class ExtractorTest extends TestCase {

    /** Reading from java file. */
    public void testJava() throws Exception {
        Resource res = new ClassPathResource("SampleJavaFile.java");
        
        ConfigurationExtractor ex 
            = new ConfigurationExtractor(res.getFile()
                .getAbsoluteFile().toString());
        
        String[] expectedIncludes = {
            "classpath*:mandatory/*.xml",
            "classpath:demo/demo*.xml"
        };
        
        String[] expectedExcludes = {
            "classpath*:exclude-*.xml"
        };
        
        assertEquals(expectedIncludes.length, ex.getInclusive().length);
        assertEquals(expectedExcludes.length, ex.getExclusive().length);
        
        for (int i = 0; i < expectedIncludes.length; i++) {
            assertEquals(expectedIncludes[i], ex.getInclusive()[i]);
        }
        
        for (int i = 0; i < expectedExcludes.length; i++) {
            assertEquals(expectedExcludes[i], ex.getExclusive()[i]);
        }
    }
}
