/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU Lesser General Public License (LGPL)
 * Version 2.1. See http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.services.gui.richclient.forms.binding;

import java.lang.reflect.Constructor;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListSelectionModel;

import org.springframework.binding.form.FormModel;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.form.binding.Binding;
import org.springframework.richclient.form.binding.support.DecoratedControlBinding;
import org.springframework.richclient.form.binding.swing.ListBinder;
import org.springframework.richclient.form.binding.swing.ScrollPaneDecoratedBinding;

import ch.elca.el4j.services.gui.richclient.forms.binding.swing.AbstractDynamicListBinding;
import ch.elca.el4j.services.gui.richclient.utils.ApplicationListenerUtils;
import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * Binder to create dynamic list bindings. The given class must have a
 * constructor in form <code>JList.class, FormModel.class, String.class</code>.
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
public class DynamicListBinder extends ListBinder {
    /**
     * Is the class of the binding to use.
     */
    private Class m_bindingClass;
    
    /**
     * Is the binding class used as decorator for the "real" binding.
     */
    private Class m_bindingDecoratorClass = ScrollPaneDecoratedBinding.class;
    
    /**
     * {@inheritDoc}
     */
    protected JComponent createControl(Map context) {
        JList list = new JList();
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return list;
    }
    
    /**
     * {@inheritDoc}
     */
    protected Binding doBind(JComponent control, FormModel formModel, 
        String formPropertyPath, Map context) {
        Reject.ifFalse(control instanceof JList);
        JList listControl = (JList) control;
        AbstractDynamicListBinding binding = instantiateBinding(
            listControl, formModel, formPropertyPath, context);
        registerApplicationListener(binding);
        return decorateBinding(binding);
    }

    /**
     * Registers the application listener on application event multicaster.
     * 
     * @param l Is the application listener to register.
     */
    protected void registerApplicationListener(ApplicationListener l) {
        ApplicationListenerUtils.registerApplicationListener(l);
        // TODO Unregister somewhere! Use weak references in application event 
        // multicaster?
    }

    /**
     * Decorates the given binding if a binding decorator is set.
     * 
     * @param binding Is the binding to decorate.
     * @return Returns the decorated binding.
     */
    protected Binding decorateBinding(Binding binding) {
        Binding decoratedBinding = binding;
        Class decoraterClass = getBindingDecoratorClass();
        if (decoraterClass != null) {
            if (!DecoratedControlBinding.class.isAssignableFrom(
                decoraterClass)) {
                CoreNotificationHelper.notifyMisconfiguration(
                    "Given binding decorator class " + decoraterClass.getName() 
                    + " is not of type "
                    + DecoratedControlBinding.class.getName());
            }
            try {
                Constructor constructor = decoraterClass.getConstructor(
                    new Class[] {Binding.class}
                );
                decoratedBinding 
                    = (DecoratedControlBinding) constructor.newInstance(
                        new Object[] {binding});
            } catch (Exception e) {
                CoreNotificationHelper.notifyMisconfiguration(
                    "There was a problem while instantiating class "
                    + decoraterClass.getName() + " using necessary constructor "
                    + "with argument of types " + Binding.class.getName(), e);
            }
        }
        
        // Set the preferred size of wrapper component with preferred size of
        // wrapped component.
        JComponent sourceComponent = binding.getControl();
        JComponent wrapperComponent = decoratedBinding.getControl();
        wrapperComponent.setPreferredSize(sourceComponent.getPreferredSize());
        
        return decoratedBinding;
    }

    /**
     * Instantiates the given binding class.
     * 
     * @param listControl
     *            Is the <code>JList</code> to bind with the java list.
     * @param formModel
     *            Is the model of the form to binding.
     * @param formPropertyPath
     *            Is the path of the form property.
     * @param context
     *            Are the context variables that can be used for instantiation.
     * @return Returns the instantiated binding.
     */
    protected AbstractDynamicListBinding instantiateBinding(JList listControl,
        FormModel formModel, String formPropertyPath, Map context) {
        AbstractDynamicListBinding binding = null;
        Class bindingClass = getBindingClass();
        if (!AbstractDynamicListBinding.class.isAssignableFrom(bindingClass)) {
            CoreNotificationHelper.notifyMisconfiguration(
                "Given binding class " + bindingClass.getName() 
                + " is not of type "
                + AbstractDynamicListBinding.class.getName());
        }
        try {
            Constructor constructor = bindingClass.getConstructor(
                new Class[] {JList.class, FormModel.class, String.class}
            );
            binding = (AbstractDynamicListBinding) constructor.newInstance(
                new Object[] {listControl, formModel, formPropertyPath});
        } catch (Exception e) {
            CoreNotificationHelper.notifyMisconfiguration(
                "There was a problem while instantiating class "
                + bindingClass.getName() + " using necessary constructor with "
                + "following argument types: "
                + JList.class.getName() + ", " + FormModel.class.getName() 
                + ", " + String.class.getName(), e);
        }
        
        return binding;
    }
    
    /**
     * @return the bindingClass
     */
    public final Class getBindingClass() {
        return m_bindingClass;
    }

    /**
     * @param bindingClass the bindingClass to set
     */
    public final void setBindingClass(Class bindingClass) {
        m_bindingClass = bindingClass;
    }

    /**
     * @return the bindingDecoratorClass
     */
    public final Class getBindingDecoratorClass() {
        return m_bindingDecoratorClass;
    }

    /**
     * @param bindingDecoratorClass the bindingDecoratorClass to set
     */
    public final void setBindingDecoratorClass(Class bindingDecoratorClass) {
        m_bindingDecoratorClass = bindingDecoratorClass;
    }
}
