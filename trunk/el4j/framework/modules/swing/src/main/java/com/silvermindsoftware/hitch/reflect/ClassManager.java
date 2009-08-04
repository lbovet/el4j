package com.silvermindsoftware.hitch.reflect;

import java.util.HashMap;
import java.util.Map;
import java.util.Collections;

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

/**
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 */
public class ClassManager {

	private static final Map<Class, ClassInfo> classInfoCache = Collections.synchronizedMap(new HashMap<Class, ClassInfo>());

	public static ClassInfo getClassInfo(Class clazz) {
		if(classInfoCache.containsKey(clazz)) {
			return classInfoCache.get(clazz);
		} else {
			ClassInfo classInfo = new ClassInfo(clazz);
			classInfoCache.put(clazz, classInfo);
			return classInfo;
		}
	}

}
