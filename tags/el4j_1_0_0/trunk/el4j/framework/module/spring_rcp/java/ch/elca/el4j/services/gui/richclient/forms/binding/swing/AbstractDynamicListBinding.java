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
package ch.elca.el4j.services.gui.richclient.forms.binding.swing;

import java.util.List;

import javax.swing.JList;
import javax.swing.ListSelectionModel;

import org.springframework.beans.support.PropertyComparator;
import org.springframework.binding.form.FormModel;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.ValueHolder;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.form.binding.swing.ListBinding;
import org.springframework.richclient.list.BeanPropertyValueListRenderer;

import ch.elca.el4j.services.gui.event.RefreshEvent;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * Binding for a <code>JList</code> with a collection. 
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
public abstract class AbstractDynamicListBinding extends ListBinding
    implements ApplicationListener {

    /**
     * Is the value model for the dynamic list content.
     */
    protected final ValueModel m_valueModel = new ValueHolder();
    
    /**
     * Constructor.
     * 
     * @param list
     *            Is the <code>JList</code> to bind with the java list.
     * @param formModel
     *            Is the model of the form to binding.
     * @param formPropertyPath
     *            Is the path of the form property.
     * @param displayedListItemProperty
     *            Is the property that must be used to display each item in
     *            <code>JList</code>.
     * @param listSelectionMode
     *            Is the selection mode of <code>JList</code>.
     */
    protected AbstractDynamicListBinding(JList list, FormModel formModel,
        String formPropertyPath, String displayedListItemProperty, 
        Integer listSelectionMode) {
        super(list, formModel, formPropertyPath);
        
        Reject.ifEmpty(displayedListItemProperty, "Property of item that should"
            + "be displayed in list must not be empty.");
        
        // Contains the selected item. This is a normally a collection of items.
        ValueModel selectedItemHolder 
            = formModel.getValueModel(formPropertyPath);
        setSelectedItemHolder(selectedItemHolder);
        
        // Is the class type of the selection property. This is normally an 
        // assignale form for a collection.
        Class selectionPropertyType 
            = formModel.getPropertyMetadata(formPropertyPath).getPropertyType();
        if (selectionPropertyType != null) {
            setSelectedItemType(selectionPropertyType);
        }
        
        // Set the selection mode for the JList.
        if (listSelectionMode != null) {
            setSelectionMode(listSelectionMode);
        } else {
            setSelectionMode(new Integer(
                ListSelectionModel.MULTIPLE_INTERVAL_SELECTION));
        }
        
        // How to display the list items in JList.
        setRenderer(
            new BeanPropertyValueListRenderer(displayedListItemProperty));
        // How to compare the list items in JList.
        setComparator(
            new PropertyComparator(displayedListItemProperty, true, true));

        // Set the used value model and fill the first time the JList with
        // items.
        setSelectableItemsHolder(m_valueModel);
        resetSelecteableItems();
    }
    
    /**
     * {@inheritDoc}
     */
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof RefreshEvent) {
            onRefreshEvent((RefreshEvent) event);
        }
    }
    
    /**
     * Will be invoked if a <code>RefreshEvent</code> has been received.
     * Per default selectable items will be reset.
     * 
     * @param event Is the refresh event.
     */
    protected void onRefreshEvent(RefreshEvent event) {
        resetSelecteableItems();
    }
    
    /**
     * Resets the selectable items of this list binding.
     */
    protected void resetSelecteableItems()  {
        List selectableItems = getActualSelectableItems();
        m_valueModel.setValue(selectableItems);
    }

    /**
     * @return Returns the actual list of selectable items.
     */
    protected abstract List getActualSelectableItems();
}
