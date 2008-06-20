/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.gui.swing.cookswing.designgridlayout;

import ch.elca.el4j.gui.swing.cookswing.binding.NoAddValueHolder;

import cookxml.core.DecodeEngine;
import cookxml.core.interfaces.Setter;

import zappini.designgridlayout.Row;


/**
 * This class is a cookXml setter, which sets the align attribute
 * of a row element.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Stefan Wismer (SWI)
 */
public class RowAlignSetter implements Setter {

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public void setAttribute(String ns, String tag, String attrNS, String attr,
        Object obj, Object value, DecodeEngine decodeEngine) throws Exception {
        
        if (obj != null && obj instanceof NoAddValueHolder) {
            if (value.equals("left")) {
                ((NoAddValueHolder<Row>) obj).getObject().left();
            } else if (value.equals("center")) {
                ((NoAddValueHolder<Row>) obj).getObject().center();
            } else if (value.equals("right")) {
                ((NoAddValueHolder<Row>) obj).getObject().right();
            }
        }
    }
}