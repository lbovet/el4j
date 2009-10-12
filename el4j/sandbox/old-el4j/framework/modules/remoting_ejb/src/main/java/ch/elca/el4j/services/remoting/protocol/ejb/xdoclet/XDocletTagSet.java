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

package ch.elca.el4j.services.remoting.protocol.ejb.xdoclet;

import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * This class implements a tag set that gathers XDoclet tags.
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
public class XDocletTagSet {

    /** The indent used for methods. */
    public static final String INDENT = "    ";
    
    /** The Javadoc comment's first line. */
    public static final String JAVADOC_FIRST_LINE = "/**\n";
    
    /** The Javadoc comment's intermediate lines. */
    public static final String JAVADOC_LINE_PREFIX = " * ";
    
    /** The Javadoc comment's last line. */
    public static final String JAVADOC_LAST_LINE = " */";
    
    /** The newline char. */
    public static final String NEWLINE = "\n";
    
    /** Map consisting of XDoclet tags. */
    private LinkedHashMap m_xDocletTags;
    
    /** Whether this tag set represents XDoclet tags for a method. */
    private boolean m_method;
    
    /**
     * Creates a new instance.
     * 
     * @param method
     *      Whether the tag set is used to describe a method or a class.
     */
    public XDocletTagSet(boolean method) {
        m_xDocletTags = new LinkedHashMap();
        m_method = method;
    }
    
    /**
     * Adds a XDocelt tag to the set.
     * 
     * @param tag
     *      The XDoclet tag to add.
     */
    public void add(XDocletTag tag) {
        if (m_xDocletTags.containsKey(tag.getTagName())) {
            // merge tags
            XDocletTag old = (XDocletTag) m_xDocletTags.get(tag.getTagName());
            try {
                old.mergeWithTag(tag);
            } catch (XDocletException e) {
                // cannot happen
            }
        } else {
            m_xDocletTags.put(tag.getTagName(), tag);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        if (m_method) {
            buffer.append(INDENT);
        }
        
        buffer.append(JAVADOC_FIRST_LINE);
        
        for (Iterator iter = m_xDocletTags.values().iterator();
                iter.hasNext();) {

            if (m_method) {
                buffer.append(INDENT);
            }
            buffer.append(JAVADOC_LINE_PREFIX);
            buffer.append(iter.next());
            buffer.append(NEWLINE);
        }
        
        if (m_method) {
            buffer.append(INDENT);
        }
        buffer.append(JAVADOC_LAST_LINE);
        return buffer.toString();
    }
}
