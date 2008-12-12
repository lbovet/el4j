/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.services.persistence.hibernate.offlining.util;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

import ch.elca.el4j.services.persistence.generic.dao.AutocollectedGenericDao;
import ch.elca.el4j.services.persistence.hibernate.dao.GenericHibernateDao;

/**
 * DAO for hibernate access to properties.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author David Bernhard (DBD)
 */
@AutocollectedGenericDao(value = "propertyDao")
public class PropertyDao extends GenericHibernateDao<OfflinerProperty, Integer>
	implements PropertyDaoInterface {
	
	/**
	 * Check if a property exists.
	 * @param name The property name.
	 * @return <code>true</code> if the property exists.
	 */
	@Transactional
	public boolean isPropertyPresent(String name) {
		return getByName(name) != null;
	}
		
	/**
	 * Get an integer-valued property.
	 * @param name The property name.
	 * @return The property value.
	 */
	@Transactional
	public int getIntProperty(String name) {
		OfflinerProperty p = getByName(name);
		if (p == null) {
			throw new IllegalArgumentException("No property of name " + name);
		}
		return Integer.parseInt(p.getPropertyValue());
	}
	
	/**
	 * Get a string-valued property.
	 * @param name The property name.
	 * @return The property value.
	 */
	@Transactional
	public String getStringProperty(String name) {
		OfflinerProperty p = getByName(name);
		if (p == null) {
			throw new IllegalArgumentException("No property of name " + name);
		}
		return p.getPropertyValue();
	}
	
	/**
	 * Get a property by name.
	 * @param name The property name.
	 * @return The OfflinerProperty element, if it exists, otherwise <code>null</code>.
	 */
	@Transactional
	private OfflinerProperty getByName(String name) {
		List<OfflinerProperty> list = findByCriteria(
			DetachedCriteria.forClass(OfflinerProperty.class)
				.add(Restrictions.eq("propertyName", name)));
		if (list.isEmpty()) {
			return null;
		} else {
			return list.get(0);
		}
	}
	
	/**
	 * Save a property.
	 * @param name The property name.
	 * @param value The property value.
	 */
	@Transactional
	public void saveProperty(String name, String value) {
		OfflinerProperty p = getByName(name);
		if (p == null) {
			p = new OfflinerProperty();
			p.setPropertyName(name);
		}
		p.setPropertyValue(value);
		saveOrUpdateAndFlush(p);
		
		// Just to be sure.
		OfflinerProperty saved = getByName(name);
		if (saved == null) {
			throw new AssertionError("Saving property failed.");
		}
	}
	
	/**
	 * Save a property.
	 * @param name The property name.
	 * @param value The property value.
	 */
	@Transactional
	public void saveProperty(String name, int value) {
		saveProperty(name, Integer.toString(value));
	}
}
