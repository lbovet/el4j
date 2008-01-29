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
package ch.elca.el4j.seam.generic;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.type.BooleanType;
import org.hibernate.type.EntityType;
import org.hibernate.type.EnumType;
import org.hibernate.type.NullableType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;

import ch.elca.el4j.seam.generic.metadata.EntityInfoBase;
import ch.elca.el4j.seam.generic.metadata.EnumFieldInfo;
import ch.elca.el4j.seam.generic.metadata.FieldInfo;
import ch.elca.el4j.services.search.QueryObject;
import ch.elca.el4j.services.search.criterias.ComparisonCriteria;
import ch.elca.el4j.services.search.criterias.Criteria;
import ch.elca.el4j.services.search.criterias.LikeCriteria;

/**
 * This class represents the current entity filters.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Stefan Wismer (SWI)
 */
@Name("filters")
@Scope(ScopeType.CONVERSATION)
public class EntityFilters implements Serializable {
    /**
     * The current entity manager.
     */
    @In(value = "#{entityManager}", required = false)
    EntityManager m_entityManager;
    
    /**
     * The injected type information provider class (from Spring config xml).
     */
    @In("#{entityInfoBase}")
    private EntityInfoBase m_entityInfoBase;
    
    /**
     * The search string.
     */
    @In(value = "searchString", required = false, scope = ScopeType.EVENT)
    @Out(value = "searchString", required = false, scope = ScopeType.EVENT)
    private String m_searchString = "";
    
    /**
     * The filters applied to the entity list (map: fieldname -> value).
     */
    private Map<String, String> m_filters = new HashMap<String, String>();
    
    /**
     * Remove all filters on the current entities.
     */
    @End
    public void removeAll() {
        if (m_filters.size() > 0) {
            m_filters.clear();
            m_entityManager.invalidateView();
        }
    }
    
    /**
     * @param fieldName    the field name whose filter should be removed
     */
    public void remove(String fieldName) {
        if (m_filters.containsKey(fieldName)) {
            m_filters.remove(fieldName);
            m_entityManager.invalidateView();
        }
    }
    
    /**
     * @param fieldName    the field name which this filter applies to
     * @param value        the value that this field has to hold
     */
    public void addExact(String fieldName, String value) {
        System.out.println("addFilter(" + fieldName + ", " + value);
        if (!m_filters.containsKey(fieldName)
            || !m_filters.get(fieldName).equals(value)) {
            
            m_filters.put(fieldName, value);
            m_entityManager.invalidateView();
        }
    }
    
    /**
     * @param fieldName    the field name which this filter applies to
     * @param value        the value that this field has to hold
     */
    public void add(String fieldName, String value) {
        addExact(fieldName, "%" + value + "%");
    }
    
    /**
     * @param fieldName    the field name which this filter applies to
     */
    public void addExact(String fieldName) {
        add(fieldName, m_searchString);
    }
    
    /**
     * @param fieldName    the field name which this filter applies to
     */
    public void add(String fieldName) {
        add(fieldName, "%" + m_searchString + "%");
    }
    
    /**
     * @param fieldName    does this field name hold a filter
     * @param value        does this field has to be equal to this value
     *                     ("" means any value)
     * @return             <code>true</code> if filter does match
     */
    public boolean isActive(String fieldName, String value) {
        if (m_filters.containsKey(fieldName)) {
            return m_filters.get(fieldName).equals(value);
        } else {
            // if value is empty then check if no filter is active on field
            return value.equals("");
        }
    }
    
    /**
     * @param queryObject    the {@link QueryObject} to apply the filters.
     * @return               the modified {@link QueryObject}
     */
    public QueryObject apply(QueryObject queryObject) {
        System.out.println(m_filters);
        for (String fieldName : m_filters.keySet()) {
            String value = m_filters.get(fieldName);
            
            FieldInfo fieldInfo = m_entityInfoBase.getEntityInfo(
                queryObject.getBeanClass().getName()).getFieldInfo(fieldName);
            Type hibernateType = fieldInfo.getType();
            
            // TODO improve type support
            Criteria criteria = null;
            if (hibernateType instanceof NullableType) {
                if (hibernateType instanceof StringType) {
                    criteria = LikeCriteria.caseInsensitive(fieldName, value);
                } else {
                    NullableType nullableType = (NullableType) hibernateType;
                    criteria = ComparisonCriteria.equalsObject(fieldName,
                        nullableType.fromStringValue(value));
                }
            } else if (fieldInfo instanceof EnumFieldInfo) {
                if ((value != null) && (!value.equals(""))) {
                    Class enumClass = ((EnumFieldInfo) fieldInfo)
                        .getEnumClass();
                    Enum enumValue = Enum.valueOf(enumClass, value);
                    criteria = ComparisonCriteria.equals(fieldName, enumValue);
                }
            }

            if (criteria != null) {
                queryObject.addCriteria(criteria);
            }
        }
        
        return queryObject;
    }
}
