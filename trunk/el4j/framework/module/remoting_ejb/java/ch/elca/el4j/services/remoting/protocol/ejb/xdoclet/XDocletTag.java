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

package ch.elca.el4j.services.remoting.protocol.ejb.xdoclet;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class represents an XDoclet tag. It allows to parse string
 * representations and helps merging multiple tags.
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
public class XDocletTag {

    /** The delimiter between two attributes. */
    public static final String DELIMITER = " ";
    
    /** The quote char. */
    public static final String QUOTES = "\"";
    
    /** The assignment operator. */
    public static final String XDOCLET_PARAMETER_ASSIGNMENT = "=";
    
    /** The XDoclet tag's prefix. */
    public static final String XDOCLET_START_SEQUENCE = "@";
    
    /** The static logger. */
    private static Log s_logger = LogFactory.getLog(XDocletTag.class);
    
    /** The map containing all &lt;attribute, value&gt; pairs. */
    private LinkedHashMap m_parameterMap = new LinkedHashMap();

    /** The XDoclet tag's name. */
    private String m_tagName;
    
    /** Creates an empty, new instance. */
    public XDocletTag() {
        // do nothing
    }
    
    /**
     * Creates a new tag instance and parses the given string.
     * 
     * @param tag
     *      A string representation of an XDoclet tag.
     *      
     * @throws XDocletException
     *      Invalid tag string format.
     */
    public XDocletTag(String tag) throws XDocletException {
        if (!tag.startsWith(XDOCLET_START_SEQUENCE)) {
            throw new XDocletException("Invalid XDoclet tag: Does not start "
                    + "with " + XDOCLET_START_SEQUENCE + " (" + tag + ")");
        }
        
        // set tag's name
        int idx = tag.indexOf(DELIMITER);
        if (idx == -1) {
            // tag with no parameters
            m_tagName = tag;
            
        } else {
            m_tagName = tag.substring(0, idx);
            
            // parse parameters
            tag = tag.substring(idx + 1).trim();
            
            while (!"".equals(tag)) {
                idx = parameterEndIndex(tag);
                String next = tag.substring(0, idx);
                parseParameter(next);
                if (idx + 1 == tag.length()) {
                    break;
                }
                tag = tag.substring(idx + 1).trim();
            }
        }
    }
    
    /**
     * Computes the index within the given string, where the parameter ends.
     * 
     * @param parameters
     *      The string with the available parameters.
     *      
     * @return Returns the index of the next parameter's end.
     */
    private int parameterEndIndex(String parameters) {
        boolean inValue = false;
        byte[] b = parameters.getBytes();
        int i = 0;
        
        while (i < b.length) {
            if (!inValue) {
                if (b[i] == '"') {
                    inValue = true;
                }
            } else {
                if (b[i] == '\\') {
                    // step over escaped chars
                    i++;
                    
                } else if (b[i] == '\"') {
                    return i;
                }
            }
            i++;
        }
        return b.length - 1;
    }
    
    /**
     * Parses the given parameter and add's it to the map.
     * 
     * @param parameter
     *      The parameter to parse. Format: <code>parameterName="value"</code>
     *      
     * @throws XDocletException
     *      If a XDoclet tag cannot be parsed.
     */
    private void parseParameter(String parameter) throws XDocletException {
        int idx = parameter.indexOf(XDOCLET_PARAMETER_ASSIGNMENT);
        if (idx == -1) {
            throw new XDocletException("Invalid XDoclet parameter assignment. ("
                    + parameter + ")");
        }
        
        String key = parameter.substring(0, idx);
        String value = parameter.substring(idx + 2);
        Object old = m_parameterMap.put(key, value);
        
        if (old != null && s_logger.isWarnEnabled()) {
            logOverride(key, old, value);
        }
    }
    
    /**
     * Adds a parameter to the map.
     * 
     * @param name
     *      The parameter's name.
     *      
     * @param value
     *      The parameter's value.
     */
    public void addParameter(String name, String value) {
        m_parameterMap.put(name, value);
    }

    /**
     * Merges this tag with another tag by overriding values hold in this tag
     * by the other's values.
     * 
     * @param tag
     *      The tag with which this one is merged.
     *      
     * @return Returns this tag after it has been merged with the other one.
     * 
     * @throws XDocletException
     *      If a XDoclet tag cannot be parsed.
     */
    public XDocletTag mergeWithTag(XDocletTag tag) throws XDocletException {
        if (tag == null || !tag.getTagName().equals(m_tagName)) {
            throw new XDocletException("Cannot merge the two tags! They don't "
                    + "have the same name.");
        }
        LinkedHashMap parameterMap = tag.getParameterMap();
        for (Iterator iter = parameterMap.entrySet().iterator();
                iter.hasNext();) {
            
            Map.Entry next = (Map.Entry) iter.next();
            Object key = next.getKey();
            Object value = next.getValue();
            
            Object old = m_parameterMap.put(key, value);
            if (old != null && s_logger.isWarnEnabled()) {
                logOverride(key, old, value);
            }
        }
        return this;
    }
    
    /**
     * Puts a override warn to the log.
     * 
     * @param key
     *      The overridden parameter's key.
     *      
     * @param oldValue
     *      The parameter's old value.
     *      
     * @param newValue
     *      The parameter's new value.
     */
    private void logOverride(Object key, Object oldValue, Object newValue) {
        s_logger.warn("Overrode XDoclet parameter '" + key + "'. Old value: '"
                + oldValue + "', new value: '" + newValue + "'.");
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer(m_tagName);
        for (Iterator iter = m_parameterMap.entrySet().iterator();
                iter.hasNext();) {
            
            Map.Entry next = (Map.Entry) iter.next();
            buffer.append(DELIMITER);
            buffer.append(next.getKey());
            buffer.append(XDOCLET_PARAMETER_ASSIGNMENT);
            buffer.append(QUOTES);
            buffer.append(next.getValue());
            buffer.append(QUOTES);
        }
        return buffer.toString();
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof XDocletTag)) {
            return false;
        }
        
        XDocletTag tag = (XDocletTag) obj;
        return getParameterMap().equals(tag.getParameterMap())
            && getTagName().equals(tag.getTagName());
    }
    
    /**
     * @return Returns the map containing all parameter-value bindings.
     */
    protected LinkedHashMap getParameterMap() {
        return m_parameterMap;
    }
    
    /**
     * @return Returns this tag's name.
     */
    public String getTagName() {
        return m_tagName;
    }
    
    /**
     * Sets the tag's name.
     * 
     * @param tagName
     *      The name to set.
     */
    public void setTagName(String tagName) {
        m_tagName = tagName;
    }
}
