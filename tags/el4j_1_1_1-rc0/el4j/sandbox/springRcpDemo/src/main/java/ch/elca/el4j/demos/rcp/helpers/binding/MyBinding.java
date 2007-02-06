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
package ch.elca.el4j.demos.rcp.helpers.binding;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.binding.Binding;

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
public class MyBinding implements Binding {

    /*
     * .
     */
//    private Set<Keyword> set;
    /**
     * .
     */
    private FormModel m_model;
    
    /**
     * Constructor.
     * @param model .
     */
    public MyBinding(FormModel model) {
        m_model = model;
    }
    
    /**
     * {@inheritDoc}
     */
    public JComponent getControl() {
        JList list = new JList();
        if (m_model.getFormObject() != null) {
            DefaultListModel mdl = new DefaultListModel();
            Book book = (Book) m_model.getFormObject();
            for (Keyword k : book.getKeywords()) {
                mdl.addElement(k);
            }
            list = new JList(mdl);
        }
        return list;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public FormModel getFormModel() {
        return m_model;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public String getProperty() {
        return "keywords";
    }

}
