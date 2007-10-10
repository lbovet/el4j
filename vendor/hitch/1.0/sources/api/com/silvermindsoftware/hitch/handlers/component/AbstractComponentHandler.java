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

import com.silvermindsoftware.hitch.ReadOnly;
import com.silvermindsoftware.hitch.reflect.ClassManager;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.HashMap;
import java.beans.Introspector;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

abstract public class AbstractComponentHandler<ComponentType, ModelType, UIType> implements ComponentHandler<ComponentType, ModelType, UIType> {

	private final Map<Class, Method> componentGetter = new HashMap<Class, Method>();
	private final Map<Class, Method> componentSetter = new HashMap<Class, Method>();

//	private String getterName = null;
//	private String setterName = null;
	private Map<String, Method> getterMethodMap = new HashMap<String, Method>();
	private Map<String, Field> fieldMap = new HashMap<String, Field>();

	// retrieves the getter field method for reading values from the component field
	public Method getComponentGetter(ComponentType component) {

		Class componentClass = component.getClass();
		Method method;

		if (this.componentGetter.containsKey(component.getClass())) {
			method = componentGetter.get(component.getClass());
		} else {
			try {
				method = ClassManager.getClassInfo(componentClass).getGetterMethod(getGetterName());
				componentGetter.put(component.getClass(), method);
			} catch (NoSuchMethodException e) {
				throw new IllegalStateException("Method " + getGetterName() + " not found on component " + componentClass.getName());
			}
		}

		return method;
	}

	// retrievesthe setter field method for setting values on the component field
	public Method getComponentSetter(ComponentType component) {
		Class componentClass = component.getClass();
		Method method;

		if (this.componentSetter.containsKey(component.getClass())) {
			method = componentSetter.get(component.getClass());
		} else {
			try {
                
                method = ClassManager.getClassInfo(
                        componentClass).getSetterMethod(getSetterName(),
                        new Class[]{getSetterParameterType()});
                
                componentSetter.put(component.getClass(),method);
                
            } catch (NoSuchMethodException e) {
				throw new IllegalStateException("Method " + getSetterName() + " not found on component " + componentClass.getName());
			}
		}

		return method;
	}


    public boolean isPopulateHandleable(ComponentType component, ModelType modelPropertyValue) {
        return true;
    }

    public boolean isUpdateHandleable(ComponentType component) {
        return true;
    }

	@SuppressWarnings("unchecked")
    public UIType preProcessPopulate(ComponentType component, ModelType modelPropertyValue) {
		return (UIType) modelPropertyValue;
	}

	@SuppressWarnings("unchecked")
	public ModelType preProcessUpdate(ComponentType component, UIType modelPropertyValue) {
		return (ModelType) modelPropertyValue;
	}

	public void postProcessPopulate(ComponentType component) {
	}

	public void postProcessUpdate(ComponentType component) {
	}

	abstract protected Class getSetterParameterType();

	abstract protected String getGetterName();

	abstract protected String getSetterName();

    public boolean getDefaultReadOnly() {
        return false;
    }

    protected Object getPropertyValue(Object object, String propertyName) {
		Object retVal = null;
		Method getterMethod = null;
		boolean foundGetter;
		boolean foundValueField = false;

		//if propertyName then reflect on object
		if (propertyName != null) {

			Class modelClass = object.getClass();
			String lookupKey = modelClass.getName() + propertyName;
			// attempt to find method in getterMap
			if (getterMethodMap.containsKey(lookupKey)) {
				try {
					retVal = getterMethodMap.get(lookupKey).invoke(object);
				} catch (IllegalAccessException e) {
					// this shouldn't happen
					throw new IllegalStateException("*** THIS SHOULD NEVER HAPPEN ***" + e.getMessage(), e);
				} catch (InvocationTargetException e) {
					throw new IllegalStateException(e.getMessage(), e);
				}
			} else if (fieldMap.containsKey(lookupKey)) {
				Field field = fieldMap.get(lookupKey);
				try {
					retVal = field.get(object);
				} catch (IllegalAccessException e) {
					// this should never happen
					throw new IllegalStateException("*** THIS SHOULD NEVER HAPPEN ***" + e.getMessage(), e);
				}
			} else {
				try {
					getterMethod =
							ClassManager.getClassInfo(modelClass).getGetterMethod(
									new StringBuilder()
											.append("get")
											.append(propertyName.substring(0, 1).toUpperCase())
											.append(propertyName.substring(1)).toString());
					foundGetter = true;
				} catch (NoSuchMethodException e) {
					// method does not exist
					foundGetter = false;
				}

				Field valueField = null;

				if (foundGetter) {
					// use getter if it exists
					try {
						retVal = getterMethod.invoke(object);
						getterMethodMap.put(lookupKey, getterMethod);
					} catch (IllegalAccessException e) {
						// if there is an access exception then set the getter to NOT found
						// and lookup the field
						foundGetter = false;

						try {
							valueField = ClassManager.getClassInfo(modelClass).getField(propertyName);
							valueField.setAccessible(true);
							foundValueField = true;
						} catch (NoSuchFieldException e1) {
							// field not found
							foundValueField = false;
						}

						try {
							if (valueField != null) {
								retVal = valueField.get(object);
							}
							fieldMap.put(lookupKey, valueField);
						} catch (IllegalAccessException e1) {
							// value field not acessible: this shouldn't happen
							// since we make it accessible
							throw new IllegalStateException("*** THIS SHOULD NEVER HAPPEN ***" + e.getMessage(), e);
						}


					} catch (InvocationTargetException e) {
						// this is  an exception that gets thrown from the
						// getter method itself. We want to rethrow this
						throw new IllegalStateException(e.getMessage(), e);
					}
				} else {

					try {
						valueField = ClassManager.getClassInfo(modelClass).getField(propertyName);
						valueField.setAccessible(true);
						foundValueField = true;
					} catch (NoSuchFieldException e) {
						// field not found
						foundValueField = false;
					}

					try {
						if (valueField != null) {
							retVal = valueField.get(object);
						}
						fieldMap.put(lookupKey, valueField);
					} catch (IllegalAccessException e) {
						// value field not acessible: this shouldn't happen
						// since we make it accessible
						throw new IllegalStateException("*** THIS SHOULD NEVER HAPPEN ***" + e.getMessage(), e);
					}

				}

				if (!foundGetter && !foundValueField) {
					throw new IllegalStateException("Getter and field for property " + propertyName + " not found for object " + modelClass.getName());
				}

			}

		} else {
			//otherwise return object
			retVal = object;
		}

		return retVal;
	}

	protected boolean isBaseType(Object modelPropertyValue) {

		Class modelFieldClass = modelPropertyValue.getClass();

		boolean retVal = false;
		if (modelFieldClass == String.class) {
			retVal = true;
		} else if (modelFieldClass == byte.class) {
			retVal = true;
		} else if (modelFieldClass == short.class) {
			retVal = true;
		} else if (modelFieldClass == int.class) {
			retVal = true;
		} else if (modelFieldClass == long.class) {
			retVal = true;
		} else if (modelFieldClass == float.class) {
			retVal = true;
		} else if (modelFieldClass == double.class) {
			retVal = true;
		} else if (modelFieldClass == boolean.class) {
			retVal = true;
		} else if (modelFieldClass == char.class) {
			retVal = true;
		} else if (modelPropertyValue instanceof Byte) {
			retVal = true;
		} else if (modelPropertyValue instanceof Short) {
			retVal = true;
		} else if (modelPropertyValue instanceof Integer) {
			retVal = true;
		} else if (modelPropertyValue instanceof Long) {
			retVal = true;
		} else if (modelPropertyValue instanceof Float) {
			retVal = true;
		} else if (modelPropertyValue instanceof Double) {
			retVal = true;
		} else if (modelPropertyValue instanceof Boolean) {
			retVal = true;
		} else if (modelPropertyValue instanceof Character) {
			retVal = true;
		}

		return retVal;
	}
}
