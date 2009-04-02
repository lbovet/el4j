/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.collections.map.AbstractReferenceMap;
import org.apache.commons.collections.map.ReferenceMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.IntroductionInterceptor;
import org.springframework.aop.support.IntroductionInfoSupport;

import ch.elca.el4j.services.persistence.generic.dao.DaoChangeNotifier.NewEntityState;
import ch.elca.el4j.services.persistence.generic.dao.IdentityFixerMergePolicy.UpdatePolicy;
import ch.elca.el4j.services.persistence.generic.dao.annotations.ReturnsUnchangedParameter;
import ch.elca.el4j.services.persistence.generic.dao.impl.DefaultDaoChangeNotifier;
import ch.elca.el4j.util.codingsupport.AopHelper;

/**
 * Fixes object identities mangled by loosing ORM context or by remoting.
 *
 * <h4>Motivation</h4>
 *
 * Object Identity is an important concept in OOP, but is not always
 * guaranteed or maintained. For instance, sending an object back and
 * forth over the wire (using any standard remoting protocol) will create new
 * objects, causing changes applied to the object not to propagate properly -
 * even within a single VM. Similarly, loosing an OR-Mapper's context between
 * load invocations typically results in creating multiple proxies to the same
 * persisted object. Accepting that loss of identity complicates the programming
 * model, and contradicts OO methodology.
 *
 * <p> Given a definition of logical identity and a means to recognize
 * immutable value types, an instance of this class fixes the identities of the
 * objects passing through it while propagating state updates to the logical
 * object's unique representative.
 *
 * <h4>Terminology</h4>
 * A <i>representative</i> is an object that has a logical identity. A given
 * identity fixer will always return the same representative for every logical
 * identity, different identity fixers may return different ones. Usually,
 * you will therefore use a singleton identity fixer, but you may allocate as
 * many as you please ;-)
 *
 * <h4>Configuration</h4>
 * To get a working identity fixer, you must write a subclass and override the
 * three abstract methods, {@link #id(Object)}, {@link #immutableValue(Object)}
 * and {@link #prepareObject(Object)}.
 *
 * <h4>Use</h4>
 * All objects received from an identity-mangling source should pass through an
 * identity fixer. {@link GenericInterceptor} provides a generic Spring AOP
 * interceptor to be wrapped around the identity-mangling objects. As an
 * alternative, manual
 * access to identity translation is granted by {@link #merge(Object, Object)}.
 *
 * <h4>Guarantees</h4>
 * During its lifetime, an identity fixer will always return the same object
 * for every logical identity. It will update the shared instance with the state
 * of the new copies - according to a specified {@link IdentityFixerMergePolicy} or by its
 * default policy -, and notify registered observers about every such update.
 * These guarantees extend to objects (directly or indirectly) referenced by the
 * translated object unless they are recognized as immutable values by
 * {@link #immutableValue(Object)}.
 * 
 * <h4>2-way merging of Collections</h4>
 * Since some identity-mangling sources are replacing collections by own implementations containing
 * also metadata, this class offers a mechanism to work on normal java collections while the source
 * still gets to work on its own versions of the collections.<br>
 * To set up a working 2-way merging, you need to:
 * <ul>
 * 	<li>implement {@link #needsAdditionalProcessing(Object)} to identify the replaced collections.</li>
 * 	<li>call {@link #reverseMerge} on every object you pass to the source</li>
 *  <li>call {@link #merge} as usual on the objects coming from the source</li>
 * </ul>
 *
 * <h4>Requirements</h4>
 * <p> This class needs {@link java.lang.reflect.ReflectPermission}
 * "suppressAccessChecks" if a security manager is present and an object
 * requiring fixing has non-public fields.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Adrian Moos (AMS)
 */
public abstract class AbstractIdentityFixer {
	/**
	 * Id for objects of anonymous types (= value types).
	 */
	protected static final Object ANONYMOUS = new Object();
	
	/** Cache for {@link #fields(Class)}. */
	static Map<Class<?>, List<Field>> s_cachedFields
		= new HashMap<Class<?>, List<Field>>();

	/** The logger. */
	static Log s_logger = LogFactory.getLog(AbstractIdentityFixer.class);
	
	/** ID generator for logging. */
	static ObjectIdentifier s_oi = new ObjectIdentifier();
	
	
	/** Indentation for tracing. */
	int m_traceIndentation = 0;
	
	/** The notifier for broadcasting changes. */
	DaoChangeNotifier m_changeNotifier;
	
	/**
	 * The representatives, keyed by their id.
	 * @see #id(Object)
	 */
	Map<Object, Object> m_representatives;
	
	/**
	 * The collection mapping for a 2-way merging [Object world -> Persistence world].
	 */
	IdentityHashMap<Collection<?>, Collection<?>> m_collectionMapping
		= new IdentityHashMap<Collection<?>, Collection<?>>();
	
	/**
	 * The temporary reverse collection mapping for a 2-way merging [Persistence world -> Object world].
	 */
	IdentityHashMap<Collection<?>, Collection<?>> m_reverseCollectionMapping
		= new IdentityHashMap<Collection<?>, Collection<?>>();
	
	/**
	 * The temporary mapping of all the collections which has to be re-fixed when merging back
	 * in the 2-way merging process [Persistence world -> Object world] keyed by their parent object
	 * and the field to access the collection.
	 */
	HashMap<IdentityFixerCollectionField, Collection<?>> m_collectionsToBeReplaced
		= new HashMap<IdentityFixerCollectionField, Collection<?>>();

	/**
	 * Constructs a new IdentityFixer. You'd never have guessed that, would you?
	 * ;-)
	 */
	public AbstractIdentityFixer() {
		this(new DefaultDaoChangeNotifier());
	}
	
	/**
	 * Constructor.
	 * @param changeNotifier The notifier for broadcasting changes.
	 */
	@SuppressWarnings("unchecked")
	public AbstractIdentityFixer(DaoChangeNotifier changeNotifier) {
		m_changeNotifier = changeNotifier;
		m_representatives = new ReferenceMap(
			AbstractReferenceMap.HARD, AbstractReferenceMap.WEAK);
	}
	
	/**
	 * Returns the notifier used to announce changes. Chiefly useful because
	 * you can subscribe to change messages there.
	 * @return see above
	 */
	public DaoChangeNotifier getChangeNotifier() {
		return m_changeNotifier;
	}
	
	
	/**
	 * Returns a list of all non-static fields of class {@code c}.
	 * @param c The concerned class
	 * @return A list of all non-static fields of the given class
	 */
	protected static List<Field> instanceFields(Class<?> c) {
		List<Field> fs = new ArrayList<Field>();
		for (Class<?> sc = c; sc != null; sc = sc.getSuperclass()) {
			for (Field f : sc.getDeclaredFields()) {
				if (!Modifier.isStatic(f.getModifiers())) {
					fs.add(f);
				}
			}
		}
		return fs;
	}
	
	
	/**
	 * Returns a list of all non-static AccessibleObjects
	 * (fields and methods, no constructors) of class {@code c}.
	 * Also searches in superclasses.
	 *
	 * @param c the concerned class
	 * @return The accessible objects of this class.
	 */
	protected static List<AccessibleObject>
	instanceAccessibleObjects(Class<?> c) {
		List<AccessibleObject> fs = new ArrayList<AccessibleObject>();
		
		//TODO: also search interfaces?
		for (Class<?> sc = c; sc != null; sc = sc.getSuperclass()) {
			
			//first, get fields
			for (Field f : sc.getDeclaredFields()) {
				if (!Modifier.isStatic(f.getModifiers())) {
					fs.add(f);
				}
			}
			
			//next, get methods
			for (Method m : sc.getDeclaredMethods()) {
				if (!Modifier.isStatic(m.getModifiers())) {
					fs.add(m);
				}
			}
		}
		return fs;
	}
	
	
	
	/**
	 * Returns the Fields corresponding to fields in class {@code c}. The fields
	 * can be read from / written into (regardless of access modifiers).
	 * @param c the class whose fields are desired
	 * @return see above.
	 */
	private static List<Field> fields(Class<?> c) {
		List<Field> fields = s_cachedFields.get(c);
		if (fields == null) {
			fields = instanceFields(c);
			s_cachedFields.put(c, fields);
			
			for (Field f : fields) {
				if (!Modifier.isPublic(f.getModifiers())) {
					final Field[] FIELDS
						= fields.toArray(new Field[fields.size()]);
					AccessController.doPrivileged(
						new PrivilegedAction<Object>() {
							public Object run() {
								Field.setAccessible(FIELDS, true);
								return null;
							}
						}
					);
				}
			}
		}
		return fields;
	}
	
	/**
	 * Actually performs the merge.
	 *
	 * @param anchor
	 *              the (presumed) representative, or null
	 * @param updated
	 *              the new version of the object
	 * @param isIdentical
	 *              whether anchor is known to be transitively identical with
	 *              updated.
	 * @param reached
	 *              the set of objects in the updated object graph that have
	 *              been (or are being) merged. Used to avoid merging an
	 *              object more than once.
	 * @param objectsToUpdate
	 *              A list of anchor objects that should updated, all other objects are not touched. If objectsToUpdate
	 *              is <code>null</code> then all reachable objects get updated.
	 * @param hintMapping
	 *              A map of [updated -> anchor] used to correctly merge collections. If no collections have to be
	 *              merged this parameter can be <code>null</code>.
	 * @return
	 *              the representative.
	 */
	@Deprecated
	@SuppressWarnings("unchecked")
	protected <T> T merge(T anchor, T updated,
		boolean isIdentical,
		IdentityHashMap<Object, Object> reached,
		List<Object> objectsToUpdate, IdentityHashMap<Object, Object> hintMapping) {
		
		// Prepare the updated object
		updated = (T) prepareObject(updated);
		
		if (immutableValue(updated)) {
			trace("", updated, " is an immutable value");
			return updated;
		}
				
		T attached = (T) reached.get(updated);
		if (attached != null) {
			trace("",  updated, " was already merged to ", attached);
			return attached;
		}

		boolean isNew = true;
		
		// choose representative
		Object id = id(updated);
		if (isIdentical) {
			// anchor is the guaranteed representative
			attached = anchor;
			assert id(anchor) == null
				|| id(anchor) == ANONYMOUS
				|| id(anchor).equals(id);
			isNew = false;
		} else {
			if (id == ANONYMOUS) {
				attached = anchor;
				isNew = attached == null;
			} else {
				// check for a corresponding representative
				if (id != null) {
					attached = (T) m_representatives.get(id);
					isNew = (attached != null) ? false : true;
				}
				// we don't have to merge if attached == updated, meaning that it equals the representative
				if (attached == updated) {
					reached.put(updated, attached);
					return attached;
				}
			}
			
			
			// if no representative found, go through graph and insert the new objects
			if (attached == null) {
				attached = updated;
			}
		}
		assert attached != null;
		if (id != ANONYMOUS && id != null) {
			m_representatives.put(id, attached);
		}
		
		// register representative
		reached.put(updated, attached);
		
		trace("merging ", updated, " to ", attached);
		m_traceIndentation++;
		if (updated.getClass().isArray()) {
			
			int l = Array.getLength(updated);
			assert Array.getLength(attached) == Array.getLength(updated);
			for (int i = 0; i < l; i++) {
				Array.set(
					attached, i,
					merge(
						attached != null ? Array.get(attached, i) : null,
						Array.get(updated, i),
						attached != null && isIdentical,
						reached,
						objectsToUpdate,
						hintMapping
					)
				);
			}
		} else if (attached instanceof Collection) {
			Collection attachedCollection = (Collection) attached;
			Collection updatedCollection = (Collection) updated;
			
			List mergedEntries = new ArrayList(attachedCollection.size());
			
			for (Object updatedObject : updatedCollection) {
				Object anchorObject = null;
				if (hintMapping != null) {
					anchorObject = hintMapping.get(updatedObject);
				}
				mergedEntries.add(merge(anchorObject,
					updatedObject, anchorObject != null && isIdentical, reached, objectsToUpdate, hintMapping));
			}
			
			
			// Only refill the collection if object has to be updated
			if (objectsToUpdate == null || objectsToUpdate.contains(attachedCollection) || isNew) {
				// write collection to be sure that it contains "exactly the same" (in the sense of identity) objects
				// that are present in the updated collection (might be less or more objects or another ordering,
				// if collection supports it)
				updatedCollection.clear();
				updatedCollection.addAll(mergedEntries);
			} else {
				// refill the new collection with the old values
				updatedCollection.clear();
				updatedCollection.addAll(attachedCollection);
			}
			attached = (T) updatedCollection;
		} else {
			boolean isUpdateNeeded = objectsToUpdate == null || objectsToUpdate.contains(attached) || isNew;
			for (Field f : fields(updated.getClass())) {
				try {
					Object fieldValue = f.get(updated);
					Object fieldValue2 = (attached != null) ? f.get(attached) : null;
					boolean collectionUpdate = fieldValue2 != null && fieldValue2 instanceof Collection 
						&& id(fieldValue2) == ANONYMOUS;
					if (objectsToUpdate != null && isUpdateNeeded && collectionUpdate) {
						objectsToUpdate.add(fieldValue2);
					}
					Object merged = merge(
						(isUpdateNeeded || collectionUpdate) ? fieldValue2 : null,
						fieldValue,
						fieldValue2 != null && isIdentical,
						reached,
						objectsToUpdate,
						hintMapping
					);
					// If objectsToUpdate is specified, perform overwrite only on entities
					// and objects stored in objectsToUpdate, not on values.
					// This is important when we reload an entity from database where not all
					// references are loaded (lazy loading). Then we don't want to overwrite
					// valid local references with not loaded (=null) references
					if (isUpdateNeeded || collectionUpdate) {
						
						f.set(attached, merged);
					}
				} catch (IllegalAccessException e) { assert false : e; }
			}
		}
		m_traceIndentation--;

		// notify state change
		NewEntityState e = new NewEntityState();
		e.setChangee(attached);
		m_changeNotifier.announce(e);
		
		return attached;
	}
	
	/**
	 * Actually performs the merge.
	 *
	 * @param anchor
	 *              the (presumed) representative, or null
	 * @param updated
	 *              the new version of the object
	 * @param policy
	 *              the policy to use.
	 * @param isIdentical
	 *              whether anchor is known to be transitively identical with
	 *              updated.
	 * @param reached
	 *              the set of objects in the updated object graph that have
	 *              been (or are being) merged. Used to avoid merging an
	 *              object more than once.
	 * @param locked
	 *              the set of id's that are locked for updating unless the new version
	 *              is the specified object. This map should be used to prevent updating a representative
	 *              multiple times which might result in a update to an old or non fully loaded object.
	 * @return
	 *              the representative.
	 */
	@SuppressWarnings("unchecked")
	protected <T> T merge(T anchor, T updated, IdentityFixerMergePolicy policy, boolean isIdentical,
		IdentityHashMap<Object, Object> reached, HashMap<Object, Object> locked) {
		
		boolean identical = isIdentical;
		T updateState = updated;
		if (policy.needsPreparation()) {
			// Prepare the updated object
			updateState = (T) prepareObject(updateState);
		}
		if (immutableValue(updateState)) {
			trace("", updateState, " is an immutable value");
			return updateState;
		}
				
		T savedState = (T) reached.get(updateState);
		if (savedState != null) {
			trace("",  updateState, " was already merged to ", savedState);
			return savedState;
		}

		boolean isNew = true;
		
		// choose representative
		Object id = id(updateState);
		
		if (identical) {
			// check if anchor would overwrite an already stored representative
			if (id(anchor) != null && m_representatives.get(id(anchor)) != null) {
				// anchor already present as representative,
				s_logger.debug("Anchor was given for a representative already present, anchor is discarded!");
				identical = false;
				assert anchor == m_representatives.get(id(anchor));
			}
		}
		if (identical) {
			
			// anchor is the guaranteed representative
			savedState = anchor;
			assert id(anchor) == null
				|| id(anchor) == ANONYMOUS
				|| id(anchor).equals(id);
			isNew = false;
		} else {
			if (id == ANONYMOUS) {
				// saved state of anonymous object might be given as anchor
				savedState = anchor;
				isNew = savedState == null;
			} else {
				// check for a corresponding representative
				if (id != null) {
					savedState = (T) m_representatives.get(id);
					isNew = (savedState == null);
				}
				// we have to merge even if attached == updated, meaning that it equals the representative already
				// in case we are in a 2-way merging scenario and supposed to fix the collections again!
				// we are only save if the object was reached already, meaning in the values of the reached map.
				if (savedState == updateState && reached.containsValue(savedState)) {
					reached.put(updateState, savedState);
					return savedState;
				}
			}
			
			
			// if no representative found, go through graph and insert the new objects
			if (savedState == null) {
				savedState = updateState;
			}
		}
		assert savedState != null;
		
		// if id of updateState is locked, return
		if (id != ANONYMOUS && id != null) {
			if (locked.get(id) != null && locked.get(id) != updateState) {
				return savedState;
			}
			if (isNew || identical) {
				m_representatives.put(id, savedState);
			}
		}
		
		// register representative
		reached.put(updateState, savedState);
		
		trace("merging ", updateState, " to ", savedState);
		m_traceIndentation++;
		if (updateState.getClass().isArray()) {
			
			int l = Array.getLength(updateState);
			assert Array.getLength(savedState) == Array.getLength(updateState);
			for (int i = 0; i < l; i++) {
				Array.set(
					savedState, i,
					merge(
						savedState != null ? Array.get(savedState, i) : null,
						Array.get(updateState, i),
						policy,
						savedState != null && identical,
						reached,
						locked
					)
				);
			}
		} else if (savedState instanceof Collection) {
			Collection savedCollection = (Collection) savedState;
			Collection updateCollection = (Collection) updateState;
			
			List mergedEntries;
			
			// Check the update policy, or take new list if new or old is immutable
			if (policy.getUpdatePolicy() == UpdatePolicy.UPDATE_ALL 
				|| (policy.getUpdatePolicy() == UpdatePolicy.UPDATE_CHOSEN 
					&& policy.getObjectsToUpdate().contains(savedCollection)) 
				|| isNew || immutableValue(savedCollection)) {
				
				// First merge the entries of the new list
				mergedEntries = new ArrayList(updateCollection.size());
				for (Object updatedObject : updateCollection) {
					Object anchorObject = null;
					if (policy.getCollectionEntryMapping() != null) {
						anchorObject = policy.getCollectionEntryMapping().get(updatedObject);
					}
					mergedEntries.add(merge(anchorObject, updatedObject, policy, 
						anchorObject != null && identical, reached, locked));
				}
				if (needsAdditionalProcessing(updateCollection)) {
					// check if this collection was already reverseMerged
					Collection<?> restoreCollection = m_reverseCollectionMapping.get(savedCollection);
					if (restoreCollection != null) {
						m_reverseCollectionMapping.remove(savedCollection);
						savedState = (T) restoreCollection;
						savedCollection = restoreCollection;
						
					}
					if (savedCollection != updateCollection) {
						// new collection pair to store mapping of
						m_collectionMapping.put(savedCollection, updateCollection);
					}
					
				}
				savedCollection.clear();
				savedCollection.addAll(mergedEntries);
				
			} else {
				mergedEntries = new ArrayList(savedCollection.size());
				for (Object updatedObject : savedCollection) {
					Object anchorObject = null;
					if (policy.getCollectionEntryMapping() != null) {
						anchorObject = policy.getCollectionEntryMapping().get(updatedObject);
					}
					mergedEntries.add(merge(anchorObject, updatedObject, policy, 
						anchorObject != null && identical, reached, locked));
				}
				savedCollection.clear();
				savedCollection.addAll(mergedEntries);
			}
			
		} else {
			boolean isUpdateNeeded = policy.getUpdatePolicy() == UpdatePolicy.UPDATE_ALL 
				|| (policy.getUpdatePolicy() == UpdatePolicy.UPDATE_CHOSEN 
					&& policy.getObjectsToUpdate().contains(savedState))
				|| isNew;
			for (Field f : fields(updateState.getClass())) {
				try {
					Object fieldValueNew = f.get(updateState);
					Object fieldValueOld = (savedState != null) ? f.get(savedState) : null;
					boolean collectionUpdate = fieldValueOld != null && fieldValueOld instanceof Collection 
						&& id(fieldValueOld) == ANONYMOUS;
					if (policy.getUpdatePolicy() == UpdatePolicy.UPDATE_CHOSEN && collectionUpdate) {
						policy.getObjectsToUpdate().add(fieldValueOld);
					}
					boolean updateAnyway = false;
					if (collectionUpdate && needsAdditionalProcessing(fieldValueNew)) {
						// check for a hint object to replace list again
						IdentityFixerCollectionField idcf = new IdentityFixerCollectionField(savedState, f);
						Collection<?> replaceCollection = m_collectionsToBeReplaced.get(idcf);
						if (replaceCollection != null) {
							updateAnyway = true;
							fieldValueOld = replaceCollection;
							m_collectionsToBeReplaced.remove(idcf);
						}
					}
					Object merged = merge(
						fieldValueOld,
						fieldValueNew,
						policy,
						fieldValueOld != null && identical,
						reached,
						locked
					);
					// If objectsToUpdate is specified, perform overwrite only on entities
					// and objects stored in objectsToUpdate, not on values.
					// This is important when we reload an entity from database where not all
					// references are loaded (lazy loading). Then we don't want to overwrite
					// valid local references with not loaded (=null) references
					if (isUpdateNeeded || updateAnyway) {
						
						f.set(savedState, merged);
					}
				} catch (IllegalAccessException e) { assert false : e; }
			}
		}
		m_traceIndentation--;

		// notify state change
		NewEntityState e = new NewEntityState();
		e.setChangee(savedState);
		m_changeNotifier.announce(e);
		
		return savedState;
	}
	
	/**
	 * Updates the unique representative by duplicating the state in
	 * {@code updated}. If no representative exists so far, one is created.
	 *
	 * <p>For every potentially modified entity, {@link NewEntityState}
	 * notification are sent using the configured change notifier.
	 *
	 * @param anchor
	 *              the representative known to be transitively identical
	 *              with {@code updated}, or null, if the representative's
	 *              logical identity is already defined.
	 * @param updated
	 *              The object holding the new state.
	 * @return The representative.
	 */
	public <T> T merge(T anchor, T updated) {
		HashMap<Object, Object> locked = new HashMap<Object, Object>();
		if (updated instanceof Collection) {
			for (Object o : (Collection<?>) updated) {
				Object id = id(o);
				if (id != null && id != ANONYMOUS) {
					locked.put(id, o);
				}
			}
		}
		T result = merge(
			anchor,
			updated,
			IdentityFixerMergePolicy.reloadAllPolicy(),
			anchor != null,
			new IdentityHashMap<Object, Object>(),
			locked
		);
		return result;
	}
	
	/**
	 * Updates the set of unique representatives according to the {@link IdentityFixerMergePolicy}.
	 * If no representative exists of an object contained by the graph of objects in {@code updated}
	 * so far, one is created.
	 *
	 * <p>For every potentially modified entity, {@link NewEntityState}
	 * notification are sent using the configured change notifier.
	 *
	 * @param anchor
	 *              the representative known to be transitively identical
	 *              with {@code updated}, or null, if the representative's
	 *              logical identity is already defined.
	 * @param updated
	 *              The object holding the new state.
	 * @param policy
	 *              The policy how to merge the representatives.
	 * @return The representative.
	 * 
	 * @see IdentityFixerMergePolicy
	 */
	public <T> T merge(T anchor, T updated, IdentityFixerMergePolicy policy) {
		HashMap<Object, Object> locked = new HashMap<Object, Object>();
		if (updated instanceof Collection) {
			for (Object o : (Collection<?>) updated) {
				Object id = id(o);
				if (id != null && id != ANONYMOUS) {
					locked.put(id, o);
				}
			}
		}
		T result = merge(
			anchor,
			updated,
			policy,
			anchor != null,
			new IdentityHashMap<Object, Object>(),
			locked
		);
		return result;
	}
	
	/**
	 * Prepare an object to be passed to an identity-mangling source.
	 * Using this method along with merge for incoming objects from the source,
	 * a 2-way merging for collections is guaranteed.
	 * 
	 * @param object
	 *              the object to be prepared.
	 * @param reached
	 *              the set of objects that have
	 *              been (or are being) reverseMerged. Used to avoid reverseMerging an
	 *              object more than once.
	 * @param mergeRecursive
	 *              if the graph of objects should be traversed recursively and prepare all objects.
	 * @return the prepared representative
	 */
	@SuppressWarnings("unchecked")
	protected Object reverseMerge(Object object, IdentityHashMap<Object, Object> reached, boolean mergeRecursive) {
		
		if (immutableValue(object)) {
			return object;
		}
		
		if (reached.get(object) == object) {
			return object;
		}
		
		if (mergeRecursive) {
			reached.put(object, object);
		}
		
		// check for collections in the fields
		for (Field f : fields(object.getClass())) {
			try {
				Object fieldValue = f.get(object);
				if (fieldValue instanceof Collection) {
					Collection fieldCollection = (Collection) fieldValue;
					Collection mappedCollection = m_collectionMapping.get(fieldCollection);
					if (mappedCollection != null && fieldCollection != mappedCollection) {
						// there exists already a mapping, fix it
						f.set(object, mappedCollection);
						List<Object> tmpList = new ArrayList<Object>(fieldCollection);
						mappedCollection.clear();
						mappedCollection.addAll(tmpList);
						m_reverseCollectionMapping.put(mappedCollection, fieldCollection);
					} else {
						// no mapping, add a hint object
						m_collectionsToBeReplaced.put(new IdentityFixerCollectionField(object, f), fieldCollection);
					}
					if (mergeRecursive) {
						for (Object o : fieldCollection) {
							reverseMerge(o, reached, mergeRecursive);
						}
					}
				} else if (mergeRecursive && fieldValue != null) {
					if (fieldValue.getClass().isArray()) {
						int l = Array.getLength(fieldValue);
						for (int i = 0; i < l; i++) {
							reverseMerge(Array.get(fieldValue, i), reached, mergeRecursive);
						}
					} else {
						reverseMerge(fieldValue, reached, mergeRecursive);
					}
				}
			} catch (IllegalAccessException e) { assert false : e; }
		}
		
		
		return object;
	}
	
	/**
	 * Prepare an object to be passed to an identity-mangling source.
	 * Using this method along with merge for incoming objects from the source,
	 * a 2-way merging for collections is guaranteed.
	 * 
	 * @param object           the object to be prepared.
	 * @param mergeRecursive   if the graph of objects should be traversed recursively and prepare all objects.
	 * @return the prepared representative
	 */
	public Object reverseMerge(Object object, boolean mergeRecursive) {
		return reverseMerge(object,
			new IdentityHashMap<Object, Object>(),
			mergeRecursive
		);
	}
	
	/**
	 * Prepare a list of objects to be passed to an identity-mangling source.
	 * Using this method along with merge for incoming objects from the source,
	 * a 2-way merging for collections is guaranteed.
	 * 
	 * @param objects           the list of objects to be prepared.
	 * @return the prepared representatives list.
	 */
	public List<Object> reverseMerge(List<Object> objects) {
		List<Object> returnList = new ArrayList<Object>(objects.size());
		for (Object o : objects) {
			returnList.add(reverseMerge(o, false));
		}
		return returnList;
	}
	
	/**
	 * @param object    the object to test
	 * @return          <code>true</code> if object is a representative.
	 */
	public boolean isRepresentative(Object object) {
		return m_representatives.containsValue(object);
	}
	
	/**
	 * @return a collection of all the representatives held by the identity fixer.	 
	 */	
	public Collection<?> getRepresentatives() {
		return m_representatives.values();
	}
	
	/**
	 * Remove an object from the representatives.
	 * @param object  the object to remove
	 */
	public void removeRepresentative(Object object) {
		Object id = id(object);
		if (m_representatives.get(id) != null && m_representatives.get(id).equals(object)) {
			// TODO: remove collection mappings from this representative, elegant solution??
			m_collectionsToBeReplaced.remove(id);
			m_representatives.remove(id);
		}
	}
	
	/**
	 * Returns the globally unique, logical id for the provided object, or
	 * {@code null}, if it has no id (yet), or {@link #ANONYMOUS} is this
	 * object is of value type. {@code o} may be null, point to an ordinary
	 * object or to an array.
	 * <p>
	 * The ID objects returned by this method must be value-comparable using
	 * {@code equals} (which implies that hashCode must be overridden as well).
	 * To permit garbage-collection, ids referring to the object they identify
	 * should do so with weak references.
	 *
	 * @param o
	 *            The object for which a globally unique, logical id will be
	 *            returned
	 * @return A globally unique, logical ID object for the given object
	 */
	// Keys become eligible for collection shortly after the object they
	// identify becomes is weakly reachable (because the latter triggers removal
	// from m_representatives.
	protected abstract Object id(Object o);
	
	/**
	 * Returns whether the given reference represents an immutable value, either
	 * because it really is a value ({@code null}) or because the referenced
	 * object's identity is not accessed and its state is not modified.
	 * {@code o} may be null, point to an ordinary object or to an array.
	 *
	 * @param o
	 *            The concerned object
	 * @return <code>True</code> if the given object represents an immutable
	 *         value, <code>false</code> otherwise
	 */
	protected abstract boolean immutableValue(Object o);
	
	/**
	 * Returns the prepared Object, is called before checked for immutability
	 * to give the id fixer the chance to convert immutable values to usable ones.
	 *
	 * @param o
	 *            The concerned object
	 * @return The prepared object.
	 */
	protected abstract Object prepareObject(Object o);
	
	/**
	 * @param o
	 *            The concerned object.
	 * @return if the object needs additional processing during a {@link AbstractIdentityFixer#merge}.
	 */
	protected boolean needsAdditionalProcessing(Object o) {
		return false;
	}
	
	/*
	 * Warning: The ReturnsUnchangedParameter annotation is not always found
	 * if a DAO is wrapped or proxied and the wrapper does not declare the 
	 * annotation again. This can cause bugs in the identity fixer algorithm.
	 * 
	 * As a guideline, all DAO save* methods must have the annotation present. 
	 */
	
	/**
	 * A generic "around advice" (as defined in AOP terminology) for remote
	 * objects. This interceptor works "out of the box" unless incoming objects
	 * have unknown logical identity. If this occurs, it attempts to infer
	 * logical identity from {@link ReturnsUnchangedParameter} annotations.
	 */
	public class GenericInterceptor
			extends IntroductionInfoSupport
			implements IntroductionInterceptor {

		/**
		 * Constructor.
		 * @param fixedInterface The marker-interface to be "introduced".
		 */
		@SuppressWarnings("unchecked")
		public GenericInterceptor(Class<?> fixedInterface) {
			publishedInterfaces.add(fixedInterface);
		}
		
		/** {@inheritDoc} */
		@SuppressWarnings("unchecked")
		public Object invoke(MethodInvocation invocation) throws Throwable {
			ReturnsUnchangedParameter rp
				= invocation.getMethod().getAnnotation(
					ReturnsUnchangedParameter.class
				);
			if (rp != null) {
				Object arg = invocation.getArguments()[rp.value()];
				if (arg instanceof List) {
					reverseMerge((List) arg);
				} else {
					reverseMerge(arg, true);
				}
				Object result = invocation.proceed();
				return merge(arg, result);
			} else {
				Object result = invocation.proceed();
				return merge(null, result);
			}
		}

		/**
		 * Convenience method returning a proxy to the supplied object
		 * that implements this "advice".
		 * @param o .
		 * @return .
		 */
		public Object decorate(Object o) {
			return AopHelper.addAdvice(o, this);
		}
	}
	
	
	////////////
	// Tracing
	////////////
	
	/**
	 * Logs a trace message. The arguments are assembled on a single line. It
	 * is assumed that arguments with even index are strings to be printed, and
	 * arguments with odd index are objects whose class and identity should be
	 * inserted.
	 * @param os .
	 */
	void trace(Object... os) {
		if (s_logger.isDebugEnabled()) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < m_traceIndentation; i++) {
				sb.append("    ");
			}
			boolean isLiteral = false;
			for (Object o : os) {
				isLiteral = o instanceof String && !isLiteral;
				if (isLiteral) {
					sb.append(o);
				} else {
					s_oi.format(o, sb);
				}
			}
			s_logger.debug(sb);
		}
	}

	/**
	 * For tracing. Assigns names to traced objects to identify them in
	 * trace output.
	 */
	private static class ObjectIdentifier {
		/** The next id to be assigned. */
		int m_maxid = 0;
		
		/** Maps every already encountered object to its id. */
		IdentityHashMap<Object, Integer> m_seen
			= new IdentityHashMap<Object, Integer>();
		
		/**
		 * Prints {code o}'s class and id to {@code toAppendTo}.
		 *
		 * @param obj
		 *            The concerned object
		 * @param toAppendTo
		 *            The StringBuilder to which the object's class and id will
		 *            be printed
		 * @return {@code toAppendTo} (for call chaining)
		 */
		public StringBuilder format(Object obj, StringBuilder toAppendTo) {
			if (obj == null) {
				return toAppendTo.append("null");
			} else {
				Integer id = m_seen.get(obj);
				if (id == null) {
					id = m_maxid++;
					m_seen.put(obj, id);
				}
				return toAppendTo.append(obj.getClass().getSimpleName())
					.append(id.toString());
			}
		}
	}
}