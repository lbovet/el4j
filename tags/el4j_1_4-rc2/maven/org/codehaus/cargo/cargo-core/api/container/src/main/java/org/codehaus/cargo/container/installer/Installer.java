/* 
 * ========================================================================
 * 
 * Copyright 2004-2006 Vincent Massol.
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
 * 
 * ========================================================================
 */
package org.codehaus.cargo.container.installer;

import org.codehaus.cargo.util.log.Loggable;

/**
 * Installs a container. 
 * 
 * @version $Id: Installer.java 1165 2006-07-31 22:13:35Z vmassol $
 */
public interface Installer extends Loggable
{
    /**
     * Installs the container.
     */
    void install();
    
    /**
     * @return the directory where the container has been installed. Note that we're returning a
     *         String instead of a File because we want to leave the possibility of using URIs for
     *         specifying the home location.
     */
    String getHome();
}