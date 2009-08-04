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
package ch.elca.el4j.services.gui.swing.cookswing.designgridlayout;

import org.w3c.dom.Element;

import ch.elca.el4j.services.gui.swing.cookswing.binding.NoAddValueHolder;

import net.java.dev.designgridlayout.DesignGridLayout;

import cookxml.core.DecodeEngine;
import cookxml.core.interfaces.Creator;


/**
 * A cookSwing creator for &lt;emptyrow&gt;s, rows inside a designgridlayout.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 */
public class EmptyRowCreator implements Creator {

	/** {@inheritDoc} */
	public Object create(String parentNS, String parentTag, Element elm,
		Object parentObj, DecodeEngine decodeEngine) throws Exception {
		
		if (parentObj == null || !(parentObj instanceof DesignGridLayout)) {
			return null;
		}
		
		((DesignGridLayout) parentObj).emptyRow();
		
		return new NoAddValueHolder<Object>(parentObj);
	}

	/** {@inheritDoc} */
	public Object editFinished(String parentNS, String parentTag, Element elm,
		Object parentObj, Object obj, DecodeEngine decodeEngine)
		throws Exception {
		
		return obj;
	}

}
