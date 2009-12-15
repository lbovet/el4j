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

package ch.elca.el4j.core.contextpassing;

/**
 * Implicit context passer interface.
 * Please refer to the documentation of the
 * remoting_and_interface_enrichment module more information on this.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Martin Zeltner (MZE)
 */
public interface ImplicitContextPasser {
	/**
	 * This method is called by the stub (e.g. ProxyBean) that makes a remote
	 * invocation to collect the implicitly passed context and add it to the
	 * invocation.
	 *
	 * @return The context that should be added to a method call.
	 */
	public Object getImplicitlyPassedContext();

	/**
	 * This method is called by the skeleton (e.g. ExporterBean) that receives a
	 * remote invocation to push the context to the bean.
	 *
	 * @param context
	 *            The received context that should be pushed to the service.
	 */
	public void pushImplicitlyPassedContext(Object context);
}
