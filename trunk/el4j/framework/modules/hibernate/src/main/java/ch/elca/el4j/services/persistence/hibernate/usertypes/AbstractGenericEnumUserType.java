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

import java.util.Map;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.type.NullableType;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

/**
 * This abstract class shares the base code of {@link GenericEnumUserType} and
 * {@link GenericEnumSetUserType}.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 */
public abstract class AbstractGenericEnumUserType
	implements UserType, ParameterizedType {
	
	/**
	 * Is the enum class.
	 */
	@SuppressWarnings("unchecked")
	protected Class<? extends SerializableEnum> m_enumClass;
	
	/**
	 * The precomputed mapping of persisted value and enum.
	 */
	protected Map<Object, SerializableEnum<?>> m_valueMapping;
	
	/**
	 * Is the nullable type.
	 */
	protected NullableType m_type;
	
	/**
	 * Are sql types who they are represented in Hibernate.
	 */
	protected int[] m_sqlTypes;
	
	/**
	 * {@inheritDoc}
	 */
	public void setParameterValues(Properties parameters) {
		String enumClassName = parameters.getProperty("enumClassName");
		try {
			m_enumClass = Class.forName(enumClassName).asSubclass(
				SerializableEnum.class);
		} catch (ClassNotFoundException cfne) {
			throw new HibernateException("Enum class not found or does not "
				+ "implement SerializableEnum", cfne);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Class<?> returnedClass() {
		return m_enumClass;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int[] sqlTypes() {
		return m_sqlTypes;
	}
	
	/**
	 * @return    all enum values of m_enumClass
	 */
	protected SerializableEnum<?>[] getEnumValues() {
		try {
			// get all enum values using values()
			SerializableEnum<?>[] values = (SerializableEnum[])
				m_enumClass.getMethod("values", new Class[] {})
					.invoke(m_enumClass, new Object[] {});
			
			return values;
		} catch (Exception e) {
			throw new HibernateException("Failed to obtain all values from "
				+ "enum '" + m_enumClass.getName() + "'", e);
		}
	}
}
