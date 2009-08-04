/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2008 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.services.persistence.hibernate;

import java.util.HashMap;
import java.util.HashSet;

import org.hibernate.collection.PersistentCollection;
import org.hibernate.collection.PersistentMap;
import org.hibernate.collection.PersistentSet;
import org.hibernate.event.PostCollectionRecreateEvent;
import org.hibernate.event.PostCollectionRecreateEventListener;
import org.hibernate.event.PostCollectionUpdateEvent;
import org.hibernate.event.PostCollectionUpdateEventListener;
import org.springframework.util.Assert;

import ch.elca.el4j.services.persistence.generic.dto.AbstractIntKeyIntOptimisticLockingDto;

/**
 * This Hibernate event listener refreshes all persisted sets and maps. This is a workaround for all entities based
 * on {@link AbstractIntKeyIntOptimisticLockingDto} because they change their hash code during the persistence process.
 * Therefore collections based on hash code have to be refreshed such that the new hash code is used. Otherwise inserted
 * entities won't be found anymore after persisting.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 */
public class HibernatePersistentCollectionRefresher implements PostCollectionRecreateEventListener,
	PostCollectionUpdateEventListener {
	
	/** {@inheritDoc} */
	public void onPostRecreateCollection(PostCollectionRecreateEvent event) {
		refreshCollection(event.getCollection());
	}

	/** {@inheritDoc} */
	public void onPostUpdateCollection(PostCollectionUpdateEvent event) {
		refreshCollection(event.getCollection());
	}
	
	/**
	 * Refresh a collection (reorganize it due to possibly changed hash codes).
	 * @param collection    the collection to update
	 */
	@SuppressWarnings("unchecked")
	private void refreshCollection(PersistentCollection collection) {
		Assert.isTrue(!collection.isDirty());
		
		if (collection instanceof PersistentSet) {
			PersistentSet set = (PersistentSet) collection;
			HashSet tmpSet = new HashSet(set);

			set.clear();
			set.addAll(tmpSet);
			set.postAction();
		} else if (collection instanceof PersistentMap) {
			PersistentMap map = (PersistentMap) collection;
			HashMap tmpMap = new HashMap(map);

			map.clear();
			map.putAll(tmpMap);
			map.postAction();
		}
	}
}
