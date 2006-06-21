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

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.springframework.binding.convert.support.AbstractConverter;

/**
 * Fake converter for <code>java.sql.Date</code>, <code>java.sql.Time</code> and
 * <code>java.sql.Timestamp</code> to <code>java.util.Date</code>.
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
public class SqlTypeToDate extends AbstractConverter {
    /**
     * {@inheritDoc}
     */
    public Class[] getTargetClasses() {
        return new Class[] {java.util.Date.class};
    }

    /**
     * {@inheritDoc}
     */
    public Class[] getSourceClasses() {
        return new Class[] {Date.class, Time.class, Timestamp.class};
    }
    
    /**
     * {@inheritDoc}
     */
    protected Object doConvert(Object source, Class targetClass)
        throws Exception {
        return source;
    }
}
