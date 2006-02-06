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
package ch.elca.el4j.services.gui.richclient.converters;

import org.springframework.binding.convert.support.AbstractConverter;

import ch.elca.el4j.util.codingsupport.Reject;

/**
 * Converter for booleans to strings.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public class BooleanToText extends AbstractConverter {
    /**
     * Default string value for <code>true</code>. 
     */
    public static final String DEFAULT_TRUE_STRING = "yes";

    /**
     * Default string value for <code>false</code>. 
     */
    public static final String DEFAULT_FALSE_STRING = "no";

    /**
     * String representation for <code>true</code>.
     */
    private String m_trueString = DEFAULT_TRUE_STRING;

    /**
     * String representation for <code>false</code>.
     */
    private String m_falseString = DEFAULT_FALSE_STRING;

    /**
     * @return Returns the falseString.
     */
    public final String getFalseString() {
        return m_falseString;
    }

    /**
     * @param falseString The falseString to set.
     */
    public final void setFalseString(String falseString) {
        m_falseString = falseString;
    }

    /**
     * @return Returns the trueString.
     */
    public final String getTrueString() {
        return m_trueString;
    }

    /**
     * @param trueString The trueString to set.
     */
    public final void setTrueString(String trueString) {
        m_trueString = trueString;
    }

    /**
     * {@inheritDoc}
     */
    public Class[] getSourceClasses() {
        return new Class[] {Boolean.class};
    }

    /**
     * {@inheritDoc}
     */
    public Class[] getTargetClasses() {
        return new Class[] {String.class};
    }

    /**
     * {@inheritDoc}
     */
    protected Object doConvert(Object source, Class targetClass) 
        throws Exception {
        Reject.ifFalse(source instanceof Boolean);
        Boolean b = (Boolean) source;
        String result = null;
        if (b != null) {
            result = b.booleanValue() ? getTrueString() : getFalseString();
        }
        return result;
    }
}