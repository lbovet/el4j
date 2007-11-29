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
package ch.elca.el4j.demos.rcp.helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.binding.format.Formatter;
import org.springframework.binding.format.InvalidFormatException;
import org.springframework.binding.format.support.AbstractFormatter;
import org.springframework.binding.format.support.DateFormatter;
import org.springframework.binding.format.support.SimpleFormatterFactory;
import org.springframework.util.StringUtils;

/**
 * 
 * 
 * Simple formatter factory that returns a custom date/time formatter.  
 * By default, this formatter is used to format all date fields in the 
 * application.
 *
 * Mostly taken from Spring RCP v.0.3 by Keith Donald
 * 
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author David Stefan (DST)
 */
public class SimpleAppFormatterFactory extends SimpleFormatterFactory {
    
    /**
     * {@inheritDoc}
     */
    public Formatter getDateTimeFormatter() {
        return new AppDateFormatter();
    }

    /**
     * Formatter for date fields in the application.
     * 
     * @author Larry and Geoffrey (by Keith)
     */
    class AppDateFormatter extends AbstractFormatter {

        /** Default Date format. */
        private final DateFormatter m_format = new DateFormatter(
            new SimpleDateFormat("dd.MM.yyyy"));

        /** Pattern to verify date contains full 4 digit year. */
        private final Pattern m_dmyPattern = Pattern
            .compile("[0-9]{1,2}.[0-9]{1,2}.[0-9]{4}");

        /**
         * {@inheritDoc}
         */
        protected String doFormatValue(Object value) {
            if (value == null) {
                return "";
            } else {
                return m_format.formatValue(value);
            }
        }

        /**
         * {@inheritDoc}
         */
        protected Object doParseValue(String formattedString, Class targetClass)
            throws InvalidFormatException, ParseException {
            String src = (String) formattedString;
            // If the user entered slashes, convert them to dashes
            if (src.indexOf('/') >= 0) {
                src = src.replace('/', '-');
            }
            Object value = null;
            if (StringUtils.hasText(src)) {

                Matcher matcher = m_dmyPattern.matcher(src);

                if (!matcher.matches()) {
                    throw new ParseException("Invalid date format: " + src, 0);
                }

                value = m_format.parseValue(src, Date.class);
            }
            return value;
        }
    }
}
