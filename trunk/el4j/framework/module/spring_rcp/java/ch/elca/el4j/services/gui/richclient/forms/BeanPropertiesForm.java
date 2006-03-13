/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.services.gui.richclient.forms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.binding.form.PropertyMetadata;
import org.springframework.binding.form.ValidatingFormModel;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.binding.Binder;
import org.springframework.richclient.form.binding.Binding;
import org.springframework.richclient.form.builder.TableFormBuilder;
import org.springframework.util.StringUtils;

import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;

/**
 * Properties form for beans.
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
public class BeanPropertiesForm extends AbstractForm 
    implements InitializingBean, BeanNameAware {

    /**
     * Are all displayed bean properties in the form.
     */
    private String[] m_shownBeanProperties;
    
    /**
     * List of bean properties that should be read only.
     */
    private List m_readOnlyBeanProperties;
    
    /**
     * Are specific binders for the shown bean properties.
     */
    private Map m_specificBinders = Collections.EMPTY_MAP;

    /**
     * Is the first focused component.
     */
    private JComponent m_firstFocusedComponent = null;

    /**
     * Is the name of this bean.
     */
    private String m_beanName;

    /**
     * {@inheritDoc}
     */
    protected JComponent createFormControl() {
        setReadOnlyProperties();
        return createComponent();
    }

    /**
     * @return Returns created component.
     */
    protected JComponent createComponent() {
        TableFormBuilder formBuilder 
            = new TableFormBuilder(getBindingFactory());
        for (int i = 0; i < m_shownBeanProperties.length; i++) {
            String propertyName = m_shownBeanProperties[i];
            Binder specificBinder 
                = (Binder) m_specificBinders.get(propertyName);
            JComponent[] components;
            if (specificBinder == null) {
                components = formBuilder.add(propertyName);
            } else {
                Binding binding = specificBinder.bind(
                    getFormModel(), propertyName, Collections.EMPTY_MAP); 
                components = formBuilder.add(binding);
            }
            
            if (m_firstFocusedComponent == null && components.length >= 2) {
                m_firstFocusedComponent = components[1];
            }
            if (i + 1 < m_shownBeanProperties.length) {
                formBuilder.row();
            }
        }
        return formBuilder.getForm();
    }

    /**
     * Sets properties read only that are in read-only-list and exists in 
     * shown properties array.
     */
    protected void setReadOnlyProperties() {
        ValidatingFormModel validatingFormModel = getValidatingFormModel();
        for (int i = 0; i < m_shownBeanProperties.length; i++) {
            String propertyName = m_shownBeanProperties[i];
            boolean readOnly = m_readOnlyBeanProperties.contains(propertyName);
            PropertyMetadata propertyMetadata 
                = validatingFormModel.getPropertyMetadata(propertyName);
            propertyMetadata.setReadOnly(readOnly);
        }
    }

    /**
     * Sets focus to first component.
     */
    public void focusFirstComponent() {
        if (m_firstFocusedComponent != null) {
            m_firstFocusedComponent.requestFocusInWindow();
        }
    }

    /**
     * @return Returns the shownBeanProperties.
     */
    public final String[] getShownBeanProperties() {
        return m_shownBeanProperties;
    }

    /**
     * Are all displayed bean properties in the form. Per default all these 
     * properties are editable. Note that the order of properties whill be taken
     * for components order too.
     * 
     * @param shownBeanProperties The shownBeanProperties to set.
     */
    public final void setShownBeanProperties(String[] shownBeanProperties) {
        m_shownBeanProperties = shownBeanProperties;
    }

    /**
     * @return Returns the readOnlyBeanProperties.
     */
    public final List getReadOnlyBeanProperties() {
        return m_readOnlyBeanProperties;
    }

    /**
     * @param readOnlyBeanProperties The readOnlyBeanProperties to set.
     */
    public final void setReadOnlyBeanProperties(
        List readOnlyBeanProperties) {
        m_readOnlyBeanProperties = readOnlyBeanProperties;
    }

    /**
     * @return the specificBinders
     */
    public final Map getSpecificBinders() {
        return m_specificBinders;
    }

    /**
     * Method to set specific binders for shown bean properties. The key of each
     * map entry is a shown bean property. The value must be of type 
     * <code>org.springframework.richclient.form.binding.Binder</code>.
     * 
     * @param specificBinders the specificBinders to set
     */
    public final void setSpecificBinders(Map specificBinders) {
        m_specificBinders = specificBinders;
    }

    /**
     * @param propertiesId The propertiesId to set.
     */
    public final void setPropertiesId(String propertiesId) {
        setId(propertiesId);
    }
    
    /**
     * @return Returns the propertiesId.
     */
    public final String getPropertiesId() {
        return getId();
    }
    
    /**
     * @param model Is the validation form model to set.
     */
    public final void setValidatingFormModel(ValidatingFormModel model) {
        super.setFormModel(model);
    }
    
    /**
     * @return Returns the validatingFormModel.
     */
    public final ValidatingFormModel getValidatingFormModel() {
        return super.getFormModel();
    }

    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(
            m_shownBeanProperties, "shownBeanProperties", this);
        
        if (getReadOnlyBeanProperties() == null) {
            setReadOnlyBeanProperties(new ArrayList());
        }
        if (!StringUtils.hasText(getPropertiesId())) {
            setPropertiesId(getBeanName());
        }
        CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(
            getPropertiesId(), "propertiesId", this);
        
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
