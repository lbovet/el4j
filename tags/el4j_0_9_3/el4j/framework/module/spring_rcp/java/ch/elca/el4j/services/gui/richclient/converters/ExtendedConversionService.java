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


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.springframework.binding.convert.Converter;
import org.springframework.binding.convert.support.AbstractFormattingConverter;
import org.springframework.binding.convert.support.DefaultConversionService;
import org.springframework.binding.format.FormatterLocator;
import org.springframework.binding.format.support.SimpleFormatterLocator;

/**
 * Extended converter service. Support for sql types.
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
public class ExtendedConversionService extends DefaultConversionService {
    /**
     * Are the added converters.
     */
    protected final List m_addedConverters 
        = Collections.synchronizedList(new ArrayList());

    /**
     * Formatter locator.
     */
    private FormatterLocator m_formatterLocator = new SimpleFormatterLocator();
    
    /**
     * Default constructor. No default converters will be used.
     */
    public ExtendedConversionService() {
        super(false);
    }

    /**
     * Constructor.
     * 
     * @param registerDefaultConverters
     *            Marks if default converters should be registered.
     */
    public ExtendedConversionService(boolean registerDefaultConverters) {
        super(registerDefaultConverters);
    }

    /**
     * {@inheritDoc}
     */
    public void setConverters(Converter[] converters) {
        synchronized (m_addedConverters) {
            m_addedConverters.clear();
            for (int i = 0; i < converters.length; i++) {
                Converter converter = converters[i];
                m_addedConverters.add(converter);
            }
        }
        super.setConverters(converters);
    }
    
    /**
     * @return Returns the formatterLocator.
     */
    public FormatterLocator getFormatterLocator() {
        return m_formatterLocator;
    }

    /**
     * @param formatterLocator The formatterLocator to set.
     */
    public void setFormatterLocator(FormatterLocator formatterLocator) {
        m_formatterLocator = formatterLocator;
        synchronized (m_addedConverters) {
            Iterator it = m_addedConverters.iterator();
            while (it.hasNext()) {
                Converter converter = (Converter) it.next();
                if (converter instanceof AbstractFormattingConverter) {
                    AbstractFormattingConverter formattingConverter
                        = (AbstractFormattingConverter) converter;
                    formattingConverter.setFormatterLocator(formatterLocator);
                }
            }
        }
    }
}
