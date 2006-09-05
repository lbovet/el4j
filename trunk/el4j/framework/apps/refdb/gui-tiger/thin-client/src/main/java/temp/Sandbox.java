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
package temp;

import ch.elca.el4j.services.persistence.generic.dao.AbstractIdentityFixer;
import ch.elca.el4j.services.persistence.generic.dao.IdentityFixedDao;
import ch.elca.el4j.services.persistence.generic.dao.GenericDao;
import ch.elca.el4j.services.persistence.generic.dto.PrimaryKeyOptimisticLockingObject;
import ch.elca.el4j.services.persistence.hibernate.HibernateProxyAwareIdentityFixer;

public class Sandbox {
    static AbstractIdentityFixer identityFixer 
        = new HibernateProxyAwareIdentityFixer();
    
    static AbstractIdentityFixer.GenericInterceptor interceptor 
        = identityFixer.new GenericInterceptor(IdentityFixedDao.class);
    
    @SuppressWarnings("unchecked")
    static <T extends PrimaryKeyOptimisticLockingObject> IdentityFixedDao<T> fix(
        GenericDao<T> identityManglingRepo) {
        
        return (IdentityFixedDao<T>)
            interceptor.decorate(identityManglingRepo);
    }
}
