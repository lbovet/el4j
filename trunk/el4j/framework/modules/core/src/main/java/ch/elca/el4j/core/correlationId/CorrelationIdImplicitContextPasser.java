/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2010 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.core.correlationId;

import ch.elca.el4j.core.contextpassing.AbstractImplicitContextPasser;
import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;

/**
 * 
 * Context Passer for CorrelationId variable.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Simon Stelling (SST)
 */
public class CorrelationIdImplicitContextPasser extends AbstractImplicitContextPasser {
	
	/**
	 * The correlation id manager through which correlation ids are retrieved and set.
	 */
	private CorrelationIdManager correlationIdManager;
	
	/**
	 * @return Returns the correlationIdManager.
	 */
	public CorrelationIdManager getCorrelationIdManager() {
		return correlationIdManager;
	}

	/**
	 * @param correlationIdManager Is the correlationIdManager to set.
	 */
	public void setCorrelationIdManager(CorrelationIdManager correlationIdManager) {
		this.correlationIdManager = correlationIdManager;
	}

	/** {@inheritDoc} */
	@Override
	public String getImplicitlyPassedContext() {
		return correlationIdManager.getCurrentCorrelationId();
	}

	/** {@inheritDoc} */
	@Override
	public void pushImplicitlyPassedContext(Object context) {
		correlationIdManager.setCurrentCorrelationId((String) context);
	}
	
	/**
	 * checks correct setup.
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		CoreNotificationHelper.notifyLackingEssentialProperty(
			"correlationIdManager", this);
	}
	
}
