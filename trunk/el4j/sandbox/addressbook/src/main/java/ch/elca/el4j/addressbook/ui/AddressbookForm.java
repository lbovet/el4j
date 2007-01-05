package ch.elca.el4j.addressbook.ui;

import javax.swing.JComponent;
import javax.swing.table.TableColumnModel;

import org.springframework.binding.form.HierarchicalFormModel;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.ObservableList;
import org.springframework.richclient.form.AbstractDetailForm;
import org.springframework.richclient.form.AbstractTableMasterForm;
import org.springframework.richclient.form.builder.TableFormBuilder;

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
//        return new String[] {"firstName", "lastName", "address.address1", 
//            "address.address2"};
        return new String[] {"firstName", "lastName", "address"};
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
//        tcm.getColumn(3).setPreferredWidth(100);
        // Checkstyle: MagicNumber on
        return comp;
    }

    /**
     * 
     * This class is the detailed form of the master/detail pair.
     *
     * <script type="text/javascript">printFileStatus
     *   ("$URL: $",
     *    "$Revision$",
     *    "$Date$",
     *    "$Author$"
     * );</script>
     *
     * @author David Stefan (DST)
     */
    private class DetailForm extends AbstractDetailForm {

        /**
         * Constructor.
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
//            formBuilder.add("address.address2", "colSpan=1 align=left");
            formBuilder.row();
            formBuilder.row();
            formBuilder.row();
            formBuilder.getLayoutBuilder().cell(createButtonBar());
            
            updateControlsForState();

            return formBuilder.getForm();
        }
    }
}