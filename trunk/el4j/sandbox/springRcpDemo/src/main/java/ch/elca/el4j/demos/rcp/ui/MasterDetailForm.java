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

import ch.elca.el4j.demos.rcp.helpers.ReflectionHelper;
import ch.elca.el4j.services.persistence.generic.dao.DaoRegistry;
import ch.elca.el4j.services.persistence.generic.dao.GenericDao;

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
 * @param <T> Domain class we'd like to display
 * @author David Stefan (DST)
 */
public class MasterDetailForm<T> extends AbstractTableMasterForm {

    /**
     * Name of form.
     */
    public static final String FORM_NAME = "MasterDetailForm";
    
    /**
     * The Dao.
     */
    private GenericDao<T> m_dao;
    
    /**
     * Reflection Helper for DOM class.
     */
    private ReflectionHelper<T> m_helper;
    
    /**
     * Construct a new MasterDetailForm using the given parent model and detail
     * object type.
     * 
     * @param parentFormModel
     *            Parent form model
     * @param property
     *            containing this forms data (must be a collection or an array)
     * @param type 
     *            Type of detail
     * @param sortProperty
     *            Field for sorting master table
     */
    public MasterDetailForm(HierarchicalFormModel parentFormModel,
        String property, Class<T> type, String sortProperty) {
        super(parentFormModel, property, FORM_NAME, type);
        setSortProperty(sortProperty);
        m_helper = new ReflectionHelper<T>(type);
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
     * {@inheritDoc}
     */
    @Override
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
            T entity = (T) formModel.getFormObject();
            m_dao.saveOrUpdate(entity);  
//            Book book = new Book();
//            book.setAuthorName("AAA");
//            book.setDescription("BBB");
//            book.setIsbnNumber("1234567890");
//            book.setName("CCC");
//            book.setPageNum(111);
//            book.setPublisher("DDD");
//            book.setVersion("EEE");
//            
//            Keyword key1 = new Keyword();
//            key1.setName("key1");
//            key1.setDescription("des key1");
//            
//            Keyword key2 = new Keyword();
//            key2.setName("key2");
//            key2.setDescription("des key2");
//            
//            Set set = new HashSet();
//            set.add(key1);
//            set.add(key2);
//            
//            book.setKeywords(set);
//            
//            m_dao.saveOrUpdate((T) book);
            
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
                if (count >= 2) {
                    formBuilder.row();
                    count = 0;
                }
            }
         
            // Add List with all keywords
            AllKeywordsForm allKeywords = new AllKeywordsForm();
            formBuilder.row();
            formBuilder.getLayoutBuilder().cell(
                getComponentFactory().createLabel("All Keywords"));
            formBuilder.getLayoutBuilder().cell(allKeywords.getControl(),
                "colSpan=1 align=left");
            formBuilder.getLayoutBuilder().unrelatedGapCol();
            
            DetailKeywordForm detailKeyword 
                = new DetailKeywordForm(getFormModel());
            getSelectionModel().addListSelectionListener(detailKeyword);
            formBuilder.getLayoutBuilder().cell(
                getComponentFactory().createLabel("This Keywords"));
            formBuilder.getLayoutBuilder().cell(detailKeyword.getControl(),
                "colSpan=1 align=left");
            formBuilder.getLayoutBuilder().unrelatedGapCol();
            
            
            formBuilder.row();
            formBuilder.row();
            formBuilder.getLayoutBuilder().cell(createButtonBar());
            
            updateControlsForState();
            



            return formBuilder.getForm();
        }
        
    }
}


/*
 * ValueModel o = (ValueModel)
 * getFormModel().getValueModel("keywords"); ValueHolder
 * allNodesHolder = new ValueHolder(o.getValue()); Map<String,
 * Object> context = new HashMap<String, Object>();
 * context.put(ListBinder.SELECTABLE_ITEMS_KEY, allNodesHolder);
 * context.put(ListBinder.RENDERER_KEY, new
 * BeanPropertyValueListRenderer( "name"));
 * context.put(ListBinder.SELECTION_MODE_KEY, new Integer(
 * ListSelectionModel.SINGLE_SELECTION));
 * formBuilder.add(getBindingFactory().createBinding(JList.class,
 * "keywords", context), "keywords");
 */

/*
 * JList list = getComponentFactory().createList(); list = new
 * JList(new String[]{"TEST"});
 * list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
 * list.setCellRenderer(new BeanPropertyValueListRenderer("name"));
 * formBuilder.add("keywords", list);
 * formBuilder.getLayoutBuilder().cell(
 * getComponentFactory().createLabel("This Keywords"));
 * formBuilder.getLayoutBuilder().cell(thisKeywords.getControl(),
 * "colSpan=1 align=left");
 * formBuilder.getLayoutBuilder().unrelatedGapCol();
 * formBuilder.add("keywords", getComponentFactory()
 * .createListValueModelComboBox( model2.getValueModel("all"),
 * model2.getValueModel("all"), ""));
 */

//JPanel view = getComponentFactory().createPanel();    
//view.add(formBuilder.getForm(), BorderLayout.WEST);
//view.add(mini.getControl(), BorderLayout.EAST);
//return view;

/*
 * formBuilder.add("keywords", "colSpan=1 align=left"); KeywordDao
 * dao2 = (KeywordDao) getApplicationContext().getBean(
 * "keywordDao"); HierarchicalFormModel model =
 * FormModelHelper.createFormModel(dao2); TestForm allKeyword = new
 * TestForm(model, "all", this); TestForm thisKeywords = new
 * TestForm(getFormModel(), "keywords", this);
 * getSelectionModel().addListSelectionListener(thisKeywords);
 * formBuilder.getLayoutBuilder().cell(
 * getComponentFactory().createLabel("All Keywords"));
 * formBuilder.getLayoutBuilder().cell(allKeyword.getControl(),
 * "colSpan=1 align=left");
 * formBuilder.getLayoutBuilder().unrelatedGapCol();
 */

//HierarchicalFormModel model = FormModelHelper
//.createFormModel((Book) getFormModel().getFormObject());
//MiniMasterDetail mini = new MiniMasterDetail(model, "keywords", this);
//getSelectionModel().addListSelectionListener(mini);

//JList masterList = getComponentFactory().createList(); 
//masterList.setModel(mini.getMasterEventList());
//masterList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//// TODO fix me ;)
//masterList.setFixedCellWidth(120);
//// Configure the list to be the size desired
//masterList.setVisibleRowCount(4);
//// Setup our selection listener so that it controls the detail form
//installSelectionHandler();
//// Install the renderer, if configured
//masterList.setCellRenderer(new BeanPropertyValueListRenderer("name"));
//JScrollPane sp = new JScrollPane(masterList);
//formBuilder.getLayoutBuilder().cell(getComponentFactory().createLabel("Keywords"));
//formBuilder.getLayoutBuilder().cell(sp, "colSpan=1 align=left");
//formBuilder.getLayoutBuilder().unrelatedGapCol();

//formBuilder.getLayoutBuilder().cell(mini.getDetail().getControl(), "colSpan=1 align=left");