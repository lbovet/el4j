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
package ch.elca.el4j.services.gui.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;
import ch.elca.el4j.services.persistence.generic.dto.PrimaryKeyObject;
import ch.elca.el4j.services.search.criterias.AbstractCriteria;
import ch.elca.el4j.services.search.criterias.Criteria;
import ch.elca.el4j.services.search.criterias.IncludeCriteria;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * 
 * Is an include search item.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Alex Mathey (AMA)
 */
public class IncludeSearchItem extends AbstractSearchItem {

    /**
     * Search item id.
     */
    public static final String DEFAULT_SEARCH_ITEM_ID = "include";
    
    /**
     * Search item type.
     */
    public static final Class DEFAULT_SEARCH_ITEM_TYPE = Boolean.class;
    
    /**
     * Search item initial value.
     */
    public static final Object DEFAULT_SEARCH_ITEM_INITIAL_VALUE = null;
    
    /**
     * Default constructor.
     */
    public IncludeSearchItem() {
        setId(DEFAULT_SEARCH_ITEM_ID);
        setType(DEFAULT_SEARCH_ITEM_TYPE);
        setInitialValue(DEFAULT_SEARCH_ITEM_INITIAL_VALUE);
    }
    
    /**
     * {@inheritDoc}
     */
    public AbstractCriteria[] getCriterias(Object[] values) {
        List<Criteria> criterias = new ArrayList<Criteria>();
        if (values.length > 0) {
            String field = getTargetProperty();
            Object value = values[0];
            Class type = getType();
            addCriterias(criterias, field, value, type);
        }
        
        AbstractCriteria[] array = new AbstractCriteria[criterias.size()];
        Iterator it = criterias.iterator();
        int i = 0;
        while (it.hasNext()) {
            AbstractCriteria criteria = (AbstractCriteria) it.next();
            array[i] = criteria;
            i++;
        }
        return array;
    }

    /**
     * Adds criterias to the given list.
     * 
     * @param criterias Is the list of criterias.
     * @param field Is the target property.
     * @param value Is the value to create a criteria with.
     * @param type Is the class type of the value.
     */
    protected void addCriterias(List<Criteria> criterias, 
                                String field, Object value,
                                Class type) {

        if (value == null) {
            return;
        }
        Reject.ifNull(type);
       
        if (Boolean.class.isAssignableFrom(type) 
            || Integer.class.isAssignableFrom(type)
            || Double.class.isAssignableFrom(type)
            || Long.class.isAssignableFrom(type)
            || Short.class.isAssignableFrom(type)
            || Byte.class.isAssignableFrom(type)
            || Float.class.isAssignableFrom(type)) {
            criterias.add(new IncludeCriteria(field, value));      
        } else if (PrimaryKeyObject.class.isAssignableFrom(type)) {
            Object key = ((PrimaryKeyObject) value).getKeyAsObject();
            addCriterias(criterias, field, key, key.getClass());
        } else if (Collection.class.isAssignableFrom(type)) {
            Iterator it = ((Collection) value).iterator();
            while (it.hasNext()) {
                Object o = (Object) it.next();
                addCriterias(criterias, field, o, o.getClass());
            }
        } else {
            CoreNotificationHelper.notifyMisconfiguration(
                "Unsupported type: " + type.getName());
        }
    }
    
}
