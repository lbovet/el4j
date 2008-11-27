/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.tests.refdb.idfixer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import ch.elca.el4j.apps.keyword.dom.Keyword;
import ch.elca.el4j.apps.refdb.dom.Book;
import ch.elca.el4j.apps.refdb.dom.Reference;
import ch.elca.el4j.services.persistence.generic.dao.AbstractIdentityFixer;
import ch.elca.el4j.services.persistence.generic.dao.ConvenienceGenericDao;
import ch.elca.el4j.services.persistence.generic.dao.DaoChangeListener;
import ch.elca.el4j.services.persistence.generic.dao.DaoRegistry;
import ch.elca.el4j.services.persistence.generic.dao.IdentityFixedDao;
import ch.elca.el4j.services.persistence.generic.dao.DaoChangeNotifier.Change;
import ch.elca.el4j.services.persistence.generic.dao.DaoChangeNotifier.NewEntityState;
import ch.elca.el4j.tests.refdb.AbstractTestCaseBase;

/**
 * Checks an identity fixer's correctness by testing an identity-fixed DAO.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Adrian Moos (AMS)
 * @author Alex Mathey (AMA)
 */
public abstract class AbstractIdentityFixerTest extends AbstractTestCaseBase {
	/** The DAO registry to be used in tests. */
	protected DaoRegistry m_daoRegistry;
	
	/** The identity fixer to be tested. */
	protected AbstractIdentityFixer m_fixer;

	/** The identity-fixed keyword DAO.*/
	private ConvenienceGenericDao<Keyword, Integer> m_keywordDao;
	/** The identity-fixed book DAO.*/
	private ConvenienceGenericDao<Book, Integer> m_bookDao;
	
	/**
	 * Returns the identity fixing proxy for the DAO that is responsible
	 * for entities of type {@code T}.
	 */
	@SuppressWarnings("unchecked")
	private <T,ID extends Serializable> ConvenienceGenericDao<T,ID> identityFixedDaoFor(Class<T> c) {
		return (ConvenienceGenericDao<T,ID>) m_fixer.new GenericInterceptor(
			IdentityFixedDao.class).decorate(getDaoRegistry().getFor(c));
	}
	

	/** {@inheritDoc} */
	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		m_keywordDao =  identityFixedDaoFor(Keyword.class);
		m_bookDao = identityFixedDaoFor(Book.class);
	}
	
	/***/
	@Test
	public void testSaveFind() {
		Keyword kw = new Keyword();
		kw.setName("test");
		Keyword skw = m_keywordDao.saveOrUpdate(kw);
		assertSame(
			"saving new keyword returned different instance",
			kw,
			skw
		);
		
		kw.setName("new");
		skw = m_keywordDao.getAll().iterator().next();
		assertSame(
			"finding a previously saved keyword returns a new instance",
			kw,
			skw
		);
		assertEquals(kw.getName(), "new");
	}
	
	/**
	 * Tests saving an entity with associations, checks proper handling
	 * of subtyping relationships among entities and change notification for
	 * attached entities.
	 */
	@Test
	public void testAssociationsAndDynamicTypeAndChangeNotifications() {
		Keyword kw = new Keyword();
		kw.setName("hibernate");
		m_keywordDao.saveOrUpdate(kw);

		Set<Keyword> kws = new HashSet<Keyword>();
		kws.add(kw);
		Book hia = new Book();
		hia.setName("hibernate in action");
		hia.setKeywords(kws);
		m_bookDao.saveOrUpdate(hia);
		
		renameKeyword();

		UpdateRecorder rec = new UpdateRecorder();
		m_fixer.getChangeNotifier().subscribe(rec);
		
		Reference ref = m_bookDao.getAll().iterator().next();
		assertNotNull("change notification missing", rec.m_change);
		assertTrue(
			"wrong change notification type",
			rec.m_change instanceof NewEntityState
		);
		assertEquals(
			"wrong changee", kw, ((NewEntityState) rec.m_change).getChangee());
		assertSame(hia, ref);
		assertTrue(
			"wrong dynamic type", ref instanceof Book);
		
		Keyword kwn = (Keyword) ref.getKeywords().iterator().next();
		assertEquals("associated keyword is different instance", kw, kwn);
		//assertEquals("state not propagated", kwn.getName(), "Another name");
		// renameKeyword() stored a new value to the database, but gets merged with current value
		assertEquals("state not propagated", kwn.getName(), "hibernate");
	}
	
	/** Renames the only keyword to "another name". */
	private void renameKeyword() {
		ConvenienceGenericDao<Keyword, Integer> otherKeywordDao
			= (ConvenienceGenericDao<Keyword, Integer>) getDaoRegistry().getFor(Keyword.class);
		Keyword okw = otherKeywordDao.getAll().iterator().next();
		okw.setName("Another name");
		otherKeywordDao.saveOrUpdate(okw);
	}
	
	/** Records the first change notification received. */
	// the order of update notifications is not specified. This test depends on
	// it only to keep the implementation simple.
	private class UpdateRecorder implements DaoChangeListener {
		/** the first change recorded, or null if there wasn't any so far. */
		Change m_change;
		
		/** {@inheritDoc} */
		public void changed(Change change) {
			if (this.m_change == null) {
				this.m_change = change;
			}
		}
	}
	
	/**
	 * @return Returns the DAO registry.
	 */
	protected DaoRegistry getDaoRegistry() {
		if (m_daoRegistry == null) {
			m_daoRegistry = (DaoRegistry) getApplicationContext()
				.getBean("daoRegistry");
		}
		return m_daoRegistry;
	}
	
}
