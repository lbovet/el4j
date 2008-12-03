/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * This program is published under the GNU General Public License (GPL) license.
 * http://www.gnu.org/licenses/gpl.txt
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.tests.person.dao.impl.hibernate;

import javax.persistence.Entity;

import ch.elca.el4j.apps.refdb.dom.File;
import ch.elca.el4j.services.persistence.generic.dao.AutocollectedGenericDao;
import ch.elca.el4j.services.persistence.hibernate.dao.GenericHibernateDao;
import ch.elca.el4j.services.persistence.hibernate.dao.extent.DataExtent;
import ch.elca.el4j.services.persistence.hibernate.dao.extent.ExtentEntity;
import ch.elca.el4j.tests.person.dom.Person;
import ch.elca.el4j.tests.person.dom.Tooth;
import static ch.elca.el4j.services.persistence.hibernate.dao.extent.ExtentCollection.collection;
import static ch.elca.el4j.services.persistence.hibernate.dao.extent.ExtentEntity.entity;

/**
 *
 * Implementation of the keyword DAO which is using Hibernate.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Alex Mathey (AMA)
 */
@AutocollectedGenericDao("personDao")
public class HibernatePersonDao
	extends GenericHibernateDao<Person, Integer>
	implements GenericHibernatePersonDaoInterface {
	
	/***  Predefined Fetching extents ***/
	
	/**
	 * Light fetching variant: only fetch the normal person fields and the brain. No one-to-many associations.
	 * @return the light fetch extent.
	 */
	public static final DataExtent LIGHT_PERSON
		= new DataExtent(Person.class).all().without("friends", "teeth").freeze();
	
	/**
	 * Heavy fetching variant: fetch the whole person graph, direct friends and the teeth.
	 * @return the heavy fetch extent.
	 */
	public static final DataExtent HEAVY_PERSON
		= new DataExtent(Person.class).all().freeze();
	
	/**
	 * Load all associated entities to a depth of 2.
	 * @return the extent for the whole person graph, depth 2.
	 */
	public static final DataExtent PERSON_GRAPH_2
		=  new DataExtent(Person.class).all(2).freeze();
	
	/**
	 * Load all associated entities to a depth of 3.
	 * @return the extent for the whole person graph, depth 3.
	 */
	public static final DataExtent PERSON_GRAPH_3
		=  new DataExtent(Person.class).all(3).freeze();
	
	
}
