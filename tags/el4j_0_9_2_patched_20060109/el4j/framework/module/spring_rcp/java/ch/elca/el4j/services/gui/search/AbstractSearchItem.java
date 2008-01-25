/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * This program is published under the GNU General Public License (GPL) license.
 * http://www.gnu.org/licenses/gpl.txt
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.services.gui.search;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;

import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;
import ch.elca.el4j.services.search.criterias.AbstractCriteria;

/**
 * Abstract form of a search item.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public abstract class AbstractSearchItem 
    implements InitializingBean, BeanNameAware {
    /**
     * Is the name of the property that is the target for searchs.
     */
    private String m_targetProperty;
    
    /**
     * Is the id of this search item.
     */
    private String m_id;
    
    /**
     * Is the type of this search item.
     */
    private Class m_type;
    
    /**
     * Is the initial value of this search item.
     */
    private Object m_initialValue;
    
    /**
     * Is the name of this bean.
     */
    private String m_beanName;
    
    /**
     * @param value Is the value for the criterias.
     * @return Returns the criterias for this search item.
     */
    public AbstractCriteria[] getCriterias(Object value) {
        return getCriterias(new Object[] {value});
    }

    /**
     * @param values Are the values for the criterias.
     * @return Returns the criterias for this search item.
     */
    public abstract AbstractCriteria[] getCriterias(Object[] values);

    /**
     * @return Returns the targetProperty.
     */
    public final String getTargetProperty() {
        return m_targetProperty;
    }

    /**
     * @param targetProperty The targetProperty to set.
     */
    public final void setTargetProperty(String targetProperty) {
        m_targetProperty = targetProperty;
    }
    
    /**
     * @return Returns the id.
     */
    public String getId() {
        return m_id;
    }

    /**
     * @param id The id to set.
     */
    public void setId(String id) {
        m_id = id;
    }

    /**
     * @return Returns the type.
     */
    public Class getType() {
        return m_type;
    }

    /**
     * @param type The type to set.
     */
    public void setType(Class type) {
        m_type = type;
    }

    /**
     * @return Returns the initialValue.
     */
    public Object getInitialValue() {
        return m_initialValue;
    }

    /**
     * @param initialValue The initialValue to set.
     */
    public void setInitialValue(Object initialValue) {
        m_initialValue = initialValue;
    }

    /**
     * @return Returns the beanName.
     */
    public final String getBeanName() {
        return m_beanName;
    }

    /**
     * {@inheritDoc}
     */
    public final void setBeanName(String beanName) {
        m_beanName = beanName;
    }

    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(
            getTargetProperty(), "targetProperty", this);
        CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(
            getId(), "id", this);
        CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(
            getType(), "type", this);
    }
}