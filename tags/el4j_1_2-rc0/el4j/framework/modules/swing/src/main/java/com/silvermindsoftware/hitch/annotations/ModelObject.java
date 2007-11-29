package com.silvermindsoftware.hitch.annotations;

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

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface ModelObject {

    /**
     * Defines the annotated object as the default model object
     * @return
     */
    boolean isDefault() default false;

    /**
     * Defines whether to autoBind this ModelObject when autoBind is
     * enabled on the form. This allows you to opt out certain certain
     * model objects from being auto bound. This is convenient when you
     * have two model objects on a form that share a large amount of
     * identical property names.
     * @return
     */
    boolean autoBind() default true;

}
