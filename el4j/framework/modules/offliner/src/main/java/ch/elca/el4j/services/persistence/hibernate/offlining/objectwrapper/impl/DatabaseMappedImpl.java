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
package ch.elca.el4j.services.persistence.hibernate.offlining.objectwrapper.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import ch.elca.el4j.services.persistence.hibernate.dao.ConvenienceGenericHibernateDao;
import ch.elca.el4j.services.persistence.hibernate.offlining.OfflinerInternalRTException;
import ch.elca.el4j.services.persistence.hibernate.offlining.impl.MappingEntry;
import ch.elca.el4j.services.persistence.hibernate.offlining.impl.UniqueKey;
import ch.elca.el4j.services.persistence.hibernate.offlining.objectwrapper.Mapped;
import ch.elca.el4j.services.persistence.hibernate.offlining.objectwrapper.Typed;
import ch.elca.el4j.services.persistence.hibernate.offlining.objectwrapper.Typed.KeyType;
import ch.elca.el4j.services.persistence.hibernate.offlining.objectwrapper.UniqueKeyed;
import ch.elca.el4j.util.objectwrapper.ObjectWrapperRTException;
import ch.elca.el4j.util.objectwrapper.impl.AbstractWrapper;


/**
 * Mapped implementation that uses a database table for mapping entries.
 * A Typed and a KeyedVersioned implementation are required to look up the correct entry.
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
public class DatabaseMappedImpl extends AbstractWrapper implements Mapped {

	/**
	 * The database dao.
	 */
	private final ConvenienceGenericHibernateDao<MappingEntry, Integer> m_dao;
	
	/**
	 * Create this wrapper.
	 * @param dao The mapping dao.
	 */
	public DatabaseMappedImpl(ConvenienceGenericHibernateDao<MappingEntry, Integer> dao) {
		m_dao = dao;
	}

	/** {@inheritDoc} */
	@Override
	public void create() throws ObjectWrapperRTException {
		if (!m_wrapper.wrappablePresent(Typed.class)) {
			throw new ObjectWrapperRTException("Requires implementation of Typed.");
		}
		if (!m_wrapper.wrappablePresent(UniqueKeyed.class)) {
			throw new ObjectWrapperRTException("Requires implementation of UniqueKeyed.");
		}
	}

	/** {@inheritDoc} */
	public MappingEntry getEntry() {
		KeyType type = m_wrapper.wrap(Typed.class, m_target).getType(); 
		UniqueKey key = m_wrapper.wrap(UniqueKeyed.class, m_target).getUniqueKey();
		String property; 
		String keyString = key.toString();
		
		if (type.equals(KeyType.LOCAL)) {
			property = "localKeyAsString";
		} else if (type.equals(KeyType.REMOTE)) {
			property = "remoteKeyAsString";
		} else {
			throw new OfflinerInternalRTException("Trying to get mapping for a null key.");
		}
		 
		DetachedCriteria criteria = DetachedCriteria.forClass(MappingEntry.class);
		criteria.add(Restrictions.eq(property, keyString));
		List<MappingEntry> entries = m_dao.findByCriteria(criteria);
		if (entries.size() > 1) {
			throw new OfflinerInternalRTException("More than one mapping entry for "
				+ m_target);
		}
		
		MappingEntry entry = null;
		if (!entries.isEmpty()) {
			entry = entries.get(0);
		}
		return entry;
	}

	/** {@inheritDoc} */
	public void setEntry(MappingEntry entry) {
		m_dao.saveOrUpdate(entry);
	}
}
