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

public class ModelObjectConfig {

    private String modelField;
    private boolean autoBind = true;
    private boolean isDefault = false;
    private String[] ignoreFields = new String[]{};

    public ModelObjectConfig(String modelField) {
        this.modelField = modelField;
    }

    public ModelObjectConfig(String modelField, boolean isDefault) {
        this.modelField = modelField;
        this.isDefault = isDefault;
    }

    public ModelObjectConfig(String modelField, boolean isDefault, boolean autoBind) {
        this.modelField = modelField;
        this.isDefault = isDefault;
        this.autoBind = autoBind;
    }

    public ModelObjectConfig(String modelField, boolean isDefault, boolean autoBind, String[] ignoreFields) {
        this.modelField = modelField;
        this.autoBind = autoBind;
        this.isDefault = isDefault;
        this.ignoreFields = ignoreFields;
    }


    public void setModelField(String modelField) {
        this.modelField = modelField;
    }

    public ModelObjectConfig setAutoBind(boolean autoBind) {
        this.autoBind = autoBind;
        return this;
    }

    public ModelObjectConfig setDefault(boolean aDefault) {
        isDefault = aDefault;
        return this;
    }

    public ModelObjectConfig setIgnoreFields(String[] ignoreFields) {
        this.ignoreFields = ignoreFields;
        return this;
    }

    public String getModelField() {
        return modelField;
    }

    public boolean isAutoBind() {
        return autoBind;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public String[] getIgnoreFields() {
        return ignoreFields;
    }
}
