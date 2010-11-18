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

import org.slf4j.MDC;

import ch.elca.el4j.services.persistence.generic.primarykey.UuidPrimaryKeyGenerator;

/**
 * 
 * Provides an implementation of the {@link CorrelationIdManager} which stores the
 * current correlation Id as the variable 'correlationId' in SLF4J's MDC.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Simon Stelling (SST)
 */
public class CorrelationIdManagerSlf4jImpl implements CorrelationIdManager {
	
	/**
	 * The variable name used to store the correlationId in SLF4J's MDC.
	 */
	private static final String CORRELATION_ID_VARNAME = "correlationId";
	
	/**
	 * UUID generator used to create new correlation ids.
	 */
	private static final UuidPrimaryKeyGenerator UUID_KEY_GENERATOR = new UuidPrimaryKeyGenerator();
	
	@Override
	public void createNewCorrelationId() {
		String correlationId = UUID_KEY_GENERATOR.getPrimaryKey();
		setCurrentCorrelationId(correlationId);
	}

	@Override
	public String getCurrentCorrelationId() {
		return MDC.get(CORRELATION_ID_VARNAME);
	}

	@Override
	public void setCurrentCorrelationId(String correlationId) {
		MDC.put(CORRELATION_ID_VARNAME, correlationId);
		
	}

	@Override
	public void clearCurrentCorrelationId() {
		MDC.remove(CORRELATION_ID_VARNAME);
	}

	
	
}
