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

import static ch.elca.el4j.services.persistence.hibernate.dao.extent.ExtentEntity.entity;

/**
 * An ExtentCollection represents a collection of an entity.
 * <br>
 * Features: <br>
 *  <ul>
 *   <li> static method collection: create a new entity-collection.
 *  </ul>
 * For further details, see {@link DataExtent}. 
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
public class ExtentCollection extends AbstractExtentPart {
	
	/** The sub-entity of the collection. */
	private ExtentEntity m_containedEntity;
	
	/** The id of the entity. */
	private String m_collectionId;
	
	/**
	 * Default Creator.
	 * @param name	the name of the collection
	 */
	public ExtentCollection(String name) {
		m_name = name;
		m_collectionId = name;
	}
	/**
	 * Default Creator.
	 * @param name	the name of the collection
	 * @param c		the class of the contained entity
	 */
	public ExtentCollection(String name, Class<?> c) {
		m_name = name;
		m_containedEntity = entity(c);
		m_collectionId = m_name + "[" + m_containedEntity.toString() + "]";
		m_containedEntity.setParent(this);
	}
	
	/**
	 * Default Creator, hidden.
	 * @param c 		the class of the contained entity
	 * @param method	the method to get the collection
	 */
	public ExtentCollection(Class<?> c, Method method) {
		m_name = toFieldName(method);
		m_containedEntity = entity(c);
		m_collectionId = m_name + "[" + m_containedEntity.toString() + "]";
		m_containedEntity.setParent(this);
	}
	
	/** {@inheritDoc} */
	public String getId() {
		return m_collectionId;
	}
	
	/** {@inheritDoc} */
	public void updateId() {
		String id = m_name + "[" + m_containedEntity.toString() + "]";
		if (!m_collectionId.equals(id)) {
			m_collectionId = id;
			if (m_parent != null) {
				m_parent.updateId();
			}
		}
	}
	
	/**
	 * Contained entity of the collection.
	 * @return the name of the field.
	 */
	public ExtentEntity getContainedEntity() {
		return m_containedEntity;
	}
	
	/**
	 * Set the entity contained in this collection.
	 * @param entity	 the contained entity of the collection.
	 */
	public void setContainedEntity(ExtentEntity entity) {
		// Method and Name of entity does not matter
		m_containedEntity = entity;
		m_containedEntity.setParent(this);
		m_collectionId = m_name + "[" + m_containedEntity.toString() + "]";
		
	}
	
	/**
	 * Returns a new Collection object, based on the given name and class.
	 * @param name	the name of the collection.
	 * @param c		the class of the contained entity.
	 * @return	the Collection object.
	 */
	public static ExtentCollection collection(String name, Class<?> c) {
		return new ExtentCollection(name, c);
	}
	
	/**
	 * Returns a new Collection object, based on the given class and method.
	 * @param c		the class of the contained entity.
	 * @param m		the method to get the collection.
	 * @return	the Collection object.
	 */
	public static ExtentCollection collection(Class<?> c, Method m) {
		return new ExtentCollection(c, m);
	}
	
	/**
	 * Returns a new Collection object, based on the given name and entity.
	 * @param name			the name of the collection.
	 * @param entity		the contained entity.
	 * @return	the Collection object.
	 */
	public static ExtentCollection collection(String name, ExtentEntity entity) {
		ExtentCollection c = new ExtentCollection(name);
		c.setContainedEntity(entity);
		return c;
		
	}
	
}
