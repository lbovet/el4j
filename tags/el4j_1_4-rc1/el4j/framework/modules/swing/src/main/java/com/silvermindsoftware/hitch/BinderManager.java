/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package com.silvermindsoftware.hitch;

import java.awt.Component;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.silvermindsoftware.hitch.annotations.BoundComponent;
import com.silvermindsoftware.hitch.annotations.Form;
import com.silvermindsoftware.hitch.annotations.ModelObject;
import com.silvermindsoftware.hitch.meta.FormMeta;
import com.silvermindsoftware.hitch.reflect.ClassManager;

/**
 * This class manages binder instances.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Stefan Wismer (SWI), based on Hitch by Brandon Goodin
 */
public class BinderManager {
    public static final BinderManager s_manager = new BinderManager();

    protected Map<Class<?>, FormMeta> m_formMap
        = new HashMap<Class<?>, FormMeta>();
    
    private BinderManager() { }
    
    /**
     * Get a binder for a specific window component.
     * 
     * @param form    the form containing the components
     * @return        the binder
     */
    public static Binder getBinder(Component form) {
        s_manager.collectAnnotationMeta(form.getClass());
        return new BinderImpl();
    }
    
    public static FormMeta getFormMetaData(Class<?> formClass) {
        return s_manager.getFormMeta(formClass);
    }
    


    @SuppressWarnings("unchecked")
    private void collectAnnotationMeta(Class formClass) {
        ErrorContext.put("- Collecting AnnotationMeta on " + formClass.getName());
        // ComponentField Meta for component class
        FormMeta formMeta;

        if (m_formMap.containsKey(formClass)) {
            formMeta = m_formMap.get(formClass);
        } else {
            formMeta = new FormMeta(formClass);
            m_formMap.put(formClass, formMeta);
        }

        if (!formMeta.isAnnotationMetaCollected()) {

            boolean autoBind = false;

            if (formClass.isAnnotationPresent(Form.class)) {
                Form form = (Form) formClass.getAnnotation(Form.class);
                autoBind = form.autoBind();
            }

            // temporary list to hold fields that have BoundComponent annotations
            List<Field> boundComponentFields = new ArrayList<Field>();

            // retrieve public component's fields to find annotated fields
            Field[] fields = ClassManager.getClassInfo(formClass).getFields();

            for (Field field : fields) {
                if (field.isAnnotationPresent(BoundComponent.class)) {
                    boundComponentFields.add(field);
                } else if (field.isAnnotationPresent(ModelObject.class)) {
                    ModelObject modelObject = field.getAnnotation(ModelObject.class);

                    ErrorContext.put("- Processing Annotation for model object field " +
                            field.getName() + " of type " + modelObject.getClass().getName());

                    if (modelObject.isDefault()) {
                        ErrorContext.put("- Model Object is default");
                        formMeta.putModelMeta("[default]", field);
                    } else {
                        ErrorContext.put("- Model Object is NOT default");
                        formMeta.putModelMeta(field.getName(), field);
                    }

                    Class modelObjectType = field.getType();

                    // autoBind if autoBind enabled. But, do not if ModelObject is a Map because fields do not yet exist
                    if (autoBind && modelObject.autoBind() && !Map.class.isAssignableFrom(modelObjectType)) {

                        // iterate modelObject fields

                        ErrorContext.put("- AutoBind Model Object" + modelObjectType.getName());


                        for (Method moMethod : ClassManager.getClassInfo(modelObjectType).getSetters()) {

                            String componentFieldName =
                                    moMethod.getName().substring(3, 4).toLowerCase() +
                                            moMethod.getName().substring(4);

                            Field formClassField = null;
                            
                            // SWI {
                            // determine if corresponding field starts with m_...
                            ErrorContext.put("- Attempting to lookup field " + componentFieldName);
                            try {
                                formClassField = ClassManager.getClassInfo(formClass).getField(componentFieldName);
                            } catch (NoSuchFieldException e) {
                                // ignored
                            }
                            try {
                                formClassField = ClassManager.getClassInfo(formClass).getField("m_" + componentFieldName);
                            } catch (NoSuchFieldException e) {
                                // ignored
                            }
                            // } SWI

                            if (formClassField != null) {
                                if (!formClassField.isAnnotationPresent(BoundComponent.class)) {
                                    ErrorContext.put("- Adding ComponentMeta for " + formClassField.getName() +
                                            " of type " + formClassField.getType().getName());

                                    formMeta.addComponentMeta(
                                            modelObject.isDefault() ? "[default]" : field.getName(),
                                            componentFieldName, formClassField, void.class,
                                            new String[]{}, true, ReadOnly.DEFAULT,
                                            moMethod.getParameterTypes()[0]);

                                    ErrorContext.removeLast();

                                }
                            } else {
                                ErrorContext.removeLast();
                            }
                        }


                        ErrorContext.removeLast();

                    }

                    ErrorContext.removeLast();

                }
            }

            for (Field field : boundComponentFields) {

                ErrorContext.put("processing BoundComponent field " + field.getName() + " of type " + field.getType().getName());

                BoundComponent boundComponent = field.getAnnotation(BoundComponent.class);

                formMeta.addComponentMeta(
                        boundComponent.modelId(),
                        boundComponent.property().equals("[default]") ?
                                field.getName() : boundComponent.property(),
                        field, boundComponent.handler(),
                        boundComponent.handlerValues(), false,
                        boundComponent.readOnly(), boundComponent.type());

                ErrorContext.removeLast();

            }

            formMeta.setAnnotationMetaCollected(true);
        }

        ErrorContext.removeLast();
    }

    public FormMeta getFormMeta(Class formClass) {
        FormMeta formMeta = null;

        if (m_formMap.containsKey(formClass)) {

            // if key exists retrieve meta for mapping
            formMeta = m_formMap.get(formClass);

            ErrorContext.put("- FormMeta retrieved for " + formClass.getName());

            // if annotaion metadata has not been collected
            if (!formMeta.isAnnotationMetaCollected())
                collectAnnotationMeta(formClass);

        } else {

            ErrorContext.put("- New FormMeta created for " + formClass.getName());

            collectAnnotationMeta(formClass);
            formMeta = m_formMap.get(formClass);

        }

        return formMeta;
    }
}
