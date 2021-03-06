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
package ch.elca.el4j.services.persistence.hibernate.usertypes;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.type.NullableType;
import org.hibernate.type.TypeFactory;

import ch.elca.el4j.util.codingsupport.SerializableEnum;

/**
 * Generic user type for enumerations. Based on <a 
 * href="http://weblog.dangertree.net/2007/09/23/mapping-java-5-enums-with-hibernate/">
 * http://weblog.dangertree.net/2007/09/23/mapping-java-5-enums-with-hibernate/</a>
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Martin Zeltner (MZE)
 * @author Stefan Wismer (SWI)
 */
public class GenericEnumUserType extends AbstractGenericEnumUserType {
	
	/**
	 * {@inheritDoc}
	 */
	public void setParameterValues(Properties parameters) {
		super.setParameterValues(parameters);

		Class<?> identifierType = null;
		try {
			identifierType = m_enumClass.getMethod("getValue",
				new Class[0]).getReturnType();
		} catch (Exception e) {
			throw new HibernateException("Failed to obtain identifier method",
				e);
		}

		m_type = (NullableType) TypeFactory.basic(identifierType.getName());

		if (m_type == null) {
			throw new HibernateException("Unsupported identifier type "
				+ identifierType.getName());
		}

		m_sqlTypes = new int[] {m_type.sqlType()};

		m_valueMapping = new HashMap<Object, SerializableEnum<?>>();
		for (SerializableEnum<?> value : getEnumValues()) {
			m_valueMapping.put(value.getValue(), value);
		}
	}


	/**
	 * {@inheritDoc}
	 */
	public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
		throws HibernateException, SQLException {
		Object identifier = m_type.get(rs, names[0]);
		if (rs.wasNull()) {
			return null;
		}

		try {
			return m_valueMapping.get(identifier);
		} catch (Exception e) {
			throw new HibernateException("Exception while invoking "
				+ "method 'values' of "
				+ "enumeration class '" + m_enumClass + "'", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void nullSafeSet(PreparedStatement st, Object value, int index)
		throws HibernateException, SQLException {
		try {
			if (value == null) {
				st.setNull(index, m_type.sqlType());
			} else {
				Object identifier = ((SerializableEnum<?>) value).getValue();
				m_type.set(st, identifier, index);
			}
		} catch (Exception e) {
			throw new HibernateException("Exception while invoking 'getValue' "
				+ "of enumeration class '" + m_enumClass + "'", e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Object assemble(Serializable cached, Object owner)
		throws HibernateException {
		return cached;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object deepCopy(Object value) throws HibernateException {
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	public Serializable disassemble(Object value) throws HibernateException {
		return (Serializable) value;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object x, Object y) throws HibernateException {
		return x == y;
	}

	/**
	 * {@inheritDoc}
	 */
	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isMutable() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object replace(Object original, Object target, Object owner)
		throws HibernateException {
		return original;
	}
}
