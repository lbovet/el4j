package com.silvermindsoftware.hitch.handlers;

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

import com.silvermindsoftware.hitch.handlers.type.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.math.BigDecimal;

public class TypeHandlerFactory {

    private Map<Object, TypeHandler> typeHandler;

    public TypeHandlerFactory() {

        this.typeHandler = new HashMap<Object, TypeHandler>();
        register(String.class, new StringTypeHandler());
        register(Integer.class, new IntegerTypeHandler());
        register(Long.class, new LongTypeHandler());
        register(Float.class, new FloatTypeHandler());
        register(Double.class, new DoubleTypeHandler());
        register(Date.class, new DateTypeHandler());
        register(BigDecimal.class, new BigDecimalTypeHandler());


    }

    public void register(Object key, TypeHandler componentHandler) {
        this.typeHandler.put(key, componentHandler);
    }

    public TypeHandler getHandler(Object key) {
        return typeHandler.get(key);
    }

}
