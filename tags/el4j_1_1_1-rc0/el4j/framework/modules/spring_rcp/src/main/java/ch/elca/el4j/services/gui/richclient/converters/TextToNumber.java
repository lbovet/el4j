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

import java.math.BigDecimal;
import java.math.BigInteger;

import org.springframework.binding.convert.support.AbstractFormattingConverter;
import org.springframework.binding.format.FormatterLocator;
import org.springframework.util.StringUtils;

/**
 * Convertor for strings to numbers.
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
public class TextToNumber extends AbstractFormattingConverter {
    /**
     * Mark if empty source values are allowed.
     */
    private final boolean m_allowEmpty;

    /**
     * Converter for strings to numbers.
     * 
     * @param formatterLocator Is the formatter locator.
     * @param allowEmpty Marks if the source value can be empty.
     */
    public TextToNumber(FormatterLocator formatterLocator, 
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
        return new Class[] {Byte.class, Short.class, Integer.class, 
            Long.class, Float.class, Double.class, BigInteger.class, 
            BigDecimal.class};
    }

    /**
     * {@inheritDoc}
     */
    protected Object doConvert(Object source, Class targetClass) 
        throws Exception {
        return (!m_allowEmpty || StringUtils.hasText((String) source)) 
            ? getFormatterLocator().getNumberFormatter(
                targetClass).parseValue((String) source, targetClass) 
                : null;
    }
}