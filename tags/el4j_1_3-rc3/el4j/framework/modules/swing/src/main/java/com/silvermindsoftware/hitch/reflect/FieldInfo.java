package com.silvermindsoftware.hitch.reflect;

import java.lang.reflect.Field;

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

public class FieldInfo {

    private Field field;

    private Class type;
    private String name;


    public FieldInfo(Field field) {
        this.field = field;
        this.name = field.getName();
        this.type = field.getType();
    }

    public Field getField() {
        return field;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FieldInfo fieldInfo = (FieldInfo) o;

        if (!name.equals(fieldInfo.name)) return false;
        if (!type.equals(fieldInfo.type)) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = type.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    public String toString() {
        return new StringBuilder().append(type.getName()).append(" ").append(name).toString();
    }
}
