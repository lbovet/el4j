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
package ch.elca.el4j.services.gui.richclient.views;

import java.awt.Component;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.apache.commons.beanutils.BasicDynaClass;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.springframework.binding.form.ValidatingFormModel;
import org.springframework.binding.form.support.DefaultFormModel;
import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.form.binding.Binder;
import org.springframework.richclient.util.GuiStandardUtils;
import org.springframework.richclient.util.SpringLayoutUtils;
import org.springframework.util.StringUtils;

import ch.elca.el4j.services.gui.richclient.forms.BeanPropertiesForm;
import ch.elca.el4j.services.gui.richclient.support.DynaBeanPropertyAccessStrategy;
import ch.elca.el4j.services.gui.richclient.utils.ComponentUtils;
import ch.elca.el4j.services.gui.search.AbstractSearchItem;
import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;
import ch.elca.el4j.services.search.QueryObject;
import ch.elca.el4j.services.search.criterias.AbstractCriteria;
import ch.elca.el4j.services.search.events.QueryObjectEvent;

/**
 * Search view.
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
public class SearchView extends AbstractView {
    /**
     * Id for the search command.
     */
    public static final String SEARCH_COMMAND_ID = "searchCommand";

    /**
     * Id for the clear command.
     */
    public static final String CLEAR_COMMAND_ID = "clearCommand";

    /**
     * Is the name suffix of the generated bean.
     */
    public static final String SEARCH_BEAN_NAME_SUFFIX = "SearchBean";
    
    /**
     * Is the search command.
     */
    protected ActionCommand m_searchCommand = null;
    
    /**
     * Is the clear command.
     */
    protected ActionCommand m_clearCommand = null;

    /**
     * Are the search items for this view.
     */
    private AbstractSearchItem[] m_searchItems;
    
    /**
     * Are the generated property names.
     */
    private String[] m_propertyNames;
    
    /**
     * Are the property types of the generated properties.
     */
    private Class[] m_propertyTypes;
    
    /**
     * Are the initial values of the generated properties.
     */
    private Object[] m_initialValues;
    
    /**
     * Is the bean properties forms for the search items.
     */
    private BeanPropertiesForm m_beanPropertiesForm;
    
    /**
     * Is the form model.
     */
    private ValidatingFormModel m_formModel;
    
    /**
     * Is the dynamically created search bean.
     */
    private DynaClass m_dynaSearchClass;
    
    /**
     * Is the panel where the search components do take place.
     */
    private JPanel m_control;
    
    /**
     * {@inheritDoc}
     */
    protected JComponent createControl() {
        m_control = getComponentFactory().createPanel(new SpringLayout());
        JComponent searchControl = getSearchComponent();
        ComponentUtils.addFocusListenerRecursivly(searchControl, 
            new FocusAdapter() {
                public void focusGained(FocusEvent e) {
                    Component focusedComponent = e.getComponent();
                    setLastFocusedComponent(focusedComponent);
                }
            }
        );
        m_control.add(searchControl);
        m_control.add(getButtonComponent());
        SpringLayoutUtils.makeCompactGrid(m_control, 
            m_control.getComponentCount(), 1, 0, 0, 0, 0);
        return m_control;
    }
    
    /**
     * @return Returns the search component. This component contains all 
     *         search items.
     */
    protected JComponent getSearchComponent() {
        generateSearchBean();
        initializeFormModel();
        initializeSearchPropertiesForm();
        
        JComponent searchComponent = m_beanPropertiesForm.getControl();
        searchComponent.setMaximumSize(searchComponent.getMinimumSize());
        GuiStandardUtils.attachDialogBorder(searchComponent);
        return searchComponent;
    }

    /**
     * Generates the search bean.
     */
    protected void generateSearchBean() {
        m_propertyNames = new String[m_searchItems.length];
        m_propertyTypes = new Class[m_searchItems.length];
        m_initialValues = new Object[m_searchItems.length];
        DynaProperty[] dynaProperties = new DynaProperty[m_searchItems.length];
        for (int i = 0; i < m_searchItems.length; i++) {
            AbstractSearchItem searchItem = m_searchItems[i];
            String propertyName = getPropertyName(searchItem);
            Class propertyType = getPropertyType(searchItem);
            m_propertyNames[i] = propertyName;
            m_propertyTypes[i] = propertyType;
            m_initialValues[i] = searchItem.getInitialValue();
            dynaProperties[i] = new DynaProperty(propertyName, propertyType);
        }
        m_dynaSearchClass = new BasicDynaClass(
            getId() + SEARCH_BEAN_NAME_SUFFIX, null, dynaProperties);
    }

    /**
     * Initializes the form model.
     */
    protected void initializeFormModel() {
        DynaBean dynaBean = getInitializedDynaBean();
        DynaBeanPropertyAccessStrategy strategy 
            = new DynaBeanPropertyAccessStrategy(dynaBean);
        m_formModel = new DefaultFormModel(strategy, false);
    }

    /**
     * Initializes the search properties form.
     */
    protected void initializeSearchPropertiesForm() {
        m_beanPropertiesForm = new BeanPropertiesForm();
        m_beanPropertiesForm.setPropertiesId(getId());
        m_beanPropertiesForm.setShownBeanProperties(m_propertyNames);
        m_beanPropertiesForm.setValidatingFormModel(m_formModel);
        
        Map<String, Binder> specificBinders = new HashMap<String, Binder>();
        for (int i = 0; i < m_searchItems.length; i++) {
            AbstractSearchItem searchItem = m_searchItems[i];
            Binder specificBinder = searchItem.getSpecificBinder();
            if (specificBinder != null) {
                String propertyName = getPropertyName(searchItem);
                specificBinders.put(propertyName, specificBinder);
            }
        }
        m_beanPropertiesForm.setSpecificBinders(specificBinders);
        
        try {
            m_beanPropertiesForm.afterPropertiesSet();
        } catch (Exception e) {
            CoreNotificationHelper.notifyMisconfiguration(
                "Form for search view '" + getId() 
                + "' could not be created.", e);
        }
    }

    /**
     * @return Returns the created and initialized bean.
     */
    protected DynaBean getInitializedDynaBean() {
        DynaBean dynaBean = null;
        try {
            dynaBean = m_dynaSearchClass.newInstance();
        } catch (Exception e) {
            CoreNotificationHelper.notifyMisconfiguration(
                "Could not create an instance of the dynamic search class from"
                + " view '" + getId() + "'.", e);
        }
        initializeDynaBean(dynaBean);
        return dynaBean;
    }
    
    /**
     * Initializes the given dynamic bean.
     * 
     * @param dynaBean Is the bean to initialize.
     */
    protected void initializeDynaBean(DynaBean dynaBean) {
        for (int i = 0; i < m_propertyNames.length; i++) {
            String propertyName = m_propertyNames[i];
            Object initialValue = m_initialValues[i];
            dynaBean.set(propertyName, initialValue);
        }
    }


    /**
     * @return Returns the button component. This component contains the search
     *         and clear button.
     */
    protected JComponent getButtonComponent() {
        m_searchCommand = new ActionCommand(getSearchCommandId()) {
            /**
             * {@inheritDoc}
             */
            protected void doExecuteCommand() {
                search();
            }
        };
        
        m_clearCommand = new ActionCommand(getClearCommandId()) {
            /**
             * {@inheritDoc}
             */
            protected void doExecuteCommand() {
                clear();
            }
        };

        CommandGroup commandGroup = CommandGroup.createCommandGroup(null, 
            new AbstractCommand[] {m_searchCommand, m_clearCommand});
        JComponent buttonBar = commandGroup.createButtonBar();
        GuiStandardUtils.attachDialogBorder(buttonBar);
        return buttonBar;
    }

    /**
     * @return Returns the id for the search command.
     */
    protected String getSearchCommandId() {
        return SEARCH_COMMAND_ID;
    }
    
    /**
     * @return Returns the id for the clear command.
     */
    protected String getClearCommandId() {
        return CLEAR_COMMAND_ID;
    }
    
    /**
     * {@inheritDoc}
     */
    public void componentFocusGained() {
        super.componentFocusGained();
        if (isControlCreated()) {
            Component c = getLastFocusedComponent();
            if (c != null) {
                c.requestFocusInWindow();
            } else {
                m_beanPropertiesForm.focusFirstComponent();
            }
        }
    }
    
    /**
     * Executes a search based on the search items.
     */
    public void search() {
        if (isControlCreated()) {
            m_formModel.commit();
            Map<Class, QueryObject> queryObjects
                = new HashMap<Class, QueryObject>();
            DynaBean dynaBean = (DynaBean) m_formModel.getFormObject();
            for (int i = 0; i < m_searchItems.length; i++) {
                AbstractSearchItem searchItem = m_searchItems[i];
                String propertyName = getPropertyName(searchItem);
                Object value = dynaBean.get(propertyName);
                AbstractCriteria[] criterias = searchItem.getCriterias(value);
                Class targetBeanClass = searchItem.getTargetBeanClass();
                QueryObject queryObject 
                    = queryObjects.get(targetBeanClass);
                if (queryObject == null) {
                    queryObject = new QueryObject(targetBeanClass);
                    queryObjects.put(targetBeanClass, queryObject);
                }
                queryObject.addCriterias(criterias);
            }
            Collection<QueryObject> queryCollection = queryObjects.values();
            QueryObjectEvent objectQueryEvent 
                = new QueryObjectEvent(this, queryCollection);
            getApplicationEventPublisher().publishEvent(objectQueryEvent);
        }
    }
    
    /**
     * Clears the search form.
     */
    public void clear() {
        if (isControlCreated()) {
            DynaBean dynaBean = getInitializedDynaBean();
            m_formModel.setFormObject(dynaBean);
        }
    }
    
    /**
     * @param searchItem
     *            Is the search item where to get data to build the property
     *            name.
     * @return Returns the property name for the given search item.
     */
    protected String getPropertyName(AbstractSearchItem searchItem) {
        return searchItem.getTargetProperty() 
            + StringUtils.capitalize(searchItem.getId());
    }
    
    /**
     * @param searchItem Is the search item where to get the property type.
     * @return Returns the property type for the given search item.
     */
    protected Class getPropertyType(AbstractSearchItem searchItem) {
        return searchItem.getType();
    }
    
    /**
     * @return Returns the searchItems.
     */
    public final AbstractSearchItem[] getSearchItems() {
        return m_searchItems;
    }

    /**
     * @param searchItems The searchItems to set.
     */
    public final void setSearchItems(AbstractSearchItem[] searchItems) {
        m_searchItems = searchItems;
    }

    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(
            getSearchItems(), "searchItems", this);
    }
}