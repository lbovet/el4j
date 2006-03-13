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

package ch.elca.el4j.tests.remoting.ejb;

import java.util.ArrayList;
import java.util.HashMap;

import ch.elca.el4j.services.remoting.RemotingServiceExporter;
import ch.elca.el4j.services.remoting.protocol.ejb.EjbConfigurationObject;
import ch.elca.el4j.services.remoting.protocol.ejb.xdoclet.XDocletException;
import ch.elca.el4j.services.remoting.protocol.ejb.xdoclet.XDocletTag;
import ch.elca.el4j.services.remoting.protocol.ejb.xdoclet.XDocletTagGenerator;
import ch.elca.el4j.services.remoting.protocol.ejb.xdoclet.XDocletTagSet;

import junit.framework.TestCase;

// Checkstyle: EmptyBlock off

/**
 * Tests the XDoclet tag support.
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
public class XDocletTagTest extends TestCase {

    /** A good sample tag. */
    public static final String WELL_FORMED_TAG1 = "@ejb.bean name=\"FooName\" "
            + "jndi-name=\"Foo\" type=\"Stateful\"";
    
    /** Another good sample tag. */
    public static final String WELL_FORMED_TAG2
        = "@ejb.bean type=\"Stateless\"";
    
    /** S sample tag without parameters. */
    public static final String WELL_FORMED_TAG3 = "@foo.bar";
    
    /** A bad tag (missing 'at'). */
    public static final String MALFORMED_TAG1 = "ejb.bean name=\"FooName\" "
        + "jndi-name=\"Foo\" type=\"Stateful\"";
    
    /** Another bad tag (missing parameter name). */
    public static final String MALFORMED_TAG2 = "@ejb.bean name\"FooName\" "
        + "jndi-name=\"Foo\" type=\"Stateful\"";
    
    /**
     * Tests the good tag.
     * 
     * @throws XDocletException
     *      This exception should never be thrown.
     */
    public void testGoodTag() throws XDocletException {
        XDocletTag tag = new XDocletTag(WELL_FORMED_TAG1);
        assertEquals("Tag is not parsed correctly.",
                WELL_FORMED_TAG1, tag.toString());
    }
    
    /**
     * Tests whether the bad tag is treated correctly (i.e. whether a
     * XDocletException is thrown).
     */
    public void testBadTag1() {
        try {
            new XDocletTag(MALFORMED_TAG1);
            fail("Didn't recognize bad tag.");
        } catch (XDocletException e) { }
    }
    
    /**
     * Tests whether the bad tag is treated correctly (i.e. whether a
     * XDocletException is thrown).
     */
    public void testBadTag2() {
        try {
            new XDocletTag(MALFORMED_TAG2);
            fail("Didn't recognize bad tag.");
        } catch (XDocletException e) { }
    }
    
    /**
     * Tests whether two tags are merged correctly.
     * 
     * @throws XDocletException
     *      This exception should never be thrown.
     */
    public void testMerge() throws XDocletException {
        XDocletTag tag1 = new XDocletTag(WELL_FORMED_TAG1);
        XDocletTag tag2 = new XDocletTag(WELL_FORMED_TAG2);
        tag1.mergeWithTag(tag2);
        assertEquals("Merged tag is invalid.", "@ejb.bean name=\"FooName\" "
            + "jndi-name=\"Foo\" type=\"Stateless\"", tag1.toString());
    }
    
    /**
     * Tests that tags with different names can't be merged.
     */
    public void testInvalidMerge() {
        try {
            // Assumption: instantiation does not throw any exceptions
            XDocletTag tag1 = new XDocletTag(WELL_FORMED_TAG1);
            XDocletTag tag3 = new XDocletTag(WELL_FORMED_TAG3);
            tag1.mergeWithTag(tag3);
            fail("Didn't recognize merge of two tags with different tag name.");
        } catch (XDocletException e) { }
    }
    
    /**
     * Integration test that uses a remoting service exporter with an according
     * configuration object.
     * 
     * @throws XDocletException
     *      This exception should never be thrown.
     */
    public void testStatefulBean() throws XDocletException {
        RemotingServiceExporter exporter = new RemotingServiceExporter();
        exporter.setServiceName("foobar");
        
        EjbConfigurationObject configObject = new EjbConfigurationObject();
        exporter.setProtocolSpecificConfiguration(configObject);
        configObject.setStateful(true);
        
        HashMap map = new HashMap();
        ArrayList list = new ArrayList();
        list.add("@ejb.dao class=\"foo.bar.test\" generate=\"true\"");
        list.add("@ejb.bean description=\"a short desc\"");
        list.add("@ejb.dao class=\"foo.bar.bar\" author=\"ELCA\"");
        map.put("class", list);
        
        configObject.setDocletTags(map);
        
        XDocletTagGenerator tagGen = new XDocletTagGenerator(exporter);
        XDocletTagSet tagSet = tagGen.getTagsForClass();
        
        assertEquals("", "/**\n * @ejb.bean name=\"foobarName\" jndi-name=\""
                + "foobar\" type=\"Stateful\" description=\"a short desc\"\n * "
                + "@ejb.dao class=\"foo.bar.bar\" generate=\"true\" "
                + "author=\"ELCA\"\n */", tagSet.toString());
    }
}
//Checkstyle: EmptyBlock on
