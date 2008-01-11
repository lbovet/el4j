/*
 * ========================================================================
 *
 * Copyright 2003-2004 The Apache Software Foundation. Code from this file
 * was originally imported from the Jakarta Cactus project.
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
package org.codehaus.cargo.container.weblogic.internal;

import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.taskdefs.Property;
import org.apache.tools.ant.types.Path;
import org.codehaus.cargo.container.ContainerCapability;
import org.codehaus.cargo.container.ContainerException;
import org.codehaus.cargo.container.configuration.LocalConfiguration;
import org.codehaus.cargo.container.internal.AntContainerExecutorThread;
import org.codehaus.cargo.container.internal.J2EEContainerCapability;
import org.codehaus.cargo.container.property.ServletPropertySet;
import org.codehaus.cargo.container.spi.AbstractInstalledLocalContainer;
import org.codehaus.cargo.container.weblogic.WebLogicPropertySet;

import sun.management.OperatingSystemImpl;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Basic support for the WebLogic application server.
 *
 * @version $Id: AbstractWebLogicInstalledLocalContainer.java 1211 2006-11-13 15:24:35Z grimsell $
 */
public abstract class AbstractWebLogicInstalledLocalContainer
    extends AbstractInstalledLocalContainer
{
    /**
     * The Bea home directory.
     */
    private String beaHome;

    /**
     * Capability of the WebLogic container.
     */
    private ContainerCapability capability = new J2EEContainerCapability();

    /**
     * {@inheritDoc}
     * @see AbstractInstalledLocalContainer#AbstractInstalledLocalContainer(LocalConfiguration)
     */
    public AbstractWebLogicInstalledLocalContainer(LocalConfiguration configuration)
    {
        super(configuration);
    }

    /**
     * Sets the Bea home directory.
     *
     * @param beaHome The BEA home directory
     */
    public final void setBeaHome(String beaHome)
    {
        this.beaHome = beaHome;
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.cargo.container.Container#getCapability()
     */
    public ContainerCapability getCapability()
    {
        return this.capability;
    }

    /**
     * Check the WLS installation directory setting and if the beaHome attribute is not set, guess
     * it.
     */
    public final void initBeaHome()
    {
        if (!new File(this.getHome()).isDirectory())
        {
            throw new ContainerException("[" + this.getHome() + "] is not a directory");
        }

        // If the beaHome attribute is not set, guess the bea home
        // directory using the parent directory of this.dir
        if (this.beaHome == null)
        {
            this.beaHome = getFileHandler().getParent(this.getHome());
        }
    }

    /**
     * {@inheritDoc}
     * @see AbstractInstalledLocalContainer#doStart(Java)
     */
    public void doStart(Java java) throws Exception
    {
        initBeaHome();
 
        
        File domainDir = new File(getConfiguration().getHome());
        
        java.setDir(domainDir);

       
        java.addSysproperty(getAntUtils().createSysProperty("weblogic.Name",
            getConfiguration().getPropertyValue(WebLogicPropertySet.SERVER)));
        
        //TODO: add domain name here as weblogic.Domain
        
        //java.addSysproperty(getAntUtils().createSysProperty("bea.home", this.beaHome));
        java.addSysproperty(getAntUtils().createSysProperty("weblogic.management.username",
            getConfiguration().getPropertyValue(WebLogicPropertySet.ADMIN_USER)));
        java.addSysproperty(getAntUtils().createSysProperty("weblogic.management.password",
            getConfiguration().getPropertyValue(WebLogicPropertySet.ADMIN_PWD)));

        // Note: The "=" in the call below is on purpose. It is left so that
        // we end up with:
        //   -Djava.security.policy==./server/lib/weblogic.policy
        // (otherwise, we would end up with:
        //   -Djava.security.policy=./server/lib/weblogic.policy, which
        //  will not add to the security policy but instead replace it).
        //java.addSysproperty(getAntUtils().createSysProperty("java.security.policy",
        //    "=./server/lib/weblogic.policy"));

        
        //update 28.11.07, FBI: use only standard security policy
        java.addSysproperty(getAntUtils().createSysProperty("java.security.policy",
                    new File(this.getHome(),"server/lib/weblogic.policy").getPath()));
        
        
        
        
        //update 28.11.07, FBI:
        //classpath dependencies are specified in pom file and therefore not added 
        //here again
        
        //Path classpath = java.createClasspath();
        //classpath.createPathElement().setLocation(new File(serverDir, "lib/weblogic_sp.jar"));
        //classpath.createPathElement().setLocation(new File(serverDir, "lib/weblogic.jar"));

        // The WebLogic startup scripts automatically includes the domain root folder in the
        // classpath. This folder is a common place to include configuration files, property files,
        // log4j configurations, etc. This is why we're adding it here.
        //classpath.createPathElement().setLocation(new File(getConfiguration().getHome()));

        // Add the tools jar to the classpath so deployment will succeed due to appc compiles
       // addToolsJarToClasspath(classpath);
        
        
        
       
        
        
        java.setClassname("weblogic.Server");
        
        
        AntContainerExecutorThread webLogicRunner = new AntContainerExecutorThread(java);
        webLogicRunner.start();
        
       
        
    }

    /**
     * {@inheritDoc}
     * @see AbstractInstalledLocalContainer#doStop(Java)
     */
    public final void doStop(Java java) throws Exception
    {
        
        
        java.setClassname("weblogic.Admin");
        java.createArg().setValue("-url");
        java.createArg().setValue("t3://localhost:"
            + getConfiguration().getPropertyValue(ServletPropertySet.PORT));
        java.createArg().setValue("-username");
        java.createArg().setValue(
            getConfiguration().getPropertyValue(WebLogicPropertySet.ADMIN_USER));
        java.createArg().setValue("-password");
        java.createArg().setValue(
            getConfiguration().getPropertyValue(WebLogicPropertySet.ADMIN_PWD));

        // Forcing WebLogic shutdown to speed up the shutdown process
        java.createArg().setValue("FORCESHUTDOWN");

        AntContainerExecutorThread webLogicRunner = new AntContainerExecutorThread(java);
        webLogicRunner.start();
    }
    
    
}   
