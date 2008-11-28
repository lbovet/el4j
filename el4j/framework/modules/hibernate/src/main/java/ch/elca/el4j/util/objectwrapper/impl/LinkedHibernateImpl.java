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
package ch.elca.el4j.util.objectwrapper.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.EntityMode;
import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.Type;

import ch.elca.el4j.util.objectwrapper.ObjectWrapperRTException;
import ch.elca.el4j.util.objectwrapper.interfaces.Linked;

/**
 * Hibernate annotation implementation of Linked.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL: https://cvs.elca.ch/subversion/el4j-internal/trunk/sandbox/cacher/src/main/java/caching/aspects/impl/LinkedHibernateImpl.java $",
 *    "$Revision: 1583 $",
 *    "$Date: 2008-08-08 14:19:17 +0200 (Fri, 08 Aug 2008) $",
 *    "$Author: dbd@ELCA.CH $"
 * );</script>
 *
 * @author David Bernhard (DBD)
 */
public class LinkedHibernateImpl extends AbstractWrapper implements Linked {

	/**
	 * Persistence annotations. These are used to search for inverse links.
	 */
	private static List<Class<?extends Annotation>> s_candidates
		= new LinkedList<Class<?extends Annotation>>();
	
	/**
	 * Initialize annotation candidates.
	 */
	static {
		s_candidates.add(OneToMany.class);
		s_candidates.add(OneToOne.class);
		s_candidates.add(ManyToMany.class);
	}
	
	/** The session factory as source of hibernate metadata. */
	private final SessionFactory m_sessionFactory;

	/** The metadata object. */
	private ClassMetadata m_meta;
	
	/** The simple properties. */
	private List<String> m_simple;
	
	/** The collection properties. */
	private List<String> m_collections;
	
	/**
	 * Create the implementation.
	 * @param sessionFactory The session factory.
	 */
	public LinkedHibernateImpl(SessionFactory sessionFactory) {
		m_sessionFactory = sessionFactory;
	}
	
	/** {@inheritDoc} */
	@Override
	public void create() {
		m_meta = m_sessionFactory.getClassMetadata(m_target.getClass());
		if (m_meta == null) {
			throw new ObjectWrapperRTException("Failed to create hibernate metadata.");
		}
		m_simple = new LinkedList<String>();
		m_collections = new LinkedList<String>();
		
		for (String property : m_meta.getPropertyNames()) {
			if (isInverse(m_target.getClass(), property)) {
				continue;
			}
			
			Type type = m_meta.getPropertyType(property);
			if (type.isCollectionType()) {
				m_collections.add(property);
			} else if (type.isAssociationType()) {
				m_simple.add(property);
			}
		}
	}
	
	/**
	 * Whether a property is marked with a mapped-by annotation and hence
	 * represents an inverse association.
	 * @param cls The domain class.
	 * @param property The property name.
	 * @return <code>true</code> if the annotation is present.
	 */
	private boolean isInverse(Class<?> cls, String property) {
		// Find the getter.
		String methodName = "get" + property.substring(0, 1)
			.toUpperCase(Locale.getDefault()) + property.substring(1);
		Method method;
		try {
			method = cls.getMethod(methodName);
		} catch (NoSuchMethodException e) {
			try {
				methodName = "is" + methodName.substring("get".length());
				method = cls.getMethod(methodName);
			} catch (NoSuchMethodException ex) {
				throw new ObjectWrapperRTException("No getter for " + property
					+ " in " + cls, ex);
			}
		} 
		
		// Get the annotations on the getter.
		boolean inverse = false;
	
	OUTER: 
		for (Annotation a : method.getAnnotations()) {
			for (Class<? extends Annotation> candidate : s_candidates) {
				if (candidate.isAssignableFrom(a.getClass()) 
					&& isInverseAnnotation(a)) {
					inverse = true;
					break OUTER;
				}
			}
		}
		
		return inverse;
	}
	
	/**
	 * Check if an annotation represents an inverse link.
	 * @param ann The annotaion.
	 * @return <code>true</code> if <code>mappedBy</code> was found and
	 * not empty.
	 */
	private boolean isInverseAnnotation(Annotation ann) {
		String str;
		try {
			Method mappedBy = ann.getClass().getMethod("mappedBy");
			str = (String) mappedBy.invoke(ann);
		} catch (NoSuchMethodException e) {
			throw new ObjectWrapperRTException("Missing mappedBy in " + ann, e);
		} catch (IllegalAccessException e) {
			throw new ObjectWrapperRTException(e);
		} catch (InvocationTargetException e) {
			throw new ObjectWrapperRTException(e);
		} 
		return !"".equals(str);
	}

	/** {@inheritDoc} */
	public Object[] getAllLinked() {
		List<Object> objects = new LinkedList<Object>();
		for (String simple : m_simple) {
			Object obj = getlinkByName(simple);
			if (obj != null) {
				objects.add(obj);
			}
		}
		for (String collectionName : m_collections) {
			Collection<?> collection = getCollectionLinkByName(collectionName);
			for (Object obj : collection) {
				if (obj != null) {
					objects.add(obj);
				}
			}
		}
		return objects.toArray();
	}

	/** {@inheritDoc} */
	public Collection<?> getCollectionLinkByName(String name) {
		return (Collection<?>) m_meta.getPropertyValue(m_target, name, EntityMode.POJO);
	}

	/** {@inheritDoc} */
	public String[] getCollectionLinkNames() {
		return m_collections.toArray(new String[m_collections.size()]);
	}

	/** {@inheritDoc} */
	public String[] getLinkNames() {
		return m_simple.toArray(new String[m_simple.size()]);
	}

	/** {@inheritDoc} */
	public Object getlinkByName(String linkName) {
		return m_meta.getPropertyValue(m_target, linkName, EntityMode.POJO);
	}
}
