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

import java.util.Collection;

import org.apache.commons.collections.map.ReferenceMap;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.dao.DataAccessException;

import ch.elca.el4j.services.persistence.hibernate.dao.extent.ExtentCollection;
import ch.elca.el4j.services.persistence.hibernate.dao.extent.ExtentEntity;

/**
 * Extent Fetcher for JPA with underlying Hibernate implementation.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Simon Stelling (SST)
 */
public class JpaHibernateExtentFetcher implements ExtentFetcher {

	/** {@inheritDoc} */
	public void fetchExtentObject(Object object, ExtentEntity entity, ReferenceMap fetchedObjects)
		throws DataAccessException {
	
		Object[] nullArg = null;
		if (object == null || entity == null || fetchedObjects == null) {
			return;
		}
		fetchedObjects.put(object, entity);
		try {
			for (ExtentEntity ent : entity.getChildEntities()) {
				Object obj = ent.getMethod().invoke(object, nullArg);
				// Initialize the object if it is a proxy
				if (obj instanceof HibernateProxy && !Hibernate.isInitialized(obj)) {
					Hibernate.initialize(obj);
				}
				if (!fetchedObjects.containsKey(obj) || !fetchedObjects.get(obj).equals(ent)) {
					fetchExtentObject(obj, ent, fetchedObjects);
				}
			}
			
			// Fetch the collections. Since we assume batch fetching for collections
			for (ExtentCollection c : entity.getCollections()) {
				Collection<?> coll = (Collection<?>) c.getMethod().invoke(object, nullArg);
				if (coll != null) {
					for (Object o : coll) {
						// Initialize the object if it is a proxy
						if (o instanceof HibernateProxy && !Hibernate.isInitialized(o)) {
							Hibernate.initialize(o);
						}
						if (!fetchedObjects.containsKey(o) || !fetchedObjects.get(o).equals(c.getContainedEntity())) {
							fetchExtentObject(o, c.getContainedEntity(), fetchedObjects);
						}
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	
	}
	
}
