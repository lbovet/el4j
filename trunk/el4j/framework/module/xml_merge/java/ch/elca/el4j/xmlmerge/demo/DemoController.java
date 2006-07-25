/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.xmlmerge.demo;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import ch.elca.el4j.xmlmerge.XmlMerge;
import ch.elca.el4j.xmlmerge.config.ConfigurableXmlMerge;
import ch.elca.el4j.xmlmerge.config.PropertyXPathConfigurer;

/**
 * Spring controller for the online demo.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$$URL$$",
 *    "$$Revision$$",
 *    "$$Date$$",
 *    "$$Author$$"
 * );</script>
 * 
 * @author Laurent Bovet (LBO)
 * @author Alex Mathey (AMA)
 */
public class DemoController implements Controller {

    /**
     * New line.
     */
    public static final String NL = System.getProperty("line.separator");
    
    /**
     * {@inheritDoc}
     */
    public ModelAndView handleRequest(HttpServletRequest request,
        HttpServletResponse response) throws Exception {

        String source1 = request.getParameter("source1");
        String source2 = request.getParameter("source2");
        String conf = request.getParameter("conf");
        
        if (source1 == null) {
             
            conf = "action.default=MERGE" + NL + NL
                + "xpath.1=/root/d" + NL
                + "action.1=REPLACE";
            
            source1 = "<root attr1=\"1\">" + NL
                + "  some text " + NL
                + "  <a attr=\"old\">" + NL
                + "    <!-- this is a comment -->" + NL
                + "    <ab />" + NL
                + "    <xx>" + NL
                + "      <yy />" + NL            
                + "    </xx>" + NL
                + "    <aa />" + NL
                + "  </a>" + NL          
                + "  <b />" + NL
                + "  <c />" + NL
                + "  <d attr1=\"bye\"/>" + NL
                + "  <e>" + NL
                + "    <f />" + NL
                + "  </e>" + NL
                + "</root>";
            
            source2 = "<root  attr2=\"2\">" + NL
                + "  , some other text " + NL
                + "  <a attr=\"new\">" + NL 
                + "    <ab />" + NL
                + "    <!-- this is another comment -->" + NL                
                + "    <xx>" + NL
                + "      <zz />" + NL            
                + "    </xx>" + NL
                + "    <aa />" + NL
                + "  </a>" + NL          
                + "  <c />" + NL 
                + "  <b />" + NL 
                + "  <d attr2=\"hello\"/>" + NL 
                + "  <g />" + NL                 
                + "</root>";            
        }
        
        XmlMerge xmlMerge = new ConfigurableXmlMerge(
            new PropertyXPathConfigurer(conf));

        String result = xmlMerge.merge(new String[] {source1, source2});

        Map model = new HashMap();

        model.put("source1", source1.trim());
        model.put("source2", source2.trim());
        model.put("conf", conf.trim());
        model.put("result", result.trim());

        return new ModelAndView("/demo.jsp", model);
    }

}
