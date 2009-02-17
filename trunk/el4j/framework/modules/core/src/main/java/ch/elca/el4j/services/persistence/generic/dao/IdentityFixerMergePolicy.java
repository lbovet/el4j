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
package ch.elca.el4j.services.persistence.generic.dao;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

/**
 * This class represents a policy on how to merge object graphs in the identity fixer.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @see AbstractIdentityFixer
 * 
 * @author Andreas Rueedlinger (ARR)
 */
public class IdentityFixerMergePolicy {

	/** The update policy. */
	private UpdatePolicy m_updatePolicy;
	
	/** The objects to update, only set when <code>m_updatePolicy == UpdatePolicy.UPDATE_CHOSEN</code>. */
	private List<Object> m_objectsToUpdate;
	
	/** The collection update policy. */
	private CollectionUpdatePolicy m_collectionUpdatePolicy;
	
	/** Is Preparation allowed. */
	private boolean m_preparationAllowed;
	
	/**
	 *	A map of [updated -> anchor] used to correctly merge collections.
	 */
	private IdentityHashMap<Object, Object> m_hintMapping;
	
	/**
	 * Default constructor.
	 * Sets update policy to all, collection base: new when update,
	 * and preparation is allowed.
	 */
	protected IdentityFixerMergePolicy() {
		m_updatePolicy = UpdatePolicy.UPDATE_ALL;
		m_collectionUpdatePolicy = CollectionUpdatePolicy.NEW_WHEN_UPDATE;
		m_preparationAllowed = true;
		m_hintMapping = new IdentityHashMap<Object, Object>();
	}
	
	/**
	 * @return the update policy of this id fixer merge policy.
	 */
	public UpdatePolicy getUpdatePolicy() {
		return m_updatePolicy;
	}
	
	/**
	 * @return  The objects to update, 
	 *     only set when <code>getUpdatePolicy == UpdatePolicy.UPDATE_CHOSEN</code>.
	 */
	public List<Object> getObjectsToUpdate() {
		return m_objectsToUpdate;
	}
	
	/**
	 * @return The collection update policy.
	 */
	public CollectionUpdatePolicy getCollectionUpdatePolicy() {
		return m_collectionUpdatePolicy;
	}
	
	/**
	 * @return if preparation is allowed.
	 */
	public boolean isPreparationAllowed() {
		return m_preparationAllowed;
	}
	
	/**
	 * @return A map of [updated -> anchor] used to correctly merge collections.
	 */
	public IdentityHashMap<Object, Object> getHintMapping() {
		return m_hintMapping;
	}
	
	/**
	 * This enumeration describes how the identity fixer should handle object updates.
	 */
	public enum UpdatePolicy {
		/**
		 * Only complement the identity fixer representatives, 
		 * no update of the old objects. 
		 */
		NO_UPDATE,
		
		/**
		 * Update the specified objects only, do not touch the others.
		 * Add new objects. 
		 */
		UPDATE_CHOSEN,
		
		/**
		 * Update all the objects.
		 */
		UPDATE_ALL
		
	}
	
	/**
	 * This enumeration describes how the identity fixer should handle collection updates.
	 */
	public enum CollectionUpdatePolicy {
		/**
		 * Always take the old collection as base for constructing the merged collection.
		 * Old meaning the one already referenced by the representative of the id fixer.
		 */
		OLD_BASE,
		
		/**
		 * Always take the new collection as base for constructing the merged collection.
		 * New meaning the one given as argument to be merged.
		 */
		NEW_BASE,
		
		/**
		 * Take the new collection as base when the collection actually gets updated,
		 * meaning that the content is coming from the new collection.
		 * Take the old collection as base otherwise.
		 */
		NEW_WHEN_UPDATE
		
	}
	
	/**
	 * @return a policy forcing all objects to be updated.
	 */
	public static IdentityFixerMergePolicy reloadAllPolicy() {
		return new IdentityFixerMergePolicy();
	}
	
	/**
	 * @param objectsToUpdate the objects to be updated.
	 * @return a policy forcing only the specified objects to be updated,
	 *     leaving the rest untouched.
	 */
	public static IdentityFixerMergePolicy reloadObjectsPolicy(List<Object> objectsToUpdate) {
		IdentityFixerMergePolicy obj = new IdentityFixerMergePolicy();
		obj.m_updatePolicy = UpdatePolicy.UPDATE_CHOSEN;
		obj.m_objectsToUpdate = new ArrayList<Object>(objectsToUpdate);
		return obj;
	}
	
	/**
	 * @return a policy leaving all the objects of the previous graph untouched,
	 *      only extending it by the new objects.
	 */
	public static IdentityFixerMergePolicy extendOnlyPolicy() {
		IdentityFixerMergePolicy obj = new IdentityFixerMergePolicy();
		obj.m_updatePolicy = UpdatePolicy.NO_UPDATE;
		return obj;
	}
	
	/**
	 * @param hintMapping        the hintMapping [updated -> anchor] used to correctly merge collections.
	 * @return a policy forcing all objects to be updated.
	 */
	public static IdentityFixerMergePolicy reloadAllPolicy(IdentityHashMap<Object, Object> hintMapping) {
		IdentityFixerMergePolicy obj = new IdentityFixerMergePolicy();
		obj.m_hintMapping = hintMapping;
		return obj;
	}
	
	/**
	 * @param objectsToUpdate the objects to be updated.
	 * @param hintMapping        the hintMapping [updated -> anchor] used to correctly merge collections.
	 * @return a policy forcing only the specified objects to be updated,
	 *     leaving the rest untouched.
	 */
	public static IdentityFixerMergePolicy reloadObjectsPolicy(List<Object> objectsToUpdate,
		IdentityHashMap<Object, Object> hintMapping) {
		IdentityFixerMergePolicy obj = new IdentityFixerMergePolicy();
		obj.m_updatePolicy = UpdatePolicy.UPDATE_CHOSEN;
		obj.m_objectsToUpdate = new ArrayList<Object>(objectsToUpdate);
		obj.m_hintMapping = hintMapping;
		return obj;
	}
	
	/**
	 * @param hintMapping        the hintMapping [updated -> anchor] used to correctly merge collections.
	 * @return a policy leaving all the objects of the previous graph untouched,
	 *      only extending it by the new objects.
	 */
	public static IdentityFixerMergePolicy extendOnlyPolicy(IdentityHashMap<Object, Object> hintMapping) {
		IdentityFixerMergePolicy obj = new IdentityFixerMergePolicy();
		obj.m_updatePolicy = UpdatePolicy.NO_UPDATE;
		obj.m_hintMapping = hintMapping;
		return obj;
	}
	
	/**
	 * @param updatePolicy       the update policy to use.
	 * @param objectsToUpdate    the objectsToUpdate if UDPATE_CHOSEN policy is chosen above.
	 * @param collectionPolicy   the collection update policy, 
	 *                  which base has to be used constructing the merged collections.
	 * @param identical          is the anchor given identical to the object to be merged.
	 * @param preparationAllowed is object preparation allowed (eg. for unwrapping proxies).
	 * @param hintMapping        the hintMapping [updated -> anchor] used to correctly merge collections.
	 * @return the custom policy specified by the arguments.
	 */
	public static IdentityFixerMergePolicy customPolicy(UpdatePolicy updatePolicy, List<Object> objectsToUpdate,
		CollectionUpdatePolicy collectionPolicy, boolean identical, boolean preparationAllowed,
		IdentityHashMap<Object, Object> hintMapping) {
		
		IdentityFixerMergePolicy obj = new IdentityFixerMergePolicy();
		obj.m_updatePolicy = updatePolicy;
		if (updatePolicy == UpdatePolicy.UPDATE_CHOSEN) {
			obj.m_objectsToUpdate = new ArrayList<Object>(objectsToUpdate);
		} else {
			obj.m_objectsToUpdate = null;
		}
		obj.m_collectionUpdatePolicy = collectionPolicy;
		obj.m_preparationAllowed = preparationAllowed;
		obj.m_hintMapping = hintMapping;
		return obj;
	}
		
	
	
}

