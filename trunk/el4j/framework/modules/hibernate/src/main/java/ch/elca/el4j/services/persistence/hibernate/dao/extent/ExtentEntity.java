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


import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import ch.elca.el4j.util.codingsupport.BeanPropertyUtils;


/**
 * A ExtentEntity represents a complex Data Type in an Extent.
 * <br>
 * Features: <br>
 *  <ul>
 *   <li> static method entity: create a new entity 
 *   <li> with/without: add/remove fields, sub-entities and collections to/from the extent
 *   <li> withSubentities: add sub-entities to the extent, convenient for adding entities you want
 *   		to define in detail.
 *   <li> all: the whole entity with all fields, entities and collections.
 *  </ul>
 *
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
public class ExtentEntity extends AbstractExtentPart {
	
	/** The class of the entity. */
	private Class<?> m_entityClass;
	
	/** The field-methods of the entity. */
	//private List<Method> m_fields;
	private List<String> m_fields;
	
	/** The child-entities of the entity. */
	private List<ExtentEntity> m_childEntities;
	
	/** The collections of the entity. */
	private List<ExtentCollection> m_collections;
	
	/** The id of the entity. */
	private String m_entityId;
	
	/** Is the entity a root entity. */
	private boolean m_root = false;
	
	/**
	 * Default Creator, hidden.
	 * @param c		the class of the entity
	 */
	public ExtentEntity(Class<?> c) {
		m_name = firstCharLower(c.getSimpleName());
		m_entityClass = c;
		m_fields = new LinkedList<String>();
		m_childEntities = new LinkedList<ExtentEntity>();
		m_collections = new LinkedList<ExtentCollection>();
		m_entityId = String.format("|%s[][][]|", m_entityClass.getName());
	}
	
	/**
	 * Default Creator, hidden.
	 * @param name	the name of the entity
	 * @param c		the class of the entity
	 */
	public ExtentEntity(String name, Class<?> c) {
		m_name = name;
		m_entityClass = c;
		m_fields = new LinkedList<String>();
		m_childEntities = new LinkedList<ExtentEntity>();
		m_collections = new LinkedList<ExtentCollection>();
		m_entityId = String.format("|%s[][][]|", m_name);
	}
	
	/**
	 * Default Creator, hidden.
	 * @param c 		the class of the entity
	 * @param method	the method to get the entity
	 */
	public ExtentEntity(Class<?> c, Method method) {
		m_name = toFieldName(method);
		m_entityClass = c;
		//m_method = method;
		m_fields = new LinkedList<String>();
		m_childEntities = new LinkedList<ExtentEntity>();
		m_collections = new LinkedList<ExtentCollection>();
		m_entityId = String.format("|%s[][][]|", m_name);
	}
	
	/** {@inheritDoc} */
	public String getId() {
		return m_entityId;
	}
	
	/** {@inheritDoc} */
	protected void updateId() {
		rebuildId();
	}
	
	/**
	 * @return if the entity is a root entity.
	 */
	public boolean isRoot() {
		return m_root;
	}
	
	/**
	 * Class of the entity.
	 * @return the class of the entity.
	 */
	public Class<?> getEntityClass() {
		return m_entityClass;
	}
	
	/**
	 * Field-methods of the entity.
	 * @return the field-methods of the entity.
	 */
	public List<String> getFields() {
		return m_fields;
	}
	
	/**
	 * Child entities.
	 * @return the child entities of the entity.
	 */
	public List<ExtentEntity> getChildEntities() {
		return m_childEntities;
	}
	
	/**
	 * Collections.
	 * @return the collections of the entity.
	 */
	public List<ExtentCollection> getCollections() {
		return m_collections;
	}
	
	/**
	 * Rebuild the id string.
	 * Go recursive through all children and get their id.
	 * If entity is root and has a parent, infinite loops are prevented
	 * by not updating the parent and outputting the hashCode when to toString is called.
	 */
	private void rebuildId() {
		// Rebuild the id string
		String id = "|";
		
		if (isRoot()) {
			id += m_entityClass.getName();
		} else {
			id += m_name;
		}
		id += m_fields.toString();
		id += m_childEntities.toString();
		id += m_collections.toString();
		id += "|";
		
		// Inform the parent if id changed
		if (!m_entityId.equals(id)) {
			m_entityId = id;
			if (m_parent != null && !isRoot()) {
				m_parent.updateId();
			}
		}
	}
	/**
	 * Add a field-method to the fields of the entity.
	 * @param field	 the field-method to add.
	 * @throws NoSuchMethodException 
	 * @throws SecurityException 
	 */
	private void addField(String field) {
		if (!m_fields.contains(field)) {
			m_fields.add(field);
			Collections.sort(m_fields);
			rebuildId();
		}
	}
	
	/**
	 * Remove a field from the fields of the entity as name.
	 * @param name	 the field as name to remove.
	 * @return returns the success of the operation
	 */
	private boolean removeField(String name) {
		if (m_fields.remove(name)) {
			rebuildId();
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Add a child-entity to the entity. Parent of child entity is set and consistency
	 * to parent class is checked.
	 * @param child	 the child to add.
	 * @throws NoSuchMethodException 
	 */
	private void addChildEntity(ExtentEntity child) throws NoSuchMethodException {
		try {
			Method m  = BeanPropertyUtils.getReadMethod(m_entityClass, child.getName());
			//child.setMethod(m);
			if (m != null) {
				child.setParent(this);
				m_childEntities.add(child);
				Collections.sort(m_childEntities);
				rebuildId();
			}
		} catch (IllegalArgumentException e) {
			throw new NoSuchMethodException(e.getMessage());
		}
	}
	
	/**
	 * Remove an entity from the children of the entity.
	 * @param name	 name of the entity to remove.
	 * @return returns the success of the operation
	 */
	private boolean removeEntity(String name) {
		for (ExtentEntity e : m_childEntities) {
			if (e.getName().equals(name)) {
				m_childEntities.remove(e);
				rebuildId();
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Add a collection to the entity.
	 * @param collection	 the collection to add.
	 * @throws NoSuchMethodException 
	 */
	private void addCollection(ExtentCollection collection) throws NoSuchMethodException {
		try {
			Method m  = BeanPropertyUtils.getReadMethod(m_entityClass, collection.getName());
			collection.setParent(this);
			boolean consistent = false;
			// Check if the class of the contained entity is consistent
			Type rawType = m.getGenericReturnType();
			if (rawType instanceof ParameterizedType) {
				Type[] pt = ((ParameterizedType) m.getGenericReturnType())
				.getActualTypeArguments();
				if (pt.length > 0 && pt[0] instanceof Class<?>) {
					if (((Class<?>) pt[0]).isAssignableFrom(
						collection.getContainedEntity().getEntityClass())) {
						
						consistent = true;
					}
				}
			}
			if (consistent) {
				m_collections.add(collection);
				Collections.sort(m_collections);
				rebuildId();
			} else {
				throw new NoSuchMethodException("Collection type [" 
					+ collection.getContainedEntity().getEntityClass().getSimpleName() 
					+ "] doesnt conform with class definition.");
			}
		} catch (IllegalArgumentException e) {
			throw new NoSuchMethodException(e.getMessage());
		}
	}
	
	/**
	 * Remove a collection from the collections of the entity.
	 * @param name	 name of the collection to remove.
	 * @return returns the success of the operation
	 */
	private boolean removeCollection(String name) {
		for (ExtentCollection e : m_collections) {
			if (e.getName().equals(name)) {
				m_collections.remove(e);
				rebuildId();
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Add a method to the extent (given as name).
	 * Automatically checks if a property, entity or a collection.
	 * @param name	 the field to add.
	 * @throws NoSuchMethodException 
	 */
	private void addMethodAsName(String name) throws NoSuchMethodException {
		try {
			Method m = BeanPropertyUtils.getReadMethod(m_entityClass, name);
			if (m != null) {
				fetchMethod(m, DataExtent.DEFAULT_LOADING_DEPTH);
			} else {
				throw new NoSuchMethodException("Method doesn't exist.");
			}
		} catch (IllegalArgumentException e) {
			throw new NoSuchMethodException(e.getMessage());
		}
	}
	
	
	//*************** Fluent API ******************//
	
	
	/**
	 * Returns a new Entity object, based on the given class.
	 * @param c		the class of the entity.
	 * @return	the Entity object.
	 */
	public static ExtentEntity rootEntity(Class<?> c) {
		ExtentEntity tmp = new ExtentEntity(c);
		tmp.m_root = true;
		return tmp;
		
	}
	
	/**
	 * Returns a new Entity object, based on the given class.
	 * @param c		the class of the entity.
	 * @return	the Entity object.
	 */
	public static ExtentEntity entity(Class<?> c) {
		return new ExtentEntity(c);
	}
	
	/**
	 * Returns a new Entity object, based on the given name and class.
	 * @param name	the name of the entity.
	 * @param c		the class of the entity.
	 * @return	the Entity object.
	 */
	public static ExtentEntity entity(String name, Class<?> c) {
		return new ExtentEntity(name, c);
	}
	
	/**
	 * Returns a new Entity object, based on the given class and method.
	 * @param c		the class of the entity.
	 * @param m		the method to get the entity.
	 * @return	the Entity object.
	 */
	public static ExtentEntity entity(Class<?> c, Method m) {
		return new ExtentEntity(c, m);
	}
	
	/**
	 * Extend the entity by the given fields.
	 * Fields are either simple properties, sub-entities or collections.
	 * @param fields	fields to be added.
	 * 
	 * @return the new ExtentEntity Object.
	 * @throws NoSuchMethodException 
	 */
	public ExtentEntity with(String... fields) throws NoSuchMethodException {
		for (String s : fields) {
			addMethodAsName(s);
		}
		return this;
	}
	
	/**
	 * Extend the entity by the given sub-entities.
	 * @param entities	entities to be added.
	 * 
	 * @return the new ExtentEntity Object.
	 * @throws NoSuchMethodException 
	 */
	public ExtentEntity withSubentities(AbstractExtentPart... entities) throws NoSuchMethodException {
		for (AbstractExtentPart entity : entities) {
			if (entity instanceof ExtentEntity) {
				addChildEntity((ExtentEntity) entity);
			} else {
				addCollection((ExtentCollection) entity);
			}
		}
		return this;
	}
	
	/**
	 * Exclude fields from the entity.
	 * Fields are either simple properties, sub-entities or collections.
	 * @param fields	fields to be excluded.
	 * 
	 * @return the new ExtentEntity Object.
	 */
	public ExtentEntity without(String...fields) {
		for (String s : fields) {
			if (!removeField(s)) {
				if (!removeEntity(s)) {
					removeCollection(s);
				}
			}
		}
		return this;
	}
	
	/**
	 * Include all fields, entities and collections of the class-entity.
	 * @param depth		Exploration depth.
	 * @return the new ExtentEntity Object.
	 */
	public ExtentEntity all(int depth) {
		if (depth > 0) {
			for (Method m : m_entityClass.getMethods()) {
				try {
					fetchMethod(m, depth);
				} catch (NoSuchMethodException e) {
					// Non persistent Method throws NoSuchMethodException
				}
			}
		}
		return this;
	}
	
	/**
	 * Add the method to the entity in the appropriate manner,
	 * as field, entity or collection.
	 * Ensure when calling the function that method exists in entity class.
	 * @param m			the method to be added.
	 * @param depth		Exploration depth.
	 * @throws NoSuchMethodException 
	 */
	private void fetchMethod(Method m, int depth) throws NoSuchMethodException {
		// Fetch only the methods with getter and no arguments
		// Exclude getClass()
		boolean isGetter = m.getName().startsWith("get") && m.getParameterTypes().length == 0;
		if (isGetter && !m.getName().equals("getClass") && !m.getName().equals("get")) {

			if (m.getReturnType().isPrimitive() || m.getReturnType().isEnum()
				|| m.getReturnType().equals(String.class)) {
				
				addField(toFieldName(m));
			} else if (Collection.class.isAssignableFrom(m.getReturnType())) {
				fetchCollection(m, depth);
			} else {
				ExtentEntity tmp = entity(m.getReturnType(), m);
				if (depth > 1) {
					tmp.all(depth - 1);
				}
				addChildEntity(tmp);
			}
		}
	}
	
	/**
	 * Add the collection method to the entity,
	 * with all its special treatment.
	 * @param m			the method to be added.
	 * @param depth		Exploration depth.
	 * @throws NoSuchMethodException 
	 */
	private void fetchCollection(Method m, int depth) throws NoSuchMethodException {
		// Special treatment if collection type
		Type rawType = m.getGenericReturnType();
		if (rawType instanceof ParameterizedType) {
			Type[] pt = ((ParameterizedType) m.getGenericReturnType())
			.getActualTypeArguments();
			if (pt.length > 0 && pt[0] instanceof Class<?>) {
				Class<?> t = (Class<?>) ((ParameterizedType) m.getGenericReturnType())
					.getActualTypeArguments()[0];
				ExtentCollection tmp = new ExtentCollection(t, m);
				if (depth > 1) {
					tmp.getContainedEntity().all(depth - 1);
				}
				addCollection(tmp);
			}
		}
		
	}
	

	/** {@inheritDoc} */
	@Override
	public String toString() {
		if (m_parent != null && isRoot()) {
			return super.nativeToString();
		} else {
			return m_entityId;
		}
	}
	
}
