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

package ch.elca.el4j.core.config;

import javax.naming.Context;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import org.springframework.jndi.JndiCallback;

/**
 * This JNDI template completes Spring's {@link
 * org.springframework.jndi.JndiTemplate}.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Andreas Bur (ABU)
 */
public class JndiTemplate extends org.springframework.jndi.JndiTemplate {

	/**
	 * Enumerates the names bound in the named context, along with the class
	 * names of objects bound to them.
	 *
	 * @param name
	 *      The name of the context to list.
	 *
	 * @return Returns an enumeration of the names and class names of the
	 *      bindings in this context. Each element of the enumeration is of type
	 *      {@link javax.naming.NameClassPair}.
	 *
	 * @throws NamingException
	 *      If a naming exception is encountered.
	 */
	@SuppressWarnings("unchecked")
	public NamingEnumeration<NameClassPair> list(final String name) throws NamingException {
		if (logger.isInfoEnabled()) {
			logger.debug("Listing JNDI objects with name [" + name + "]");
		}
		return (NamingEnumeration<NameClassPair>) execute(new JndiCallback() {
			public Object doInContext(Context ctx) throws NamingException {
				return ctx.list(name);
			}
		});
	}
	
	/**
	 * Enumerates the names bound in the named context, along with the objects
	 * bound to them.
	 *
	 * @param name
	 *      The name of the context to list.
	 *
	 * @return Returns an enumeration of the bindings in this context. Each
	 *      element of the enumeration is of type {@link javax.naming.Binding}.
	 *
	 * @throws NamingException
	 *      If a naming exception is encountered.
	 */
	@SuppressWarnings("unchecked")
	public NamingEnumeration<NameClassPair> listBindings(final String name)
		throws NamingException {
		
		if (logger.isInfoEnabled()) {
			logger.debug("Listing JNDI bindings with name [" + name + "]");
		}
		return (NamingEnumeration<NameClassPair>) execute(new JndiCallback() {
			public Object doInContext(Context ctx) throws NamingException {
				return ctx.listBindings(name);
			}
		});
	}
}
