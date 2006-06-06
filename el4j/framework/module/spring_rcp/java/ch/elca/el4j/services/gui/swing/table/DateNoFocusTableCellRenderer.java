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
package ch.elca.el4j.services.gui.swing.table;

import java.text.DateFormat;

import javax.swing.JLabel;

import ch.elca.el4j.util.codingsupport.Reject;

/**
 * Table cell renderer for dates.
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
public class DateNoFocusTableCellRenderer
    extends DefaultNoFocusTableCellRenderer {
    /**
     * Is the date format used to display the date.
     */
    private DateFormat m_dateFormat;
    
    /**
     * Default constructor.
     */
    public DateNoFocusTableCellRenderer() {
        this(DateFormat.getDateInstance());
    }
    
    /**
     * Constructor with date format.
     * 
     * @param dateFormat Is the used date format to convert dates to strings.
     */
    public DateNoFocusTableCellRenderer(DateFormat dateFormat) {
        Reject.ifNull(dateFormat);
        m_dateFormat = dateFormat;
        setHorizontalAlignment(JLabel.CENTER);
    }
    
    /**
     * @return Returns the dateFormat.
     */
    public final DateFormat getDateFormat() {
        return m_dateFormat;
    }

    /**
     * @param dateFormat The dateFormat to set.
     */
    public final void setDateFormat(DateFormat dateFormat) {
        m_dateFormat = dateFormat;
    }

    /**
     * {@inheritDoc}
     */
    protected Object convertValueObject(Object value) {
        Object result = value;
        if (value != null && m_dateFormat != null) {
            result = m_dateFormat.format(value);
        }
        return result;
    }
}
