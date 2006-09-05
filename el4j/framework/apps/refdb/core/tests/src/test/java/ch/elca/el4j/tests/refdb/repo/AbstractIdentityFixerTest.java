/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU General Public License (GPL) Version 2.0.
 * http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.tests.refdb.repo;

import java.util.HashSet;
import java.util.Set;

import ch.elca.el4j.apps.keyword.dto.KeywordDto;
import ch.elca.el4j.apps.refdb.dto.BookDto;
import ch.elca.el4j.apps.refdb.dto.ReferenceDto;
import ch.elca.el4j.services.persistence.generic.dao.AbstractIdentityFixer;
import ch.elca.el4j.services.persistence.generic.dao.DaoChangeListener;
import ch.elca.el4j.services.persistence.generic.dao.DaoRegistry;
import ch.elca.el4j.services.persistence.generic.dao.GenericDao;
import ch.elca.el4j.services.persistence.generic.dao.IdentityFixedDao;
import ch.elca.el4j.services.persistence.generic.dao.DaoChangeNotifier.Change;
import ch.elca.el4j.services.persistence.generic.dao.DaoChangeNotifier.NewEntityState;
import ch.elca.el4j.tests.refdb.AbstractTestCaseBase;

/**
 * Checks an identity fixer's correctness by testing an identity-fixed 
 * repository. 
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Adrian Moos (AMS)
 */
public abstract class AbstractIdentityFixerTest /*extends AbstractTestCaseBase*/ {
    /** The repository registry to be used in tests. *//*
    protected DaoRegistry m_repoRegistry;
    
    *//** The identity fixer to be tested. *//*
    protected AbstractIdentityFixer m_fixer;

    *//***//*
    private IdentityFixedDao<KeywordDto> m_keywordRepo;
    *//***//*
    private IdentityFixedDao<ReferenceDto> m_refRepo;

    *//**
     * Returns the identity fixing proxy for the repository that is responsible
     * for entities of type {@code T}.
     *//*
    @SuppressWarnings("unchecked")
    private <T> 
    IdentityFixedDao<T> identityFixedRepoFor(Class<T> c) {
        return (IdentityFixedDao<T>) m_fixer.new GenericInterceptor(
                IdentityFixedDao.class
            ).decorate(
                m_repoRegistry.getFor(c)
            );
    }
    

    *//** {@inheritDoc} *//*
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        m_keywordRepo = identityFixedRepoFor(KeywordDto.class);
        m_refRepo =     identityFixedRepoFor(ReferenceDto.class);
    }
    
    *//***//*
    public void testSaveFind() {
        KeywordDto kw = new KeywordDto();
        kw.setName("test");
        KeywordDto skw = m_keywordRepo.saveOrUpdate(kw); 
        assertSame(
            "saving new keyword returned different instance",
            kw, 
            skw
        );
        
        skw = m_keywordRepo.findAll().iterator().next();
        assertSame(
            "finding a previously saved keyword returns a new instance",
            kw,
            skw
        );
    }
    
    *//** 
     * Tests saving an entity with associations, checks proper handling
     * of subtyping relationships among entities and change notification for
     * attached entities.
     *//*
    public void testAssociationsAndDynamicTypeAndChangeNotifications() {
        KeywordDto kw = new KeywordDto();
        kw.setName("hibernate");
        m_keywordRepo.saveOrUpdate(kw);

        Set<KeywordDto> kws = new HashSet<KeywordDto>();
        kws.add(kw);
        BookDto hia = new BookDto();
        hia.setName("hibernate in action");
        hia.setKeywords(kws);
        m_refRepo.saveOrUpdate(hia);
        
        renameKeyword();

        UpdateRecorder rec = new UpdateRecorder();
        m_fixer.getChangeNotifier().subscribe(rec);
        
        ReferenceDto ref = m_refRepo.findAll().iterator().next();
        assertNotNull("change notification missing", rec.m_change);
        assertTrue(
            "wrong change notification type",
            rec.m_change instanceof NewEntityState
        );
        assertEquals(
            "wrong changee", kw, ((NewEntityState) rec.m_change).getChangee());
        assertSame(hia, ref);
        assertTrue(
            "wrong dynamic type", ref instanceof BookDto);
        
        KeywordDto kwn = (KeywordDto) ref.getKeywords().iterator().next();
        assertEquals("associated keyword is different instance", kw, kwn);
        assertEquals("state not propagated", kwn.getName(), "Another name");
    }
    
    *//** Renames the only keyword to "another name". *//*
    private void renameKeyword() {
        GenericDao<KeywordDto> otherKeywordRepo
            = m_repoRegistry.getFor(KeywordDto.class);
        KeywordDto okw = otherKeywordRepo.findAll().iterator().next();
        okw.setName("Another name");
        otherKeywordRepo.saveOrUpdate(okw);
    }
    
    *//** Records the first change notification received. *//*
    // the order of update notifications is not specified. This test depends on
    // it only to keep the implementation simple.
    private class UpdateRecorder implements DaoChangeListener {
        *//** the first change recorded, or null if there wasn't any so far. *//*
        Change m_change;
        
        *//** {@inheritDoc} *//*
        public void changed(Change change) {
            if (this.m_change == null) {
                this.m_change = change;
            }
        }
    }*/
}
