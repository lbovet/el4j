package ch.elca.el4j.internal.apps.dao.hibernate;

import java.util.List;

import ch.elca.el4j.internal.apps.dao.LookupDao;
import ch.elca.el4j.internal.apps.model.Role;

/**
 * Hibernate implementation of LookupDao.
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
public class LookupDaoHibernate extends UniversalDaoHibernate implements LookupDao {

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<Role> getRoles() {
        log.debug("retrieving all role names...");

        return getHibernateTemplate().find("from Role order by name");
    }
}
