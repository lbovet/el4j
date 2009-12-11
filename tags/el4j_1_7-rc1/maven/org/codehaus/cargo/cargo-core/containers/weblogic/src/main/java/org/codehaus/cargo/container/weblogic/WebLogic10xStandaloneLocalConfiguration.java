/**
 * 
 */
package org.codehaus.cargo.container.weblogic;

import java.io.File;
import java.util.Iterator;

import org.apache.tools.ant.types.FilterChain;
import org.apache.tools.ant.util.FileUtils;
import org.codehaus.cargo.container.Container;
import org.codehaus.cargo.container.ContainerException;
import org.codehaus.cargo.container.LocalContainer;
import org.codehaus.cargo.container.configuration.ConfigurationCapability;
import org.codehaus.cargo.container.deployable.Deployable;
import org.codehaus.cargo.container.deployable.DeployableType;
import org.codehaus.cargo.container.deployable.WAR;
import org.codehaus.cargo.container.spi.configuration.AbstractStandaloneLocalConfiguration;
import org.codehaus.cargo.container.weblogic.internal.WebLogicStandaloneLocalConfigurationCapability;

/**
 * 
 * StandaloneLocalConfiguration class for WebLogic 10x
 * 
 * @author Frank Bitzer (FBI)
 *
 */
public class WebLogic10xStandaloneLocalConfiguration extends
		AbstractStandaloneLocalConfiguration {

	/**
     * Capability of the WebLogic standalone configuration.
     */
    private static ConfigurationCapability capability = 
        new WebLogicStandaloneLocalConfigurationCapability();

    /**
     * {@inheritDoc}
     * @see AbstractStandaloneLocalConfiguration#AbstractStandaloneLocalConfiguration(String)
     */
    public WebLogic10xStandaloneLocalConfiguration(String dir)
    {
        super(dir);

        setProperty(WebLogicPropertySet.ADMIN_USER, "weblogic");
        setProperty(WebLogicPropertySet.ADMIN_PWD, "weblogic");
        setProperty(WebLogicPropertySet.SERVER, "server");
        setProperty(WebLogicPropertySet.DOMAIN, "domain");
        
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.cargo.container.configuration.Configuration#getCapability()
     */
    public ConfigurationCapability getCapability()
    {
        return capability;
    }
    
    /**
     * {@inheritDoc}
     * @see AbstractStandaloneLocalConfiguration#configure(LocalContainer)
     */
    protected void doConfigure(LocalContainer container) throws Exception
    {
        setupConfigurationDir();

        FilterChain filterChain = createWebLogicFilterChain();

        getResourceUtils().copyResource(RESOURCE_PATH + container.getId() + "/config.xml",
            new File(getHome(), "config.xml"), filterChain);

        getResourceUtils().copyResource(RESOURCE_PATH + container.getId()
            + "/DefaultAuthenticatorInit.ldift",
            new File(getHome(), "DefaultAuthenticatorInit.ldift"), filterChain);

        setupDeployables(container);
    }

    /**
     * @return an Ant filter chain containing implementation for the filter tokens used in the 
     *         WebLogic configuration files
     */
    private FilterChain createWebLogicFilterChain()
    {
        FilterChain filterChain = createFilterChain();
        
        StringBuffer appTokenValue = new StringBuffer(" ");
        
        Iterator it = getDeployables().iterator();
        while (it.hasNext())
        {
            Deployable deployable = (Deployable) it.next();

            if ((deployable.getType() == DeployableType.WAR) && ((WAR) deployable).isExpandedWar())
            {
                String context = ((WAR) deployable).getContext();
                appTokenValue.append("<Application "); 
                appTokenValue.append("Name=\"_" + context + "_app\" ");
                appTokenValue.append(
                    "Path=\"" + getFileHandler().getParent(deployable.getFile()) + "\" "); 
                appTokenValue.append(
                    "StagedTargets=\"server\" StagingMode=\"stage\" TwoPhase=\"true\"");
                appTokenValue.append(">");
                
                appTokenValue.append("<WebAppComponent ");
                appTokenValue.append("Name=\"" + context + "\" "); 
                appTokenValue.append("Targets=\"server\" ");
                appTokenValue.append("URI=\"" + context + "\"");
                appTokenValue.append("/></Application>");
            }
        }
        
        getAntUtils().addTokenToFilterChain(filterChain, "weblogic.apps", appTokenValue.toString());
            
        return filterChain;
    }

    /**
     * Deploy the Deployables to the weblogic configuration.
     * 
     * @param container the container to configure
     */
    protected void setupDeployables(Container container)
    {
        try 
        {
            FileUtils fileUtils = FileUtils.newFileUtils();

            // Create the applications directory
            String appDir = getFileHandler().createDirectory(getHome(), "autodeploy");
            
            // Deploy all deployables into the applications directory
            Iterator it = getDeployables().iterator();
            while (it.hasNext())
            {
                Deployable deployable = (Deployable) it.next();
                if ((deployable.getType() == DeployableType.WAR) 
                    && ((WAR) deployable).isExpandedWar())
                {
                    continue;
                }

                fileUtils.copyFile(deployable.getFile(),
                    getFileHandler().append(appDir, getFileHandler().getName(deployable.getFile())),
                    null, true);
            }
            
            // Deploy the cargocpc web-app by copying the WAR file
            getResourceUtils().copyResource(RESOURCE_PATH + "cargocpc.war",
                new File(appDir, "cargocpc.war"));
        }
        catch (Exception e)
        {
            throw new ContainerException("Failed to deploy Deployables in the " 
                + container.getName() + " [" + getHome() + "] domain directory", e);
        }
    }    

    /**
     * {@inheritDoc}
     * @see Object#toString()
     */
    public String toString()
    {
        return "WebLogic Standalone Configuration";
    }

}
