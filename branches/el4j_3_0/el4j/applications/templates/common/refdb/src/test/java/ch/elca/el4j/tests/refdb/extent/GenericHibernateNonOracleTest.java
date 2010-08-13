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
package ch.elca.el4j.tests.refdb.extent;


import static org.junit.Assert.fail;

import org.hibernate.LazyInitializationException;
import org.junit.Test;

import ch.elca.el4j.apps.refdb.dao.impl.hibernate.GenericHibernateFileDaoInterface;
import ch.elca.el4j.apps.refdb.dao.impl.hibernate.HibernateFileDao;
import ch.elca.el4j.apps.refdb.dom.File;
import ch.elca.el4j.apps.refdb.dom.Reference;

/**
*
* Test case for <code>GenericHibernateDao</code> to test
* the Extent-functionality, extension of GenericHibernateDaoTest with tests not running with oracle.
*
* @svnLink $Revision$;$Date$;$Author$;$URL$
*
* @author Andreas Rueedlinger (ARR)
*/
public class GenericHibernateNonOracleTest extends GenericHibernateDaoTest {

	/**
	 * This test checks the lazy loading of file content.
	 */
	@Test
	public void testInsertFileLazyContent() {
		Reference fakeReference = addDefaultFakeReference();
		
		// Use HibernateFileDao to set extent
		GenericHibernateFileDaoInterface dao = (GenericHibernateFileDaoInterface) getFileDao();
		File file = new File();
		file.setReference(fakeReference);
		file.setName("iBatis Developer Guide");
		file.setMimeType("text/plain");
		byte[] content = "This is only a test content.".getBytes();
		file.setContent(content);
		dao.saveOrUpdate(file);
		
		// Only load the header
		File file2 = dao.findById(file.getKey(), HibernateFileDao.HEADER);
		// Load without extent
		File file3 = dao.findById(file.getKey());
		
		try {
			if (file2.getContent() != null) {
				fail("Content was loaded although not told to.");
			}
			if (file3.getContent() != null) {
				fail("Content was loaded although not told to.");
			}
		} catch (LazyInitializationException e) {
			s_logger.debug("Expected exception catched.", e);
		}
		
	}
	
	/**
	 * Test refresh after lazy loading and repeat the query.
	 */
	@Test
	public void testRefreshCatching() {
		Reference fakeReference = addDefaultFakeReference();
		
		// Use HibernateFileDao to set extent
		GenericHibernateFileDaoInterface dao = (GenericHibernateFileDaoInterface) getFileDao();
		File file = new File();
		file.setReference(fakeReference);
		file.setName("iBatis Developer Guide");
		file.setMimeType("text/plain");
		byte[] content = "This is only a test content.".getBytes();
		file.setContent(content);
		
		dao.saveOrUpdate(file);
		
		File file2 = dao.findById(file.getKey());
		if (file2.getContent() != null) {
			fail("Could load lazy content.");
		} else {
			file2 = dao.refresh(file2, HibernateFileDao.ALL);
			if (file2.getContent() == null) {
				fail("Lazy loading problem still exists after refreshing.");
			}
		}
	}
}
