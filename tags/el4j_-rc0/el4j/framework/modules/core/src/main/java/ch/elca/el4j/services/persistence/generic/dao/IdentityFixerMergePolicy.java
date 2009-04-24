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
	
	/** Should Preparation be performed. */
	private boolean m_performPreparation;
	
	/**
	 *	A map of [updated -> anchor] used to correctly merge collections.
	 */
	private IdentityHashMap<Object, Object> m_collectionEntryMapping;
	
	/**
	 * Default constructor.
	 * Sets update policy to all and preparation will be performed.
	 */
	protected IdentityFixerMergePolicy() {
		m_updatePolicy = UpdatePolicy.UPDATE_ALL;
		m_performPreparation = true;
		m_collectionEntryMapping = new IdentityHashMap<Object, Object>();
	}
	
	/**
	 * Constructor to customize the policy.
	 * @param updatePolicy            the update policy to use.
	 * @param objectsToUpdate         the objectsToUpdate if UDPATE_CHOSEN policy is chosen above.
	 * @param performPreparation      should preparation be performed (eg. for unwrapping proxies).
	 * @param collectionEntryMapping  the collectionEntryMapping [updated -> anchor] 
	 *               used to correctly merge collections.
	 */
	protected IdentityFixerMergePolicy(UpdatePolicy updatePolicy, List<Object> objectsToUpdate,
		boolean performPreparation, IdentityHashMap<Object, Object> collectionEntryMapping) {
		m_updatePolicy = updatePolicy;
		m_objectsToUpdate = objectsToUpdate;
		m_performPreparation = performPreparation;
		m_collectionEntryMapping = collectionEntryMapping;
	}
	
	/**
	 * @return the update policy of this id fixer merge policy.
	 */
	public UpdatePolicy getUpdatePolicy() {
		return m_updatePolicy;
	}
	
	/**
	 * Set the objects to update explicitly.
	 * @param objectsToUpdate the list of objects to update.
	 */
	public void setObjectsToUpdate(List<Object> objectsToUpdate) {
		m_objectsToUpdate = objectsToUpdate;
	}
	
	/**
	 * @return  The objects to update, 
	 *     only set when <code>getUpdatePolicy == UpdatePolicy.UPDATE_CHOSEN</code>.
	 */
	public List<Object> getObjectsToUpdate() {
		return m_objectsToUpdate;
	}
	
	/**
	 * @return if preparation is needed.
	 */
	public boolean needsPreparation() {
		return m_performPreparation;
	}
	
	/**
	 * @return A map of [updated -> anchor] used to correctly merge collections.
	 */
	public IdentityHashMap<Object, Object> getCollectionEntryMapping() {
		return m_collectionEntryMapping;
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
		IdentityFixerMergePolicy obj = new IdentityFixerMergePolicy(UpdatePolicy.UPDATE_CHOSEN, 
			new ArrayList<Object>(objectsToUpdate), true, new IdentityHashMap<Object, Object>());
		return obj;
	}
	
	/**
	 * @return a policy leaving all the objects of the previous graph untouched,
	 *      only extending it by the new objects.
	 */
	public static IdentityFixerMergePolicy extendOnlyPolicy() {
		IdentityFixerMergePolicy obj = new IdentityFixerMergePolicy(UpdatePolicy.NO_UPDATE, null, true, 
			new IdentityHashMap<Object, Object>());
		return obj;
	}
	
	/**
	 * @param collectionEntryMapping  the collectionEntryMapping [updated -> anchor] 
	 *              used to correctly merge collections.
	 * @return a policy forcing all objects to be updated.
	 */
	public static IdentityFixerMergePolicy reloadAllPolicy(IdentityHashMap<Object, Object> collectionEntryMapping) {
		IdentityFixerMergePolicy obj = new IdentityFixerMergePolicy(UpdatePolicy.UPDATE_ALL, null, 
			true, collectionEntryMapping);
		return obj;
	}
	
	/**
	 * @param objectsToUpdate         the objects to be updated.
	 * @param collectionEntryMapping  the collectionEntryMapping [updated -> anchor] 
	 *              used to correctly merge collections.
	 * @return a policy forcing only the specified objects to be updated,
	 *     leaving the rest untouched.
	 */
	public static IdentityFixerMergePolicy reloadObjectsPolicy(List<Object> objectsToUpdate,
		IdentityHashMap<Object, Object> collectionEntryMapping) {
		IdentityFixerMergePolicy obj = new IdentityFixerMergePolicy(UpdatePolicy.UPDATE_CHOSEN, 
			new ArrayList<Object>(objectsToUpdate), true, collectionEntryMapping);
		return obj;
	}
	
	/**
	 * @param collectionEntryMapping  the collectionEntryMapping [updated -> anchor] 
	 *              used to correctly merge collections.
	 * @return a policy leaving all the objects of the previous graph untouched,
	 *      only extending it by the new objects.
	 */
	public static IdentityFixerMergePolicy extendOnlyPolicy(IdentityHashMap<Object, Object> collectionEntryMapping) {
		IdentityFixerMergePolicy obj = new IdentityFixerMergePolicy(UpdatePolicy.NO_UPDATE, null, 
			true, collectionEntryMapping);
		return obj;
	}
	
	/**
	 * @param updatePolicy           the update policy to use.
	 * @param objectsToUpdate        the objectsToUpdate if UDPATE_CHOSEN policy is chosen above.
	 * @param performPreparation     should preparation be performed (eg. for unwrapping proxies).
	 * @param collectionEntryMapping the collectionEntryMapping [updated -> anchor] 
	 *              used to correctly merge collections.
	 * @return the custom policy specified by the arguments.
	 */
	public static IdentityFixerMergePolicy customPolicy(UpdatePolicy updatePolicy, List<Object> objectsToUpdate,
		boolean performPreparation,	IdentityHashMap<Object, Object> collectionEntryMapping) {
		
		List<Object> objs = null;
		if (updatePolicy == UpdatePolicy.UPDATE_CHOSEN) {
			objs = new ArrayList<Object>(objectsToUpdate);
		}
		IdentityFixerMergePolicy obj = new IdentityFixerMergePolicy(updatePolicy, objs, 
			performPreparation, collectionEntryMapping);
		return obj;
	}
		
	
	
}

