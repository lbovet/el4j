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

import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import org.springframework.binding.form.ValidatingFormModel;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.ValueHolder;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.form.binding.Binding;
import org.springframework.richclient.form.binding.swing.ListBinder;
import org.springframework.richclient.list.BeanPropertyValueListRenderer;

import ch.elca.el4j.apps.keyword.dao.KeywordDao;

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
public class AllKeywordsForm extends AbstractForm {

    /**
     * .
     */
    private JList m_list;
    
    /**
     * Property.
     */
    private String m_property;
    
    /**
     * Constructor.
     */
    public AllKeywordsForm() {
        super();
        KeywordDao dao 
            = (KeywordDao) getApplicationContext().getBean("keywordDao");
        ValidatingFormModel model = FormModelHelper.createFormModel(dao);
        setFormModel(model);
        m_property = "all";
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected JComponent createFormControl() {

        ValueModel o = (ValueModel) getFormModel().getValueModel(m_property);

        ValueHolder allNodesHolder = new ValueHolder(o.getValue());

        Map<String, Object> context = new HashMap<String, Object>();
        context.put(ListBinder.SELECTABLE_ITEMS_KEY, allNodesHolder);
        context.put(ListBinder.RENDERER_KEY, new BeanPropertyValueListRenderer(
            "name"));
        context.put(ListBinder.SELECTION_MODE_KEY, new Integer(
            ListSelectionModel.SINGLE_SELECTION));
        Binding binding = getBindingFactory().createBinding(JList.class,
            m_property, context);

        JList list = (JList) binding.getControl();

        m_list = getComponentFactory().createList();
        m_list.setSize(50,50);
        m_list.setModel(list.getModel());
        m_list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        m_list.setCellRenderer(new BeanPropertyValueListRenderer("name"));
        JScrollPane sp = new JScrollPane(m_list);
        return sp;
    }
}
