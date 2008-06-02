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
package ch.elca.el4j.services.search.criterias;

import java.io.Serializable;


/**
 * A Criteria represents some restrictions for a Query (e.g. used to
 *   get values from a database).
 *   
 * @see QueryObject
 * 
 * Refer to tests of keyword/test module for example usages
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 *  @author Martin Zeltner (MZE)
 */
public interface Criteria extends Serializable {
    
    /**
     * @deprecated No longer use this method as it was used in an older
     *   implementation only.
     * @return Returns the criteria type.
     */
    public String getType();
    
    /**
     * The full Criteria as SQL representation. This does not
     *  mean that we want to break encapsulation (Criteria should
     *  be independent of the database) but SQL is a representation
     *  that is easy to understand. 
     * @return an SQL string as it could occur in a SQL WHERE clause
     */
    public String getSqlWhereCondition();
    
}
