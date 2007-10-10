package com.silvermindsoftware.hitch;

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

import java.util.ArrayList;
import java.util.List;

public class ErrorContext {

    private static class ThreadLocalList extends ThreadLocal {
      public Object initialValue() {
        return new ArrayList<String>();
      }

      public List<String> getList() {
        return (List<String>) super.get();
      }
    }

    private static ThreadLocalList list = new ThreadLocalList();
    private static String[] stringArray = new String[0];

    public static void clear() {
      list.getList().clear();
    }

    public static void put(String text) {
      list.getList().add(text);
    }

    public static void removeLast() {
        list.getList().remove(list.getList().size()-1);
    }

    public static String[] get() {
      return (String[])list.getList().toArray(stringArray);
    }

    public static String getAsString() {
        StringBuilder sb = new StringBuilder();
        for(String error : list.getList()) {
            sb.append(error).append('\n');
        }
        return sb.toString();
    }

}
