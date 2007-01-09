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
package ch.elca.el4j.applications.refdb.dao.impl.hibernate;

import ch.elca.el4j.applications.refdb.dao.FileDao;
import ch.elca.el4j.applications.refdb.dom.File;

/**
 * 
 * DAO for files which is using Hibernate.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Alex Mathey (AMA)
 */
public class HibernateFileDao extends GenericHibernateFileDao<File, Integer>
    implements FileDao {

    /**
     * Creates a new HibernateFileDao instance.
     */
    public HibernateFileDao() {
        setPersistentClass(File.class);
    }
    
}
