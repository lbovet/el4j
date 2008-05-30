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
package ch.elca.el4j.applications.refdbseam;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;
import org.springframework.dao.DataRetrievalFailureException;

import ch.elca.el4j.services.persistence.generic.dao.ConvenienceGenericDao;
import ch.elca.el4j.services.persistence.generic.dao.DaoRegistry;
import ch.elca.el4j.services.persistence.generic.dto.AbstractIntKeyIntOptimisticLockingDto;

/**
 * 
 * This class is a generic EntityHome.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Philippe Jacot (PJA)
 * 
 * @param <T> Entity Type
 */
@Scope(ScopeType.CONVERSATION)
public abstract class AbstractEntityHome
    <T extends AbstractIntKeyIntOptimisticLockingDto> {
    
    /**
     * The logger to log to. 
     */
    @Logger
    protected Log m_log;
    
    /**
     * The dao to work on.
     */
    private ConvenienceGenericDao<T, Integer> m_dao;
    
    /**
     * The current instance.
     */
    private T m_instance;
    
    /**
     * The class of our entity.
     */
    private Class<T> m_entityClass;
       
    /**
     * The spring dao registry to load the registry from.
     */
    @In("#{daoRegistry}")
    private DaoRegistry m_daoRegistry;
    
    /**
     * Create a new Home for a given class. 
     * Try to guess the entity type.
     */
    public AbstractEntityHome() {
        super();
        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            // If the type cannot be determined, an error will be generated
            // in the create method.
            // Seems that we cannot throw this error here, as there is some
            // runtime code generation that prevents a check in the constructor
            // from working.
            ParameterizedType paramType = (ParameterizedType) type;
            m_entityClass = (Class<T>) paramType.getActualTypeArguments()[0];
        } 
    }
       
    /**
     * Create a new Home for a given class.
     * @param entityClass The class this home works with.
     */
    public AbstractEntityHome(Class<T> entityClass) {
        super();
        if (entityClass == null) {
            throw new IllegalArgumentException(
                "EntityClass should not be null");
        }
        m_entityClass = entityClass;
    }
    
    
    /**
     * Get the dao.
     * @return The dao
     */
    protected ConvenienceGenericDao<T, Integer> getDao() {
        if (m_dao == null) {
            m_dao = (ConvenienceGenericDao<T, Integer>) 
                m_daoRegistry.getFor(m_entityClass);
        }
        return m_dao;
    }
    
    
    /**
     * Set the dao.
     * @param dao The dao
     */
    protected void setDao(ConvenienceGenericDao<T, Integer> dao) {
        m_dao = dao;
    }
    
    /**
     * Abstract method the get the id of this entity.
     * @return The id of the current entity
     */
    protected Integer getId() {
        return null;
    }
    
    /**
     * Create a new instance.
     * @return The new instance
     */
    protected T createInstance() {
        try {
            m_log.info("Creating a new instance of #0", m_entityClass);
            return m_entityClass.newInstance();
        } catch (InstantiationException e) {
            m_log.error("Error instantiating #0", e , m_entityClass);
        } catch (IllegalAccessException e) {
            m_log.error("Error instantiating #0", e , m_entityClass);
        }
        return null;
    }
    
    /**
     * Try to load the instance.
     * @return The found entity
     */
    protected T loadInstance() {
        m_log.debug("Trying to load instance with ID #0", getId());
        try {
            return getDao().findById(getId());
        } catch (DataRetrievalFailureException e) {
            handleEntityNotFound(e);            
            return null;
        } 
    }
    
    /**
     * Executed if a entity specified by a ID was not found.
     * 
     * @param drfe The thrown exception
     */
    protected void handleEntityNotFound(DataRetrievalFailureException drfe) { 
        m_log.trace("Handling entity not found");
    }
       
    /**
     * Is the current instance persisted?
     * @return Whether the instance is persisted.
     */
    public boolean isPersisted() {
        return m_instance == null ? false : m_instance.getKey() != 0;
    }
    
    @Create
    public void create() {
        if (m_entityClass == null) {
            throw new IllegalStateException("Entity Type not defined");
        }
    }
    
    /**
     * Persist the current instance.
     * @return Page action from persisting
     */
    public String persist() {
        if (getInstance() == null) {
            throw new IllegalStateException("No instance available");
        }
        m_log.info("Trying to persist or update entity with ID #0 ",  
            m_instance.getKey());
        m_instance = getDao().saveOrUpdate(getInstance());
        m_log.info("Persisted or updated entity with ID #0", 
            m_instance.getKey());

        return "persisted";
    }
    
    /**
     * Delete the current entity if it is persisted.
     * @return Deleted message or null if nothing was deleted
     */
    public String delete() {
        if (getInstance() != null && isPersisted()) {
            m_dao.delete(getInstance());
            m_instance = null;
            return "deleted";
        }
        return null;
    }
    
    /**
     * Get the current instance.
     * @return The current instance
     */
    public T getInstance() {
        if (m_instance == null 
            || getId() != null && m_instance.getKey() != getId().intValue()) {
            if (getId() == null) {
                // Create a new Instance
                m_instance = createInstance();
            } else {
                m_instance = loadInstance();
            }
        }
        return m_instance;
    }
}
