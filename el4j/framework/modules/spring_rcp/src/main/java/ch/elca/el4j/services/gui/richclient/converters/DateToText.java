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

/**
 * Converter for dates and strings.
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
public class DateToText extends AbstractFormattingConverter {
    /**
     * Mark if empty source values are allowed.
     */
    private final boolean m_allowEmpty;

    /**
     * Converter for dates and strings.
     * 
     * @param formatterLocator Is the formatter locator.
     * @param allowEmpty Marks if the source value can be empty.
     */
    public DateToText(FormatterLocator formatterLocator, 
        boolean allowEmpty) {
        super(formatterLocator);
        m_allowEmpty = allowEmpty;
    }

    /**
     * {@inheritDoc}
     */
    public Class[] getSourceClasses() {
        return new Class[] {java.util.Date.class, Date.class, Time.class, 
            Timestamp.class};
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
        Object result = null;
        if (!m_allowEmpty || source != null) {
            Class sourceClass = source.getClass();
            Formatter formatter; 
            if (Date.class.isAssignableFrom(sourceClass)) {
                formatter = getFormatterLocator().getDateFormatter();
            } else if (Timestamp.class.isAssignableFrom(sourceClass)) {
                formatter = getFormatterLocator().getDateTimeFormatter();
            } else if (Time.class.isAssignableFrom(sourceClass)) {
                formatter = getFormatterLocator().getTimeFormatter();
            } else {
                formatter = getFormatterLocator().getDateTimeFormatter();
            }
            result = formatter.formatValue(source);
        } else {
            result = "";
        }
        return result;
    }
}