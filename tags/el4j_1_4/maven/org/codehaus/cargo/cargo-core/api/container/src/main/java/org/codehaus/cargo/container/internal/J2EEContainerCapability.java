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
package org.codehaus.cargo.container.internal;

import org.codehaus.cargo.container.ContainerCapability;
import org.codehaus.cargo.container.deployable.DeployableType;

/**
 * Capability for J2EE containers.
 * 
 * @version $Id: J2EEContainerCapability.java 969 2006-03-27 11:34:18Z vmassol $
 */
public class J2EEContainerCapability implements ContainerCapability
{
    /**
     * {@inheritDoc}
     * @see ContainerCapability#supportsDeployableType(org.codehaus.cargo.container.deployable.DeployableType)
     */
    public boolean supportsDeployableType(DeployableType type)
    {
        boolean supported = false;
        
        if ((type == DeployableType.WAR) || (type == DeployableType.EAR))
        {
            supported = true;
        }
        
        return supported;
    }
}