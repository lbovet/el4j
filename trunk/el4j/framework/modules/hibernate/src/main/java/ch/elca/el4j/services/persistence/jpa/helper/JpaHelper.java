/*
 * Project: KnowHow
 *
 * Copyright 2008 by ELCA Informatik AG
 * Steinstrasse 21, CH-8036 Zurich
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of ELCA Informatik AG ("Confidential Information"). You
 * shall not disclose such "Confidential Information" and shall
 * use it only in accordance with the terms of the license
 * agreement you entered into with ELCA.
 */

package ch.elca.el4j.services.persistence.jpa.helper;

import javax.persistence.EntityManager;

import ch.elca.el4j.services.persistence.jpa.util.JpaQuery;

/**
 * Utility class and methods to make database access easier.
 *
 * Type safety is given under the following conditions:
 *
 * When searching, the class provided must be a domain class and any properties
 * provided must be applicable to that class.
 * For example, selectFrom(A.class).with("b", some_b)
 * requires that A is a domain class with a property b of a type corresponding
 * to the class of some_b.
 *
 * @author David Bernhard (dab)
 */
public interface JpaHelper {

	/**
	 * Initiate a select query.
	 * @param <T> Generic type parameter for the class to select from.
	 * @param cls The class to select from.
	 * @return The query object.
	 */
	public <T> JpaQuery<T> selectFrom(Class<T> cls);

	/**
	 * Contains an entry.
	 * @param <T> The type parameter.
	 * @param instance The object to check if the entity manager contains it.
	 * @return Boolean, true it is contained.
	 */
	public <T> Boolean contains(T instance);
	
	/**
	 * Merge the state of the given entity into the current persistence context.
	 * Transition from <code>DETACHED</code> to <code>MANAGED</code>.
	 * 
	 * @param <T> The type parameter, to allow us to return the merged object.
	 * @param instance The object to merge.
	 * @return The managed entity.
	 */
	public <T> T merge(T instance);
	
	/**
	 * Remove the given entity from the persistence context, causing a managed entity to become detached. 
	 * Unflushed changes made to the entity if any (including removal of the entity), will not be synchronized 
	 * to the database. Entities which previously referenced the detached entity will continue to reference it.
	 * Transition from <code>MANAGED</code> to <code>DETACHED</code>.
	 * 
	 * @param <T> The type parameter.
	 * @param instance The object to detach.
	 */
	public <T> void detach(T instance);
	
	/**
	 * Makes an entity managed and persistent. Transition from state <code>NEW</code>/<code>REMOVED</code> 
	 * to <code>MANAGED</code>. 
	 * On the next <code>flush</code> or <code>commit</code> it is inserted into the database.<br>
	 * @param <T> The type parameter.
	 * @param instance The object to persist.
	 */
	public <T> void persist(T instance);

	/**
	 * Delete a <code>MANAGED</code> entity. Transition from state <code>MANAGED</code> to <code>REMOVED</code>.
	 * On the next <code>flush</code> or <code>commit</code> it is removed from the database.<br> 
	 * @param <T> The type parameter.
	 * @param instance The instance to delete.
	 */
	public <T> void remove(T instance);

	/**
	 * Refresh the state of the instance from the database, overwriting changes made to the entity, if any.
	 * Transition from state <code>MANAGED</code> to <code>MANAGED</code> again.
	 * @param <T> The type parameter.
	 * @param instance The instance to refresh.
	 */
	public <T> void refresh(T instance);
	
	/**
	 * Finds an entity by its primary key.
	 * 
	 * @param <T> The type parameter, to allow us to return the searched object.
	 * @param clazz The entity class.
	 * @param key The primary key.
	 * @return The searched entity.
	 */
	public <T> T findByKey(Class<T> clazz, Integer key);

	/**
	 * Clear the session.
	 */
	public void clear();
	
	/**
	 * Flush the session.
	 */
	public void flush();
	
	public void doInTransaction(Runnable r);
}
