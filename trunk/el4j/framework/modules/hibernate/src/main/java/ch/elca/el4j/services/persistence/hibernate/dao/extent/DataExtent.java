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

import static ch.elca.el4j.services.persistence.hibernate.dao.extent.ExtentEntity.rootEntity;


/**
 * A DataExtent represents the extent of the graphs of objects to be
 * loaded in a hibernate query.<br>
 * Be careful in not mixing up the notion of an extent:<br>
 * In this context it is referring to the extent of the DOM to be loaded from the underlying
 * persistent store.<br>
 * In other contexts like for example OpenJPA the notion can have a different meaning.<br>
 * <br>
 * Principle:<br>
 * An Extent represents a part of the DOM that should be loaded together.<br>
 * It can be used to pool together associated entities in order 
 * to provide performance improvements over standard data fetching.<br>
 * Specifying the extent when loading entities with Hibernate 
 * allows for tuning of lazy loading and eager fetching behavior.<br>
 * For details about how to use the “ Fetch Type ” in order to control whether a field is fetched eagerly or lazily,
 * see the corresponding reference manual of Java Persistence API 
 * (eg. {@link http://java.sun.com/javaee/5/docs/api/javax/persistence/FetchType.html})<br>
 * 
 * In a DataExtent we distinguish between fields, entities and collections:<br>
 * <ul>
 * 	<li> Fields: ... TODO: java fields or fields in db??
 * 	<li> Entities: ... TODO: see fields
 * 	<li> Collections: all data types implementing the {@link java.util.Collection} interface should be
 * 		added to the extent as collection for a proper fetching at runtime.
 * </ul>
 * Note that any part of the extent that is eagerly loaded according to the JPA metadata rules cannot be changed to 
 * a lazy loading behavior with DataExtent. On the other hand, all as lazy loading indicated parts can be forced to
 * be loaded at runtime in each query.<br>
 * Also, parts of the extent that does not get loaded but accessed at runtime will throw a LazyLoadingException as it 
 * would without using DataExtent's.<br>
 * <br> 
 * Features: <br>
 *  <ul>
 *   <li> new DataExtent: provide root class and optionally the name of the root 
 *   <li> with/without: add/remove fields, sub-entities and/or collections to/from the extent.
 *   <li> withSubentities: add sub-entities to the extent, convenient for adding entities you want
 *   		to define in detail.
 *   <li> all: add everything to the graph of objects.
 *   <li> see also features of {@link ExtentEntity} to write your code in a convenient way
 *  </ul>
 *
 *  Remark: Be sure to import the static methods {@link ExtentEntity#entity} and 
 *  	{@link ExtentCollection#collection} to create easily new Entities and Collections.<br> 
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
 * 	ex.withSubentities(
 *		collection("teeth",
 *			entity(Tooth.class)
 *				.with("owner")
 *			),
 *		collection("friends", ex.getRootEntity())
 *	);
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
 * @see ch.elca.el4j.apps.refdb.dao.impl.hibernate.HibernateFileDao for an example 
 * 
 * @author Andreas Rueedlinger (ARR)
 */
public class DataExtent implements Serializable {
	/** Defines the default loading depth. */
	public static final int DEFAULT_LOADING_DEPTH = 1;
	
	/** The root entity. */
	private ExtentEntity m_rootEntity;
	
	/**
	 * Default Creator.
	 * @param c		the class of the root entity.
	 */
	public DataExtent(Class<?> c) {
		m_rootEntity = rootEntity(c);
	}
	
	/**
	 * Root entity of the extent.
	 * @return 		the root entity of the extent.
	 */
	public ExtentEntity getRootEntity() {
		return m_rootEntity;
	}
	
	/**
	 * The id of the data extent.
	 * If two extents contain the same subparts,
	 * this id should be equal.
	 * @return the id of the extent.
	 */
	public String getExtentId() {
		return m_rootEntity.getId();
	}
	
	//****************** Fluent API **********************//
	
	/**
	 * Extend the extent by the given fields.
	 * Fields are either simple properties, sub-entities or collections.
	 * @param fields	fields to be added.
	 * 
	 * @return the new DataExtent Object.
	 * @throws NoSuchMethodException 
	 */
	public DataExtent with(String... fields) throws NoSuchMethodException {
		m_rootEntity.with(fields);
		return this;
	}
	
	/**
	 * Extend the extent by the given sub-entities.
	 * @param entities	entities to be added.
	 * 
	 * @return the new DataExtent Object.
	 * @throws NoSuchMethodException 
	 */
	public DataExtent withSubentities(AbstractExtentPart... entities) throws NoSuchMethodException {
		m_rootEntity.withSubentities(entities);
		return this;
	}
	
	/**
	 * Exclude fields from the extent.
	 * Fields are either simple properties, sub-entities or collections.
	 * @param fields	fields to be excluded.
	 * 
	 * @return the new DataExtent Object.
	 */
	public DataExtent without(String...fields) {
		m_rootEntity.without(fields);
		return this;
	}

	/**
	 * Include all fields, entities and collections of the class.
	 * Exploration depth is DEFAULT_LOADING_DEPTH.
	 * 
	 * @return the new DataExtent Object.
	 */
	public DataExtent all() {
		m_rootEntity.all(DEFAULT_LOADING_DEPTH);
		return this;
	}
	
	/**
	 * Include all fields, entities and collections of the class.
	 * @param depth		Exploration depth
	 * 
	 * @return the new DataExtent Object.
	 */
	public DataExtent all(int depth) {
		m_rootEntity.all(depth);
		return this;
	}
	
	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return getExtentId().hashCode();
	}
	
	/** {@inheritDoc} */
	@Override
	public boolean equals(Object object) {
		if (super.equals(object)) {
			return true;
		} else if (object instanceof DataExtent) {
			return getExtentId().equals(((DataExtent) object).getExtentId());
		} else {
			return false;
		}
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return getExtentId();
	}

	
}
