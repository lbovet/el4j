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
package ch.elca.el4j.services.gui.richclient.pagecomponents.descriptors.impl;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.richclient.core.LabeledObjectSupport;
import org.springframework.util.StringUtils;

import ch.elca.el4j.services.gui.richclient.pagecomponents.descriptors.ExtendedPageComponentDescriptor;
import ch.elca.el4j.services.gui.richclient.pagecomponents.descriptors.GroupDescriptor;
import ch.elca.el4j.services.gui.richclient.pagecomponents.descriptors.LayoutDescriptor;

/**
 * Abstract page component descriptor.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public abstract class AbstractGroupPageComponentDescriptor 
    extends LabeledObjectSupport implements ExtendedPageComponentDescriptor, 
        BeanNameAware, ApplicationContextAware, InitializingBean,
        LayoutDescriptor, GroupDescriptor {
    /**
     * Is the id of this page component descriptor.
     */
    private String m_id;

    /**
     * Is the preferred position argument for the page component.
     */
    private Object m_preferredPositionArgument;
    
    /**
     * Is the preferred position index for the page component.
     */
    private Integer m_preferredPositionIndex;

    /**
     * Is the name of the preferred group. 
     */
    private String m_preferredGroup;

    /**
     * Is the name of the configured group. 
     */
    private String m_configuredGroup;

    /**
     * Is the application context this bean has been created with.
     */
    private ApplicationContext m_applicationContext;
    
    /**
     * Is the name of this bean.
     */
    private String m_beanName;

    /**
     * @return Returns the id.
     */
    public final String getId() {
        return m_id;
    }

    /**
     * @param id The id to set.
     */
    public final void setId(String id) {
        m_id = id;
    }

    /**
     * {@inheritDoc}
     */
    public final Object getPreferredPositionArgument() {
        return m_preferredPositionArgument;
    }

    /**
     * @param preferredPositionArgument The preferredPositionArgument to set.
     */
    public final void setPreferredPositionArgument(
        Object preferredPositionArgument) {
        m_preferredPositionArgument = preferredPositionArgument;
    }

    /**
     * {@inheritDoc}
     */
    public final Integer getPreferredPositionIndex() {
        return m_preferredPositionIndex;
    }

    /**
     * @param preferredPositionIndex The preferredPositionIndex to set.
     */
    public final void setPreferredPositionIndex(
        Integer preferredPositionIndex) {
        m_preferredPositionIndex = preferredPositionIndex;
    }

    /**
     * @return Returns the preferredGroup.
     */
    public final String getPreferredGroup() {
        return m_preferredGroup;
    }

    /**
     * @param preferredGroup The preferredGroup to set.
     */
    public final void setPreferredGroup(String preferredGroup) {
        m_preferredGroup = preferredGroup;
    }

    /**
     * @return Returns the configuredGroup.
     */
    public final String getConfiguredGroup() {
        return m_configuredGroup;
    }

    /**
     * @param configuredGroup The configuredGroup to set.
     */
    public final void setConfiguredGroup(String configuredGroup) {
        m_configuredGroup = configuredGroup;
    }

    /**
     * @return Returns the applicationContext.
     */
    public final ApplicationContext getApplicationContext() {
        return m_applicationContext;
    }

    /**
     * @param applicationContext The applicationContext to set.
     */
    public final void setApplicationContext(
        ApplicationContext applicationContext) {
        m_applicationContext = applicationContext;
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
        if (!StringUtils.hasText(getId())) {
            setId(beanName);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception { }
    
    /**
     * {@inheritDoc}
     * 
     * Fire method now public visible.
     */
    public void firePropertyChange(String propertyName, boolean oldValue, 
        boolean newValue) {
        super.firePropertyChange(propertyName, oldValue, newValue);
    }
    
    /**
     * {@inheritDoc}
     * 
     * Fire method now public visible.
     */
    public void firePropertyChange(String propertyName, int oldValue, 
        int newValue) {
        super.firePropertyChange(propertyName, oldValue, newValue);
    }
    
    /**
     * {@inheritDoc}
     * 
     * Fire method now public visible.
     */
    public void firePropertyChange(String propertyName, Object oldValue, 
        Object newValue) {
        super.firePropertyChange(propertyName, oldValue, newValue);
    }
}
