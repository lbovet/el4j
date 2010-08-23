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
package ch.elca.el4j.services.persistence.generic.dao;

import java.lang.reflect.Field;

/**
 * This class is container for a collection containing object, holding both the instance and the field
 * giving access to the collection.
 * This container is used in the {@link AbstractIdentityFixer} to remember which collections has to be
 * replaced in the 2-way merging.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Andreas Rueedlinger (ARR)
 */
public class IdentityFixerCollectionField {
	/** See corresponding getter for information. */
	private Object m_instance;
	
	/** See corresponding getter for information. */
	private Field m_field;
	
	/**
	 * Constructs a new collection field.
	 * @param instance the instance containing the collection.
	 * @param field the field containing the collection.
	 */
	public IdentityFixerCollectionField(Object instance, Field field) {
		assert instance != null;
		assert field != null;
		m_instance = instance;
		m_field = field;
	}
	
	/**
	 * @return the contained instance.
	 */
	public Object getInstance() {
		return m_instance;
	}
	
	/**
	 * @return the contained field.
	 */
	public Field getField() {
		return m_field;
	}
	
	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return m_instance.getClass().hashCode() * 31 + m_field.hashCode();
	}
	
	/** {@inheritDoc} */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o instanceof IdentityFixerCollectionField) {
			IdentityFixerCollectionField idcf = (IdentityFixerCollectionField) o;
			return m_instance == idcf.m_instance && m_field.equals(idcf.m_field);
		} else {
			return false;
		}
	}
}
