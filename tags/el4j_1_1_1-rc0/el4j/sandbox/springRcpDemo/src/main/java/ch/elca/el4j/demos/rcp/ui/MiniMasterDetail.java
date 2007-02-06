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
package ch.elca.el4j.demos.rcp.ui;

import java.util.Set;

import javax.swing.JComponent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.springframework.binding.form.FormModel;
import org.springframework.binding.form.HierarchicalFormModel;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.ObservableList;
import org.springframework.richclient.form.AbstractDetailForm;
import org.springframework.richclient.form.binding.BindingFactory;
import org.springframework.richclient.form.builder.TableFormBuilder;
import org.springframework.richclient.list.BeanPropertyValueListRenderer;

import ca.odell.glazedlists.swing.EventSelectionModel;

import ch.elca.el4j.apps.keyword.dom.Keyword;
import ch.elca.el4j.apps.refdb.dom.Book;

/**
 * 
 * This class is ...
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
public class MiniMasterDetail extends SimpleListMasterDetailForm 
    implements ListSelectionListener {

    
    private MasterDetailForm.DetailForm m_form;
    
    public static final String FORM_NAME = "keyword";

    /**
     * Construct a new GenericContactForm using the given parent model and detail object
     * type.
     * 
     * @param parentFormModel Parent form model
     * @param property containing this forms data (must be a collection or an array)
     */
    public MiniMasterDetail(HierarchicalFormModel parentFormModel, String property,
        MasterDetailForm.DetailForm form) {
        super( parentFormModel, property, FORM_NAME, Keyword.class );
        setCellRenderer(new BeanPropertyValueListRenderer("name"));
        m_form = form;
        configure();
    }

    /*
    * (non-Javadoc)
    * @see org.springframework.richclient.form.AbstractMasterForm#createDetailForm(org.springframework.binding.form.NestingFormModel,
    *      org.springframework.binding.value.ValueModel)
    */
   @Override
   protected AbstractDetailForm createDetailForm(HierarchicalFormModel parentFormModel, ValueModel valueHolder,
           ObservableList masterList) {
       return new AbstractDetailForm( parentFormModel, "contactDetail", valueHolder, masterList ) {

           @Override
           protected JComponent createFormControl() {
               BindingFactory bf = getBindingFactory();
               TableFormBuilder formBuilder = new TableFormBuilder(bf);
               formBuilder.setLabelAttributes( "colGrId=label colSpec=right:pref" );

               formBuilder.row();
//               formBuilder.add( bf.createBoundComboBox( "typeId", MasterLists.CONTACT_TYPE ), "align=left" );
               formBuilder.add("name", "align=left");
               formBuilder.row();
               formBuilder.add("description", "align=left");

               updateControlsForState();

               return formBuilder.getForm();
           }
           
           @Override
        public void postCommit(FormModel formModel) {
               Book book = (Book) m_form.getFormObject();
               Keyword keyword = (Keyword) formModel.getFormObject();
               Set<Keyword> keywords = book.getKeywords();
               keywords.add(keyword);
               book.setKeywords(keywords);
               m_form.getFormModel().commit();
               super.postCommit(formModel);
            }
       };
   }


    public void valueChanged(ListSelectionEvent e) {
        EventSelectionModel model = (EventSelectionModel) e.getSource();
        getFormData().clear();
        if (!model.getSelected().isEmpty()) {
            Book book = (Book) model.getSelected().get(0);
            getFormData().addAll(book.getKeywords());
        }
    }
    
    public AbstractDetailForm getDetail() {
        return getDetailForm();
    }

}
