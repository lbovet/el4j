package ch.elca.el4j.services.persistence.hibernate.criteria;


import java.util.Iterator;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;

import ch.elca.el4j.services.search.QueryObject;
import ch.elca.el4j.services.search.criterias.AbstractCriteria;
import ch.elca.el4j.services.search.criterias.ComparisonCriteria;
import ch.elca.el4j.services.search.criterias.LikeCriteria;

/**
 * 
 * This class transforms the EL4J criteria of a given <code>QueryObject</code>
 * into the corresponding Hibernate criteria.
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
public class CriteriaTransformer {
     
    /**
     * Hide default constructor.
     */
    protected CriteriaTransformer() { };
    
    /**
     * Transforms EL4J criteria of the given <code>QueryObject</code> into the
     * corresponding Hibernate criteria.
     * 
     * @param query
     *            the query object whose criteria will be transformed
     * @param domainObjectClass
     *            the class of the domain object for which the Hibernate
     *            criteria will be generated
     * @return the Hibernate criteria corresponding to the
     *         <code>QueryObject</code>'s EL4J criteria.
     */
    public static DetachedCriteria transform(QueryObject query,
        Class domainObjectClass) {
        
        // Hibernate criteria for the domain object.
        DetachedCriteria hibernateCriteria
            = DetachedCriteria.forClass(domainObjectClass);
        
        // List of EL4J criteria.
        List el4jCriteriaList = query.getCriteriaList();
        
        // Conversion from EL4J criteria to Hibernate criteria.
        Iterator it = el4jCriteriaList.iterator();
        
        AbstractCriteria currentEl4jCriteria;
        LikeCriteria currentEl4jLikeCriteria;
        
        String currentCriteriaField;
        Object currentCriteriaValue;
        
        while (it.hasNext()) {
            currentEl4jCriteria = (AbstractCriteria) it.next();
            currentCriteriaField = currentEl4jCriteria.getField();
            currentCriteriaValue = currentEl4jCriteria.getValue();
            
            if (currentEl4jCriteria instanceof LikeCriteria) {
                currentEl4jLikeCriteria = (LikeCriteria) currentEl4jCriteria;
                if (currentEl4jLikeCriteria.isCaseSensitive().booleanValue()) {
                    hibernateCriteria.add(Expression.like(currentCriteriaField,
                        currentCriteriaValue));
                } else {
                    hibernateCriteria.add(Expression.like(currentCriteriaField,
                        currentCriteriaValue).ignoreCase());
                }
            } else if (currentEl4jCriteria instanceof ComparisonCriteria) {
                hibernateCriteria.add(Expression.eq(currentCriteriaField,
                    currentCriteriaValue));
            } 
            
            // Handling of IncludeCriteria is not working yet. At the moment,
            // IncludeCriteria have to be treated individually in the search 
            // methods of the application making use of IncludeCriteria. This
            // will be replaced in a future version.
            
            /* else if (currentEl4jCriteria instanceof IncludeCriteria) {
                
                DetachedCriteria subcrit
                    = hibernateCriteria.createCriteria( 
                        currentCriteriaField).setProjection(Property
                            .forName("name"));
                hibernateCriteria.add(Subqueries
                    .in(currentCriteriaValue, subcrit));
            }*/
        }
        
        return hibernateCriteria;
    }
    
}
