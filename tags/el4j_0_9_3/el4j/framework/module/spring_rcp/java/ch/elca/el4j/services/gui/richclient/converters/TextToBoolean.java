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
import org.springframework.util.StringUtils;

import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * Converter for strings to booleans.
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
public class TextToBoolean extends AbstractConverter {
    /**
     * Are possible strings for boolean value <code>true</code>.
     */
    public static final String[] TRUE_VALUES 
        = {"true", "on", "yes", "y", "1"};

    /**
     * Are possible strings for boolean value <code>false</code>.
     */
    public static final String[] FALSE_VALUES 
        = {"false", "off", "no", "n", "0"};

    /**
     * Marks if default boolean string should be taken.
     */
    private boolean m_useDefaultBooleanStrings = true;
    
    /**
     * String to replesent boolean value <code>true</code>.
     */
    private String m_trueString;

    /**
     * String to replesent boolean value <code>false</code>.
     */
    private String m_falseString;

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
     * @return Returns the useDefaultBooleanStrings.
     */
    public final boolean isUseDefaultBooleanStrings() {
        return m_useDefaultBooleanStrings;
    }

    /**
     * @param useDefaultBooleanStrings The useDefaultBooleanStrings to set.
     */
    public final void setUseDefaultBooleanStrings(
        boolean useDefaultBooleanStrings) {
        m_useDefaultBooleanStrings = useDefaultBooleanStrings;
    }

    /**
     * {@inheritDoc}
     */
    public Class[] getSourceClasses() {
        return new Class[] {String.class};
    }
    
    /**
     * {@inheritDoc}
     */
    public Class[] getTargetClasses() {
        return new Class[] {Boolean.class};
    }

    /**
     * Checks if the given source string represents <code>true</code>.
     * 
     * @param source Is the source string to check.
     * @return Returns <code>true</code> if the given string represents it.
     */
    protected boolean isTrueString(String source) {
        boolean result = false;
        if (StringUtils.hasText(source)) {
            String text = source.trim();
            result = text.equalsIgnoreCase(getTrueString());
            if (isUseDefaultBooleanStrings()) {
                for (int i = 0; !result && i < TRUE_VALUES.length; i++) {
                    String s = TRUE_VALUES[i];
                    result = s.equalsIgnoreCase(text);
                }
            }
        }
        return result;
    }
    
    /**
     * Checks if the given source string represents <code>true</code>.
     * 
     * @param source Is the source string to check.
     * @return Returns <code>true</code> if the given string represents it.
     */
    protected boolean isFalseString(String source) {
        boolean result = false;
        if (StringUtils.hasText(source)) {
            String text = source.trim();
            result = text.equalsIgnoreCase(getFalseString());
            if (isUseDefaultBooleanStrings()) {
                for (int i = 0; !result && i < FALSE_VALUES.length; i++) {
                    String s = FALSE_VALUES[i];
                    result = s.equalsIgnoreCase(text);
                }
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    protected Object doConvert(Object source, Class targetClass) 
        throws Exception {
        Reject.ifFalse(source instanceof String);
        String text = (String) source;
        Object result = null;
        if (StringUtils.hasText(text)) {
            boolean trueString = isTrueString(text);
            boolean falseString = isFalseString(text);
            if (trueString && !falseString) {
                result = Boolean.TRUE;
            } else if (!trueString && falseString) {
                result = Boolean.FALSE;
            } else {
                CoreNotificationHelper.notifyMisconfiguration(
                    "Unable to convert string value '" 
                    + text + "' to Boolean.");
            }
        }
        return result;
    }
}