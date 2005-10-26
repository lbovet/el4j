/*
 * EL4J, the Enterprise Library for Java, complementing Spring http://el4j.sf.net
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

package ch.elca.el4j.web.filter;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class for filtering xpf files. Depending on whether the browser is xslt
 * compatible or not, this filter generates a html page and sends it to the
 * client or lets the browser do the xslt transformation.
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
public final class XpfFilter implements Filter {

    /** The static logger. */
    protected static Log s_logger = LogFactory.getLog(XpfFilter.class);
    
    /**
     * List of browsers known to support XSLT. Currently, only IE 6.0 fully
     * supports it. Mozilla-based browser lack the support of "document.write".
     */
    private static String[] s_xsltCompatibleBrowsers = {/*"MSIE 6"*/};

    /** 
     * List of browsers known to be NOT compatible; for instance, Opera claims
     * to be IE 6.0 compatible, but doesn't support XSLT.
     */
    private static String[] s_xsltIncompatibleBrowsers = {"Opera"};

    private static final String DEFAULT_ENCODING = "UTF-8";
    
    private FilterConfig m_config;
    
    private String m_encoding = DEFAULT_ENCODING;
    
    private Templates m_xslTemplates;
    

    /**
     * Initialize xsl templates.
     */
    public void init(FilterConfig filterConfig) throws ServletException {
        m_config = filterConfig;

//      Get the charset from the config
        String charset = m_config.getInitParameter("encoding"); 

        // In case a charset is defined in web.xml, take this one, else take
        // the default one
        if (charset != null) {
            m_encoding = charset;
        }
        
        // Get the XSL stylesheet from the config
        String stylesheet = m_config.getInitParameter("stylesheet");
        String stylePath = m_config.getServletContext().getRealPath(stylesheet);
        Source styleSource = new StreamSource(stylePath);

        // Compile the XSL stylesheet once for all
        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            m_xslTemplates = factory.newTemplates(styleSource);
        } catch (TransformerConfigurationException tce) {
            s_logger.error("Exception during XSL transformation configuration",
                    tce);
            throw new ServletException(tce);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void destroy() {
        m_config = null;
    }

    /**
     * Apply the xpf filter. If the browser is not xslt compatible, this method
     * will generate the html page. Otherwise, the xslt transformation will be
     * handed to the browser.
     */
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Test if the client browser is able to make the XSL transformation by
        // itself
        if (!isBrowserXSLTCompatible(httpRequest.getHeader("user-agent"))) {
//            PrintWriter out = response.getWriter();
            //response.reset();
            CharResponseWrapper responseWrapper = new CharResponseWrapper(
                    httpResponse);
            chain.doFilter(request, responseWrapper);

            // Get response from servlet
            StringReader sr = new StringReader(new String(responseWrapper
                    .toString()));
            Source xmlSource = new StreamSource(sr);
            
            try {
                Transformer transformer = m_xslTemplates.newTransformer();
                CharArrayWriter caw = new CharArrayWriter();
                StreamResult result = new StreamResult(caw);

                transformer.transform(xmlSource, result);
                response.setContentLength(caw.toString().length());
                response.setContentType("text/html; charset=" + m_encoding);
                PrintWriter out = response.getWriter();
                out.write(caw.toString());
            } catch (Exception ex) {
                s_logger.error("Exception during XSL transformation", ex);
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    /**
     * Tests whether the client browser is able to perform XSLT transforms.
     * 
     * @param userAgent
     *            the browser's user agent string
     * @return True if the browser can perform the XSLT transforms
     */
    private boolean isBrowserXSLTCompatible(String userAgent) {

        // First check if the browser is clearly NOT compatible:
        for (int i = 0; i < s_xsltIncompatibleBrowsers.length; i++) {
            if (userAgent.indexOf(s_xsltIncompatibleBrowsers[i]) != -1) {
                return false;
            }
        }

        // Then, check if it is explicitly known as compatible:
        for (int i = 0; i < s_xsltCompatibleBrowsers.length; i++) {
            if (userAgent.indexOf(s_xsltCompatibleBrowsers[i]) != -1) {
                return true;
            }
        }

        // If no clear answer could be found, play it on the safe side:
        return false;
    }

    public class CharResponseWrapper extends HttpServletResponseWrapper {
        private CharArrayWriter output;

        public CharResponseWrapper(HttpServletResponse response) {
            super(response);
            output = new CharArrayWriter();
        }

        public PrintWriter getWriter() {
            return new PrintWriter(output);
        }

        public String toString() {
            return output.toString();
        }
    }

}