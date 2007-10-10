package com.silvermindsoftware.hitch.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright 2007 Brandon Goodin
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class ClassInfo {

    /* Class Hierarchy */
    public List<Class> classList = new ArrayList<Class>();

    /* Fields */
    Field[] fields;

    /* Methods */
    Method[] getters;
    Method[] setters;
    Method[] methods;

    public ClassInfo(Class clazz) {
        collectInfo(clazz);
    }

    private void collectInfo(Class clazz) {
        classList.add(clazz);
        getSuperClass(clazz);
        collectFields();
        collectMethods();
    }

    private void collectFields() {

        List<FieldInfo> fieldInfoList = new ArrayList<FieldInfo>();
        List<Field> fieldList = new ArrayList<Field>();

        for (Class clazz : classList) {
            for (Field field : clazz.getDeclaredFields()) {
                FieldInfo fieldInfo = new FieldInfo(field);
                if (!fieldInfoList.contains(fieldInfo)) {
                    fieldInfoList.add(fieldInfo);
                    fieldList.add(fieldInfo.getField());
                }
            }
        }

        fields = fieldList.toArray(new Field[]{});

    }

    private void collectMethods() {

        List<MethodInfo> setterInfoList = new ArrayList<MethodInfo>();
        List<MethodInfo> getterInfoList = new ArrayList<MethodInfo>();
        List<MethodInfo> methodsInfoList = new ArrayList<MethodInfo>();

        List<Method> setterList = new ArrayList<Method>();
        List<Method> getterList = new ArrayList<Method>();
        List<Method> methodList = new ArrayList<Method>();

        for (Class clazz : classList) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.getName().startsWith("set")) {
                    MethodInfo methodInfo = new MethodInfo(method);
                    if (!setterInfoList.contains(methodInfo)) {
                        setterInfoList.add(methodInfo);
                        setterList.add(methodInfo.getMethod());
                    }
                } else
                if ((method.getName().startsWith("get") || method.getName().startsWith("is"))
                        && !method.getName().equals("getClass")
                        && !method.getName().equals("get")) {
                    MethodInfo methodInfo = new MethodInfo(method);
                    if (!getterInfoList.contains(methodInfo)) {
                        getterInfoList.add(methodInfo);
                        getterList.add(methodInfo.getMethod());
                    }
                } else {
                    MethodInfo methodInfo = new MethodInfo(method);
                    if (!methodsInfoList.contains(methodInfo)) {
                        methodsInfoList.add(methodInfo);
                        methodList.add(methodInfo.getMethod());
                    }
                }
            }
        }

        getters = getterList.toArray(new Method[]{});
        setters = setterList.toArray(new Method[]{});
        methods = methodList.toArray(new Method[]{});

    }

    private void getSuperClass(Class clazz) {
        Class superClazz = clazz.getSuperclass();
        if (superClazz != null) {
            classList.add(superClazz);
            getSuperClass(superClazz);
        }
    }

    private static boolean arrayContentsEq(Object[] a1, Object[] a2) {
        if (a1 == null) {
            return a2 == null || a2.length == 0;
        }

        if (a2 == null) {
            return a1.length == 0;
        }

        if (a1.length != a2.length) {
            return false;
        }

        for (int i = 0; i < a1.length; i++) {
            if (a1[i] != a2[i]) {
                return false;
            }
        }

        return true;
    }


    /**
     * Retrieves a single field (public, private, protected or package) from the provided class
     * or one of its superclasses. The lowest subclass field is collected when there is a duplicate.
     * A duplicate is defined as having the same field name and type.
     *
     * @return
     */
    public Field getField(String name) throws NoSuchFieldException {

        if (name == null || name.trim().equals("")) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        ;

        Field retField = null;

        for (Field field : fields) {
            if (field.getName().equals(name)) {
                retField = field;
                break;
            }
        }

        if (retField == null) {
            throw new NoSuchFieldException("No method found for method named " + name);
        }

        return retField;
    }

    /**
     * Retrieves a single getter method (public, private, protected or package) from the provided class
     * or one of its superclasses. The lowest subclass method is collected when there is a duplicate.
     * A duplicate is defined as having the same method name and parameter types.
     *
     * @return
     */
    public Method getSetterMethod(String name, Class[] parameterTypes) throws NoSuchMethodException {
        if (name == null || name.trim().equals(""))
            throw new IllegalArgumentException("Name cannot be null or empty");
        
        Method retSetter = null;

        for (Method setter : setters) {
            if (setter.getName().equals(name) &&
                    arrayContentsEq(parameterTypes, setter.getParameterTypes())) {
                retSetter = setter;
                break;
            }
        }

        if (retSetter == null)
            throw new NoSuchMethodException("No method found for method named " + name);

        return retSetter;
    }

    /**
     * Retrieves a single setter method (public, private, protected or package) from the provided class
     * or one of its superclasses. The lowest subclass method is collected when there is a duplicate.
     * A duplicate is defined as having the same method name and parameter types.
     *
     * @return
     */
    public Method getGetterMethod(String name) throws NoSuchMethodException {
        if (name == null || name.trim().equals(""))
            throw new IllegalArgumentException("Name cannot be null or empty");

        Method retGetter = null;

        for (Method getter : getters) {
            if (getter.getName().equals(name) && getter.getParameterTypes().length == 0) {
                retGetter = getter;
                break;
            }
        }

        if (retGetter == null)
            throw new NoSuchMethodException("No method found for method named " + name);

        return retGetter;
    }

    /**
     * Retrieves a single method that is not a setter or getter (public, private, protected or package)
     * from the provided class or one of its superclasses. Only the lowest subclass method is collected
     * when there is a duplicate. A duplicate is defined as having the same method name and parameter
     * types.
     *
     * @return
     */
    public Method getMethod(String name, Class[] parameterTypes) throws NoSuchMethodException {
        if (name == null || name.trim().equals(""))
            throw new IllegalArgumentException("Name cannot be null or empty");

        Method retMethod = null;

        for (Method method : methods) {
            if (method.getName().equals(name) &&
                    arrayContentsEq(parameterTypes, method.getParameterTypes())) {
                retMethod = method;
                break;
            }
        }

        if (retMethod == null)
            throw new NoSuchMethodException("No method found for method named " + name);

        return retMethod;
    }

    /**
     * Array of all fields (public, private, protected or package) for the provided class
     * and all of its superclasses. The lowest subclass field is collected when there is
     * a duplicate. A duplicate is defined as having the same field name and type .
     *
     * @return
     */
    public Field[] getFields() {
        return fields;
    }

    /**
     * Array of all setter methods (public, private, protected or package) for the provided
     * class and all of its superclasses. The lowest subclass method is collected when there
     * is a duplicate. A duplicate is defined as having the same method name and parameter
     * types.
     *
     * @return
     */
    public Method[] getSetters() {
        return setters;
    }

    /**
     * Array of all getter methods (public, private, protected or package) for the provided
     * class and all of its superclasses. The lowest subclass method is collected when there
     * is a duplicate. A duplicate is defined as having the same method name and parameter
     * types.
     *
     * @return
     */
    public Method[] getGetters() {
        return getters;
    }

    /**
     * Array of all methods that are not setters or getters (public, private, protected or package)
     * for the provided class and all of its superclasses. The lowest subclass method is collected
     * when there is a duplicate. A duplicate is defined as having the same method name and parameter
     * types.
     *
     * @return
     */
    public Method[] getMethods() {
        return getters;
    }
}
