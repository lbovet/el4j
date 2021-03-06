/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2008 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.demos.gui.TableCellRenderers;

import javax.swing.table.DefaultTableCellRenderer;

import org.joda.time.DateTime;

import ch.elca.el4j.util.codingsupport.JodaTimeUtils;

/**
 * 
 * This class is a TableCellRenderer that outputs JodaTime DateTimes nicely.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Daniel Thomas (DTH)
 */
public class JodaTimeTableCellRenderer extends DefaultTableCellRenderer {
	
	/**
	 * Default constructor.
	 */
	public JodaTimeTableCellRenderer() { super(); }
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setValue(Object value) {
		setText((value == null) ? "" : JodaTimeUtils.getDateTimeString((DateTime) value, "dd.MM.yy HH:mm"));
			
	}
}
