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
package ch.elca.el4j.services.persistence.hibernate.dao.extent;

import java.lang.reflect.Method;

import org.springframework.util.Assert;

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
	
	/** Is the ExtentCollection frozen, eg. must not be changed anymore. */
	private boolean m_frozen;
	
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
	protected void updateId() {
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
		Assert.state(!m_frozen, "DataExtent is frozen and cannot be changed anymore");
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
	
	/**
	 * Merge two ExtentCollections. Returns the union of the extents.
	 * The class of the two contained entities of the collections should be the same,
	 * the name and the parent is taken from this object.
	 * @param other	the collection to be merged with.
	 * @return	the merged collection.
	 */
	public ExtentCollection merge(ExtentCollection other) {
		Assert.state(!m_frozen, "DataExtent is frozen and cannot be changed anymore");
		if (m_containedEntity.getEntityClass().equals(other.m_containedEntity.getClass()) && !this.equals(other)) {
			m_containedEntity.merge(other.m_containedEntity);
		}
		return this;
	}
	
	/**
	 * Freeze the ExtentCollection, meaning that no further changes to it are possible.
	 * @return the frozen ExtentCollection.
	 */
	public ExtentCollection freeze() {
		if (!m_frozen) {
			m_frozen = true;
			m_containedEntity.freeze();
		}
		return this;
	}
	
}
