package com.silvermindsoftware.hitch.handlers.type;

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

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

public class DateTypeHandler implements TypeHandler {
	public Object convert(Object value) {
        if (value == null) return value;
        Date retVal = null;
		if (value instanceof Date) {
			retVal = (Date) value;
		} else {
			try {
				retVal = DateFormat.getInstance().parse(String.valueOf(value));
			} catch (ParseException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}
		return retVal;
	}
}
