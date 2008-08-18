/* 
 * ========================================================================
 * 
 * Copyright 2004-2005 Vincent Massol.
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
package org.codehaus.cargo.container;

import org.codehaus.cargo.container.deployable.DeployableType;

/**
 * Represents the capability of a container. More specifically what deployable type it supports,
 * etc.
 * 
 * @version $Id: ContainerCapability.java 607 2005-11-05 13:07:56Z vmassol $
 */
public interface ContainerCapability
{
    /**
     * @param type the deployable type
     * @return true if the container supports the specified deployable type
     */
    boolean supportsDeployableType(DeployableType type);
}