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
package ch.elca.el4j.apps.refdb.gui.bindings;

import java.util.List;

import javax.swing.JList;
import javax.swing.ListSelectionModel;

import org.springframework.binding.form.FormModel;

import ch.elca.el4j.apps.refdb.gui.brokers.ServiceBroker;
import ch.elca.el4j.apps.refdb.service.ReferenceService;
import ch.elca.el4j.services.gui.richclient.forms.binding.swing.AbstractDynamicListBinding;

/**
 * Dynmaic binding list for keywords.
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
public class KeywordDynamicListBinding extends AbstractDynamicListBinding {
    /**
     * Constructor.
     * 
     * @param list Is the <code>JList</code> to bind with the java list.
     * @param formModel Is the model of the form to binding.
     * @param formPropertyPath Is the path of the form property.
     */
    public KeywordDynamicListBinding(JList list, FormModel formModel, 
        String formPropertyPath) {
        super(list, formModel, formPropertyPath, "name", 
            new Integer(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION));
    }

    /**
     * {@inheritDoc}
     */
    protected List getActualSelectableItems() {
        ReferenceService service = ServiceBroker.getReferenceService();
        return service.getAllKeywords();
    }
}
