package com.silvermindsoftware.hitch.handlers.component;

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

import javax.swing.*;

public class JSliderComponentHandler
        extends AbstractComponentHandler<JSlider, Object, Object>
        implements ComponentHandler<JSlider, Object, Object> {

    protected Class getSetterParameterType() {
        return int.class;
    }

    protected String getGetterName() {
        return "getValue";
    }

    protected String getSetterName() {
        return "setValue";
    }


    public boolean isPopulateHandleable(JSlider component, Object modelPropertyValue) {
        return modelPropertyValue != null;
    }
}