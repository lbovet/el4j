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
package ch.elca.el4j.services.persistence.jpa.dao.extentstrategies;

import org.apache.commons.collections.map.ReferenceMap;
import org.springframework.dao.DataAccessException;

import ch.elca.el4j.services.persistence.hibernate.dao.extent.ExtentEntity;

/**
 * Strategy interface for fetching extents.
 * 
 * Injected into GenericJpaDao by JpaExtentFetcherInjectorBeanPostProcessor.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Simon Stelling (SST)
 */
public interface ExtentFetcher {

	/**
	 * Sub-method of the extent-based fetching, steps
	 * through the entities and calls the required methods.
	 * 
	 * Implementations must be thread-safe.
	 * 
	 * @param object			the object to load in given extent
	 * @param entity			the extent entity
	 * @param fetchedObjects	the HashMap with all the already fetched objects
	 * 
	 * @throws DataAccessException
	 */
	public void fetchExtentObject(Object object, ExtentEntity entity, ReferenceMap fetchedObjects)
		throws DataAccessException;
	
}
