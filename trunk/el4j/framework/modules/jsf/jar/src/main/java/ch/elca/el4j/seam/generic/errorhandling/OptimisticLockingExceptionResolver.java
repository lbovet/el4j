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
package ch.elca.el4j.seam.generic.errorhandling;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.hibernate.Session;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;

import ch.elca.el4j.seam.generic.Temporary;


/**
 * This class is used to resolve interactively an optimistic locking exception.
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
@Name("oleResolver")
@Scope(ScopeType.CONVERSATION)
public class OptimisticLockingExceptionResolver implements Serializable {
    /**
     * The ID of the conflict.
     */
    @RequestParameter("conflictId")
    private String m_conflictId;
    
    /**
     * The current (valid) object from the database.
     */
    @Out(value = "currentObject")
    private Object m_currentObject;
    
    /**
     * The modified (invalid) object.
     */
    @Out(value = "entity")
    private Object m_staleObject;
    
    /**
     * The page to redirect to when conflict is resolved.
     */
    @Out(value = "masterPage")
    private String m_masterPage;
    
    /**
     * A map containing all optimistic locking conflicts.
     */
    @In("#{conflicts}")
    private Temporary m_conflicts;
    
    /**
     * The current hibernate session.
     */
    @In("#{hibernateSession}")
    private Session m_session;
    
    
    /**
     * Initialize all necessary member fields.
     */
    @Begin
    @Create
    public void init() {
        EntityConflict conflict
            = (EntityConflict) m_conflicts.remove(m_conflictId);
        m_currentObject = conflict.getCurrentObject();
        m_staleObject = conflict.getStaleObject();
        m_masterPage = conflict.getRedirectPage();
    }
    
    /**
     * @return    a list of field names whose values have changed
     */
    @SuppressWarnings("unchecked")
    public String[] getChangedFields() {
        ArrayList<String> list = new ArrayList<String>();
        try {
            Map<String, PropertyDescriptor> currentProp
                = BeanUtils.describe(m_currentObject);
            Map<String, PropertyDescriptor> staleProp
                = BeanUtils.describe(m_staleObject);
            for (String prop : currentProp.keySet()) {
                Object current = currentProp.get(prop);
                Object stale = staleProp.get(prop);
                if (!current.equals(stale)) {
                    list.add(prop);
                }
            }
        } catch (Exception e) {
            return new String[0];
        }
        
        return list.toArray(new String[0]);
    }
    
    /**
     * @return    the entity class name
     */
    public String getEntityClassName() {
        return m_currentObject.getClass().getName();
    }
    
    /**
     * Save the entity.
     * 
     * @return    the page to redirect to (used by Seam)
     */
    @End(beforeRedirect = true)
    public String save() {
        m_session.merge(m_staleObject);
        return m_masterPage;
    }
}
