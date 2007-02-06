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
package ch.elca.el4j.demos.rcp.helpers.converter;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.springframework.binding.convert.ConversionContext;
import org.springframework.binding.convert.ConversionException;
import org.springframework.binding.convert.Converter;
/**
 * 
 * This class is ...
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
public class StringToSqlDataConverter implements Converter {
    
    /**
     * Formatter.
     */
    private SimpleDateFormat m_simpleFormatter 
        = new SimpleDateFormat("dd.MM.yyyy");
    
    /**
     * {@inheritDoc}
     * @throws  
     */
    public Object convert(Object obj, Class class1,
        ConversionContext conversioncontext) throws ConversionException {
        if (obj == null) {
            return null;
        } else {
            try {
                return new Date(m_simpleFormatter.parse((String) obj)
                    .getTime());
            } catch (ParseException e) {
                throw new ConversionException(e, class1);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public Class[] getSourceClasses() {
        return new Class[]{String.class};
    }

    /**
     * {@inheritDoc}
     */
    public Class[] getTargetClasses() {
        return new Class[]{Date.class};
    }

}
