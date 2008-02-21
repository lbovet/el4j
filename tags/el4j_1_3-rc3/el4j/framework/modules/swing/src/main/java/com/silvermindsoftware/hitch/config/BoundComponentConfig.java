package com.silvermindsoftware.hitch.config;

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

public class BoundComponentConfig {

    private Class type = void.class;
    private String componentFieldName;
    private String modelPropertyName;
    private Class handlerClass = void.class;
    private String[] handlerParameters = new String[]{};
    private ReadOnly readOnly = ReadOnly.DEFAULT;

    public BoundComponentConfig(String sharedPropertyName) {

        this.componentFieldName = sharedPropertyName;
        this.modelPropertyName = sharedPropertyName;

    }

    public BoundComponentConfig(String sharedPropertyName, Class handlerClass) {

        this.componentFieldName = sharedPropertyName;
        this.modelPropertyName = sharedPropertyName;
        this.handlerClass = handlerClass;

    }

    public BoundComponentConfig(String sharedPropertyName, String[] handlerParameters) {

        this.componentFieldName = sharedPropertyName;
        this.modelPropertyName = sharedPropertyName;
        this.handlerParameters = handlerParameters;

    }

    public BoundComponentConfig(String sharedPropertyName, Class handlerClass, String[] handlerParameters) {

        this.componentFieldName = sharedPropertyName;
        this.modelPropertyName = sharedPropertyName;
        this.handlerClass = handlerClass;
        this.handlerParameters = handlerParameters;

    }

    public BoundComponentConfig(String componentFieldName, String modelPropertyName) {

        this.componentFieldName = componentFieldName;
        this.modelPropertyName = modelPropertyName;

    }

    public BoundComponentConfig(String componentFieldName, String modelPropertyName, Class handlerClass) {

        this.componentFieldName = componentFieldName;
        this.modelPropertyName = modelPropertyName;
        this.handlerClass = handlerClass;

    }

    public BoundComponentConfig(String componentFieldName, String modelPropertyName, String[] handlerParameters) {

        this.componentFieldName = componentFieldName;
        this.modelPropertyName = modelPropertyName;
        this.handlerParameters = handlerParameters;

    }

    public BoundComponentConfig(String componentFieldName, String modelPropertyName, Class handlerClass, String[] handlerParameters) {

        this.componentFieldName = componentFieldName;
        this.modelPropertyName = modelPropertyName;
        this.handlerClass = handlerClass;
        this.handlerParameters = handlerParameters;

    }

    public BoundComponentConfig(String componentFieldName, String modelPropertyName, Class handlerClass, String[] handlerParameters, ReadOnly readOnly, Class type) {

        this.componentFieldName = componentFieldName;
        this.modelPropertyName = modelPropertyName;
        this.handlerClass = handlerClass;
        this.handlerParameters = handlerParameters;
        this.readOnly = readOnly;
        this.type = type;

    }

    public String getComponentFieldName() {
        return componentFieldName;
    }

    public String getModelPropertyName() {
        return modelPropertyName;
    }

    public Class getHandlerClass() {
        return handlerClass;
    }

    public String[] getHandlerParameters() {
        return handlerParameters;
    }

    public ReadOnly getReadOnly() {
        return readOnly;
    }

    public Class getType() {
        return type;
    }

}
