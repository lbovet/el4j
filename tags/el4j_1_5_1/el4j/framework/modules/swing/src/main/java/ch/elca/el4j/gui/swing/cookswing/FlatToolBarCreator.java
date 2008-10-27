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
package ch.elca.el4j.gui.swing.cookswing;

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.w3c.dom.Element;

import cookxml.core.DecodeEngine;
import cookxml.core.exception.CookXmlException;
import cookxml.core.exception.CreatorException;
import cookxml.core.interfaces.Creator;

/**
 * A cookSwing creator for &lt;flattoolbar&gt;s, a convenience {@link JToolBar}
 * having flat style.
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
public class FlatToolBarCreator implements Creator {

	/** {@inheritDoc} */
	public Object create(String parentNS, String parentTag, Element elm,
		Object parentObj, DecodeEngine decodeEngine) throws CreatorException {
		
		return new JToolBar();
	}

	/** {@inheritDoc} */
	public Object editFinished(String parentNS, String parentTag, Element elm,
		Object parentObj, Object obj, DecodeEngine decodeEngine)
		throws CookXmlException {
		
		JToolBar toolbar = (JToolBar) obj;
		
		// Checkstyle: MagicNumber off
		Border border = new EmptyBorder(2, 6, 2, 6);
		// Checkstyle: MagicNumber on
		
		for (Component child : toolbar.getComponents()) {
			if (child instanceof JButton) {
				JButton button = (JButton) child;
				button.setBorder(border);
				button.setVerticalTextPosition(JButton.BOTTOM);
				button.setHorizontalTextPosition(JButton.CENTER);
				button.setFocusable(false);
				button.setText("");
			}
		}
		
		return toolbar;
	}
}
