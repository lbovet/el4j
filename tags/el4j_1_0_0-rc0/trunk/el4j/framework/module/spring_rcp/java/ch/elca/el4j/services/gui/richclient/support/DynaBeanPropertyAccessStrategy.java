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
package ch.elca.el4j.services.gui.richclient.support;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.springframework.beans.BeansException;
import org.springframework.beans.NullValueInNestedPathException;
import org.springframework.beans.PropertyAccessor;
import org.springframework.binding.MutablePropertyAccessStrategy;
import org.springframework.binding.PropertyMetadataAccessStrategy;
import org.springframework.binding.support.PropertyChangeSupportUtils;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.AbstractValueModel;
import org.springframework.binding.value.support.ValueHolder;
import org.springframework.util.Assert;
import org.springframework.util.CachingMapDecorator;

import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;

/**
 * An implementation of <code>MutablePropertyAccessStrategy</code> that provides
 * access to a <code>org.apache.commons.beanutils.DynaBean</code> of Commons
 * BeanUtils.
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
public class DynaBeanPropertyAccessStrategy 
    implements MutablePropertyAccessStrategy {

    private final ValueModel domainObjectHolder;

    private final String basePropertyPath;

    private final ValueModelCache valueModelCache;

    private final PropertyMetadataAccessStrategy metaAspectAccessor;

    /**
     * Creates a new instance of BeanPropertyAccessStrategy that will provide access
     * to the properties of the provided JavaBean.
     * 
     * @param bean JavaBean to be accessed through this class. 
     */
    public DynaBeanPropertyAccessStrategy(DynaBean dynaBean) {
        this(new ValueHolder(dynaBean));
    }

    /**
     * Creates a new instance of BeanPropertyAccessStrategy that will provide access
     * to the JavaBean contained by the provided value model.  
     * 
     * @param domainObjectHolder value model that holds the JavaBean to 
     * be accessed through this class
     */
    public DynaBeanPropertyAccessStrategy(final ValueModel domainObjectHolder) {
        Assert.notNull(domainObjectHolder, "domainObjectHolder must not be null.");
        this.domainObjectHolder = domainObjectHolder;
        this.basePropertyPath = "";
        this.valueModelCache = new ValueModelCache();
        this.metaAspectAccessor = new BeanPropertyMetaAspectAccessor();
    }

    /**
     * Creates a child instance of BeanPropertyAccessStrategy that will delegate to its 
     * parent for property access.
     * 
     * @param parent BeanPropertyAccessStrategy which will be used to provide property access
     * @param basePropertyPath property path that will as a base when accessing the parent   
     * BeanPropertyAccessStrategy
     */
    protected DynaBeanPropertyAccessStrategy(DynaBeanPropertyAccessStrategy parent, String basePropertyPath) {
        this.domainObjectHolder = parent.getPropertyValueModel(basePropertyPath);
        this.basePropertyPath = basePropertyPath;
        this.valueModelCache = parent.valueModelCache;
        this.metaAspectAccessor = new BeanPropertyMetaAspectAccessor();
    }

    public ValueModel getDomainObjectHolder() {
        return domainObjectHolder;
    }

    public ValueModel getPropertyValueModel(String propertyPath) throws BeansException {
        return (ValueModel)valueModelCache.get(getFullPropertyPath(propertyPath));
    }

    /**
     * Returns a property path that includes the base property path of the class.
     */
    private String getFullPropertyPath(String propertyPath) {
        return basePropertyPath == "" ? propertyPath : basePropertyPath + '.' + propertyPath;
    }

    /**
     * Extracts the property name from a propertyPath. 
     */
    private String getPropertyName(String propertyPath) {
        int lastSeparator = getLastPropertySeparatorIndex(propertyPath);
        if (lastSeparator == -1) {
            return propertyPath;
        } else {
            if (propertyPath.charAt(lastSeparator) == PropertyAccessor.NESTED_PROPERTY_SEPARATOR_CHAR) {
                return propertyPath.substring(lastSeparator + 1);
            } else {
                return propertyPath.substring(lastSeparator);
            }
        }
    }

    /**
     * Returns the property name component of the provided property path. 
     */
    private String getParentPropertyPath(String propertyPath) {
        int lastSeparator = getLastPropertySeparatorIndex(propertyPath);
        return lastSeparator == -1 ? "" : propertyPath.substring(0, lastSeparator);
    }

    /**
     * Returns the index of the last nested property separator in
     * the given property path, ignoring dots in keys 
     * (like "map[my.key]").
     */
    private int getLastPropertySeparatorIndex(String propertyPath) {
        boolean inKey = false;
        for (int i = propertyPath.length() - 1; i >= 0; i--) {
            switch (propertyPath.charAt(i)) {
            case PropertyAccessor.PROPERTY_KEY_SUFFIX_CHAR:
                inKey = true;
                break;
            case PropertyAccessor.PROPERTY_KEY_PREFIX_CHAR:
                return i;
            case PropertyAccessor.NESTED_PROPERTY_SEPARATOR_CHAR:
                if (!inKey) {
                    return i;
                }
                break;
            }
        }
        return -1;
    }

    public MutablePropertyAccessStrategy getPropertyAccessStrategyForPath(String propertyPath) throws BeansException {
        return new DynaBeanPropertyAccessStrategy(this, getFullPropertyPath(propertyPath));
    }

    public MutablePropertyAccessStrategy newPropertyAccessStrategy(ValueModel domainObjectHolder) {
        return new DynaBeanPropertyAccessStrategy(domainObjectHolder);
    }

    public Object getDomainObject() {
        return domainObjectHolder.getValue();
    }

    public PropertyMetadataAccessStrategy getMetadataAccessStrategy() {
        return metaAspectAccessor;
    }

    public Object getPropertyValue(String propertyPath) throws BeansException {
        return getPropertyValueModel(propertyPath).getValue();
    }

    /**
     * A cache of value models generated for specific property paths. 
     */
    private class ValueModelCache extends CachingMapDecorator {

        protected Object create(Object propertyPath) {
            String fullPropertyPath = getFullPropertyPath((String)propertyPath);
            String parentPropertyPath = getParentPropertyPath(fullPropertyPath);
            ValueModel parentValueModel = parentPropertyPath == "" ? domainObjectHolder
                    : (ValueModel)valueModelCache.get(parentPropertyPath);
            return new BeanPropertyValueModel(parentValueModel, fullPropertyPath);
        }
    }

    /**
     * A value model that wraps a single JavaBean property. Delegates to the beanWrapperr for getting and 
     * setting the value. If the wrapped JavaBean supports publishing property change events this class will
     * also register a property change listener so that changes to the property made outside of this
     * value model may also be detected and notified to any value change listeners registered with 
     * this class.
     */
    private class BeanPropertyValueModel extends AbstractValueModel {

        private final ValueModel parentValueModel;

        private final String propertyPath;

        private final String propertyName;

        private PropertyChangeListener beanPropertyChangeListener;

        private Object savedParentObject;

        private Object savedPropertyValue;

        private boolean settingBeanProperty;

        public BeanPropertyValueModel(ValueModel parentValueModel, String propertyPath) {
            this.parentValueModel = parentValueModel;
            this.parentValueModel.addValueChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    parentValueChanged();
                }
            });            
            this.propertyPath = propertyPath;
            this.propertyName = getPropertyName(propertyPath);
            DynaBean domainObject = (DynaBean) getDomainObject();
            this.savedPropertyValue = domainObject.get(propertyPath);
            updateBeanPropertyChangeListener();
        }

        public Object getValue() {
            DynaBean domainObject = (DynaBean) getDomainObject();
            this.savedPropertyValue = domainObject.get(propertyPath);
            return savedPropertyValue;
        }

        public void setValue(Object value) {
            // TODO: make this thread safe
            try {
                settingBeanProperty = true;
                DynaBean domainObject = (DynaBean) getDomainObject();
                domainObject.set(propertyPath, value);
            } finally {
                settingBeanProperty = false;
            }
            fireValueChange(savedPropertyValue, value);
        }

        /**
         * Called when the parent JavaBean changes.
         */
        private void parentValueChanged() {
            updateBeanPropertyChangeListener();
            if (savedParentObject == null) {
                String parentProperyPath = getParentPropertyPath(propertyPath);
                throw new NullValueInNestedPathException(
                        getMetadataAccessStrategy().getPropertyType(parentProperyPath), parentProperyPath,
                        "Parent object has changed to null. The property this value model encapsulates no longer exists!");
            }
            fireValueChange(savedPropertyValue, getValue());
        }

        /**
         * Called by the parent JavaBean if it supports PropertyChangeEvent 
         * notifications and the property wrapped by this value model
         * has changed.
         */
        private void propertyValueChanged() {
            if (!settingBeanProperty) {
                fireValueChange(savedPropertyValue, getValue());
            }
        }

        /**
         * If the parent JavaBean supports property change notification register this class 
         * as a property change listener.
         */
        private synchronized void updateBeanPropertyChangeListener() {
            final Object currentParentObject = parentValueModel.getValue();
            if (currentParentObject != savedParentObject) {
                // remove PropertyChangeListener from old parent 
                if (beanPropertyChangeListener != null) {
                    PropertyChangeSupportUtils.removePropertyChangeListener(savedParentObject, propertyName, beanPropertyChangeListener);
                    beanPropertyChangeListener = null;
                }
                // install PropertyChangeListener on new parent
                if (currentParentObject != null && PropertyChangeSupportUtils.supportsBoundProperties(currentParentObject.getClass())) {
                    beanPropertyChangeListener = new PropertyChangeListener() {
                        public void propertyChange(PropertyChangeEvent evt) {
                            propertyValueChanged();
                        }
                    };
                    PropertyChangeSupportUtils.addPropertyChangeListener(currentParentObject, propertyName,
                            beanPropertyChangeListener);
                }
                savedParentObject = currentParentObject;
            }
        }
    }

    /**
     * Implementation of PropertyMetadataAccessStrategy that 
     * simply delegates to the beanWrapper.
     */
    private class BeanPropertyMetaAspectAccessor 
        implements PropertyMetadataAccessStrategy {

        public Class getPropertyType(String propertyPath) {
            DynaProperty dynaProperty = getDynaProperty(propertyPath);
            if (dynaProperty == null) {
                CoreNotificationHelper.notifyMisconfiguration(
                    "Property on path '" + propertyPath + "' does not exists.");
            }
            return dynaProperty.getType();
        }

        public boolean isReadable(String propertyPath) {
            return getDynaProperty(propertyPath) == null ? false : true;
        }

        public boolean isWriteable(String propertyPath) {
            return getDynaProperty(propertyPath) == null ? false : true;
        }

        /**
         * @param propertyPath Is the path where the property can be found. 
         * @return Returns the dyna property for the given protperty or 
         *         <code>null</code> if it does not exists.
         */
        protected DynaProperty getDynaProperty(String propertyPath) {
            DynaProperty dynaProperty = null;
            DynaBean domainObject = (DynaBean) getDomainObject();
            DynaClass dynaClass = domainObject.getDynaClass();
            try {
                dynaProperty = dynaClass.getDynaProperty(
                    getFullPropertyPath(propertyPath));
            } catch (IllegalArgumentException e) {
                dynaProperty = null;
            }
            return dynaProperty;
        }
    }

}