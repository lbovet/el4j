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

import org.springframework.binding.convert.support.AbstractConverter;

import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * Converter for <code>java.util.Date</code> to <code>java.sql.Date</code>,
 * <code>java.sql.Time</code> and <code>java.sql.Timestamp</code>.
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
public class DateToSqlType extends AbstractConverter {
    /**
     * {@inheritDoc}
     */
    public Class[] getSourceClasses() {
        return new Class[] {java.util.Date.class};
    }

    /**
     * {@inheritDoc}
     */
    public Class[] getTargetClasses() {
        return new Class[] {Date.class, Time.class, Timestamp.class};
    }

    /**
     * {@inheritDoc}
     */
    protected Object doConvert(Object source, Class targetClass)
        throws Exception {
        if (source == null) {
            return null;
        }
        Reject.ifFalse(source instanceof java.util.Date);
        java.util.Date dateSource = (java.util.Date) source;
        Object result = null;
        if (Date.class.isAssignableFrom(targetClass)) {
            result = new Date(dateSource.getTime());
        } else if (Time.class.isAssignableFrom(targetClass)) {
            result = new Time(dateSource.getTime());
        } else if (Timestamp.class.isAssignableFrom(targetClass)) {
            result = new Timestamp(dateSource.getTime());
        } else {
            CoreNotificationHelper.notifyMisconfiguration(
                "Unsupported target class " + targetClass.getName() + ".");
        }
        return result;
    }
}
