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

import javax.swing.JComponent;

import ch.elca.el4j.gui.swing.cookswing.binding.NoAddValueHolder;

import cookxml.core.DecodeEngine;
import cookxml.core.interfaces.Adder;

import zappini.designgridlayout.Row;

/**
 * This cookXML adder handles &lt;row&gt; elemts: They get added to the parent
 * designgridlayout.
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
public class RowAdder implements Adder {

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public boolean add(String parentNS, String parentTag, Object parent,
        Object child, DecodeEngine decodeEngine) throws Exception {
        
        if (parentTag.equals("row") && parent instanceof NoAddValueHolder) {
            // read span attribute
            String spanAttr = decodeEngine.getCurrentElement()
                .getAttribute("colspan");
            int span = 1;
            try {
                span = Integer.parseInt(spanAttr);
            } catch (NumberFormatException e) {
                span = 1;
            }
            
            // create row
            ((NoAddValueHolder<Row>) parent).getObject()
                .add((JComponent) child, span);
            return true;
        }
        return false;
    }

}
