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

package ch.elca.el4j.services.persistence.ibatis.dao;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

/**
 * Convenience sqlmap client dao support class to be able to return the 
 * convenience sqlmap client template without casting it.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public class ConvenienceSqlMapClientDaoSupport extends SqlMapClientDaoSupport {
    /**
     * @return Returns the sqlmap client template casted to the convenience
     *         model of it.
     */
    public ConvenienceSqlMapClientTemplate 
    getConvenienceSqlMapClientTemplate() {
        return (ConvenienceSqlMapClientTemplate) getSqlMapClientTemplate();
    }
    
    /**
     * @param template
     *            Is the convenience sqlmap client template to set.
     */
    public void setConvenienceSqlMapClientTemplate(
        ConvenienceSqlMapClientTemplate template) {
        setSqlMapClientTemplate(template);
    }
}
