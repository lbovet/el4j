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

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ComponentMeta {

    private Class modelPropertyType;
    private Class modelType;
    private String modelPropertyName;
    private String modelId;
    private Field componentField;
    private Field modelField;
    private Method modelGetter;
    private Method modelSetter;

    private Class componentHandler;
    private String[] handlerValues;

    private boolean autoBound;
    private ReadOnly readOnly;

    public ComponentMeta(String propertyName, String modelId) {
        this.modelPropertyName = propertyName;
        this.modelId = modelId;
    }

    public ComponentMeta(
            Class modelType, String modelPropertyName, String modelId,
            Field componentField, Field modelField,
            Method modelGetter, Method modelSetter,
            Class componentHandler, String[] handlerValues,
            boolean autoBound, ReadOnly readOnly, Class modelPropertyType) {

        this.modelType = modelType;
        this.modelPropertyName = modelPropertyName;
        this.modelId = modelId;
        this.componentField = componentField;
        this.componentField.setAccessible(true);
        this.modelField = modelField;
        if(modelField != null)
            this.modelField.setAccessible(true);
        this.modelGetter = modelGetter;
        this.modelSetter = modelSetter;
        this.componentHandler = componentHandler;
        this.handlerValues = handlerValues;
        this.autoBound = autoBound;
        this.readOnly = readOnly;
        this.modelPropertyType = modelPropertyType;

    }

    public Class getModelType() {
        return modelType;
    }

    public String getModelPropertyName() {
        return modelPropertyName;
    }

    public String getModelId() {
        return modelId;
    }

    public Field getComponentField() {
        return componentField;
    }

    public Field getModelField() {
        return modelField;
    }

    public boolean isModelGetter() {
        return modelGetter != null;
    }

    public Method getModelGetter() {
        return modelGetter;
    }

    public boolean isModelSetter() {
        return modelSetter != null;
    }

    public Method getModelSetter() {
        return modelSetter;
    }

    public Class getComponentHandler() {
        return componentHandler;
    }

    public String[] getHandlerValues() {
        return handlerValues;
    }

    public boolean isAutoBound() {
        return autoBound;
    }

    public ReadOnly getReadOnly() {
        return readOnly;
    }

    public Class getModelPropertyType() {
        return modelPropertyType;
    }

    public String toString() {
        return modelPropertyName + modelId;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ComponentMeta that = (ComponentMeta) o;

        if (modelId != null ? !modelId.equals(that.modelId) : that.modelId != null) return false;
        if (modelPropertyName != null ? !modelPropertyName.equals(that.modelPropertyName) : that.modelPropertyName != null)
            return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (modelPropertyName != null ? modelPropertyName.hashCode() : 0);
        result = 31 * result + (modelId != null ? modelId.hashCode() : 0);
        return result;
    }
}
