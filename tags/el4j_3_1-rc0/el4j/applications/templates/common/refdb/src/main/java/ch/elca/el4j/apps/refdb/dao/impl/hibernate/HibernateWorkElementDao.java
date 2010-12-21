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
package ch.elca.el4j.apps.refdb.dao.impl.hibernate;

import org.springframework.stereotype.Repository;

import ch.elca.el4j.apps.refdb.dao.WorkElementDao;
import ch.elca.el4j.apps.refdb.dom.WorkElement;
import ch.elca.el4j.services.persistence.hibernate.dao.GenericHibernateDao;


/**
 * 
 * This class is a hibernate dao for the WorkElement entity.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Daniel Thomas (DTH)
 */
@Repository("workElementDao")
public class HibernateWorkElementDao extends GenericHibernateDao<WorkElement, Integer> 
	implements WorkElementDao {

	

}
