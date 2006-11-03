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

//Checkstyle: MagicNumber off

package ch.elca.el4j.tests.xmlmerge;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.jdom.Element;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import ch.elca.el4j.core.context.ModuleApplicationContext;
import ch.elca.el4j.services.xmlmerge.Configurer;
import ch.elca.el4j.services.xmlmerge.Matcher;
import ch.elca.el4j.services.xmlmerge.MergeAction;
import ch.elca.el4j.services.xmlmerge.XmlMerge;
import ch.elca.el4j.services.xmlmerge.action.CompleteAction;
import ch.elca.el4j.services.xmlmerge.action.OrderedMergeAction;
import ch.elca.el4j.services.xmlmerge.config.AttributeMergeConfigurer;
import ch.elca.el4j.services.xmlmerge.config.ConfigurableXmlMerge;
import ch.elca.el4j.services.xmlmerge.config.PropertyXPathConfigurer;
import ch.elca.el4j.services.xmlmerge.factory.XPathOperationFactory;
import ch.elca.el4j.services.xmlmerge.merge.DefaultXmlMerge;

import junit.framework.TestCase;

/**
 * 
 * This class tests several functionalities of the xml_merge module, using a
 * <code>DefaultXmlMerge</code> instance.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL:https://svn.sourceforge.net/svnroot/el4j/trunk/el4j/framework/modules/xml_merge/common/src/test/java/ch/elca/el4j/tests/xmlmerge/DefaultMergeTest.java $",
 *    "$Revision:1078 $",
 *    "$Date:2006-09-04 16:40:08 +0000 (Mo, 04 Sep 2006) $",
 *    "$Author:mathey $"
 * );</script>
 *
 * @author Laurent Bovet (LBO)
 * @author Alex Mathey (AMA)
 */
public class DefaultMergeTest extends TestCase {

    /**
     * New line.
     */
    public static final String NL = System.getProperty("line.separator");
    
    /**
     * Tests a simple merge of two strings.
     * @throws Exception If an error occurs during the test
     */
    public void testSimpleMerge() throws Exception {
        
        String xml1 = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"
            + "<root attr1=\"1\">"
            + "some "
            + "text"
            + "  <a attr=\"old\">"
            + "    <!-- this is a comment -->"
            + "      <xx>"
            + "        <yy/>"
            + "      </xx>"
            + "    <aa/>"
            + "  </a>"
            + "  <b/>"
            + "  <c/>"
            + "  <d/>"
            + "  <e>"
            + "    <f/>"
            + "  </e>"
            + "</root>";
        
        String xml2 = "<root attr2=\"2\">"
            + "other text"
            + "  <a attr=\"new\">"
            + "    <ab/>"
            + "    <!-- this is an other comment -->"
            + "      <xx>"
            + "        <zz/>"
            + "      </xx>"            
            + "    <aa/>"
            + "  </a>"
            + "  <c/>"
            + "  <b/>"
            + "  <d/>"
            + "  <g/>"
            + "</root>";

        String result = new DefaultXmlMerge().merge(new String[] {xml1,
            xml2});        
        
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NL
            +  "<root attr1=\"1\" attr2=\"2\">" + NL
            + "  some text other text" + NL
            + "  <a attr=\"new\">" + NL
            + "    <!-- this is a comment -->" + NL
            + "    <ab />" + NL
            + "    <!-- this is an other comment -->" + NL
            + "    <xx>" + NL
            + "      <yy />" + NL
            + "      <zz />" + NL
            + "    </xx>" + NL 
            + "    <aa />" + NL 
            + "  </a>" + NL
            + "  <c />" + NL
            + "  <b />" + NL
            + "  <c />" + NL
            + "  <d />" + NL
            + "  <e>" + NL
            + "    <f />" + NL
            + "  </e>" + NL 
            + "  <g />" + NL
            + "</root>";
        
        assertEquals(expected.trim(), result.trim());

    }
    
    /**
     * Tests a merge of three strings.
     * @throws Exception If an error occurs during the test
     */
    public void testThreeMerges() throws Exception {
        
        String[] sources = {
            "<root><a/></root>",
            "<root><a>hello</a></root>",
            "<root><a><b/></a></root>" };
        
        String result = new DefaultXmlMerge().merge(sources);
        
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NL
            + "<root>" + NL
            + "  <a>" + NL
            + "    hello" + NL
            + "    <b />" + NL
            + "  </a>" + NL
            + "</root>";
        
        assertEquals(expected.trim(), result.trim());
        
    }
              
    /**
     * Tests programmatic configuration of an XmlMerge instance, using an
     * XPathOperationFactory.
     * 
     * @throws Exception
     *             If an error occurs during the test
     */
    public void testXPathOperationFactory() throws Exception {
        
        String[] sources = {
            "<root><a/><c/></root>",
            "<root><a><b/></a><c><d/></c></root>" };
        
        XmlMerge xmlMerge = new DefaultXmlMerge();
        
        MergeAction mergeAction = new OrderedMergeAction();
                
        XPathOperationFactory factory = new XPathOperationFactory();
        factory.setDefaultOperation(new CompleteAction());

        Map map = new LinkedHashMap();
        map.put("/root/a", new OrderedMergeAction());

        factory.setOperationMap(map);

        mergeAction.setActionFactory(factory);

        xmlMerge.setRootMergeAction(mergeAction);

        String result = xmlMerge.merge(sources);
        
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NL
            + "<root>" + NL
            + "  <a>" + NL
            + "    <b />" + NL
            + "  </a>" + NL
            + "  <c />" + NL
            + "</root>";
        
        assertEquals(expected.trim(), result.trim());
    }
    
    /**
     * Tests configuration of an XmlMerge instance with XPath and Properties,
     * using a PropertyXPathConfigurer.
     * 
     * @throws Exception
     *             If an error occurs during the test
     */
    public void testPropertyXPathConfigurer() throws Exception {
        
        String[] sources = {
            "<root><a/><c/></root>",
            "<root><a><b/></a><c><d/></c></root>" };
        
        Properties props = new Properties();
        props.load(getClass().getResourceAsStream("test.properties"));        
        Configurer configurer = new PropertyXPathConfigurer(props);         
        XmlMerge xmlMerge = new ConfigurableXmlMerge(new DefaultXmlMerge(),
            configurer);
        
        String result = xmlMerge.merge(sources);
        
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NL
            + "<root>" + NL
            + "  <a>" + NL
            + "    <b />" + NL
            + "  </a>" + NL
            + "  <c />" + NL
            + "</root>";
        
        assertEquals(expected.trim(), result.trim());
    }
    
    /**
     * Tests the InsertAction in conjunction with the SkipMatcher, inserting the
     * patch elements after the original elements of the same tag in the result.
     * 
     * @throws Exception
     *             If an error occurs during the test
     */
    public void testInsertAction() throws Exception {
        
        String[] sources = {
            "<root><a id=\"a1\"/><b id=\"b1\"/>"
                + "<b id=\"b2\"/><c id=\"c1\"/></root>",
            "<root><a id=\"a2\"/><b id=\"b3\"/>"
                + "<b id=\"b4\"/><c id=\"c2\"/><d/></root>"};
        
        Properties props = new Properties();
        props.setProperty("action.default", "INSERT");
        props.setProperty("matcher.default", "SKIP");
        Configurer configurer = new PropertyXPathConfigurer(props);         
        XmlMerge xmlMerge = new ConfigurableXmlMerge(new DefaultXmlMerge(),
            configurer);
        
        String result = xmlMerge.merge(sources);
        
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NL
            + "<root>" + NL
            + "  <a id=\"a1\" />" + NL
            + "  <a id=\"a2\" />" + NL
            + "  <b id=\"b1\" />" + NL
            + "  <b id=\"b2\" />" + NL
            + "  <b id=\"b3\" />" + NL
            + "  <b id=\"b4\" />" + NL
            + "  <c id=\"c1\" />" + NL
            + "  <c id=\"c2\" />" + NL
            + "  <d />" + NL
            + "</root>";
        
        assertEquals(expected.trim(), result.trim());
    }
    
    /**
     * Tests the creation of an XML Spring Resource on-the-fly by merging XML
     * documents read from other resources.
     * 
     * @throws Exception
     *             If an error occurs during the test
     */
    public void testSpringResource() throws Exception {
        
        ApplicationContext appContext = new ModuleApplicationContext(
            new String[] { 
                "classpath*:mandatory/*.xml",
                "classpath*:etc/template/xmlmerge-config.xml"
            }, null, false, null);
        
        Resource r = (Resource) appContext.getBean("merged");

        InputStream in = r.getInputStream();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        int len;
        byte[] buffer = new byte[1024];
        while ((len = in.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
       
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NL
            + "<root>" + NL
            + "  <a>" + NL
            + "    <b />" + NL
            + "  </a>" + NL
            + "  <c />" + NL
            + "</root>";
       
        assertEquals(expected.trim(), bos.toString().trim());
    }
    
    /**
     * Tests a DTD merge of two source files using a DtdInsertAction: inserts
     * the patch elements in the result according to the order specified in the
     * original document's DTD.
     * 
     * @throws Exception
     *             If an error occurs during the test
     */
    public void testDtdMerge() throws Exception {
        
        InputStream[] streams 
            = new InputStream[] { 
                this.getClass().getResourceAsStream("sqlmap1.xml"), 
                this.getClass().getResourceAsStream("sqlmap2.xml") };
                
        
        File outputFile 
            = File.createTempFile("xml-merge-common-tests", "out2.xml");
        outputFile.deleteOnExit();
        
        OutputStream out = new FileOutputStream(outputFile);
        
        Properties props = new Properties();
        props.load(this.getClass().getResourceAsStream("test-dtd.properties"));
        
    
        XmlMerge xmlMerge = new ConfigurableXmlMerge(
            new PropertyXPathConfigurer(props));        
        InputStream in = xmlMerge.merge(streams);
        
        writeFromTo(in, out);
        
        in.close();                
        out.close();        
    }
    
    /**
     * Tests a merge of an element's attributes.
     * 
     * @throws Exception
     *             If an error occurs during the test
     */
    public void testAttributes() throws Exception {
        
        String[] sources = {
                
            "<root>"
                + " <a id='1' owk='3' bla='4' />"  
                + "</root>",

            "<root>"
                + " <a id='1' attr='2' ku='3' />"  
                + "</root>"
        };
        
        String result = new DefaultXmlMerge().merge(sources);
        
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NL
            + "<root>" + NL
            + "  <a id=\"1\" owk=\"3\" bla=\"4\" attr=\"2\" ku=\"3\" />" + NL
            + "</root>";
        
        assertEquals(expected.trim(), result.trim());
        
    }
    
    /**
     * Tests a merge using the IdentityMapper.
     * 
     * @throws Exception
     *             If an error occurs during the test
     */
    public void testIdMapper() throws Exception {
        
        String[] sources = {
            "<root>"
                + " <a id='a1'/>"
                + " <a id='b1'/>"
                + " <a id='b2'/>"
                + " <c id='c1'/>"
                + " <c id='c2'/>"
                + "</root>",
                
            "<root>"
                + " <a id='b1'>"
                + "   <b/>"
                + " </a>"
                + " <c id='c1' attr='2'/>"
                + "</root>" };

        Properties props = new Properties();        
        props.setProperty("matcher.default", "ID");
        Configurer configurer = new PropertyXPathConfigurer(props);         
        XmlMerge xmlMerge = new ConfigurableXmlMerge(configurer);
        
        String result = xmlMerge.merge(sources);
        
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NL
            + "<root>" + NL
            + "  <a id=\"a1\" />" + NL
            + "  <a id=\"b1\">" + NL
            + "    <b />" + NL
            + "  </a>" + NL
            + "  <a id=\"b2\" />" + NL
            + "  <c id=\"c1\" attr=\"2\" />" + NL            
            + "  <c id=\"c2\" />" + NL
            + "</root>";
        
        assertEquals(expected.trim(), result.trim());
    }
    
    /**
     * Tests configuration of an XmlMerge instance with inline attributes in the
     * patch document.
     * 
     * @throws Exception
     *             If an error occurs during the test
     */
    public void testAttributeMerge() throws Exception {
        
        String[] sources = {
            "<root>"
                + " <a>"
                + "  <b/>"
                + " </a>"
                + " <d/>"
                + " <e id='1'/>"
                + " <e id='2'/>"  
                + "</root>",
                
            "<root xmlns:merge='http://xmlmerge.el4j.elca.ch'>"
                + " <a merge:action='replace'>hello</a>"
                + " <c/>"
                + " <d merge:action='delete'/>"
                + " <e id='2' newAttr='3' merge:matcher='ID'/>"
                + "</root>"
        };
        
        
        String result = new ConfigurableXmlMerge(new AttributeMergeConfigurer())
            .merge(sources);
        
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NL
            + "<root>" + NL
            + "  <a>hello</a>" + NL
            + "  <c />" + NL
            + "  <e id=\"1\" />" + NL
            + "  <e id=\"2\" newAttr=\"3\" />" + NL 
            + "</root>";
        
        assertEquals(expected.trim(), result.trim());
        
    }
    
    /**
     * Writes from an InputStream to an OutputStream.
     * 
     * @param in
     *            The InputStream to read from
     * @param out
     *            The Outputstream to write to
     */
    private void writeFromTo(InputStream in, OutputStream out) {
        int len = 0;
        byte[] buffer = new byte[1024];

        try {
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }
    
    /**
     * 
     * This class is a custom matcher. With this matcher, the original and patch
     * elements match only if their tag name is "servlet-name".
     *
     * <script type="text/javascript">printFileStatus
     *   ("$URL:https://svn.sourceforge.net/svnroot/el4j/trunk/el4j/framework/modules/xml_merge/common/src/test/java/ch/elca/el4j/tests/xmlmerge/DefaultMergeTest.java $",
     *    "$Revision:1078 $",
     *    "$Date:2006-09-04 16:40:08 +0000 (Mo, 04 Sep 2006) $",
     *    "$Author:mathey $"
     * );</script>
     *
     * @author Laurent Bovet (LBO)
     * @author Alex Mathey (AMA)
     */
    public static class ServletNameMatcher implements Matcher {

        /**
         * {@inheritDoc}
         */
        public boolean matches(Element originalElement, Element patchElement) {
            String originalServletName = originalElement
                .getChildText("servlet-name");
            String patchServletName = patchElement.getChildText("servlet-name");

            return patchServletName != null && originalServletName != null
                && originalServletName.trim().equals(patchServletName.trim());
        }
    }

    /**
     * Tests a merge using a custom matcher.
     * 
     * @throws Exception
     *             If an error occurs during the test
     */
    public void testCustomMatcher() throws Exception {
        
        String[] sources = new String[] {
            "<web-app>"
                + "  <servlet>"
                + "    <servlet-name>"
                + "      hello"
                + "    </servlet-name>"
                + "    <servlet-class>"
                + "      test.HelloServlet"
                + "    </servlet-class>"
                + ""
                + "  </servlet>"
                + "  <servlet>"
                + "    <servlet-name>"
                + "      bye"
                + "    </servlet-name>"
                + "    <servlet-class>"
                + "      test.ByeServlet"
                + "    </servlet-class>"
                + " </servlet>"
                + ""
                + "  <servlet-mapping>  "
                + "  <servlet-name>"
                + "      hello"
                + "    </servlet-name>"
                + "    <url-pattern>"
                + "      /hello"
                + "    </url-pattern>"
                + "  </servlet-mapping>"
                + ""
                + "  <servlet-mapping>"
                + "    <servlet-name>"
                + "      bye"
                + "    </servlet-name>"
                + "    <url-pattern>"
                + "      /bye"
                + "    </url-pattern>"
                + "  </servlet-mapping>"
                + "</web-app> ",
            
            "<web-app>" 
                + "  <servlet>" 
                + "    <servlet-name>" 
                + "      bye" 
                + "    </servlet-name>" 
                + "<init-param>" 
                + "            <param-name>" 
                + "            message" 
                + "            </param-name>" 
                + "            <param-value>" 
                + "              Bye bye!" 
                + "            </param-value>" 
                + "         </init-param>" 
                + "" 
                + "  </servlet>" 
                + "      </web-app>"            
        };
        
        String conf = "xpath.path1=/web-app/servlet" + NL
                + "matcher.path1=ch.elca.el4j.tests.xmlmerge."
                + "DefaultMergeTest$ServletNameMatcher" + NL
                + "xpath.path2=/web-app/servlet/servlet-name" + NL
                + "action.path2=PRESERVE" + NL
                + "xpath.path3=/web-app/servlet/init-param" + NL
                + "action.path3=INSERT";
                
        Configurer configurer = new PropertyXPathConfigurer(conf);         
        XmlMerge xmlMerge = new ConfigurableXmlMerge(configurer);
        
        String result = xmlMerge.merge(sources);
        
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NL
                + "<web-app>" + NL
                + "  <servlet>" + NL
                + "    <servlet-name>hello</servlet-name>" + NL
                + "    <servlet-class>test.HelloServlet</servlet-class>" + NL
                + "  </servlet>" + NL
                + "  <servlet>" + NL
                + "    <servlet-name>bye</servlet-name>" + NL
                + "    <servlet-class>test.ByeServlet</servlet-class>" + NL
                + "    <init-param>" + NL
                + "      <param-name>message</param-name>" + NL
                + "      <param-value>Bye bye!</param-value>" + NL
                + "    </init-param>" + NL
                + "  </servlet>" + NL
                + "  <servlet-mapping>" + NL
                + "    <servlet-name>hello</servlet-name>" + NL
                + "    <url-pattern>/hello</url-pattern>" + NL
                + "  </servlet-mapping>" + NL
                + "  <servlet-mapping>" + NL
                + "    <servlet-name>bye</servlet-name>" + NL
                + "    <url-pattern>/bye</url-pattern>" + NL
                + "  </servlet-mapping>" + NL
                + "</web-app>";        
        
        assertEquals(expected.trim(), result.trim());        
    }
        
}

//Checkstyle: MagicNumber on
