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
package ch.elca.el4j.services.gui.swing.cookswing.binding;

import cookxml.cookxml.setter.VarSetter;
import cookxml.core.DecodeEngine;
import cookxml.core.exception.SetterException;

/**
 * This setter enhances the {@link VarSetter} so that it can set variables of
 * type {@link NoAddValueHolder}.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 */
public class NoAddVarSetter extends VarSetter {

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public void setAttribute(String ns, String tag, String attrNS, String attr,
		Object obj, Object value, DecodeEngine decodeEngine)
		throws SetterException {
		
		if (obj instanceof NoAddValueHolder) {
			if (value == null || !(value instanceof String)) {
				return;
			}
			String str = (String) value;
			if (str.length() == 0) {
				return;
			}
			decodeEngine.getVarLookup().setVariable(
				str, ((NoAddValueHolder) obj).getObject(), decodeEngine);
		} else {
			super.setAttribute(ns, tag, attrNS, attr, obj, value, decodeEngine);
		}
		
	}
}
