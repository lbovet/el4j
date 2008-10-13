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
package org.codehaus.cargo.container.tomcat;

import org.codehaus.cargo.container.LocalContainer;
import org.codehaus.cargo.container.tomcat.internal.AbstractTomcatManagerInstalledLocalDeployer;

/**
 * A Tomcat manager-based deployer to perform deployment to a local container.
 * 
 * @version $Id: TomcatManagerInstalledLocalDeployer.java 1157 2006-07-27 19:57:13Z vmassol $
 */
public class TomcatManagerInstalledLocalDeployer extends AbstractTomcatManagerInstalledLocalDeployer
{
    /**
     * {@inheritDoc}
     * @see AbstractTomcatManagerInstalledLocalDeployer#AbstractTomcatManagerInstalledLocalDeployer(org.codehaus.cargo.container.LocalContainer)
     */
    public TomcatManagerInstalledLocalDeployer(LocalContainer container)
    {
        super(container);
    }
}