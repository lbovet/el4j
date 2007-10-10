package com.silvermindsoftware.hitch;

/**
 * Copyright 2007 Brandon Goodin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.silvermindsoftware.hitch.annotations.BoundComponent;
import com.silvermindsoftware.hitch.annotations.Form;
import com.silvermindsoftware.hitch.annotations.ModelObject;
import com.silvermindsoftware.hitch.meta.FormMeta;
import com.silvermindsoftware.hitch.reflect.ClassManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class contains all the base metadata creation code
 */
public abstract class AbstractBinder implements Binder {

    private static final Log log = LogFactory.getLog(AbstractBinder.class);

    protected Map<Class, FormMeta> formMap = new HashMap<Class, FormMeta>();

    abstract public void updateModel(Container component, String... modelId);

    abstract public void populateForm(Container component, String... modelId);

    @SuppressWarnings("unchecked")
    public void collectAnnotationMeta(Class formClass) {
        ErrorContext.put("- Collecting AnnotationMeta on " + formClass.getName());
        // ComponentField Meta for component class
        FormMeta formMeta;

        if (formMap.containsKey(formClass)) {
            formMeta = formMap.get(formClass);
        } else {
            formMeta = new FormMeta(formClass);
            formMap.put(formClass, formMeta);
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

                            try {
                                ErrorContext.put("- Attempting to lookup field " + componentFieldName);
                                formClassField = ClassManager.getClassInfo(formClass).getField(componentFieldName);

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
                            } catch (NoSuchFieldException e) {
                                // ignored
                            } finally {
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

        if (formMap.containsKey(formClass)) {

            // if key exists retrieve meta for mapping
            formMeta = formMap.get(formClass);

            ErrorContext.put("- FormMeta retrieved for " + formClass.getName());

            // if annotaion metadata has not been collected
            if (!formMeta.isAnnotationMetaCollected())
                collectAnnotationMeta(formClass);

        } else {

            ErrorContext.put("- New FormMeta created for " + formClass.getName());

            collectAnnotationMeta(formClass);
            formMeta = formMap.get(formClass);

        }

        return formMeta;
    }

}
