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

import com.silvermindsoftware.hitch.AbstractBinder;
import com.silvermindsoftware.hitch.Binder;
import com.silvermindsoftware.hitch.ReadOnly;
import com.silvermindsoftware.hitch.meta.FormMeta;
import com.silvermindsoftware.hitch.reflect.ClassManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BinderConfig {

    private static final Log log = LogFactory.getLog(BinderConfig.class);
    private FormMeta formMeta;
    private FormConfig formConfig;

    public BinderConfig(Class componentClass, Binder binder) {
        this(componentClass, binder, new FormConfig());
    }

    public BinderConfig(Class componentClass, Binder binder, FormConfig formConfig) {
        AbstractBinder abstractBinder = (AbstractBinder) binder;
        this.formMeta = abstractBinder.getFormMeta(componentClass);
        this.formConfig = formConfig;
    }

    public BinderConfig bindModel(ModelObjectConfig modelObjectConfig) {

        bindCompleteModel(
                modelObjectConfig.getModelField(),
                modelObjectConfig.getModelField(),
                modelObjectConfig.isDefault(),
                modelObjectConfig.isAutoBind(),
                modelObjectConfig.getIgnoreFields());

        return this;
    }

    /**
     * @param modelField
     * @param ignoreFields
     */
    public BinderConfig bindDefaultModel(String modelField, String... ignoreFields) {
        return bindCompleteModel("[default]", modelField, true, true, ignoreFields);
    }


    /**
     * @param modelField
     * @param ignoreFields
     */
    public BinderConfig bindModel(String modelField, String... ignoreFields) {
        return bindCompleteModel(modelField, modelField, false, true, ignoreFields);
    }

    /**
     * @param modelField
     */
    public BinderConfig bindDefaultModel(String modelField) {
        return bindCompleteModel("[default]", modelField, true, true);
    }


    /**
     * @param modelField
     */
    public BinderConfig bindModel(String modelField) {
        return bindCompleteModel(modelField, modelField, false, true);
    }

    private BinderConfig bindCompleteModel(String modelFieldName, String modelField, boolean defaultModel, boolean autoBindModelObject, String... ignoreFields) {
        formMeta.putModelMeta(modelFieldName, modelField);
        autoBind(modelField, defaultModel, autoBindModelObject, ignoreFields);
        return this;
    }


    private void autoBind(String modelField, boolean defaultModel, boolean autoBindModelObject, String... ignoreFields) {

        List ignoreFieldsList = Arrays.asList(ignoreFields);

        if (formConfig.isAutoBind() && autoBindModelObject) {

            Class formClass = formMeta.getComponentClass();

            Field field = null;
            try {
                field = ClassManager.getClassInfo(formClass).getField(modelField);
            } catch (NoSuchFieldException e) {
                //ignored
            }

            // iterate modelObject fields
            Class modelObjectType = field.getType();

            if (!Map.class.isAssignableFrom(modelObjectType)) { // can't autobind maps
                for (Method moMethod : ClassManager.getClassInfo(modelObjectType).getSetters()) {
                    String componentFieldName =
                            moMethod.getName().substring(3, 4).toLowerCase() +
                                    moMethod.getName().substring(4);

                    Field formClassField = null;

                    try {
                        formClassField = ClassManager.getClassInfo(formClass).getField(componentFieldName);

                        if (!ignoreFieldsList.contains(formClassField.getName())) {

                            formMeta.addComponentMeta(
                                    defaultModel ? "[default]" : field.getName(),
                                    componentFieldName, formClassField, void.class,
                                    new String[]{}, true, ReadOnly.DEFAULT, moMethod.getParameterTypes()[0]);

                        }

                    } catch (NoSuchFieldException e) {
                        // ignored
                    }
                }
            }
        }
    }

    /**
     * @param boundComponentConfig
     */
    public BinderConfig bindComponentToDefault(BoundComponentConfig boundComponentConfig) {
        formMeta.addComponentMeta("[default]", boundComponentConfig, false);
        return this;
    }

    /**
     * @param boundComponentConfigs
     */
    public BinderConfig bindComponentToDefault(BoundComponentConfig... boundComponentConfigs) {
        for (BoundComponentConfig boundComponentConfig : boundComponentConfigs) {
            formMeta.addComponentMeta("[default]", boundComponentConfig, false);
        }
        return this;
    }

    /**
     * @param modelId
     * @param boundComponentConfig
     */
    public BinderConfig bindComponent(String modelId, BoundComponentConfig boundComponentConfig) {
        formMeta.addComponentMeta(modelId, boundComponentConfig, false);
        return this;
    }

    /**
     * @param modelId
     * @param boundComponentConfigs
     */
    public BinderConfig bindComponent(String modelId, BoundComponentConfig... boundComponentConfigs) {
        for (BoundComponentConfig boundComponentConfig : boundComponentConfigs) {
            formMeta.addComponentMeta(modelId, boundComponentConfig, false);
        }
        return this;
    }

}
