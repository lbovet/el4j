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
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.type.StringType;
import org.hibernate.type.TypeFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * Generic user type for set of enumerations implementing
 * {@link SerializableEnum}.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Stefan Wismer (SWI)
 */
public class GenericEnumSetUserType extends AbstractGenericEnumUserType {
	
	/**
	 * The separator to use.
	 */
	private String m_separator = ",";

	/**
	 * {@inheritDoc}
	 */
	public void setParameterValues(Properties parameters) {
		super.setParameterValues(parameters);
		
		String separator = parameters.getProperty("separator");
		if (separator != null && separator.length() > 0) {
			m_separator = separator;
		}

		m_type = (StringType) TypeFactory.basic(String.class.getName());
		m_sqlTypes = new int[] {m_type.sqlType()};

		// It's important to use toString() while inserting!
		m_valueMapping = new HashMap<Object, SerializableEnum<?>>();
		for (SerializableEnum<?> value : getEnumValues()) {
			m_valueMapping.put(value.getValue().toString(), value);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
		throws HibernateException, SQLException {
		String setAsString = (String) m_type.get(rs, names[0]);
		EnumSet set = EnumSet.noneOf((Class<? extends Enum>) m_enumClass);
		
		if (!rs.wasNull() && StringUtils.hasLength(setAsString)) {
			try {
				String[] values = setAsString.split(m_separator);
				
				for (String value : values) {
					set.add(m_valueMapping.get(value));
				}
			} catch (Exception e) {
				throw new HibernateException("Exception while invoking "
					+ "method 'values' of "
					+ "enumeration class '" + m_enumClass + "'", e);
			}
		}

		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public void nullSafeSet(PreparedStatement st, Object value, int index)
		throws HibernateException, SQLException {
		try {
			EnumSet set = ((EnumSet) value);
			if (CollectionUtils.isEmpty(set)) {
				st.setNull(index, m_type.sqlType());
			} else {
				StringBuilder sb = null;
				
				for (Iterator iterator = set.iterator(); iterator.hasNext();) {
					SerializableEnum e = (SerializableEnum) iterator.next();
					if (sb == null) {
						sb = new StringBuilder();
					} else {
						sb.append(m_separator);
					}
					sb.append(e.getValue());
				}
				m_type.set(st, sb.toString(), index);
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
		return deepCopy(cached);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Object deepCopy(Object value) throws HibernateException {
		if (value != null) {
			EnumSet enumSet = (EnumSet) value;
			return enumSet.clone();
		} else {
			return EnumSet.noneOf((Class<? extends Enum>) m_enumClass);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Serializable disassemble(Object value) throws HibernateException {
		return (Serializable) deepCopy(value);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public boolean equals(Object x, Object y) throws HibernateException {
		if (x != null && y != null) {
			EnumSet enumSetX = (EnumSet) x;
			EnumSet enumSetY = (EnumSet) y;
			return enumSetX.equals(enumSetY);
		} else {
			return false;
		}
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
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object replace(Object original, Object target, Object owner)
		throws HibernateException {
		return deepCopy(original);
	}

}
