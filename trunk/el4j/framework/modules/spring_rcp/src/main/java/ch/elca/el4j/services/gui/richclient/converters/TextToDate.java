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
package ch.elca.el4j.services.gui.richclient.converters;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.springframework.binding.convert.support.AbstractFormattingConverter;
import org.springframework.binding.format.Formatter;
import org.springframework.binding.format.FormatterLocator;
import org.springframework.util.StringUtils;

import ch.elca.el4j.util.codingsupport.Reject;

/**
 * Converter for strings to dates.
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
public class TextToDate extends AbstractFormattingConverter {
    /**
     * Mark if empty source values are allowed.
     */
    private final boolean m_allowEmpty;
    
    /**
     * Converter for strings and dates.
     * 
     * @param formatterLocator Is the formatter locator.
     * @param allowEmpty Marks if the source value can be empty.
     */
    public TextToDate(FormatterLocator formatterLocator, 
        boolean allowEmpty) {
        super(formatterLocator);
        m_allowEmpty = allowEmpty;
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
        return new Class[] {java.util.Date.class, Date.class, Time.class, 
            Timestamp.class};
    }

    /**
     * {@inheritDoc}
     */
    protected Object doConvert(Object source, Class targetClass)
        throws Exception {
        Reject.ifNull(targetClass);
        Reject.ifNotAssignableTo(source, String.class);
        String s = (String) source;
        Object result = null;
        if (!m_allowEmpty || StringUtils.hasText((String) source)) {
            Formatter formatter; 
            if (Date.class.isAssignableFrom(targetClass)) {
                formatter = getFormatterLocator().getDateFormatter();
            } else if (Timestamp.class.isAssignableFrom(targetClass)) {
                formatter = getFormatterLocator().getDateTimeFormatter();
            } else if (Time.class.isAssignableFrom(targetClass)) {
                formatter = getFormatterLocator().getTimeFormatter();
            } else {
                formatter = getFormatterLocator().getDateTimeFormatter();
            }
            result = formatter.parseValue(s, java.util.Date.class);
            result = convertToSqlType((java.util.Date) result, targetClass);
        }
        return result;
    }
    
    /**
     * Converts <code>java.util.Date</code> to <code>java.sql.Date</code>, 
     * <code>java.sql.Timestamp</code> or <code>java.sql.Time</code>.
     * 
     * @param utilDate Is the util date to convert.
     * @param targetClass Is the class to convert into.
     * @return Returns the converted date.
     * @throws Exception If any problem occurs.
     */
    protected Object convertToSqlType(java.util.Date utilDate, 
        Class targetClass) throws Exception {
        Reject.ifNull(targetClass);
        Object result = null;
        if (utilDate != null) {
            long millis = utilDate.getTime();
            if (Date.class.isAssignableFrom(targetClass)) {
                result = new Date(millis);
            } else if (Timestamp.class.isAssignableFrom(targetClass)) {
                result = new Timestamp(millis);
            } else if (Time.class.isAssignableFrom(targetClass)) {
                result = new Time(millis);
            } else {
                result = utilDate;
            }
        }
        return result;
    }

}