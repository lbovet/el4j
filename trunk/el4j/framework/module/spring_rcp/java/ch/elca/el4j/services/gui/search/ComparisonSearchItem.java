/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.services.gui.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;
import ch.elca.el4j.services.persistence.generic.dto.PrimaryKeyObject;
import ch.elca.el4j.services.search.criterias.AbstractCriteria;
import ch.elca.el4j.services.search.criterias.ComparisonCriteria;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * Is a comparison search item.
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
public class ComparisonSearchItem extends AbstractSearchItem {
    /**
     * Search item id.
     */
    public static final String DEFAULT_SEARCH_ITEM_ID = "comparison";

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
    public ComparisonSearchItem() {
        setId(DEFAULT_SEARCH_ITEM_ID);
        setType(DEFAULT_SEARCH_ITEM_TYPE);
        setInitialValue(DEFAULT_SEARCH_ITEM_INITIAL_VALUE);
    }

    /**
     * {@inheritDoc}
     */
    public AbstractCriteria[] getCriterias(Object[] values) {
        List criterias = new ArrayList();
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
    protected void addCriterias(List criterias, String field, Object value,
        Class type) {

        if (value == null) {
            return;
        }
        Reject.ifNull(type);
        
        // TODO Add methods in comparison criteria to directly send wrapper
        // classes of primitive types.
        if (Boolean.class.isAssignableFrom(type)) {
            criterias.add(ComparisonCriteria.equals(
                field, ((Boolean) value).booleanValue()));
        } else if (Integer.class.isAssignableFrom(type)) {
            criterias.add(ComparisonCriteria.equals(
                field, ((Integer) value).intValue()));
        } else if (Double.class.isAssignableFrom(type)) {
            criterias.add(ComparisonCriteria.equals(
                field, ((Double) value).doubleValue()));
        } else if (Long.class.isAssignableFrom(type)) {
            criterias.add(ComparisonCriteria.equals(
                field, ((Long) value).longValue()));
        } else if (Short.class.isAssignableFrom(type)) {
            criterias.add(ComparisonCriteria.equals(
                field, ((Short) value).shortValue()));
        } else if (Byte.class.isAssignableFrom(type)) {
            criterias.add(ComparisonCriteria.equals(
                field, ((Byte) value).byteValue()));
        } else if (Float.class.isAssignableFrom(type)) {
            criterias.add(ComparisonCriteria.equals(
                field, ((Float) value).floatValue()));
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
