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

import java.io.IOException;

import org.codehaus.cargo.container.RemoteContainer;
import org.codehaus.cargo.container.deployable.Deployable;
import org.codehaus.cargo.container.tomcat.internal.AbstractTomcatRemoteDeployer;
import org.codehaus.cargo.container.tomcat.internal.TomcatManagerException;

/**
 * A special Tomcat5x manager-based deployer to perform deployment to a remote container.
 * 
 * @version $Id: Tomcat5xRemoteDeployer.java 1157 2006-07-27 19:57:13Z vmassol $
 */
public class Tomcat5xRemoteDeployer extends AbstractTomcatRemoteDeployer
{
    /**
     * {@inheritDoc}
     * @see AbstractTomcatRemoteDeployer#AbstractTomcatRemoteDeployer(org.codehaus.cargo.container.RemoteContainer)
     */
    public Tomcat5xRemoteDeployer(RemoteContainer container)
    {
        super(container); 
    }

    /**
     * {@inheritDoc}
     *
     * <p>This is a special implementation of undeploy command for Tomcat 5.x.</p>
     *
     * @see Tomcat4xRemoteDeployer#performUndeploy(org.codehaus.cargo.container.deployable.Deployable)
     * @see org.codehaus.cargo.container.tomcat.internal.AbstractTomcatManagerDeployer#performUndeploy(org.codehaus.cargo.container.deployable.Deployable)
     */
    protected void performUndeploy(Deployable deployable) throws TomcatManagerException, IOException
    {
        getTomcatManager().undeploy(getPath(deployable));        
    }
}