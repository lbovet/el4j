/*
 * ========================================================================
 *
 * Copyright 2006 Vincent Massol.
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
package org.codehaus.cargo.container.spi.deployer;

import org.codehaus.cargo.container.InstalledLocalContainer;
import org.codehaus.cargo.container.deployer.DeployerType;

/**
 * Base deployer to deploy to installed local containers.
 *
 * @version $Id: AbstractInstalledLocalDeployer.java 1160 2006-07-30 20:15:34Z vmassol $
 */
public abstract class AbstractInstalledLocalDeployer extends AbstractLocalDeployer
{
    /**
     * {@inheritDoc}
     * @see AbstractLocalDeployer#AbstractLocalDeployer(org.codehaus.cargo.container.LocalContainer)
     */
    public AbstractInstalledLocalDeployer(InstalledLocalContainer container)
    {
        super(container);
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.cargo.container.deployer.Deployer#getType()
     */
    public DeployerType getType()
    {
        return DeployerType.INSTALLED;
    }
}