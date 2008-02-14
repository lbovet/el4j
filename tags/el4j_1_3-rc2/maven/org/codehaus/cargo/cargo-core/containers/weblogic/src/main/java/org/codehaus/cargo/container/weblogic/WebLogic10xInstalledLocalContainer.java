/**
 * 
 */
package org.codehaus.cargo.container.weblogic;

import java.io.File;


import org.apache.tools.ant.taskdefs.Java;
import org.codehaus.cargo.container.configuration.LocalConfiguration;
import org.codehaus.cargo.container.spi.AbstractInstalledLocalContainer;
import org.codehaus.cargo.container.weblogic.internal.AbstractWebLogicInstalledLocalContainer;;

/**
 * 
 * Support for Bea WebLogic 10.x application server.
 * 
 * 
 * @author Frank Bitzer (FBI)
 *
 *@version $Id$
 */
public class WebLogic10xInstalledLocalContainer extends
		org.codehaus.cargo.container.weblogic.internal.AbstractWebLogicInstalledLocalContainer {

	
	/**
     * Unique container id.
     */
    public static final String ID = "weblogic10x";

    /**
     * {@inheritDoc}
     * @see AbstractWebLogicInstalledLocalContainer#AbstractInstalledLocalContainer(org.codehaus.cargo.container.configuration.LocalConfiguration)
     */
    public WebLogic10xInstalledLocalContainer(LocalConfiguration configuration)
    {
        super(configuration);
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.cargo.container.Container#getName()
     */
    public final String getName()
    {
        return "WebLogic 10.x";
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.cargo.container.Container#getId()
     */
    public final String getId()
    {
        return ID;
    }
    
    
    
    /**
     * {@inheritDoc}
     * @see AbstractInstalledLocalContainer#doStart(Java)
     */
    public void doStart(Java java) throws Exception
    {
    	File domainDir = new File(getConfiguration().getHome());
    	
    	//we need to do some more configuration here than its necessary for WebLogic 8x
    	
    	
    	//check if config file exists
    	//if not, domain and server must be generated at first
    	//this is done by the weblogic.Server Java application
    	//when weblogic.management.GenerateDefaultConfig is set to "true"
    	if (!new File(domainDir,"config/config.xml").exists()){
         	java.addSysproperty(getAntUtils().
        			createSysProperty("weblogic.management.GenerateDefaultConfig","true"));
        }
        
    	
    	//memory limitations
       //java.createArg().setValue("-Xms256m");
       //java.createArg().setValue("-Xmx512m");
    	
    	
    	//we use Jrockit because SUN JVM sometimes causes OutOfMemory Exceptions
    	java.setJvm(new File(
    			getConfiguration().getPropertyValue(
    					WebLogicPropertySet.JVM)).getPath());
    	
    	
    	
    	
    	
        java.setFork(true);
        java.setFailonerror(true);
    	
        
        java.createJvmarg().setValue("-jrockit");
        
        java.createJvmarg().setValue("-Xms256m");
        java.createJvmarg().setValue("-Xmx512m");
        
        java.createJvmarg().setValue("-Xverify:none");
        java.createJvmarg().setValue("-da");
        
       // java.createJvmarg().setValue("-verbose");
        
        
    	java.addSysproperty(getAntUtils().createSysProperty("platform.home", 
    			new File(this.getHome()).getPath()));
    	
    	java.addSysproperty(getAntUtils().createSysProperty("wls.home", 
    			new File(this.getHome(),"server").getPath()));
    	
    	java.addSysproperty(getAntUtils().createSysProperty("weblogic.home", 
    			new File(this.getHome(),"server").getPath()));
    	
    	
    	java.addSysproperty(getAntUtils().createSysProperty("wli.home", 
    			new File(this.getHome(),"integration").getPath()));
    	
    	
    	java.addSysproperty(getAntUtils().createSysProperty("weblogic.management.discover", 
    			"true"));
    	
    	
       // java.addSysproperty(getAntUtils().createSysProperty("weblogic.Domain",
        //        getConfiguration().getPropertyValue(WebLogicPropertySet.DOMAIN)));

        
        
    	
        
         
    	super.doStart(java);
    	
    }
    
    
}
