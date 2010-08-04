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
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import ch.elca.el4j.services.persistence.generic.dto.AbstractIntOptimisticLockingDto;
import ch.elca.el4j.services.persistence.generic.dto.PrimaryKeyObject;
import ch.elca.el4j.services.persistence.jpa.util.JpaQuery;
import ch.elca.el4j.services.persistence.jpa.util.QueryException;

/**
 * Implementation of DataService.
 *
 * @author David Bernhard (dab)
 */
public class JpaHelperImpl implements JpaHelper {

	/**
	 * The entity manager.
	 */
	@PersistenceContext
	private EntityManager em;
	
	@Override 
	public <T> JpaQuery<T> selectFrom(Class<T> cls) {
		return new JpaQuery<T>(cls, this);
	}

	/**
	 * Check if an instance is new. 
	 * @param instance The instance.
	 * @return true if it is new.
	 * @throws IllegalArgumentException If the instance is not an entity.
	 */
	private boolean isNew(Object instance) throws IllegalArgumentException {
		// TODO We won't need this once we've changed to a clean JPA
		// programming model.
		if (instance instanceof PrimaryKeyObject) {
			PrimaryKeyObject dto
				= (PrimaryKeyObject) instance;
			return dto.isKeyNew();
		} else {
			throw new IllegalArgumentException("Cannot persist " + instance
				+ " : Not an entity, class is "
				+ instance.getClass().getName());
		}
	}

	@Override 
	@Transactional 
	public <T> Boolean contains(T instance) {
		T t = instance;
		return em.contains(t);
	}
	
	@Override 
	@Transactional 
	public <T> T merge(T instance) {
		T t = instance;
		if (em.contains(t)) {
			LoggerFactory.getLogger(this.getClass())
				.warn("merge is called on a MANAGED entity instead on a DETACHED one.");
		}
		t = em.merge(t);
		return t;
	}
	
	@Override 
	@Transactional 
	public <T> void detach(T instance) {
		T t = instance;
		if (!em.contains(t)) {
			LoggerFactory.getLogger(this.getClass())
				.warn("merge is not called on a MANAGED entity.");
		}
		em.detach(t);
	}
	
	@Override 
	@Transactional 
	public <T> void persist(T instance) {
		T t = instance;
		if (em.contains(t)) {
			LoggerFactory.getLogger(this.getClass())
				.warn("persist is called on a MANAGED entity instead on a NEW one.");
		}
		em.persist(t);
	}

	@Override 
	@Transactional 
	public <T> void remove(T instance) {
		T t = instance;
		if (!em.contains(t)) {
			LoggerFactory.getLogger(this.getClass()).warn("remove is not called on a MANAGED entity.");
		}
		em.remove(t);
	}

	@Override 
	@Transactional 
	public <T> void refresh(T instance) {
		T t = instance;
		if (!em.contains(t)) {
			LoggerFactory.getLogger(this.getClass()).warn("refresh is not called on a MANAGED entity.");
		}
		em.refresh(t);
	}

	@Override
	@Transactional
	public <T> T findByKey(Class<T> clazz, Integer key) {
		T t = em.find(clazz, key);
		return t;
	}
	
	
	@Override 
	public void clear() {
		em.clear();
	}

	@Override 
	@Transactional 
	public void flush() {
		em.flush();
	}
	
	/**
	 * Getter for entity manager.
	 */
	public EntityManager getEntityManager() {
		return em;
	}

	/**
	 * Set the entity manager.
	 * @param em The entity manager.
	 */
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}
	
	@Override 
	@Transactional 
	public void doInTransaction(Runnable r) {
		r.run();
	}
}
