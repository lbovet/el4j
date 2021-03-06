package com.silvermindsoftware.hitch.config;

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
public class FormConfig {

	private boolean autoBind = false;

	public FormConfig() {
	}

	public FormConfig(boolean autoBind) {
		this.autoBind = autoBind;
	}

	public boolean isAutoBind() {
		return autoBind;
	}

	public void setAutoBind(boolean autoBind) {
		this.autoBind = autoBind;
	}

}
