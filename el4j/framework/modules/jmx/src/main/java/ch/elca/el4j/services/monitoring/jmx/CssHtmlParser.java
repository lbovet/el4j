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

package ch.elca.el4j.services.monitoring.jmx;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.util.StringUtils;

import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;

/**
 * This MBean adds css information to a given html page.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public class CssHtmlParser implements CssHtmlParserMBean {
    /**
     * Contains the stylesheet content to add to html page.
     */
    private String m_stylesheetContent;
    
    /**
     * Constructor.
     * 
     * @param stylesheetPath
     *            Is the path to the needed stylesheet.
     * @throws IOException
     *             If reading stylesheet makes trouble.
     */
    public CssHtmlParser(String stylesheetPath) throws IOException {
        StringBuffer sb = new StringBuffer();
        
        File stylesheetFile = new File(stylesheetPath);
        if (stylesheetFile.exists()) {
            BufferedReader br 
                = new BufferedReader(new FileReader(stylesheetFile));
            String line = br.readLine();
            while (StringUtils.hasLength(line)) {
                sb.append(line);
                line = br.readLine();
            }
        } else {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            InputStream is = cl.getResourceAsStream(stylesheetPath);
            if (is != null && is.available() > 0) {
                int character;
                while ((character = is.read()) != -1) {
                    sb.append((char) character);
                }
            } else {
                CoreNotificationHelper.notifyMisconfiguration(
                    "Stylesheet '" + stylesheetPath + "' not found.");
            }
        }
        
        if (!StringUtils.hasText(sb.toString())) {
            CoreNotificationHelper.notifyMisconfiguration(
                "Stylesheet '" + stylesheetPath + "' has no content.");
        }
        
        StringBuffer stylesheetContent = new StringBuffer();
        stylesheetContent.append("<style type=\"text/css\"><!-- ");
        stylesheetContent.append(sb.toString());
        stylesheetContent.append(" --></style>");
        m_stylesheetContent = stylesheetContent.toString();
    }
    
    /**
     * {@inheritDoc}
     * 
     * Asks which action should be done for the given request uri string. If
     * <code>null</code> is returned the underlying parser takes action.
     */
    public String parseRequest(String s) {
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * Adds css content directly before the head end tag in html page. If no
     * head end tag exists, no css will be added. The lookup of the head end tag
     * is case insensitive.
     */
    public String parsePage(String s) {
        String answer = s;
        if (StringUtils.hasText(s)) {
            int insertionPoint = s.toLowerCase().indexOf("</head>");
            if (insertionPoint > 0) {
                StringBuffer sb = new StringBuffer();
                sb.append(s.substring(0, insertionPoint));
                sb.append(m_stylesheetContent);
                sb.append(s.substring(insertionPoint));
                answer = sb.toString();
            }
        }
        return answer;
    }
}
