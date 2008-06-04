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

import com.silvermindsoftware.hitch.ReadOnly;

import java.lang.annotation.*;

/**
 * BoundComponent to bind domain objects to Swing components
 */
@Target({ElementType.FIELD})
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface BoundComponent {
    /**
	 * <p>
	 * If specified. This is the type the component value
	 * will be converted to before being set on the model
     * object.
	 * <p/>
	 * If not specified an attempt will be made to determine
     * the type to be set based on the model objects getter
     * return type or field type.
	 *
	 * @return
	 */
	Class type() default void.class;

    /**
	 * <p>
	 * If specified. This is the property the component
	 * will be bound to on the model class.
	 * <p/>
	 * If not specified an attempt will be made to find an
	 * identically named property on the model.
	 *
	 * @return
	 */
	String property() default "[default]";

	/**
	 * If specified. This is the id of the ModelObject
	 * that will be used when performing bindings.
	 * <p/>
	 * If not it will use the default ModelObject
	 *
	 * @return
	 */
	String modelId() default "[default]";

	/**
	 * If specified. This is a custom component handler
	 * that will be used to handle value extraction and
	 * setting of a component.
	 *
	 * If not specified the default class component handler
	 * will be used. void.class is used in this case because
	 * Java annotations does not provide a means to default
	 * a value to null.
	 *
	 * @return
	 */
	Class handler() default void.class;

	/**
	 * This is a set of string parameters that can be passed
	 * component handler. These parameters need to be passed
	 * in the form of "[property]=[value]". You can pass
	 * as many property/value combos as you like. In order for
	 * the property/value to be useful your ComponentHandler
	 * must have compatible setters.
	 *
	 * @return
	 */
	String[] handlerValues() default "";

    /**
     * This attribute specifies whether a component is read only.
     * If read only is true the component will be updated during
     * the updateForm but will not be accessed when performing
     * an updateModel. If default is specified then the default behavior
     * is used. This could be different for the type of component being
     * bound. Currently JLabels are defaulted to be read-only.
     * @return
     */
    ReadOnly readOnly() default ReadOnly.DEFAULT;
    

}
