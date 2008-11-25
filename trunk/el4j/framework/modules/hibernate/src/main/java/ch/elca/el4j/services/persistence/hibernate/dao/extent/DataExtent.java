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

import static ch.elca.el4j.services.persistence.hibernate.dao.extent.ExtentEntity.entity;


/**
 * A DataExtent represents the extent of the graphs of objects to be
 * loaded in a hibernate query.<br>
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
 * @author Andreas Rueedlinger (ARR)
 */
public class DataExtent implements Serializable {
	/** Defines the default loading depth. */
	public static final int DEFAULT_LOADING_DEPTH = 1;
	
	/** The root entity. */
	private ExtentEntity m_rootEntity;
	
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

	
}
