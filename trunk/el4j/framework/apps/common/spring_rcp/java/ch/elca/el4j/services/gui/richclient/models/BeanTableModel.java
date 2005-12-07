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
package ch.elca.el4j.services.gui.richclient.models;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.richclient.table.BaseTableModel;
import org.springframework.util.ClassUtils;

import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;

/**
 * Table model for beans.
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
public class BeanTableModel extends BaseTableModel 
    implements InitializingBean, BeanNameAware, ApplicationContextAware {
    /**
     * Wrapper class for java beans.
     */
    private BeanWrapper m_beanWrapper = new BeanWrapperImpl();

    /**
     * Is the bean the model is made for.
     */
    private Class m_beanClass;
    
    /**
     * Is an instance of the given bean class.
     */
    private Object m_beanInstance;

    /**
     * As column displayed properies of the given bean type.
     */
    private String[] m_columnPropertyNames;
    
    /**
     * Are the classes of the corresponding bean properties.
     */
    private Class[] m_columnClasses;

    /**
     * Flag to enable cell editing.
     */
    private boolean m_enableCellEditableInternal = false;
    
    /**
     * Helper for message source.
     */
    private MessageSourceAccessor m_messageSourceAccessor;

    /**
     * Is the application context this was created with.
     */
    private ApplicationContext m_applicationContext;

    /**
     * Is the name of this bean.
     */
    private String m_beanName;

    /**
     * {@inheritDoc}
     * 
     * Looks up names for columns by using the given column property names.
     * First look-up code is 
     * <code>className + "." + columnPropertyName</code> while first letter of
     * class name must be lowercase. Second look-up code is 
     * <code>columnPropertyName</code> standalone. If no message could be found
     * the <code>columnPropertyName</code> will be taken as column name.
     */
    public String[] createColumnNames() {
        String[] columnNames = new String[m_columnPropertyNames.length];
        String className = ClassUtils.getShortNameAsProperty(m_beanClass);
        for (int i = 0; i < m_columnPropertyNames.length; i++) {
            String columnPropertyName = m_columnPropertyNames[i];
            try {
                columnNames[i] = m_messageSourceAccessor.getMessage(
                    className + "." + columnPropertyName);
            } catch (NoSuchMessageException e) {
                columnNames[i] = m_messageSourceAccessor.getMessage(
                    columnPropertyName, columnPropertyName);
            }            
        }
        return columnNames;
    }

    /**
     * {@inheritDoc}
     */
    protected Object getValueAtInternal(Object row, int columnIndex) {
        m_beanWrapper.setWrappedInstance(row);
        return m_beanWrapper.getPropertyValue(
            m_columnPropertyNames[columnIndex]);
    }

    /**
     * {@inheritDoc}
     */
    protected boolean isCellEditableInternal(Object row, int columnIndex) {
        if (isEnableCellEditableInternal()) {
            m_beanWrapper.setWrappedInstance(row);
            return m_beanWrapper.isWritableProperty(
                m_columnPropertyNames[columnIndex]);
        } else {
            return false;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    protected void setValueAtInternal(Object value, Object bean, 
        int columnIndex) {        
        m_beanWrapper.setWrappedInstance(bean);
        m_beanWrapper.setPropertyValue(
            m_columnPropertyNames[columnIndex], value);        
    }

    /**
     * {@inheritDoc}
     * 
     * Both displayed columns are strings.
     */
    protected Class[] createColumnClasses() {
        if (m_columnClasses == null
            && m_columnPropertyNames != null) {
            m_columnClasses = new Class[m_columnPropertyNames.length];
            m_beanWrapper.setWrappedInstance(m_beanInstance);
            for (int i = 0; i < m_columnClasses.length; i++) {
                String propertyName = m_columnPropertyNames[i];
                m_columnClasses[i] 
                    = m_beanWrapper.getPropertyType(propertyName);
            }
        }
        return m_columnClasses;
    }
    
    /**
     * @return Returns the beanClass.
     */
    public final Class getBeanClass() {
        return m_beanClass;
    }

    /**
     * @param beanClass The beanClass to set.
     */
    public final void setBeanClass(Class beanClass) {
        if (beanClass != null) {
            try {
                m_beanInstance = beanClass.newInstance();
            } catch (Exception e) {
                CoreNotificationHelper.notifyMisconfiguration(
                    "Given bean class '" + beanClass.getName() + "' could not "
                    + "be instantiated by using default constructor.");
            }
        }
        m_beanClass = beanClass;
    }

    /**
     * @return Returns the columnPropertyNames.
     */
    public final String[] getColumnPropertyNames() {
        return m_columnPropertyNames;
    }

    /**
     * @param columnPropertyNames The columnPropertyNames to set.
     */
    public final void setColumnPropertyNames(String[] columnPropertyNames) {
        m_columnPropertyNames = columnPropertyNames;
    }
    
    /**
     * @return Returns the columnClasses.
     */
    public final Class[] getColumnClasses() {
        return m_columnClasses;
    }

    /**
     * @param columnClasses The columnClasses to set.
     */
    public final void setColumnClasses(Class[] columnClasses) {
        m_columnClasses = columnClasses;
    }

    /**
     * @return Returns the enableCellEditableInternal.
     */
    public final boolean isEnableCellEditableInternal() {
        return m_enableCellEditableInternal;
    }

    /**
     * @param enableCellEditableInternal The enableCellEditableInternal to set.
     */
    public final void setEnableCellEditableInternal(
        boolean enableCellEditableInternal) {
        m_enableCellEditableInternal = enableCellEditableInternal;
    }

    /**
     * @param messageSource Is the message source to set.
     */
    public final void setMessageSource(MessageSource messageSource) {
        if (messageSource != null) {
            m_messageSourceAccessor = new MessageSourceAccessor(messageSource);
        } else {
            m_messageSourceAccessor = null;
        }
    }

    /**
     * {@inheritDoc}
     * 
     * Checks properties <code>beanClass</code>, 
     * <code>columnPropertyNames</code> and <code>messageSource</code>.
     */
    public void afterPropertiesSet() throws Exception {
        CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(
            m_beanClass, "beanClass", this);
        CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(
            m_columnPropertyNames, "columnPropertyNames", this);
        
        if (m_messageSourceAccessor == null) {
            MessageSource messageSource 
                = (MessageSource) getApplicationContext().getBean(
                    "messageSource");
            setMessageSource(messageSource);
        }
        CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(
            m_messageSourceAccessor, "messageSource", this);
    }

    
    
    
    /**
     * @return Returns the application context.
     */
    public ApplicationContext getApplicationContext() {
        return m_applicationContext;
    }
    
    /**
     * {@inheritDoc}
     */
    public void setApplicationContext(ApplicationContext applicationContext) 
        throws BeansException {
        m_applicationContext = applicationContext;
    }

    /**
     * @return Returns the name of this bean.
     */
    public String getBeanName() {
        return m_beanName;
    }
    
    /**
     * {@inheritDoc}
     */
    public void setBeanName(String beanName) {
        m_beanName = beanName;
    }
}
