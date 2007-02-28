/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.demos.rcp.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumnModel;

import org.springframework.binding.form.FormModel;
import org.springframework.binding.form.HierarchicalFormModel;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.ObservableList;
import org.springframework.richclient.form.AbstractDetailForm;
import org.springframework.richclient.form.AbstractTableMasterForm;
import org.springframework.richclient.form.builder.TableFormBuilder;

import ch.elca.el4j.demos.rcp.helpers.EntityHelper;
import ch.elca.el4j.services.persistence.generic.dao.DaoRegistry;
import ch.elca.el4j.services.persistence.generic.dao.GenericDao;

/**
 * 
 * This class is a prototype for a master/detail pair. It works together with 
 * the master/detail view and is completely generic. 
 * It gets the domain class from the view and uses the ReflectionHelper to get
 * the needed knowledge about the domain class.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @param <T> Domain class we'd like to display
 * @author David Stefan (DST)
 */
public class MasterDetailForm<T> extends AbstractTableMasterForm {

    /**
     * Name of form.
     */
    public static final String FORM_NAME = "MasterDetailForm";
    
    /**
     * The Generic Dao we use to get and store our domain objects.
     */
    private GenericDao<T> m_dao;
    
    /**
     * Reflection Helper for domain class.
     */
    private EntityHelper<T> m_helper;
    
    /**
     * Construct a new MasterDetailForm using the given parent model and detail
     * object type.
     * 
     * @param parentFormModel
     *            The formModel containing the data for the table
     * @param property
     *            The property, i.e. getter method, containing this forms data
     *            (must be a collection or an array)
     * @param type
     *            The class type of the detail form
     * @param sortProperty
     *            Field for sorting master table
     */
    public MasterDetailForm(HierarchicalFormModel parentFormModel,
        String property, Class<T> type, String sortProperty) {
        super(parentFormModel, property, FORM_NAME, type);
        setSortProperty(sortProperty);
        m_helper = new EntityHelper<T>(type);
        DaoRegistry registry 
            = (DaoRegistry) getApplicationContext().getBean("daoRegistry");
        m_dao = (GenericDao<T>) registry.getFor(type);
    }
    
    /**
     * Get the property names to show in columns of the master table.
     * 
     * @return String[] array of property names
     */
    protected String[] getColumnPropertyNames() {
        List<String> properties = m_helper.getProperties();
        return properties.toArray(new String[0]);
    }

    /**
     * {@inheritDoc}
     */
    protected AbstractDetailForm createDetailForm(HierarchicalFormModel 
        parentFormModel, ValueModel valueHolder, ObservableList masterList) {
        
        return new DetailForm(parentFormModel, "detailForm", valueHolder,
            masterList);
    }

    /**
     * 
     * {@inheritDoc}
     */
    protected JComponent createFormControl() {
        JComponent comp = super.createFormControl();
        TableColumnModel tcm = getMasterTable().getColumnModel();
        for (int i = 0; i < m_helper.getProperties().size(); i++) {
            // Checkstyle: MagicNumber off
            tcm.getColumn(i).setPreferredWidth(100);
            // Checkstyle: MagicNumber on
        }
        return comp;
    }

    
    /**
     * Define what we would like to do when the delete command is executed.
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void deleteSelectedItems() {
        ListSelectionModel sm = getSelectionModel();
        if (sm.isSelectionEmpty()) {
            return;
        }
        getDetailForm().reset();

        int min = sm.getMinSelectionIndex();
        int max = sm.getMaxSelectionIndex();

        Collection<T> toDelete = new ArrayList<T>();
        
        // Loop backwards and delete each selected item in the interval
        for (int index = max; index >= min; index--) {
            if (sm.isSelectedIndex(index)) {
                toDelete.add((T) getMasterEventList().getElementAt(index));
                getMasterEventList().remove(index);
            }
        }
        m_dao.delete(toDelete);
    }
    
    /**
     * According to the Spring manual, this method needs to be overriden for 
     * Hibernate to work.
     * 
     * {@inheritDoc}
     */
    protected Class getMasterCollectionType(ValueModel collectionPropertyVM) {
        return List.class;
    }
    
    /**
     * This class is the detailed form of the master/detail pair.
     */
    class DetailForm extends AbstractDetailForm {

        /**
         * Constructor.
         * 
         * @param parentFormModel
         *            The parent form model
         * @param formId The Id of the form
         * @param childFormObjectHolder The Child Form Object Holder
         * @param masterList
         *            The list containing all elements of the master table
         */
        public DetailForm(HierarchicalFormModel parentFormModel, String formId,
            ValueModel childFormObjectHolder, ObservableList masterList) {
            super(parentFormModel, formId, childFormObjectHolder, masterList);
        }
        
        /**
         * Do the database storing stuff in the postCommit of the detail form.
         * {@inheritDoc}
         */
        @SuppressWarnings("unchecked")
        @Override
        public void postCommit(FormModel formModel) {
            super.postCommit(formModel);
            T entity = (T) formModel.getFormObject();
            m_dao.saveOrUpdate(entity);  
        }

        /**
         * {@inheritDoc}
         */
        protected JComponent createFormControl() {    
            int count = 0;
            TableFormBuilder formBuilder = new TableFormBuilder(
                getBindingFactory());
            formBuilder.setLabelAttributes("colGrId=label colSpec=right:pref");

            // Add properties of detail form
            formBuilder.row();
            List<String> properties = m_helper.getProperties();
            for (String elem : properties) {
                formBuilder.add(elem, "colSpan=1 align=left");
                count++;
                // Split the properties into two rows, that's why we need the 2
                if (count >= 2) {
                    formBuilder.row();
                    count = 0;
                }
            }  
            formBuilder.row();
            formBuilder.row();
            formBuilder.getLayoutBuilder().cell(createButtonBar());
            updateControlsForState();
            return formBuilder.getForm();
        }
    }
}