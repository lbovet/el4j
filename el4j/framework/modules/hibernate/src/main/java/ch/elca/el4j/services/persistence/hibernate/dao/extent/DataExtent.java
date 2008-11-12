/*
 * Copyright 2006 by ELCA Informatique SA
 * Av. de la Harpe 22-24, 1000 Lausanne 13
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of ELCA Informatique SA. ("Confidential Information"). You
 * shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license
 * agreement you entered into with ELCA.
 */
package ch.elca.el4j.services.persistence.hibernate.dao.extent;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.persistence.Transient;

import static ch.elca.el4j.services.persistence.hibernate.dao.extent.ExtentEntity.entity;


/**
 * A DataExtent represents the extent of the graphs of objects to be
 * loaded in a hibernate query.<br>
 * <br>
 * Features: <br>
 *  <ul>
 *   <li> new DataExtent: provide root class and optionally the name of the root 
 *   <li> with/without: add/remove fields to/from the extent
 *   <li> include/exclude: add/remove sub-entities 
 *   <li> includeList/excludeList: add/remove collections (must implement
 *   		{@see java.util.Collection} interface
 *   <li> see also features of {@see ExtentEntity} to write your code in a convenient way
 *  </ul>
 *
 *  Remark: Be sure to import the static method {@see ExtentEntity#entity} to create easily new Entities<br> 
 *  <br>
 *  Sample code: <br>
 * 		<code>
 * 			<pre>
 * 	// The Extent Object of type 'Person'
 * 	ex = new DataExtent(Person.class);
 * 	// Construct a complex graph:
 * 	// Person has a List of Teeth, a Tooth has a 'Person' as owner, 
 * 	// the owner has a list of 'Person' as friends, the friends are again
 * 	// the same 'Person'-entity as defined in the beginning.
 * 	ex.includeList(entity("teeth", Tooth.class)
 *		.include(entity("owner", Person.class).includeList("friends", ex.getRootEntity())));
 *	
 *	// Same code semantics, but without fluent API:	
 *	ExtentCollection teeth = new ExtentCollection("teeth", Tooth.class);
 *	ExtentEntity p1 = entity("owner", Person.class);
 *	ExtentCollection friends = new ExtentCollection("friends", Person.class);
 *	teeth.getContainedEntity().addChildEntity(p1);
 *	p1.addCollection(friends);
 *	friends.setContainedEntity(ex.getRootEntity());
 *	ex.getRootEntity().addCollection(teeth);
 *			</pre>
 *		</code>
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Andreas Rueedlinger (ARR)
 */
public class DataExtent implements Serializable {
	/** Defines the default loading depth. */
	public static final int DEFAULT_LOADING_DEPTH = 1;
	
	/** The root entity. */
	private ExtentEntity m_rootEntity;
	
	/**
	 * Map indicating which entities have already been validated.
	 */
	/*private HashSet<ExtentEntity> m_validatedEntities
		= new HashSet<ExtentEntity>();
	*/
	/**
	 * Default Creator.
	 * @param root	the root entity of the extent
	 */
	public DataExtent(ExtentEntity root) {
		m_rootEntity = root;
	}
	
	/**
	 * Default Creator.
	 * @param c		the class of the root entity.
	 */
	public DataExtent(Class<?> c) {
		m_rootEntity = entity(c);
	}
	
	/**
	 * Default Creator.
	 * @param name	the name of the root entity.
	 * @param c		the class of the root entity.
	 */
	public DataExtent(String name, Class<?> c) {
		m_rootEntity = entity(name, c);
	}
	
	/**
	 * Root entity of the extent.
	 * @return 		the root entity of the extent.
	 */
	public ExtentEntity getRootEntity() {
		return m_rootEntity;
	}
	
	/**
	 * Set the root entity of the extent.
	 * @param root	the root entity of the extent to be set.
	 */
	public void setRootEntity(ExtentEntity root) {
		m_rootEntity = root;
	}
	
	/**
	 * Validate the extent: check class, entities and fields.
	 * @return if the extent is a valid extent.
	 */
	/*public boolean validate() {

		m_validatedEntities.clear();
		boolean valid = validateEntity(m_rootEntity);
		m_validatedEntities.clear();
		return valid;
		
	}*/
	/**
	 * Validates the correctness of a sub-entity.
	 * @return if the entity is valid
	 */
	/*private boolean validateEntity(ExtentEntity entity) {
		
		Class<?>[] nullClass = null;
		try {
			// Check the base type fields
			entity.validateFields();
			
			m_validatedEntities.add(entity);
			
			// Fetch the child entities
			for (ExtentEntity ent : entity.getChildEntities()) {
				entity.getEntityClass().getMethod(ent.getGetterName(), nullClass);
				if (m_validatedEntities.contains(ent)) {
					if (!validateEntity(ent)) {
						return false;
					}
				}
			}
			// Fetch the collections
			for (ExtentCollection c : entity.getCollections()) {
				Method m = entity.getEntityClass().getMethod(c.getGetterName(), nullClass);
				// Check the Collection type
				Class<?> t = (Class<?>) ((ParameterizedType) m.getGenericReturnType())
				.getActualTypeArguments()[0];
				if (!t.equals(c.getContainedEntity().getEntityClass())) {
					return false;
				}
				if (!m_validatedEntities.contains(c.getContainedEntity().hashCode())) {
					if (!validateEntity(c.getContainedEntity())) {
						return false;
					}
				}
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}*/
	
	//****************** Fluent API **********************//
	
	/**
	 * Extend the extent by fields.
	 * @param fields	fields to be added.
	 * 
	 * @return the new DataExtent Object.
	 * @throws NoSuchMethodException 
	 */
	public DataExtent with(String... fields) throws NoSuchMethodException {
		for (String s : fields) {
			m_rootEntity.addFieldAsName(s);
		}
		return this;
	}
	
	/**
	 * Exclude fields from the extent.
	 * @param fields	fields to be excluded.
	 * 
	 * @return the new DataExtent Object.
	 */
	public DataExtent without(String...fields) {
		m_rootEntity.removeFields(fields);
		return this;
	}
	
	/**
	 * Extend the extent by an entity given as class,
	 * name is the name of the class.
	 * If fields are empty, all the fields/entities of the class are added.
	 * @param c			class of the entity to include.
	 * @param fields	fields to be added to the included entity.
	 * 
	 * @return the new DataExtent Object.
	 * @throws NoSuchMethodException 
	 */
	public DataExtent include(Class<?> c, String... fields) throws NoSuchMethodException {
		return include(c.getSimpleName(), c, fields);
	}
	
	/**
	 * Extend the extent by an entity given as class,
	 * name is the name of the class.
	 * Explore all the fields and sub-entities to given depth.
	 * @param c			class of the entity to include.
	 * @param depth		exploration-depth.
	 * 
	 * @return the new DataExtent Object.
	 * @throws NoSuchMethodException 
	 */
	public DataExtent include(Class<?> c, int depth) throws NoSuchMethodException {
		return include(c.getSimpleName(), c, depth);
	}
	
	/**
	 * Extend the extent by an entity given as class and name.
	 * If fields are empty, all the fields/entities of the class are added.
	 * @param name		name of the entity.
	 * @param c			class of the entity to include.
	 * @param fields	fields to be added to the included entity.
	 * 
	 * @return the new DataExtent Object.
	 * @throws NoSuchMethodException 
	 */
	public DataExtent include(String name, Class<?> c, String... fields) throws NoSuchMethodException {
		ExtentEntity tmp = entity(name, c);
		if (fields.length > 0) {
			for (String s : fields) {
				tmp.addFieldAsName(s);
			}
		} else {
			// Fetch all fields if empty
			tmp.fetchAllFields(DEFAULT_LOADING_DEPTH);
		}
		m_rootEntity.addChildEntity(tmp);
		return this;
	}
	
	/**
	 * Extend the extent by an entity given as class and name.
	 * Explore all the fields and sub-entities to given depth.
	 * @param name		name of the entity.
	 * @param c			class of the entity to include.
	 * @param depth		exploration-depth.
	 * 
	 * @return the new DataExtent Object.
	 * @throws NoSuchMethodException 
	 */
	public DataExtent include(String name, Class<?> c, int depth) throws NoSuchMethodException {
		ExtentEntity tmp = entity(name, c);

		tmp.fetchAllFields(depth);
		m_rootEntity.addChildEntity(tmp);
		return this;
	}
	
	/**
	 * Extend the extent by the given entity.
	 * @param entity	the entity to add.
	 * @return	the new DataExtent Object.
	 * @throws NoSuchMethodException 
	 */
	public DataExtent include(ExtentEntity entity) throws NoSuchMethodException {
		m_rootEntity.addChildEntity(entity);
		return this;
	}
	
	/**
	 * Extend the extent by an entity given as class,
	 * name is the name of the class.
	 * If fields are empty, all the fields/entities of the class are added.
	 * @param c			class of the entity to include.
	 * @param fields	fields to be added to the included entity.
	 * 
	 * @return the new DataExtent Object.
	 * @throws NoSuchMethodException 
	 */
	public DataExtent includeList(Class<?> c, String... fields) throws NoSuchMethodException {
		return includeList(c.getSimpleName(), c, fields);
	}
	
	/**
	 * Extend the extent by an entity given as class,
	 * name is the name of the class.
	 * Explore all the fields and sub-entities to given depth.
	 * @param c			class of the entity to include.
	 * @param depth		exploration-depth.
	 * 
	 * @return the new DataExtent Object.
	 * @throws NoSuchMethodException 
	 */
	public DataExtent includeList(Class<?> c, int depth) throws NoSuchMethodException {
		return includeList(c.getSimpleName(), c, depth);
	}
	
	/**
	 * Extend the extent by an entity given as class and name.
	 * If fields are empty, all the fields/entities of the class are added.
	 * @param name		name of the entity.
	 * @param c			class of the entity to include.
	 * @param fields	fields to be added to the included entity.
	 * 
	 * @return the new DataExtent Object.
	 * @throws NoSuchMethodException 
	 */
	public DataExtent includeList(String name, Class<?> c, String... fields) throws NoSuchMethodException {
		ExtentCollection tmp = new ExtentCollection(name, c);
		if (fields.length > 0) {
			for (String s : fields) {
				tmp.getContainedEntity().addFieldAsName(s);
			}
		} else {
			// Fetch all fields if empty
			tmp.getContainedEntity().fetchAllFields(DEFAULT_LOADING_DEPTH);
		}
		m_rootEntity.addCollection(tmp);
		return this;
	}
	
	/**
	 * Extend the extent by an entity given as class and name.
	 * Explore all the fields and sub-entities to given depth.
	 * @param name		name of the entity.
	 * @param c			class of the entity to include.
	 * @param depth		exploration-depth.
	 * 
	 * @return the new DataExtent Object.
	 * @throws NoSuchMethodException 
	 */
	public DataExtent includeList(String name, Class<?> c, int depth) throws NoSuchMethodException {
		ExtentCollection tmp = new ExtentCollection(name, c);
		tmp.getContainedEntity().fetchAllFields(depth);
		m_rootEntity.addCollection(tmp);
		return this;
	}
	
	/**
	 * Extend the extent by a collection containing the given entity.
	 * @param entity	the entity in the collection to be added.
	 * @return the new DataExtent Object.
	 * @throws NoSuchMethodException 
	 */
	public DataExtent includeList(ExtentEntity entity) throws NoSuchMethodException {
		String name = entity.getName();
		return includeList(name, entity);
	}
	
	/**
	 * Extend the extent by a collection containing the given entity,
	 * name of the collection is name.
	 * @param name		the name of the collection.
	 * @param entity	the entity in the collection to be added.
	 * @return the new DataExtent Object.
	 * @throws NoSuchMethodException 
	 */
	public DataExtent includeList(String name, ExtentEntity entity) throws NoSuchMethodException {
		ExtentCollection tmp = new ExtentCollection(name);
		tmp.setContainedEntity(entity);
		m_rootEntity.addCollection(tmp);
		return this;
	}
	
	
	/**
	 * Exclude the entity/collection given as string from the extent.
	 * @param name		name of the entity/collection.
	 * 
	 * @return the new DataExtent Object.
	 */
	public DataExtent exclude(String name) {
		if (!m_rootEntity.removeEntity(name)) {
			m_rootEntity.removeCollection(name);
		}
		return this;
	}
	
	/**
	 * Exclude the entity given as class from the extent.
	 * @param c		class of the entity.
	 * 
	 * @return the new DataExtent Object.
	 */
	public DataExtent exclude(Class<?> c) {
		if (!m_rootEntity.removeEntity(c)) {
			m_rootEntity.removeCollection(c);
		}
		return this;
	}
	
	/**
	 * Include all fields, entities and collections of the class.
	 * Exploration depth is DEFAULT_LOADING_DEPTH.
	 * 
	 * @return the new DataExtent Object.
	 */
	public DataExtent all() {
		m_rootEntity.fetchAllFields(DEFAULT_LOADING_DEPTH);
		return this;
	}
	
	/**
	 * Include all fields and entities of the class.
	 * @param depth		Exploration depth
	 * 
	 * @return the new DataExtent Object.
	 */
	public DataExtent all(int depth) {
		m_rootEntity.fetchAllFields(depth);
		return this;
	}

	
}
