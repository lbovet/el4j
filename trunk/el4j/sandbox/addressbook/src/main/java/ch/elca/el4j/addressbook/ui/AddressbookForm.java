package ch.elca.el4j.addressbook.ui;

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

import ch.elca.el4j.addressbook.dao.ContactDao;
import ch.elca.el4j.addressbook.dom.Contact;

/**
 * 
 * This class is a prototype for a master/detail pair.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author David Stefan (DST)
 */
public class AddressbookForm extends AbstractTableMasterForm {

    /**
     * Name of form.
     */
    public static final String FORM_NAME = "addressbook";
    
    /**
     * The ContactDao.
     */
    private ContactDao m_dao;

    /**
     * Construct a new AddressbookForm using the given parent model and detail
     * object type.
     * 
     * @param parentFormModel
     *            Parent form model
     * @param property
     *            containing this forms data (must be a collection or an array)
     * @param type 
     *            Type of detail
     */
    public AddressbookForm(HierarchicalFormModel parentFormModel,
        String property, Class type) {
        super(parentFormModel, property, FORM_NAME, type);
        setSortProperty("firstName");
    }

    /**
     * Get the property names to show in columns of the master table.
     * 
     * @return String[] array of property names
     */
    protected String[] getColumnPropertyNames() {
        return new String[] {"firstName", "lastName", "address", "city"};
    }

    /**
     * {@inheritDoc}
     */
    protected AbstractDetailForm createDetailForm(HierarchicalFormModel 
        parentFormModel, ValueModel valueHolder, ObservableList masterList) {

        return new DetailForm(parentFormModel, "contactDetail", valueHolder,
            masterList);
    }

    /**
     * 
     * {@inheritDoc}
     */
    protected JComponent createFormControl() {
        JComponent comp = super.createFormControl();
        // Checkstyle: MagicNumber off
        TableColumnModel tcm = getMasterTable().getColumnModel();
        tcm.getColumn(0).setPreferredWidth(100);
        tcm.getColumn(1).setPreferredWidth(100);
        tcm.getColumn(2).setPreferredWidth(100);
        tcm.getColumn(3).setPreferredWidth(100);
        // Checkstyle: MagicNumber on
        return comp;
    }

    
    /**
     * Define what we would like to do when the delete command is executed.
     */
    @Override
    protected void deleteSelectedItems() {
        ListSelectionModel sm = getSelectionModel();

        if (sm.isSelectionEmpty()) {
            return;
        }

        getDetailForm().reset();

        int min = sm.getMinSelectionIndex();
        int max = sm.getMaxSelectionIndex();

        // Loop backwards and delete each selected item in the interval
        for (int index = max; index >= min; index--) {
            if (sm.isSelectedIndex(index)) {
                Contact contact = (Contact) getMasterEventList().getElementAt(
                    index);
                m_dao.delete(contact);
                getMasterEventList().remove(index);
            }
        }
    }
    
    /**
     * This class is the detailed form of the master/detail pair.
     */
    private class DetailForm extends AbstractDetailForm {

        /**
         * Constructor.
         * 
         * @param parentFormModel .
         * @param formId .
         * @param childFormObjectHolder .
         * @param masterList .
         */
        public DetailForm(HierarchicalFormModel parentFormModel, String formId,
            ValueModel childFormObjectHolder, ObservableList masterList) {
            super(parentFormModel, formId, childFormObjectHolder, masterList);

        }
        
        /**
         * Do the database storing stuff in the postCommit of the detail form.
         * {@inheritDoc}
         */
        @Override
        public void postCommit(FormModel formModel) {
            super.postCommit(formModel);
            Contact entity = new Contact();
            entity.setFirstName((String) formModel.getValueModel("firstName")
                .getValue());
            entity.setLastName((String) formModel.getValueModel("lastName")
                .getValue());
            entity.setAddress((String) formModel.getValueModel("address")
                .getValue());
            entity.setCity((String) formModel.getValueModel("city").getValue());
            m_dao.saveOrUpdate(entity);
        }

        /**
         * {@inheritDoc}
         */
        protected JComponent createFormControl() {
            TableFormBuilder formBuilder 
                = new TableFormBuilder(getBindingFactory());
            formBuilder.setLabelAttributes("colGrId=label colSpec=right:pref");

//            formBuilder.row();
//            formBuilder.add("firstName", "colSpan=1 align=left");
//            formBuilder.add("address.address1", "colSpan=1 align=left");
//           
//            formBuilder.row();
//            formBuilder.add("lastName", "colSpan=1 align=left");
//            formBuilder.add("address.address2", "colSpan=1 align=left");
//            formBuilder.row();
//            formBuilder.add("contactType", "colSpan=1 align=left");
//            formBuilder.row();
//            formBuilder.row();
//            formBuilder.getLayoutBuilder().cell(createButtonBar());

            formBuilder.row();
            formBuilder.add("firstName", "colSpan=1 align=left");
            formBuilder.add("address", "colSpan=1 align=left");
           
            formBuilder.row();
            formBuilder.add("lastName", "colSpan=1 align=left");
            formBuilder.add("city", "colSpan=1 align=left");
            formBuilder.row();
            formBuilder.row();
            formBuilder.row();
            formBuilder.getLayoutBuilder().cell(createButtonBar());
            
            updateControlsForState();

            return formBuilder.getForm();
        }
    }

    /**
     * @param dao The Dao we are using.
     */
    public void setDao(ContactDao dao) {
        this.m_dao = dao;
    }
}