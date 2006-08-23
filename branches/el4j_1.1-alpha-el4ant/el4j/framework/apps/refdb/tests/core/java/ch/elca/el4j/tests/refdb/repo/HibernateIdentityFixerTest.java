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

import org.hibernate.SessionFactory;

import ch.elca.el4j.services.persistence.dao.HibernateRepositoryRegistry;
import ch.elca.el4j.services.persistence.hibernate.HibernateProxyAwareIdentityFixer;

/**
 * An AbstractIdentityFixerTest using the generic hibernate repository and
 * the HibernateProxyAwareIdentityFilter.
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
public class HibernateIdentityFixerTest extends AbstractIdentityFixerTest {
    /** {@inheritDoc} */
    @Override
    protected String[] getExcludeConfigLocations() {
        return new String[] {
            "classpath*:scenarios/dataaccess/hibernate/keyword-core-repository-hibernate-config.xml"
        };
    }

    /** {@inheritDoc} */
    @Override
    protected String[] getIncludeConfigLocations() {
        return new String[] {
            "classpath:optional/interception/methodTracing.xml",
            "classpath*:mandatory/*.xml",
            "classpath*:scenarios/db/raw/*.xml",
            "classpath*:scenarios/dataaccess/hibernate/*.xml",
            "classpath*:optional/interception/transactionJava5Annotations.xml"};
    }

    /** {@inheritDoc} */
    @Override
    protected void setUp() throws Exception {
        SessionFactory sf = (SessionFactory) getApplicationContext().getBean(
            "sessionFactory");

        HibernateRepositoryRegistry hrr = new HibernateRepositoryRegistry();
        hrr.setSessionFactory(sf);
        hrr.afterPropertiesSet();
        m_repoRegistry = hrr;
        m_fixer = new HibernateProxyAwareIdentityFixer();
        super.setUp();
    }
}