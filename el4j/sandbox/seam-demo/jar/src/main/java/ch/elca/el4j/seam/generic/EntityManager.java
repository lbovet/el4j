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
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

import ch.elca.el4j.services.persistence.generic.dao.ConvenienceGenericDao;
import ch.elca.el4j.services.persistence.generic.dao.impl.FallbackDaoRegistry;
import ch.elca.el4j.services.search.QueryObject;


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
@SuppressWarnings("unchecked")
public class EntityManager implements Serializable, PagedEntityManager {
    /**
     * The injected DAO registry (from Spring config xml).
     */
    @In("#{daoRegistry}")
    private FallbackDaoRegistry m_daoRegistry;
    
    /**
     * The currently selected filters.
     * @see EntityFilters
     */
    @In(value = "#{filters}", required = false)
    private EntityFilters m_filters;
   
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
    
    private int m_entitiesCount = -1;
    
    /**
     * The currently active entity class name.
     */
    private String m_currentEntityClassName;

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
    private boolean m_viewReset = true;
    
    @Create
    public void create() {
        System.out.println("@create");
    }
    
    @Destroy
    public void destroy() {
        System.out.println("@destroy");
    }
    
    /**
     * Invalidates the current entites.
     */
    public void invalidateView() {
        System.out.println("invalidate");
        m_entitiesCount = -1;
        m_entities = null;
        m_viewReset = true;
    }
    
    /**
     * Flush entites and restore default values.
     */
    public void reset() {
        System.out.println("reset");
        invalidateView();
        m_entity = null;
        m_firstResult = 0;
        m_maxResults = QueryObject.getDefaultMaxResults();
    }
    
    /**
     * @return    the number of entities
     */
    @Begin(join = true)
    public int getEntityCount() {
        System.out.print("getEntityCount()");
        if (m_entitiesCount == -1) {
            refreshEntityClassName();
            m_entitiesCount = getEntityCount(m_currentEntityClassName);
        }
        System.out.println(" > " + m_entitiesCount);
        return m_entitiesCount;
    }
    
    /** {@inheritDoc} */
    public int getEntityCount(String entityClassName) {
        if (entityClassName == null) {
            m_facesMessages.add("entityClassName must not be null!");
            return 0;
        }
        System.out.println("getEntityCount(" + entityClassName + ")");
        if (!entityClassName.equals(m_currentEntityClassName)
            || m_entitiesCount == -1) {
            
            QueryObject queryObject = getQuery(entityClassName);
            
            ConvenienceGenericDao dao = getDao(entityClassName);
            if (dao == null) {
                m_entitiesCount = -1;
            } else {
                m_entitiesCount = dao.findCountByQuery(queryObject);
                System.out.println("There are " + m_entitiesCount + " entites available.");
            }
        }
        return m_entitiesCount;
        
    }
    
    /** {@inheritDoc} */
    public void setRange(int first, int count) {
        System.out.println("setRange(" + first + ", " + count + ")");
        if (first != m_firstResult || m_maxResults != count) {
            m_entities = null;
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
    @Factory(value = "entities", scope = ScopeType.EVENT)
    public List<Object> getEntities() {
        System.out.println("--createEntities");
        refreshEntityClassName();
        return getEntities(m_currentEntityClassName);
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
     * @param newEntity    the new object to save or update
     * @param viewId       the view to redirect to afterwards
     * @return             the viewId again (used by Seam)
     */
    @End(beforeRedirect = true)
    public String saveOrUpdateAndRedirect(Object newEntity, String viewId) {
        saveOrUpdate(newEntity);

        return viewId;
    }
    
    /**
     * @param newEntity    the object to save or update
     * @return             <code>null</code> (used by Seam)
     */
    public String saveOrUpdate(Object newEntity) {
        ConvenienceGenericDao dao = getDao();
        dao.saveOrUpdate(newEntity);
        
        return null;
    }
    
    /**
     * @param selEntity    the object to delete
     * @param viewId       the view to redirect to afterwards
     * @return             the viewId again (used by Seam)
     */
    @End(beforeRedirect = true)
    public String deleteAndRedirect(Object selEntity, String viewId) {
        delete(selEntity);

        return viewId;
    }

    /**
     * @param selEntity    the object to delete
     * @return             <code>null</code> (used by Seam)
     */
    public String delete(Object selEntity) {
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
    @End(beforeRedirect = true)
    public String redirectTo(String viewId) {
        invalidateView();
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
        return viewId;
    }
    
    /**
     * Cancels editing (and ends Seam conversation).
     * 
     * Normally used like this:
     * <pre>&lt;s:button value="Cancel" view="redirectPage"
     *  action="entityManager.cancelEdit()"&gt;</pre>
     */
    @End(beforeRedirect = true)
    public void cancelEdit() { }
    
    /**
     * @param clsName      the class name of the the new entity to create
     * @param viewId       the view to redirect to
     * @return             the viewId again (used by Seam)
     */
    @Begin(join = true)
    public String createEntity(String clsName, String viewId) {
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
     * @return    <code>true</code> if m_currentEntityClassName has changed
     */
    protected boolean refreshEntityClassName() {
        if (m_overrideEntityClassName != null
            && !m_overrideEntityClassName.equals("")
            && !m_overrideEntityClassName.equals(m_currentEntityClassName)) {
            
            reset();
            m_currentEntityClassName = m_overrideEntityClassName;
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * @param className    the name of the class
     * @return             the entity class that should be used
     */
    protected Class<?> getEntityClass(String className) {
        try {
            return className != null
                ? Class.forName(className) : null;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
    
    /**
     * Load entities into member variable m_entities.
     * 
     * @param className    the name of the class which entites should be loaded
     */
    protected void loadEntities(String className) {
        if (className == null) {
            m_facesMessages.add("No entity class defined.");
            /*for (StackTraceElement e : Thread.getAllStackTraces().get(Thread.currentThread())) {
                System.out.println(e.toString());
            }*/
            return;
        }
        if (m_entities == null) {
            QueryObject queryObject = getQuery(className);
            
            System.out.println("---- Execute query: " + queryObject);
            System.out.println("----  from: " + queryObject.getFirstResult() + " size: " + queryObject.getMaxResults());
            
            ConvenienceGenericDao dao = getDao();
            m_entities = dao.findByQuery(queryObject);
            System.out.println(m_entities.size() + " entites loaded.");
            
            for (Object o : m_entities) {
                System.out.println("    " + o.toString());
            }
        }
    }
    
    /**
     * @param className    the name of the class which entites should be loaded
     * @return    the query which fits the current entity class name and
     *            the filters or <code>null</code> if an error occured.
     */
    protected QueryObject getQuery(String className) {
        Class<?> entityClass = getEntityClass(className);
        if (entityClass == null) {
            return null;
        }
        
        QueryObject queryObject = new QueryObject(entityClass);
        
        m_filters.apply(queryObject);
        
        queryObject.setFirstResult(m_firstResult);
        queryObject.setMaxResults(m_maxResults);
        
        return queryObject;
    }
    
    /**
     * @return    the current DAO
     */
    protected ConvenienceGenericDao getDao() {
        refreshEntityClassName();
        return getDao(m_currentEntityClassName);
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
