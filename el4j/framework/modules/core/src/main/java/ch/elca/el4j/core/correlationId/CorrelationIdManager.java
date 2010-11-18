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

/**
 * Provides the functionality to set and get the current correlation id.
 * Usually implementations will be available as Spring bean under the name
 * 'correlationIdManager'.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Simon Stelling (SST)
 */
public interface CorrelationIdManager {

	/**
	 * sets the current correlation id to the specified value.
	 * @param correlationId the new value
	 */
	public void setCurrentCorrelationId(String correlationId);
	
	/**
	 * generates a new correlation Id value and sets it as the new current value.
	 */
	public void createNewCorrelationId(); 
	
	/**
	 * Clears the correlation id.
	 * Subsequent calls to getCurrentCorrelationId() will return null until
	 * setCurrentCorrelationId() or createNewCorrelationId() are called.
	 */
	public void clearCurrentCorrelationId();
	
	/**
	 * @return the current correlation id
	 */
	public String getCurrentCorrelationId();
	
}
