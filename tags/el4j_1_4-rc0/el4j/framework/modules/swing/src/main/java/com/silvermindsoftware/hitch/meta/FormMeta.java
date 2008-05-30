package com.silvermindsoftware.hitch.meta;

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

import com.silvermindsoftware.hitch.ReadOnly;
import com.silvermindsoftware.hitch.config.BoundComponentConfig;
import com.silvermindsoftware.hitch.reflect.ClassManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * This class holds all of the meta data gathered from the different
 * configuration mediums (annotations, BinderConfig).
 */
public class FormMeta {

    private static final Log log = LogFactory.getLog(FormMeta.class);

    private boolean annotationMetaCollected;

    private Class componentClass;
    private Map<String, ModelMeta> modelMap;
    private List<ComponentMeta> componentMetaLists;

    public FormMeta(Class componentClass) {
        this(componentClass, new HashMap<String, ModelMeta>(), new ArrayList<ComponentMeta>());
    }

    public FormMeta(Class componentClass, Map modelMap, List componentMetaLists) {

        this.annotationMetaCollected = false;
        this.componentClass = componentClass;
        this.modelMap = modelMap;
        this.componentMetaLists = componentMetaLists;

    }

    public void putModelMeta(String id, String modelField) {

        try {
            putModelMeta(id, ClassManager.getClassInfo(componentClass).getField(modelField));
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException(
                    "Field with name " + modelField +
                            " was not found in class" + componentClass.getName());
        }

    }

    public void putModelMeta(String id, Field modelField) {
        if (modelMap.containsKey(id)) {
            log.info(
                    "Model of type "
                            + (modelMap.get(id)).getModelField().getType().getName()
                            + " with id of '" + id + "' already exists for class "
                            + componentClass.getName());
        } else {
            modelMap.put(id, new ModelMeta(modelField));
        }

    }

    public ModelMeta getModelMeta(String modelId) {

        if (!modelMap.containsKey(modelId))
            throw new IllegalStateException(
                    "Model identified by " + modelId
                            + " does not exist for class " + componentClass.getName());

        return modelMap.get(modelId);
    }

    public void addComponentMeta(String modelId, BoundComponentConfig boundComponentConfig, boolean autoBound) {

        Field field;

        try {
            field = ClassManager.getClassInfo(componentClass).getField(boundComponentConfig.getComponentFieldName());
            field.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException(
                    boundComponentConfig.getComponentFieldName() + " field was not found on class " + componentClass.getName());
        }

        addComponentMeta(
                modelId, boundComponentConfig.getModelPropertyName(),
                field, boundComponentConfig.getHandlerClass(),
                boundComponentConfig.getHandlerParameters(), autoBound,
                boundComponentConfig.getReadOnly(), boundComponentConfig.getType());
    }

    public void addComponentMeta(
            String modelId, String modelProperty,
            Field componentField, Class componentHandler,
            String[] handlerParameters, boolean autoBound,
            ReadOnly readOnly, Class type) {

        if (!modelMap.containsKey(modelId))
            throw new IllegalArgumentException("No model object found for id " + modelId);

        ModelMeta modelMeta =
                modelMap.get(modelId);

        Class modelType = modelMeta.getModelField().getType();

        ComponentMeta tempComponentMeta = new ComponentMeta(modelProperty, modelId);

        if (componentMetaLists.contains(tempComponentMeta)) {

            ComponentMeta componentMeta =
                    componentMetaLists.get(
                            componentMetaLists.indexOf(
                                    tempComponentMeta));

            if (componentMeta.isAutoBound() && autoBound == false) {
                componentMetaLists.add(
                        componentMetaLists.indexOf(tempComponentMeta),
                        createComponentMeta(
                                modelType, modelProperty, modelId, componentField,
                                componentHandler, handlerParameters, autoBound, readOnly, type));
            } else {
                log.info(
                        "ComponentMeta already exists for property "
                                + modelProperty + " of model id" + modelId
                                + " for class " + modelType.getName());
            }

        } else {
            componentMetaLists.add(
                    createComponentMeta(
                            modelType, modelProperty, modelId, componentField,
                            componentHandler, handlerParameters, autoBound, readOnly, type));
        }
    }

    private ComponentMeta createComponentMeta(Class modelType, String modelProperty, String modelId, Field componentField, Class componentHandler, String[] handlerParameters, boolean autoBound, ReadOnly readOnly, Class type) {
        // attain getter/setter/field for model property
        Class propertyType = null;

        Method modelPropertyGetterMethod = null;
        boolean modelPropertyGetterMethodFound = false;

        Method modelPropertySetterMethod = null;

        Field modelPropertyField = null;

        // getter
        try {
            if (Map.class.isAssignableFrom(modelType)) {
                modelPropertyGetterMethod =
                        ClassManager.getClassInfo(modelType).getMethod(
                                "get", new Class[]{Object.class});

                if (modelPropertyGetterMethod != null) {
                    propertyType = modelPropertyGetterMethod.getReturnType();
                    modelPropertyGetterMethodFound = true;
                }
            } else {
                modelPropertyGetterMethod =
                        ClassManager.getClassInfo(modelType).getGetterMethod(
                                composeGetterName(modelProperty));
                modelPropertyGetterMethod.setAccessible(true);
                if ( modelPropertyGetterMethod != null ) {
                    propertyType = modelPropertyGetterMethod.getReturnType();
                    modelPropertyGetterMethod.setAccessible(true);
                    modelPropertyGetterMethodFound = true;
                }
            }

        } catch (NoSuchMethodException e) {
            log.info(e.getMessage());
        }

        // field
        try {
            modelPropertyField = ClassManager.getClassInfo(modelType).getField(modelProperty);
            modelPropertyField.setAccessible(true);
            if (propertyType == null) propertyType = modelPropertyField.getType();
        } catch (NoSuchFieldException e) {
            if (!modelPropertyGetterMethodFound)
                throw new IllegalStateException(
                        "A getter method or a field was not found for property with name " + modelProperty);
        }

        // setter
        try {
            if (Map.class.isAssignableFrom(modelType)) {
                modelPropertySetterMethod = ClassManager.getClassInfo(modelType).getSetterMethod("put", new Class[]{Object.class, Object.class});
            } else {
                modelPropertySetterMethod = ClassManager.getClassInfo(modelType).getSetterMethod(composeSetterName(modelProperty), new Class[]{propertyType});
                modelPropertySetterMethod.setAccessible(true);
            }
        } catch (NoSuchMethodException e) {
            log.info(e.getMessage());
        }

        ComponentMeta componentMeta =
                new ComponentMeta(
                        modelType, modelProperty, modelId,
                        componentField, modelPropertyField,
                        modelPropertyGetterMethod, modelPropertySetterMethod,
                        componentHandler, handlerParameters,
                        autoBound, readOnly,
                        type == void.class ? propertyType : type
                );
        return componentMeta;
    }

    public Iterator<ComponentMeta> getComponentMetaIterator() {
        return componentMetaLists.iterator();
    }

    public void setAnnotationMetaCollected(boolean annotationMetaCollected) {
        this.annotationMetaCollected = annotationMetaCollected;
    }

    public boolean isAnnotationMetaCollected() {
        return annotationMetaCollected;
    }

    public Class getComponentClass() {
        return componentClass;
    }

    protected String composeSetterName(String propertyName) {
        StringBuilder sb = new StringBuilder("set");
        sb.append(propertyName.substring(0, 1).toUpperCase());
        sb.append(propertyName.substring(1));
        return sb.toString();
    }

    protected String composeGetterName(String propertyName) {
        StringBuilder sb = new StringBuilder("get");
        sb.append(propertyName.substring(0, 1).toUpperCase());
        sb.append(propertyName.substring(1));
        return sb.toString();
    }


}
