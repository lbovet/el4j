package org.codehaus.mojo.webstart;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License" );
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.jar.JarSignMojo;

/**
 * Just exists to make JarSignMojo implement JarSignerMojo.
 * 
 * The JarSignerMojo interface should be moved to the jar plugin.
 * 
 * @author dboden
 *
 */
class JarSignMojo2 extends JarSignMojo implements JarSignerMojo {

}