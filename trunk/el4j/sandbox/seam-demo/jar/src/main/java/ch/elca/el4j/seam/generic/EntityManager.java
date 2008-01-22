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
import java.util.List;
import java.util.Map;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.faces.FacesMessages;

import ch.elca.el4j.seam.generic.metadata.EntityInfoBase;
import ch.elca.el4j.seam.generic.metadata.EnumFieldInfo;
import ch.elca.el4j.seam.generic.metadata.FieldInfo;
import ch.elca.el4j.services.persistence.generic.dao.ConvenienceGenericDao;
import ch.elca.el4j.services.persistence.generic.dao.impl.FallbackDaoRegistry;
import ch.elca.el4j.services.search.QueryObject;
import ch.elca.el4j.services.search.criterias.ComparisonCriteria;
import ch.elca.el4j.services.search.criterias.Criteria;
import ch.elca.el4j.services.search.criterias.LikeCriteria;


/**
 * This class provides access to the persistence layer from Seam pages.
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
@Name("entityManager")
@Scope(ScopeType.CONVERSATION)
public class EntityManager implements Serializable, PagedEntityManager {
    /**
     * The injected DAO registry (from Spring config xml).
     */
    @In("#{daoRegistry}")
    private FallbackDaoRegistry m_daoRegistry;
    
    /**
     * The injected type information provider class (from Spring config xml).
     */
    @In("#{entityInfoBase}")
    private EntityInfoBase m_entityInfoBase;

   
    /**
     * The injected entity class name.
     * The loaded entites are available afterwards using <code>#{entites}</code>
     */
    @In(value = "#{entityClassName}", required = false)
    private String m_overrideEntityClassName;
    
    /**
     * The selected entity (for detail view).
     */
    @Out(value = "entity", required = false)
    private Object m_entity;
    
    /**
     * Is the outjected <code>entity</code> a new empty object?
     */
    @Out(value = "newEntity", required = false)
    private boolean m_isEntityNew;
    
    /**
     * The JSF messages.
     */
    @In(value = "facesMessages")
    private FacesMessages m_facesMessages;
    
    
    /**
     * The currently loaded entities.
     */
    private List<Object> m_entities;
    
    /**
     * The currently active entity class name.
     */
    private String m_currentEntityClassName;
    
    /**
     * The conditions applied to the entity list (map: fieldname -> value).
     */
    private Map<String, String> m_currentConditions
        = new HashMap<String, String>();

    /**
     * @see QueryObject#setFirstResult(int)
     */
    private int m_firstResult;
    
    /**
     * @see QueryObject#setMaxResults(int)
     */
    private int m_maxResults;
    
    /**
     * Is view reset (for paged table). <code>true</code> if view should be
     * reset to the first page.
     */
    private boolean m_viewReset;
    
    /**
     * The dirty flag for m_entities.
     */
    private boolean m_entitiesDirtyFlag = true;
    
    //private String m_previousPage;
    
    /** {@inheritDoc} */
    public int getEntityCount(String entityClassName) {
        QueryObject queryObject = getQuery(entityClassName);
        
        ConvenienceGenericDao dao = getDao();
        System.out.println("There are " + dao.findCountByQuery(queryObject) + " entites available.");
        return dao.findCountByQuery(queryObject);
    }
    
    /** {@inheritDoc} */
    public void setRange(int first, int count) {
        if (first != m_firstResult || m_maxResults != count) {
            m_entitiesDirtyFlag = true;
        }
        m_firstResult = first;
        m_maxResults = count;
    }
    
    /** {@inheritDoc} */
    public boolean isViewReset() {
        return m_viewReset;
    }
    
    /** {@inheritDoc} */
    public void setViewReset(boolean value) {
        m_viewReset = value;
    }
    
    /**
     * @return    the entities of the currently selected entity class
     */
    @Begin(join = true)
    @Factory(value = "entities", scope = ScopeType.PAGE)
    public List<Object> getEntities() {
        System.out.println("--createEntities");
        return getEntities(getEntityClassName());
    }
    
    /** {@inheritDoc} */
    @Begin(join = true)
    public List<Object> getEntities(String entityClassName) {
        System.out.println("--getEntities(" + entityClassName + ")");

        if (entityClassName == null) {
            m_facesMessages.add("No entity class defined.");
            return null;
        }
        
        loadEntities(entityClassName);
        
        return m_entities;
    }
    
    /**
     * @param entityClassName    the class name of the entites to load
     * @return                   the entities as {@link Object[]}
     */
    public Object[] getAllEntities(String entityClassName) {
        if (entityClassName == null) {
            m_facesMessages.add("No entity class defined.");
            return null;
        }
        
        ConvenienceGenericDao dao = getDao(entityClassName);
        
        return dao != null ? dao.getAll().toArray() : null;
    }
    
    /**
     * Remove all conditions on the current entities.
     */
    public void removeAllConditions() {
        if (m_currentConditions.size() > 0) {
            m_currentConditions.clear();
            m_entitiesDirtyFlag = true;
            m_viewReset = true;
        }
    }
    
    /**
     * @param fieldName    the field name whose condition should be removed
     */
    public void removeCondition(String fieldName) {
        if (m_currentConditions.containsKey(fieldName)) {
            m_currentConditions.remove(fieldName);
            m_entitiesDirtyFlag = true;
            m_viewReset = true;
        }
    }
    
    /**
     * @param fieldName    the field name which this condition applies to
     * @param value        the value that this field has to hold
     * @return             <code>null</code> (stay on this page)
     */
    public String addCondition(String fieldName, String value) {
        if (!m_currentConditions.containsKey(fieldName)
            || !m_currentConditions.get(fieldName).equals(value)) {
            
            m_currentConditions.put(fieldName, value);
            m_entitiesDirtyFlag = true;
            m_viewReset = true;
        }
        return null;
    }
    
    /**
     * @param fieldName    does this field name hold a condition
     * @param value        does this field has to be equal to this value
     *                     ("" means any value)
     * @return             <code>true</code> if condition does match
     */
    public boolean isConditionActive(String fieldName, String value) {
        if (m_currentConditions.containsKey(fieldName)) {
            return m_currentConditions.get(fieldName).equals(value);
        } else {
            // if value is empty then check if no condition is active on field
            return value.equals("");
        }
    }
    
    
    /**
     * @param newEntity    the new object to persist
     * @param viewId       the view to redirect to afterwards
     * @return             the viewId again (used by Seam)
     */
    @End(beforeRedirect = true)
    public String persistAndRedirect(Object newEntity, String viewId) {
        persist(newEntity);

        return viewId;
    }
    
    /**
     * @param newEntity    the object to persist
     * @return             <code>null</code> (used by Seam)
     */
    public String persist(Object newEntity) {
        ConvenienceGenericDao dao = getDao();
        dao.saveOrUpdate(newEntity);
        
        return null;
    }
    
    // TODO updateAndRedirect = persistAndRedirect
    @End(beforeRedirect = true)
    public String updateAndRedirect(Object newEntity, String viewId) {
        update(newEntity);

        return viewId;
    }

    // TODO update = persist
    public String update(Object newEntity) {
        ConvenienceGenericDao dao = getDao();
        dao.saveOrUpdate(newEntity);

        return null;
    }
    
    /**
     * @param selEntity    the object to remove
     * @param viewId       the view to redirect to afterwards
     * @return             the viewId again (used by Seam)
     */
    @End(beforeRedirect = true)
    public String removeAndRedirect(Object selEntity, String viewId) {
        remove(selEntity);

        return viewId;
    }

    /**
     * @param selEntity    the object to remove
     * @return             <code>null</code> (used by Seam)
     */
    public String remove(Object selEntity) {
        ConvenienceGenericDao dao = getDao();
        if (selEntity == m_entity) {
            m_entity = null;
        }
        dao.delete(selEntity);
        
        return null;
    }
    
    /**
     * @param viewId       the view to redirect to
     * @return             the viewId again (used by Seam)
     */
    @End(beforeRedirect = false)
    public String redirectTo(String viewId) {
        m_entities = null;
        m_entity = null;
        return viewId;
    }
    
    /**
     * @param sel          the selected object to edit
     * @param viewId       the view to redirect to
     * @return             the viewId again (used by Seam)
     */
    public String edit(Object sel, String viewId) {
        m_entity = sel;
        m_isEntityNew = false;
        //Conversation conv = (Conversation) Component.getInstance("org.jboss.seam.core.conversation");
        //m_previousPage = conv.getViewId();
        return viewId;
    }
    
    /*public String cancelEdit() {
        return m_previousPage;
    }*/
    
    /**
     * @param clsName      the class name of the the new entity to create
     * @param viewId       the view to redirect to
     * @return             the viewId again (used by Seam)
     */
    @Begin(join = true)
    public String create(String clsName, String viewId) {
        try {
            m_currentEntityClassName = clsName;
            m_entity = Class.forName(clsName).newInstance();
            m_isEntityNew = true;
        } catch (Exception e) {
            m_facesMessages.add(
                "Could not create entity of type " + clsName + ".");
        }
        return viewId;
    }
    
    
    /**
     * @return    the entity class name that should be used
     */
    protected String getEntityClassName() {
        if (m_overrideEntityClassName != null
            && !m_overrideEntityClassName.equals("")) {
            
            if (!m_overrideEntityClassName.equals(m_currentEntityClassName)) {
                flush();
            }
            
            m_currentEntityClassName = m_overrideEntityClassName;
        }
        return m_currentEntityClassName;
    }
    
    /**
     * Flush entites and cache, restore default values.
     */
    protected void flush() {
        m_currentConditions.clear();
        m_entities = null;
        m_entitiesDirtyFlag = true;
        m_firstResult = 0;
        m_maxResults = 100;
    }

    /**
     * @return    the entity class that should be used
     */
    protected Class<?> getEntityClass() {
        try {
            return getEntityClassName() != null
                ? Class.forName(getEntityClassName()) : null;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
    
    /**
     * Load entities into member variable m_entities.
     */
    protected void loadEntities(String className) {
        if (className == null) {
            m_facesMessages.add("No entity class defined.");
            /*for (StackTraceElement e : Thread.getAllStackTraces().get(Thread.currentThread())) {
                System.out.println(e.toString());
            }*/
            return;
        }
        if (m_entities == null || m_entitiesDirtyFlag) {
            QueryObject queryObject = getQuery(className);
            
            System.out.println("---- Execute query: " + queryObject);
            System.out.println("----  from: " + queryObject.getFirstResult() + " size: " + queryObject.getMaxResults());
            
            ConvenienceGenericDao dao = getDao();
            m_entities = dao.findByQuery(queryObject);
            m_entitiesDirtyFlag = false;
            System.out.println(m_entities.size() + " entites loaded.");
            
            for (Object o : m_entities) {
                System.out.println("    " + o.toString());
            }
        }
    }
    
    /**
     * @return    the query which fits the current entity class name and
     *            the conditions.
     */
    protected QueryObject getQuery(String className) {
        Class<?> entityClass = getEntityClass();
        if (entityClass == null) {
            return null;
        }
        
        QueryObject queryObject = new QueryObject(entityClass);
        
        for (String fieldName : m_currentConditions.keySet()) {
            String value = m_currentConditions.get(fieldName);
            
            FieldInfo fieldInfo = m_entityInfoBase.getEntityInfo(
                className).getFieldInfo(fieldName);
            String fieldType = fieldInfo.getTypeString();
            
            Criteria criteria;
            // TODO complete list
            if (fieldType.equals("boolean")) {
                criteria = ComparisonCriteria.equals(fieldName, Boolean
                    .parseBoolean(value));
            } else if (fieldType.equals("enum")) {
                if ((value == null) || (value.equals(""))) {
                    criteria = null;
                } else {
                    Class enumClass = ((EnumFieldInfo) fieldInfo)
                        .getEnumClass();
                    Enum enumValue = Enum.valueOf(enumClass, value);

                    criteria = ComparisonCriteria.equals(fieldName, enumValue);
                }
            } else if (fieldType.equals("string")) {
                criteria = LikeCriteria.caseInsensitive(fieldName, value);
            } else {
                criteria = null;
            }

            if (criteria != null) {
                queryObject.addCriteria(criteria);
            }
        }
        
        queryObject.setFirstResult(m_firstResult);
        queryObject.setMaxResults(m_maxResults);
        
        return queryObject;
    }
    
    /**
     * @return    the current DAO
     */
    protected ConvenienceGenericDao getDao() {
        return getDao(getEntityClassName());
    }
    
    /**
     * @param entityClassName    the entity class name
     * @return                   the DAO of that class
     */
    protected ConvenienceGenericDao getDao(String entityClassName) {
        if (entityClassName == null) {
            m_facesMessages.add("entityClassName must not be null!");
        } else {
            try {
                return (ConvenienceGenericDao) m_daoRegistry.getFor(
                    Class.forName(entityClassName));
            } catch (ClassNotFoundException e) {
                m_facesMessages.add(
                    "Could not find DAO for class " + entityClassName);
            }
        }
        return null;
    }
}
