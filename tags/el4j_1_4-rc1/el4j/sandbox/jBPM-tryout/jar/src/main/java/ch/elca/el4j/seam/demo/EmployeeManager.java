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
package ch.elca.el4j.seam.demo;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

import ch.elca.el4j.seam.demo.entities.Employee;
import ch.elca.el4j.services.persistence.generic.dao.ConvenienceGenericDao;
import ch.elca.el4j.services.persistence.generic.dao.impl.FallbackDaoRegistry;
import ch.elca.el4j.services.search.QueryObject;
import ch.elca.el4j.services.search.criterias.Criteria;
import ch.elca.el4j.services.search.criterias.LikeCriteria;

/**
 * This class demonstrates how to handle {@link Employee}s without the generic
 * {@link EntityManager}.
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
@Name("employeeManager")
@Scope(ScopeType.CONVERSATION)
public class EmployeeManager implements Serializable {
    /**
     * The logger.
     */
    private static Log s_logger = LogFactory.getLog(EmployeeManager.class);
    
    /**
     * The search string (injected user form input).
     */
    @In(value = "searchEmployee", required = false)
    private String m_searchEmployee;
    
    /**
     * The list of all currently loaded employees.
     */
    @Out(value = "employees")
    private List<Employee> m_employees;
    
    /**
     * The selected employee (used when user clicks on edit).
     */
    @SuppressWarnings("unused")
    @Out(value = "selectedEmployee", required = false)
    private Employee m_selectedEmployee;
    
    /**
     * The DAO registry.
     */
    @In("#{daoRegistry}")
    private FallbackDaoRegistry m_daoRegistry;
    
    /**
     * The employee DAO.
     */
    private ConvenienceGenericDao<Employee, Serializable> m_dao;
    
    /**
     * Create the Seam component.
     */
    @SuppressWarnings("unchecked")
    @Create
    public void create() {
        s_logger.debug("@create");
        m_dao = (ConvenienceGenericDao) m_daoRegistry.getFor(Employee.class);
    }
    
    /**
     * Destroy the Seam component.
     */
    @Destroy
    public void destroy() {
        s_logger.debug("@destroy");
    }
    
    /**
     * Reload all entities.
     */
    public void reset() {
        m_employees = m_dao.getAll();
    }
    
    /**
     * @return    the currently loaded employees
     */
    @Factory(value = "employees")
    public List<Employee> getEmployees() {
        if (m_employees == null) {
            reset();
        }
        return m_employees;
    }
    
    /**
     * Search for all entites "where value of propertyName = searchEmployee".
     * @param propertyName    the property name to apply the search to
     */
    @Begin(join = true)
    public void find(String propertyName) {
        QueryObject queryObject = new QueryObject(Employee.class);
        Criteria criteria = LikeCriteria.caseInsensitive(
            propertyName, m_searchEmployee);
        queryObject.addCriteria(criteria);
        
        m_employees = m_dao.findByQuery(queryObject);
    }
    
    /**
     * ATTENTION: Optimistic locking exception is NOT handled.
     * See {@link ch.elca.el4j.seam.generic.EntityManager} for how to implement
     * 
     * @param newEmployee    the new employee to save or update
     * @param viewId         the view to redirect to afterwards
     * @return               the viewId again (used by Seam)
     */
    @End(beforeRedirect = true)
    public String saveAndRedirect(Employee newEmployee, String viewId) {
        save(newEmployee);
        return viewId;
    }
    
    /**
     * ATTENTION: Optimistic locking exception is NOT handled.
     * See {@link ch.elca.el4j.seam.generic.EntityManager} for how to implement
     * 
     * @param newEmployee    the new employee to save or update
     */
    @End(beforeRedirect = true)
    public void save(Employee newEmployee) {
        m_dao.saveOrUpdate(newEmployee);
        reset();
    }
    
    /**
     * @param selectedEmployee    the selected employee to edit
     * @param viewId              the view to redirect to
     * @return                    the viewId again (used by Seam)
     */
    @Begin(join = true)
    public String edit(Employee selectedEmployee, String viewId) {
        m_selectedEmployee = selectedEmployee;
        return viewId;
    }
    
    /**
     * @param selectedEmployee    the selected employee to delete
     */
    @End(beforeRedirect = true)
    public void delete(Employee selectedEmployee) {
        m_dao.delete(selectedEmployee);
        reset();
    }
    
    /**
     * Cancels editing (and ends Seam conversation if necessary).
     */
    @End(beforeRedirect = true)
    public void cancelEdit() { }
}
