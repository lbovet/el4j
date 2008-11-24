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

import ch.elca.el4j.util.codingsupport.BeanPropertyUtils;


/**
 * A ExtentEntity represents a complex Data Type in an Extent.
 * <br>
 * Features: <br>
 *  <ul>
 *   <li> static method entity: create a new entity 
 *   <li> with/without: add/remove fields to/from the extent
 *   <li> include/exclude: add/remove sub-entities 
 *   <li> includeList/excludeList: add/remove collections (must implement
 *   		{@link java.util.Collection} interface
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
	
	/**
	 * Default Creator, hidden.
	 * @param c		the class of the entity
	 */
	private ExtentEntity(Class<?> c) {
		m_name = firstCharLower(c.getSimpleName());
		m_entityClass = c;
		m_fields = new LinkedList<String>();
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
		m_fields = new LinkedList<String>();
		m_childEntities = new LinkedList<ExtentEntity>();
		m_collections = new LinkedList<ExtentCollection>();
	}
	
	/**
	 * Default Creator, hidden.
	 * @param c 		the class of the entity
	 * @param method	the method to get the entity
	 */
	private ExtentEntity(Class<?> c, Method method) {
		m_name = toFieldName(method);
		m_entityClass = c;
		//m_method = method;
		m_fields = new LinkedList<String>();
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
	 * Add a field-method to the fields of the entity.
	 * @param field	 the field-method to add.
	 * @throws NoSuchMethodException 
	 * @throws SecurityException 
	 */
	private void addField(String field) {
		m_fields.add(field);
	}
	
	/**
	 * Remove a field from the fields of the entity as name.
	 * @param name	 the field as name to remove.
	 * @return returns the success of the operation
	 */
	private boolean removeField(String name) {
		/*for (Method m : m_fields) {
			if (m.getName().equals(toGetterName(name))) {
				return m_fields.remove(m);
			}
		}*/
		return m_fields.remove(name);
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
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Validates the correctness of the fields of this entity.
	 * @return if the fields of the entity is valid
	 *//*
	private boolean validateFields() {
		return validateFields(m_fields);
	}
	
	*//**
	 * Checks the validation of the given methods in this entity.
	 * @param fields	the fields to validate.
	 * @return if the fields are valid
	 *//*
	private boolean validateFields(List<Method> fields) {
		for (Method m : fields) {
			if (!validateField(m)) {
				return false;
			}
		}
		return true;
	}
	
	*//**
	 * Checks the validation of the given method in this entity.
	 * @param field		the field to validate.
	 * @return if the field is valid
	 *//*
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
	}*/
	
	/**
	 * Checks the validation of the given field in this entity.
	 * @param name		the field-name to validate.
	 * @return if the field is valid
	 *//*
	private boolean validateFieldName(String name) {
		try {
			m_entityClass.getMethod(name);
			return true;
		} catch (Exception e) {
			return false;
		}
	}*/
	
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
	 * Extend the entity by an entity given as class,
	 * name is the name of the class.
	 * If fields are empty, all the fields/entities of the class are added.
	 * @param c			class of the entity to include.
	 * @param fields	fields to be added to the included entity.
	 * 
	 * @return the new ExtentEntity Object.
	 * @throws NoSuchMethodException 
	 *//*
	public ExtentEntity include(Class<?> c, String... fields) throws NoSuchMethodException {
		return include(c.getSimpleName(), c, fields);
	}
	
	*//**
	 * Extend the entity by an entity given as class,
	 * name is the name of the class.
	 * Explore all the fields and sub-entities to given depth.
	 * @param c			class of the entity to include.
	 * @param depth		exploration-depth.
	 * 
	 * @return the new ExtentEntity Object.
	 * @throws NoSuchMethodException 
	 *//*
	public ExtentEntity include(Class<?> c, int depth) throws NoSuchMethodException {
		return include(c.getSimpleName(), c, depth);
	}
	
	*//**
	 * Extend the entity by an entity given as class and name.
	 * If fields are empty, all the fields/entities of the class are added.
	 * @param name		name of the entity.
	 * @param c			class of the entity to include.
	 * @param fields	fields to be added to the included entity.
	 * 
	 * @return the new ExtentEntity Object.
	 * @throws NoSuchMethodException 
	 *//*
	public ExtentEntity include(String name, Class<?> c, String... fields) throws NoSuchMethodException {
		ExtentEntity tmp = entity(name, c);
		if (fields.length > 0) {
			for (String s : fields) {
				tmp.addMethodAsName(s);
			}
		} else {
			// Fetch all fields if empty
			tmp.fetchAllFields(DataExtent.DEFAULT_LOADING_DEPTH);
		}
		addChildEntity(tmp);
		return this;
	}
	
	*//**
	 * Extend the entity by an entity given as class and name.
	 * Explore all the fields and sub-entities to given depth.
	 * @param name		name of the entity.
	 * @param c			class of the entity to include.
	 * @param depth		exploration-depth.
	 * 
	 * @return the new ExtentEntity Object.
	 * @throws NoSuchMethodException 
	 *//*
	public ExtentEntity include(String name, Class<?> c, int depth) throws NoSuchMethodException {
		ExtentEntity tmp = entity(name, c);

		addChildEntity(tmp.fetchAllFields(depth));
		return this;
	}
	
	*//**
	 * Extend the entity by the given entity.
	 * @param entity	the entity to add.
	 * @return	the new ExtentEntity Object.
	 * @throws NoSuchMethodException 
	 *//*
	public ExtentEntity include(ExtentEntity entity) throws NoSuchMethodException {
		addChildEntity(entity);
		return this;
	}
	
	*//**
	 * Extend the entity by an entity given as class,
	 * name is the name of the class.
	 * If fields are empty, all the fields/entities of the class are added.
	 * @param c			class of the entity to include.
	 * @param fields	fields to be added to the included entity.
	 * 
	 * @return the new ExtentEntity Object.
	 * @throws NoSuchMethodException 
	 *//*
	public ExtentEntity includeList(Class<?> c, String... fields) throws NoSuchMethodException {
		return includeList(c.getSimpleName(), c, fields);
	}
	
	*//**
	 * Extend the entity by an entity given as class,
	 * name is the name of the class.
	 * Explore all the fields and sub-entities to given depth.
	 * @param c			class of the entity to include.
	 * @param depth		exploration-depth.
	 * 
	 * @return the new ExtentEntity Object.
	 * @throws NoSuchMethodException 
	 *//*
	public ExtentEntity includeList(Class<?> c, int depth) throws NoSuchMethodException {
		return includeList(c.getSimpleName(), c, depth);
	}
	
	*//**
	 * Extend the entity by an entity given as class and name.
	 * If fields are empty, all the fields/entities of the class are added.
	 * @param name		name of the entity.
	 * @param c			class of the entity to include.
	 * @param fields	fields to be added to the included entity.
	 * 
	 * @return the new ExtentEntity Object.
	 * @throws NoSuchMethodException 
	 *//*
	public ExtentEntity includeList(String name, Class<?> c, String... fields) throws NoSuchMethodException {
		ExtentCollection tmp = new ExtentCollection(name, c);
		if (fields.length > 0) {
			for (String s : fields) {
				tmp.getContainedEntity().addMethodAsName(s);
			}
		} else {
			// Fetch all fields if empty
			tmp.getContainedEntity().fetchAllFields(DataExtent.DEFAULT_LOADING_DEPTH);
		}
		addCollection(tmp);
		return this;
	}
	
	*//**
	 * Extend the  by an entity given as class and name.
	 * Explore all the fields and sub-entities to given depth.
	 * @param name		name of the entity.
	 * @param c			class of the entity to include.
	 * @param depth		exploration-depth.
	 * 
	 * @return the new ExtentEntity Object.
	 * @throws NoSuchMethodException 
	 *//*
	public ExtentEntity includeList(String name, Class<?> c, int depth) throws NoSuchMethodException {
		ExtentCollection tmp = new ExtentCollection(name, c);
		tmp.getContainedEntity().fetchAllFields(depth);
		addCollection(tmp);
		return this;
	}
	
	*//**
	 * Extend the  by a collection containing the given entity.
	 * @param entity	the entity in the collection to be added.
	 * @return the new ExtentEntity Object.
	 * @throws NoSuchMethodException 
	 *//*
	public ExtentEntity includeList(ExtentEntity entity) throws NoSuchMethodException {
		String name = entity.getName();
		return includeList(name, entity);
	}
	
	*//**
	 * Extend the  by a collection containing the given entity,
	 * name of the collection is name.
	 * @param name		the name of the collection.
	 * @param entity	the entity in the collection to be added.
	 * @return the new ExtentEntity Object.
	 * @throws NoSuchMethodException 
	 *//*
	public ExtentEntity includeList(String name, ExtentEntity entity) throws NoSuchMethodException {
		ExtentCollection tmp = new ExtentCollection(name);
		tmp.setContainedEntity(entity);
		addCollection(tmp);
		return this;
	}
	
	
	*//**
	 * Exclude the entity/collection given as string from the .
	 * @param name		name of the entity/collection.
	 * 
	 * @return the new ExtentEntity Object.
	 *//*
	public ExtentEntity exclude(String name) {
		if (!removeEntity(name)) {
			removeCollection(name);
		}
		return this;
	}
	
	*//**
	 * Exclude the entity given as class from the .
	 * @param c		class of the entity.
	 * 
	 * @return the new ExtentEntity Object.
	 *//*
	public ExtentEntity exclude(Class<?> c) {
		if (!removeEntity(c)) {
			removeCollection(c);
		}
		return this;
	}*/
	
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
		// Exclude getClass() and transient getters
		boolean isGetter = m.getName().startsWith("get") && m.getParameterTypes().length == 0;
		if (isGetter && !m.getName().equals("getClass") && !m.getName().equals("get")
			/*&& m.getAnnotation(Transient.class) == null*/) {

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
		} else {
			throw new NoSuchMethodException("No persistent field of the data type.");
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
	
	//******** trash *************//
	
	/**
	 * Set Class of the entity. Class must be consistent with
	 * the defined fields and child entities.
	 * @param c		the class of the entity.
	 * @throws NoSuchMethodException 
	 *//*
	private void setEntityClass(Class<?> c) throws NoSuchMethodException {
		Class<?> oldClass = m_entityClass;
		m_entityClass = c;
		if (!validateFields()) {
			m_entityClass = oldClass;
			throw new NoSuchMethodException("New Entity class is not consistent "
				+ "with defined fields and child-entities.");
		}
		
	}
	
	*//**
	 * Set Field-methods of the entity.
	 * @param fields	 the field-methods of the entity.
	 * @throws NoSuchMethodException 
	 *//*
	private void setFields(List<Method> fields) throws NoSuchMethodException {
		if (validateFields(fields)) {
			m_fields = fields;
		} else {
			throw new NoSuchMethodException("Fields to add does not conform with entity class.");
		}
	}
	
	*//**
	 * Set Field-methods of the entity as a list of strings.
	 * @param names	 the field-names of the entity.
	 * @throws NoSuchMethodException 
	 *//*
	private void setFieldsAsNames(List<String> names) throws NoSuchMethodException {
		for (String s : names) {
			String mName = toGetterName(s);
			if (validateFieldName(mName)) {
				m_fields.add(m_entityClass.getMethod(mName));
			}
		}
	}*/
	
	/**
	 * Remove a field-method from the fields of the entity.
	 * @param field	 the field-method to remove.
	 * @return returns the success of the operation
	 *//*
	private boolean removeField(Method field) {
		return m_fields.remove(field);
	}
	
	
	*//**
	 * Remove field-methods from the fields of the entity.
	 * @param fields	 the field-methods to remove.
	 * @return returns the success of the operation
	 *//*
	private boolean removeFields(Method... fields) {
		return m_fields.remove(fields);
	}
	
	*//**
	 * Set Child entities. Methods of child entities are set and consistency
	 * to parent class is checked.
	 * @param children	 the child entities of the entity.
	 * @throws NoSuchMethodException 
	 *//*
	private void setChildEntities(List<ExtentEntity> children) throws NoSuchMethodException {
		for (ExtentEntity ent : children) {
			Method m  = m_entityClass.getMethod(ent.getGetterName());
			ent.setMethod(m);
		}
		m_childEntities = children;
	}*/

	/**
	 * Remove an entity from the children of the entity.
	 * @param c	 class of the entity to remove.
	 * @return returns the success of the operation
	 *//*
	private boolean removeEntity(Class<?> c) {
		for (ExtentEntity e : m_childEntities) {
			if (e.getEntityClass().equals(c)) {
				m_childEntities.remove(e);
				return true;
			}
		}
		return false;
	}*/

	/**
	 * Set Child collections.
	 * @param collections	 the collections of the entity.
	 * @throws NoSuchMethodException 
	 *//*
	private void setCollections(List<ExtentCollection> collections) throws NoSuchMethodException {
		for (ExtentCollection c : collections) {
			Method m  = m_entityClass.getMethod(c.getGetterName());
			c.setMethod(m);
		}
		m_collections = collections;
	}*/

	/**
	 * Remove a collection from the collections of the entity.
	 * @param c	 class of the collection to remove.
	 * @return returns the success of the operation
	 *//*
	private boolean removeCollection(Class<?> c) {
		for (ExtentCollection e : m_collections) {
			if (e.getName().equals(c)) {
				m_collections.remove(e);
				return true;
			}
		}
		return false;
	}
	*/
}
