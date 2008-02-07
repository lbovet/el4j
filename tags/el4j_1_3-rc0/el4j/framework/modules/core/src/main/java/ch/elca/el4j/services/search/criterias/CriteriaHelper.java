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
package ch.elca.el4j.services.search.criterias;

import ch.elca.el4j.services.search.QueryObject;

/**
 * Convenience support for using Criterias. 
 *
 *  For sample usage please refer to {@link QueryObject}  
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Philipp Oser (POS)
 */
public class CriteriaHelper {
    
    /**
     * Convenience method for {@link AndCriteria}
     * @param criteria
     * @return
     */
    public static Criteria and(Criteria... criteria){
        return new AndCriteria(criteria);
    }
    
    /**
     * Convenience method for {@link OrCriteria}
     * @param criteria
     * @return
     */
    public static Criteria or(Criteria... criteria){
        return new OrCriteria(criteria);
    }
    
    /**
     * Convenience method for {@link NotCriteria}
     * @param criteria
     * @return
     */    
    public static Criteria not(Criteria criteria){
        return new NotCriteria(criteria);
    }
    
    /**
     * Convenience method for string comparison
     * @param field
     * @param value
     * @return
     */
    public static Criteria eq(String field, String value){
        return new ComparisonCriteria(field, value, "=", value.getClass().getName());
    }
    
    /**
     * Convenience method for like criteria (is case insensitive)
     * @param field
     * @param value
     * @return
     */
    public static Criteria like(String field, String value){
        return LikeCriteria.caseInsensitive(field,value);
    }
    
    /** 
     * Apply operator (from functional programming)
     *  (used internally) 
     * @param criterias must not be null
     * @return
     */
    static String[] applyToSqlWhereCondition( Criteria criterias[] ){
        String[] result = new String[criterias.length];
        
        for (int i = 0; i < criterias.length; i++) {
            result[i] = criterias[i].getSqlWhereCondition();
        }
        return result;
    }
    
}
