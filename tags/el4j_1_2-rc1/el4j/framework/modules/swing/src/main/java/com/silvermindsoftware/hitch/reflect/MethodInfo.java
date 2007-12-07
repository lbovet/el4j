package com.silvermindsoftware.hitch.reflect;

import java.lang.reflect.Method;
import java.util.Arrays;

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

public class MethodInfo {

    private Method method;
    private String name;
    private Class[] parameterTypes;
    private Class returnType;

    public MethodInfo(Method method) {

        this.method = method;
        this.returnType = method.getReturnType();
        this.name = method.getName();
        this.parameterTypes = method.getParameterTypes();

    }

    public Method getMethod() {
        return method;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MethodInfo that = (MethodInfo) o;

        if (!name.equals(that.name)) return false;
        if (!Arrays.equals(parameterTypes, that.parameterTypes)) return false;
        if (!returnType.equals(that.returnType)) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = name.hashCode();
        result = 31 * result + Arrays.hashCode(parameterTypes);
        result = 31 * result + returnType.hashCode();
        return result;
    }


    public String toString() {
        StringBuilder params = new StringBuilder("");
        boolean start = true;
        for(Class clazz : parameterTypes ) {
            if(!start) params.append(",");
            params.append(clazz.getName());
            start = false;
        }

        return new StringBuilder(returnType.getName())
                .append(" ").append(name)
                .append("(").append(params.toString()).append(")").toString();
    }
}
