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

import com.silvermindsoftware.hitch.handlers.ComponentHandlerFactory;
import com.silvermindsoftware.hitch.handlers.TypeHandlerFactory;
import com.silvermindsoftware.hitch.handlers.component.ComponentHandler;
import com.silvermindsoftware.hitch.handlers.type.TypeHandler;
import com.silvermindsoftware.hitch.handlers.type.UnknownTypeHandler;
import com.silvermindsoftware.hitch.meta.ComponentMeta;
import com.silvermindsoftware.hitch.meta.FormMeta;
import com.silvermindsoftware.hitch.meta.ModelMeta;
import com.silvermindsoftware.hitch.reflect.ClassInfo;
import com.silvermindsoftware.hitch.reflect.ClassManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * This class contains all of the code used to updateModel/populateForm between
 * Swing components and model objects (Object reflection/Type Handlers).
 */
public class BinderImpl extends AbstractBinder implements Binder {

    private static final Log log = LogFactory.getLog(BinderImpl.class);

    private static final ComponentHandlerFactory COMPONENT_HANDLER_FACTORY = new ComponentHandlerFactory();
    private static final TypeHandlerFactory TYPE_HANDLER_FACTORY = new TypeHandlerFactory();
    private static final TypeHandler unknownTypeHandler = new UnknownTypeHandler();


    public void updateModel(Container container, String... modelId) {

        FormMeta formMeta =
                getFormMeta(container.getClass());

        for (Iterator<ComponentMeta> it = formMeta.getComponentMetaIterator(); it.hasNext();) {
            ComponentMeta componentMeta = it.next();
            // check to see if update should occur for particular model objects
            if (modelId != null && modelId.length > 0) {

                Arrays.sort(modelId);

                if (Arrays.binarySearch(
                        modelId,
                        componentMeta.getModelId()) < 0) {
                    continue;
                }
            }

            // get container field
            Field componentField = componentMeta.getComponentField();

            // get model object
            ModelMeta modelMeta = formMeta.getModelMeta(componentMeta.getModelId());
            Field modelField = modelMeta.getModelField();
            Object modelObject = null;
            try {
                modelObject = modelField.get(container);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(
                        "IllegalAccessException: " + e.getMessage() + "occured while retrieving model field " +
                                modelField.getName() + " of type " + modelField.getType(), e);
            }

            // get form container
            JComponent formComponent = null;
            try {
                formComponent = (JComponent) componentField.get(container);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(
                        "IllegalAccessException: " + e.getMessage() + "occured while retrieving component field " +
                                componentField.getName() + " of type " + componentField.getType(), e);
            }

            ComponentHandler componentHandler =
                    getComponentHandler(componentMeta, container, formComponent);

            boolean readOnly = false;
            if (componentMeta.getReadOnly() == ReadOnly.DEFAULT) {
                readOnly = componentHandler.getDefaultReadOnly();
            } else if (componentMeta.getReadOnly() == ReadOnly.TRUE) {
                readOnly = true;
            }

            if (!readOnly) {

                if (componentHandler.isUpdateHandleable(formComponent)) {
                    Object formComponentValue = null;

                    Method getter = componentHandler.getComponentGetter(formComponent);

                    try {
                        // convert value and get
                        formComponentValue = getter.invoke(formComponent);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(
                                "IllegalAccessException: " + e.getMessage() + "occured while retrieving form component value from " +
                                        componentField.getName() + " of type " + componentField.getType() + " using " + getter.getName(), e);
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException(
                                "InvocationTargetException: " + e.getMessage() + "occured while retrieving form component value from " +
                                        componentField.getName() + " of type " + componentField.getType() + " using " + getter.getName(), e);
                    }

                    //retrieve type handler and convert to compatible type

                    if (componentMeta.isModelSetter()) {
                        try {
                            if (Map.class.isAssignableFrom(componentMeta.getModelType())) {
                                componentMeta.getModelSetter().invoke(
                                        modelObject, componentMeta.getModelPropertyName(), convert(
                                        componentHandler.preProcessUpdate(
                                                formComponent,
                                                formComponentValue),
                                        componentMeta.getModelPropertyType()));

                            } else {
                                componentMeta.getModelSetter().invoke(
                                        modelObject, convert(
                                        componentHandler.preProcessUpdate(
                                                formComponent,
                                                formComponentValue),
                                        componentMeta.getModelPropertyType()));
                            }

                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(
                                    "IllegalAccessException: " + e.getMessage() + " occurred while setting value " +
                                            formComponentValue + " on " + modelObject + " with " +
                                            componentMeta.getModelSetter().getName(), e);
                        } catch (InvocationTargetException e) {
                            throw new RuntimeException(
                                    "RuntimeException: " + e.getMessage() + " occurred while setting value " +
                                            formComponentValue + " on " + modelObject + " with " +
                                            componentMeta.getModelSetter().getName(), e);
                        } catch (Exception e) {
                            throw new RuntimeException(
                                    "Exception: " + e.getMessage() + " occurred while setting value " +
                                            formComponentValue + " on " + modelObject + " with " +
                                            componentMeta.getModelSetter().getName(), e);
                        }
                    } else {
                        try {
                            componentMeta.getModelField().set(
                                    modelObject, convert(
                                    componentHandler.preProcessUpdate(
                                            formComponent,
                                            formComponentValue),
                                    componentMeta.getModelField().getType()));
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(
                                    "IllegalAccessException: " + e.getMessage() + " occurred while setting value " +
                                            formComponentValue + " on " + modelObject + " to field " +
                                            componentMeta.getModelField().getName(), e);
                        } catch (Exception e) {
                            throw new RuntimeException(
                                    "Exception: " + e.getMessage() + " occurred while setting value " +
                                            formComponentValue + " on " + modelObject + " to field " +
                                            componentMeta.getModelField().getName(), e);
                        }
                    }

                    componentHandler.postProcessUpdate(formComponent);
                }
            }

        }

    }

    public void populateForm(Container container, String... modelId) {

        FormMeta formMeta =
                getFormMeta(container.getClass());

        for (Iterator<ComponentMeta> it = formMeta.getComponentMetaIterator(); it.hasNext();) {
            ComponentMeta componentMeta = it.next();

            // check to see if update should occur for particular model objects
            if (modelId != null && modelId.length > 0) {

                Arrays.sort(modelId);

                if (Arrays.binarySearch(
                        modelId,
                        componentMeta.getModelId()) < 0) {
                    continue;
                }
            }

            // get container field
            Field componentField = componentMeta.getComponentField();

            // get model values
            ModelMeta modelMeta = formMeta.getModelMeta(componentMeta.getModelId());
            Field modelField = modelMeta.getModelField();
            Object modelObject = null;
            try {
                modelObject = modelField.get(container);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e.getMessage(), e);
            }

            Object modelFieldValue = null;

            if (componentMeta.isModelGetter()) {
                try {
                    if (Map.class.isAssignableFrom(componentMeta.getModelType())) {
                        ClassInfo modelClassInfo = ClassManager.getClassInfo(componentMeta.getModelType());
                        Method keySetMethod = null;
                        try {
                            keySetMethod = modelClassInfo.getMethod("keySet", new Class[]{});
                        } catch (NoSuchMethodException e) {
                            log.error(e.getMessage(), e);
                            throw new RuntimeException(e.getMessage(), e);
                        }

                        Set<String> keySet = (Set) keySetMethod.invoke(modelObject);

                        for (String key : keySet) {
                            if (componentMeta.getModelPropertyName().equalsIgnoreCase(key)) {
                                modelFieldValue = componentMeta.getModelGetter().invoke(modelObject, key);
                                break;
                            }
                        }

                    } else {
                        modelFieldValue = componentMeta.getModelGetter().invoke(modelObject);
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e.getMessage(), e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            } else {
                try {
                    modelFieldValue = componentMeta.getModelField().get(modelObject);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }

            JComponent formComponent = null;

            try {
                formComponent = (JComponent) componentField.get(container);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }

            if (formComponent == null) {
                throw new IllegalStateException("Form container named " + componentField.getName() + " was not found");
            }

            ComponentHandler componentHandler =
                    getComponentHandler(componentMeta, container, formComponent);

            if (componentHandler.isPopulateHandleable(formComponent, modelFieldValue)) {


                Method setter = componentHandler.getComponentSetter(formComponent);

                try {
                    ErrorContext.put("- Error occured when setting " + setter.getName() + " on " + componentField.getName());

                    Object convertedValue = convert(
                            componentHandler.preProcessPopulate(formComponent, modelFieldValue),
                            setter.getParameterTypes()[0]);

                    setter.invoke(formComponent, convertedValue);

                } catch (IllegalAccessException e) {
                    throw new RuntimeException("IllegalAccessException" + ErrorContext.getAsString(), e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException("InvocationTargetException" + ErrorContext.getAsString().toString(), e);
                } finally {
                    ErrorContext.clear();
                }


                componentHandler.postProcessPopulate(formComponent);
            }
        }
    }

    private ComponentHandler getComponentHandler(ComponentMeta componentMeta, Container container, JComponent formComponent) {
        ComponentHandler componentHandler;// attempt to retrieve container handler if custom is defined
        if (componentMeta.getComponentHandler() != null) {
            componentHandler = getCustomComponentHandler(container, componentMeta);

        } else {

            componentHandler =
                    COMPONENT_HANDLER_FACTORY.getHandler(
                            formComponent.getClass());
        }
        return componentHandler;
    }

    private ComponentHandler getCustomComponentHandler(Container container, ComponentMeta componentMeta) {
        ComponentHandler componentHandler;
        String key = container.getClass().getName() + componentMeta.getComponentField().getName();
        //if not create and popoulate properties and add to container handler
        componentHandler =
                COMPONENT_HANDLER_FACTORY.getHandler(key);


        if (componentHandler == null) {
            try {
                if (componentMeta.getComponentHandler() == void.class) {
                    Class componentFieldClass = componentMeta.getComponentField().getType();
                    componentHandler = (ComponentHandler) COMPONENT_HANDLER_FACTORY.getHandlerType(componentFieldClass).newInstance();
                } else {
                    componentHandler = (ComponentHandler) componentMeta.getComponentHandler().newInstance();
                }
            } catch (InstantiationException e) {
                throw new IllegalStateException(e.getMessage(), e);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }

            if (componentMeta.getHandlerValues() != null) {
                for (String propertyValue : componentMeta.getHandlerValues()) {
                    if (propertyValue.length() > 0) {
                        String[] split = propertyValue.split("=");
                        String name = split[0];
                        String value = split[1];
                        Class componentHandlerClass = componentHandler.getClass();
                        try {

                            Method method =
                                    ClassManager.getClassInfo(componentHandlerClass).getSetterMethod(
                                            "set" + name.substring(0, 1).toUpperCase() + name.substring(1),
                                            new Class[]{String.class});

                            method.invoke(componentHandler, value);

                        } catch (NoSuchMethodException e) {
                            throw new IllegalStateException(e.getMessage(), e);
                        } catch (IllegalAccessException e) {
                            throw new IllegalStateException(e.getMessage(), e);
                        } catch (InvocationTargetException e) {
                            throw new IllegalStateException(e.getMessage(), e);
                        }
                    }
                }
            }

            COMPONENT_HANDLER_FACTORY.register(key, componentHandler);
        }
        return componentHandler;
    }

    public void addComponentHandler(Class componentClass, String fieldName, Class componentHandlerClass, String... properties) {

        String componentHandlerKey = componentClass.getName() + fieldName;

        ComponentHandler componentHandler;
        try {
            componentHandler = (ComponentHandler) componentHandlerClass.newInstance();
        } catch (InstantiationException e) {
            throw new IllegalStateException("Failed to instantiate " + componentHandlerClass.getName(), e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Could not access default constructor on " + componentHandlerClass.getName(), e);
        }

        COMPONENT_HANDLER_FACTORY.register(componentHandlerKey, componentHandler);
    }


    public Object convert(Object value, Class toType) {
        Object retVal;
        TypeHandler typeHandler = TYPE_HANDLER_FACTORY.getHandler(toType);
        if (typeHandler == null)
            retVal = unknownTypeHandler.convert(value);
        else
            retVal = typeHandler.convert(value);
        return retVal;
    }
}
