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
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Transient;


/**
 * A ExtentEntity represents a complex Data Type in an Extent.
 * <br>
 * Features: <br>
 *  <ul>
 *   <li> static method entity: create a new entity 
 *   <li> with/without: add/remove fields to/from the extent
 *   <li> include/exclude: add/remove sub-entities 
 *   <li> includeList/excludeList: add/remove collections (must implement
 *   		{@see java.util.Collection} interface
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
	private List<Method> m_fields;
	
	/** The child-entities of the entity. */
	private List<ExtentEntity> m_childEntities;
	
	/** The collections of the entity. */
	private List<ExtentCollection> m_collections;
	
	/**
	 * Default Creator, hidden.
	 * @param c		the class of the entity
	 */
	private ExtentEntity(Class<?> c) {
		m_name = firstCharLower(c.getSimpleName());
		m_entityClass = c;
		m_fields = new LinkedList<Method>();
		m_childEntities = new LinkedList<ExtentEntity>();
		m_collections = new LinkedList<ExtentCollection>();
	}
	
	/**
	 * Default Creator, hidden.
	 * @param name	the name of the entity
	 * @param c		the class of the entity
	 */
	private ExtentEntity(String name, Class<?> c) {
		m_name = name;
		m_entityClass = c;
		m_fields = new LinkedList<Method>();
		m_childEntities = new LinkedList<ExtentEntity>();
		m_collections = new LinkedList<ExtentCollection>();
	}
	
	/**
	 * Default Creator, hidden.
	 * @param c 		the class of the entity
	 * @param method	the method to get the entity
	 */
	private ExtentEntity(Class<?> c, Method method) {
		m_name = toFieldName(method.getName());
		m_entityClass = c;
		m_method = method;
		m_fields = new LinkedList<Method>();
		m_childEntities = new LinkedList<ExtentEntity>();
		m_collections = new LinkedList<ExtentCollection>();
	}
	
	/**
	 * Class of the entity.
	 * @return the class of the entity.
	 */
	public Class<?> getEntityClass() {
		return m_entityClass;
	}
	
	/**
	 * Set Class of the entity. Class must be consistent with
	 * the defined fields and child entities.
	 * @param c		the class of the entity.
	 * @throws NoSuchMethodException 
	 */
	public void setEntityClass(Class<?> c) throws NoSuchMethodException {
		Class<?> oldClass = m_entityClass;
		m_entityClass = c;
		if (!validateFields()) {
			m_entityClass = oldClass;
			throw new NoSuchMethodException("New Entity class is not consistent "
				+ "with defined fields and child-entities.");
		}
		
	}
	
	/**
	 * Field-methods of the entity.
	 * @return the field-methods of the entity.
	 */
	public List<Method> getFields() {
		return m_fields;
	}

	/**
	 * Fields of the entity as getter names.
	 * @return the getter names of the entity.
	 */
	/*public List<String> getGetters() {
		List<String> getters = new LinkedList<String>();
		for (String s : m_fields) {
			getters.add("get" + firstCharUpper(s));
		}
		return getters;
	}*/
	
	/**
	 * Set Field-methods of the entity.
	 * @param fields	 the field-methods of the entity.
	 * @throws NoSuchMethodException 
	 */
	public void setFields(List<Method> fields) throws NoSuchMethodException {
		if (validateFields(fields)) {
			m_fields = fields;
		} else {
			throw new NoSuchMethodException("Fields to add does not conform with entity class.");
		}
	}
	
	/**
	 * Set Field-methods of the entity as a list of strings.
	 * @param names	 the field-names of the entity.
	 * @throws NoSuchMethodException 
	 */
	public void setFieldsAsNames(List<String> names) throws NoSuchMethodException {
		for (String s : names) {
			String mName = toGetterName(s);
			if (validateFieldName(mName)) {
				m_fields.add(m_entityClass.getMethod(mName));
			}
		}
	}
	
	/**
	 * Add a field-method to the fields of the entity.
	 * @param field	 the field-method to add.
	 */
	public void addField(Method field) {
		if (validateField(field)) {
			m_fields.add(field);
		}
	}
	
	/**
	 * Add a field-method to the fields of the entity as string.
	 * @param name	 the field-method to add.
	 * @throws NoSuchMethodException 
	 */
	public void addFieldAsName(String name) throws NoSuchMethodException {
		String mName = toGetterName(name);
		m_fields.add(m_entityClass.getMethod(mName));
	}
	
	/**
	 * Remove a field-method from the fields of the entity.
	 * @param field	 the field-method to remove.
	 * @return returns the success of the operation
	 */
	public boolean removeField(Method field) {
		return m_fields.remove(field);
	}
	
	/**
	 * Remove a field from the fields of the entity as name.
	 * @param name	 the field as name to remove.
	 * @return returns the success of the operation
	 */
	public boolean removeField(String name) {
		for (Method m : m_fields) {
			if (m.getName().equals(toGetterName(name))) {
				return m_fields.remove(m);
			}
		}
		return false;
	}
	
	/**
	 * Remove field-methods from the fields of the entity.
	 * @param fields	 the field-methods to remove.
	 * @return returns the success of the operation
	 */
	public boolean removeFields(Method... fields) {
		return m_fields.remove(fields);
	}
	
	/**
	 * Remove fields as names from the fields of the entity.
	 * @param names	 the field names to remove.
	 * @return returns the success of the operation
	 */
	public boolean removeFields(String... names) {
		for (String name : names) {
			if (!removeField(name)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Child entities.
	 * @return the child entities of the entity.
	 */
	public List<ExtentEntity> getChildEntities() {
		return m_childEntities;
	}
	
	/**
	 * Set Child entities. Methods of child entities are set and consistency
	 * to parent class is checked.
	 * @param children	 the child entities of the entity.
	 * @throws NoSuchMethodException 
	 */
	public void setChildEntities(List<ExtentEntity> children) throws NoSuchMethodException {
		for (ExtentEntity ent : children) {
			Method m  = m_entityClass.getMethod(ent.getGetterName());
			ent.setMethod(m);
		}
		m_childEntities = children;
	}
	
	/**
	 * Add a child-entity to the entity. Method of child entity is set and consistency
	 * to parent class is checked.
	 * @param child	 the child to add.
	 * @throws NoSuchMethodException 
	 */
	public void addChildEntity(ExtentEntity child) throws NoSuchMethodException {
		Method m  = m_entityClass.getMethod(child.getGetterName());
		child.setMethod(m);
		m_childEntities.add(child);
	}
	
	/**
	 * Remove an entity from the children of the entity.
	 * @param name	 name of the entity to remove.
	 * @return returns the success of the operation
	 */
	public boolean removeEntity(String name) {
		for (ExtentEntity e : m_childEntities) {
			if (e.getName().equals(name)) {
				m_childEntities.remove(e);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Remove an entity from the children of the entity.
	 * @param c	 class of the entity to remove.
	 * @return returns the success of the operation
	 */
	public boolean removeEntity(Class<?> c) {
		for (ExtentEntity e : m_childEntities) {
			if (e.getEntityClass().equals(c)) {
				m_childEntities.remove(e);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Collections.
	 * @return the collections of the entity.
	 */
	public List<ExtentCollection> getCollections() {
		return m_collections;
	}
	
	/**
	 * Set Child collections.
	 * @param collections	 the collections of the entity.
	 * @throws NoSuchMethodException 
	 */
	public void setCollections(List<ExtentCollection> collections) throws NoSuchMethodException {
		for (ExtentCollection c : collections) {
			Method m  = m_entityClass.getMethod(c.getGetterName());
			c.setMethod(m);
		}
		m_collections = collections;
	}
	
	/**
	 * Add a collection to the entity.
	 * @param collection	 the collection to add.
	 * @throws NoSuchMethodException 
	 */
	public void addCollection(ExtentCollection collection) throws NoSuchMethodException {
		Method m  = m_entityClass.getMethod(collection.getGetterName());
		collection.setMethod(m);
		m_collections.add(collection);
	}
	
	/**
	 * Remove a collection from the collections of the entity.
	 * @param name	 name of the collection to remove.
	 * @return returns the success of the operation
	 */
	public boolean removeCollection(String name) {
		for (ExtentCollection e : m_collections) {
			if (e.getName().equals(name)) {
				m_collections.remove(e);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Remove a collection from the collections of the entity.
	 * @param c	 class of the collection to remove.
	 * @return returns the success of the operation
	 */
	public boolean removeCollection(Class<?> c) {
		for (ExtentCollection e : m_collections) {
			if (e.getName().equals(c)) {
				m_collections.remove(e);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Validates the correctness of the fields of this entity.
	 * @return if the fields of the entity is valid
	 */
	private boolean validateFields() {
		return validateFields(m_fields);
	}
	
	/**
	 * Checks the validation of the given methods in this entity.
	 * @param fields	the fields to validate.
	 * @return if the fields are valid
	 */
	private boolean validateFields(List<Method> fields) {
		for (Method m : fields) {
			if (!validateField(m)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Checks the validation of the given method in this entity.
	 * @param field		the field to validate.
	 * @return if the field is valid
	 */
	private boolean validateField(Method field) {
		try {
			Method m = m_entityClass.getMethod(field.getName());
			if (m.equals(field)) {
				return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Checks the validation of the given field in this entity.
	 * @param name		the field-name to validate.
	 * @return if the field is valid
	 */
	private boolean validateFieldName(String name) {
		try {
			m_entityClass.getMethod(name);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	
	
	//*************** Fluent API ******************//
	
	
	/**
	 * Returns a new Entity object, based on the given class.
	 * @param c		the class of the entity.
	 * @return	the Entity object.
	 */
	static public ExtentEntity entity(Class<?> c) {
		return new ExtentEntity(c);
	}
	
	/**
	 * Returns a new Entity object, based on the given name and class.
	 * @param name	the name of the entity.
	 * @param c		the class of the entity.
	 * @return	the Entity object.
	 */
	static public ExtentEntity entity(String name, Class<?> c) {
		return new ExtentEntity(name, c);
	}
	
	/**
	 * Returns a new Entity object, based on the given class and method.
	 * @param c		the class of the entity.
	 * @param m		the method to get the entity.
	 * @return	the Entity object.
	 */
	static public ExtentEntity entity(Class<?> c, Method m) {
		return new ExtentEntity(c, m);
	}
	
	/**
	 * Extend the entity by fields.
	 * @param fields	fields to be added.
	 * 
	 * @return the new ExtentEntity Object.
	 * @throws NoSuchMethodException 
	 */
	public ExtentEntity with(String... fields) throws NoSuchMethodException {
		for (String s : fields) {
			addFieldAsName(s);
		}
		return this;
	}
	
	/**
	 * Exclude fields from the entity.
	 * @param fields	fields to be excluded.
	 * 
	 * @return the new ExtentEntity Object.
	 */
	public ExtentEntity without(String...fields) {
		removeFields(fields);
		return this;
	}
	
	/**
	 * Extend the entity by an entity given as class,
	 * name is the name of the class.
	 * If fields are empty, all the fields/entities of the class are added.
	 * @param c			class of the entity to include.
	 * @param fields	fields to be added to the included entity.
	 * 
	 * @return the new ExtentEntity Object.
	 * @throws NoSuchMethodException 
	 */
	public ExtentEntity include(Class<?> c, String... fields) throws NoSuchMethodException {
		return include(c.getSimpleName(), c, fields);
	}
	
	/**
	 * Extend the entity by an entity given as class,
	 * name is the name of the class.
	 * Explore all the fields and sub-entities to given depth.
	 * @param c			class of the entity to include.
	 * @param depth		exploration-depth.
	 * 
	 * @return the new ExtentEntity Object.
	 * @throws NoSuchMethodException 
	 */
	public ExtentEntity include(Class<?> c, int depth) throws NoSuchMethodException {
		return include(c.getSimpleName(), c, depth);
	}
	
	/**
	 * Extend the entity by an entity given as class and name.
	 * If fields are empty, all the fields/entities of the class are added.
	 * @param name		name of the entity.
	 * @param c			class of the entity to include.
	 * @param fields	fields to be added to the included entity.
	 * 
	 * @return the new ExtentEntity Object.
	 * @throws NoSuchMethodException 
	 */
	public ExtentEntity include(String name, Class<?> c, String... fields) throws NoSuchMethodException {
		ExtentEntity tmp = entity(name, c);
		if (fields.length > 0) {
			for (String s : fields) {
				tmp.addFieldAsName(s);
			}
		} else {
			// Fetch all fields if empty
			tmp.fetchAllFields(DataExtent.DEFAULT_LOADING_DEPTH);
		}
		addChildEntity(tmp);
		return this;
	}
	
	/**
	 * Extend the entity by an entity given as class and name.
	 * Explore all the fields and sub-entities to given depth.
	 * @param name		name of the entity.
	 * @param c			class of the entity to include.
	 * @param depth		exploration-depth.
	 * 
	 * @return the new ExtentEntity Object.
	 * @throws NoSuchMethodException 
	 */
	public ExtentEntity include(String name, Class<?> c, int depth) throws NoSuchMethodException {
		ExtentEntity tmp = entity(name, c);

		addChildEntity(tmp.fetchAllFields(depth));
		return this;
	}
	
	/**
	 * Extend the entity by the given entity.
	 * @param entity	the entity to add.
	 * @return	the new ExtentEntity Object.
	 * @throws NoSuchMethodException 
	 */
	public ExtentEntity include(ExtentEntity entity) throws NoSuchMethodException {
		addChildEntity(entity);
		return this;
	}
	
	/**
	 * Extend the entity by an entity given as class,
	 * name is the name of the class.
	 * If fields are empty, all the fields/entities of the class are added.
	 * @param c			class of the entity to include.
	 * @param fields	fields to be added to the included entity.
	 * 
	 * @return the new ExtentEntity Object.
	 * @throws NoSuchMethodException 
	 */
	public ExtentEntity includeList(Class<?> c, String... fields) throws NoSuchMethodException {
		return includeList(c.getSimpleName(), c, fields);
	}
	
	/**
	 * Extend the entity by an entity given as class,
	 * name is the name of the class.
	 * Explore all the fields and sub-entities to given depth.
	 * @param c			class of the entity to include.
	 * @param depth		exploration-depth.
	 * 
	 * @return the new ExtentEntity Object.
	 * @throws NoSuchMethodException 
	 */
	public ExtentEntity includeList(Class<?> c, int depth) throws NoSuchMethodException {
		return includeList(c.getSimpleName(), c, depth);
	}
	
	/**
	 * Extend the entity by an entity given as class and name.
	 * If fields are empty, all the fields/entities of the class are added.
	 * @param name		name of the entity.
	 * @param c			class of the entity to include.
	 * @param fields	fields to be added to the included entity.
	 * 
	 * @return the new ExtentEntity Object.
	 * @throws NoSuchMethodException 
	 */
	public ExtentEntity includeList(String name, Class<?> c, String... fields) throws NoSuchMethodException {
		ExtentCollection tmp = new ExtentCollection(name, c);
		if (fields.length > 0) {
			for (String s : fields) {
				tmp.getContainedEntity().addFieldAsName(s);
			}
		} else {
			// Fetch all fields if empty
			tmp.getContainedEntity().fetchAllFields(DataExtent.DEFAULT_LOADING_DEPTH);
		}
		addCollection(tmp);
		return this;
	}
	
	/**
	 * Extend the  by an entity given as class and name.
	 * Explore all the fields and sub-entities to given depth.
	 * @param name		name of the entity.
	 * @param c			class of the entity to include.
	 * @param depth		exploration-depth.
	 * 
	 * @return the new ExtentEntity Object.
	 * @throws NoSuchMethodException 
	 */
	public ExtentEntity includeList(String name, Class<?> c, int depth) throws NoSuchMethodException {
		ExtentCollection tmp = new ExtentCollection(name, c);
		tmp.getContainedEntity().fetchAllFields(depth);
		addCollection(tmp);
		return this;
	}
	
	/**
	 * Extend the  by a collection containing the given entity.
	 * @param entity	the entity in the collection to be added.
	 * @return the new ExtentEntity Object.
	 * @throws NoSuchMethodException 
	 */
	public ExtentEntity includeList(ExtentEntity entity) throws NoSuchMethodException {
		String name = entity.getName();
		return includeList(name, entity);
	}
	
	/**
	 * Extend the  by a collection containing the given entity,
	 * name of the collection is name.
	 * @param name		the name of the collection.
	 * @param entity	the entity in the collection to be added.
	 * @return the new ExtentEntity Object.
	 * @throws NoSuchMethodException 
	 */
	public ExtentEntity includeList(String name, ExtentEntity entity) throws NoSuchMethodException {
		ExtentCollection tmp = new ExtentCollection(name);
		tmp.setContainedEntity(entity);
		addCollection(tmp);
		return this;
	}
	
	
	/**
	 * Exclude the entity/collection given as string from the .
	 * @param name		name of the entity/collection.
	 * 
	 * @return the new ExtentEntity Object.
	 */
	public ExtentEntity exclude(String name) {
		if (!removeEntity(name)) {
			removeCollection(name);
		}
		return this;
	}
	
	/**
	 * Exclude the entity given as class from the .
	 * @param c		class of the entity.
	 * 
	 * @return the new ExtentEntity Object.
	 */
	public ExtentEntity exclude(Class<?> c) {
		if (!removeEntity(c)) {
			removeCollection(c);
		}
		return this;
	}
	
	/**
	 * Add all fields to the entity.
	 * @param depth		Exploration depth.
	 * @return the new ExtentEntity Object.
	 */
	public ExtentEntity fetchAllFields(int depth) {
		if (depth > 0) {
			try {
				for (Method m : m_entityClass.getMethods()) {
					fetchMethod(m, depth);
				}
			} catch (NoSuchMethodException e) {
				// Method exists as we came here having found it!
			}
		}
		return this;
	}
	
	/**
	 * Add the method to the entity in the appropriate manner,
	 * as field, entity or collection.
	 * @param m			the method to be added.
	 * @param depth		Exploration depth.
	 * @throws NoSuchMethodException 
	 */
	private void fetchMethod(Method m, int depth) throws NoSuchMethodException {
		// Fetch only the methods with getter and no arguments
		// Exclude getClass() and Transient fields
		boolean isGetter = m.getName().startsWith("get") && m.getParameterTypes().length == 0;
		if (isGetter && !m.getName().equals("getClass") 
			&& m.getAnnotation(Transient.class) == null && !m.getName().equals("get")) {

			if (m.getReturnType().isPrimitive() || m.getReturnType().isEnum()
				|| m.getReturnType().equals(String.class)) {
				
				addField(m);
			} else if (Collection.class.isAssignableFrom(m.getReturnType())) {
				fetchCollection(m, depth);
			} else {
				ExtentEntity tmp = entity(m.getReturnType(), m);
				if (depth > 1) {
					tmp.fetchAllFields(depth - 1);
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
				String collectionName = m.getName().substring(3);
				collectionName = collectionName.substring(0, 1).toLowerCase() + collectionName.substring(1);
				ExtentCollection tmp = new ExtentCollection(t, m);
				if (depth > 1) {
					tmp.getContainedEntity().fetchAllFields(depth - 1);
				}
				addCollection(tmp);
			}
		}
		
	}
	
}