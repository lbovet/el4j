/**
 * 
 */
package org.codehaus.cargo.container.weblogic;



import java.io.File;

import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.Path;
import org.codehaus.cargo.container.ContainerException;
import org.codehaus.cargo.container.InstalledLocalContainer;
import org.codehaus.cargo.container.LocalContainer;
import org.codehaus.cargo.container.deployable.Deployable;
import org.codehaus.cargo.container.deployable.DeployableType;
import org.codehaus.cargo.container.deployable.WAR;
import org.codehaus.cargo.container.internal.AntContainerExecutorThread;
import org.codehaus.cargo.container.property.ServletPropertySet;
import org.codehaus.cargo.container.spi.deployer.AbstractCopyingInstalledLocalDeployer;
import org.codehaus.cargo.container.spi.deployer.AbstractInstalledLocalDeployer;
import org.codehaus.cargo.container.weblogic.internal.AbstractWebLogicInstalledLocalContainer;

/**
 * 
 * Deploying functionalities for WebLogic 10x
 * 
 * @author Frank Bitzer (FBI)
 *
 */
public class Weblogic10xInstalledLocalDeployer extends
		AbstractInstalledLocalDeployer {
	
	 /**
     * {@inheritDoc}
     * @see AbstractCopyingInstalledLocalDeployer#AbstractCopyingInstalledLocalDeployer(InstalledLocalContainer)
     */
    public Weblogic10xInstalledLocalDeployer(InstalledLocalContainer container)
    {
        super(container);
    }

    /**
     * Specifies the directory {@link org.codehaus.cargo.container.deployable.Deployable}s should
     * be copied to. For Weblogic 10x this is the <code>autodeploy</code> directory.
     *
     * @return Deployable directory
     */
    public String getDeployableDir()
    {
        return getFileHandler().append(getContainer().getConfiguration().getHome(), "autodeploy");
    }

    
    /**
     * Deploy files to WebLogic by calling weblogic.Deployer utility
     * 
     * @author Frank Bitzer (FBI)
     */
	public void deploy(Deployable deployable) {
		
		
	
        getLogger().info("Deploying [" + deployable.getFile()+ "]",
        		this.getClass().getName());

        
		try 
        {
			
				//commented out - deployment is done by using weblogic.deployer
//            FileUtils fileUtils = FileUtils.newFileUtils();
//
//            
//
//            //deploy WAR or exploded war
//            //for target dir/filename, use name of application as 
//            //received from "deployable.getContext()"
//            if ((deployable.getType() == DeployableType.WAR) 
//                   || ((WAR) deployable).isExpandedWar())
//            {
//                 
//            	
//               fileUtils.copyFile(deployable.getFile(),
//                    getFileHandler().append(appDir,  ((WAR) deployable).getContext()),
//                    null, true);
//                
//            } else {
//            	
//            	 throw new ContainerException("Deployable " 
//            			 + deployable.getFile() + " must be a WAR or"
//            			 + " exploded WAR file.");
//            	
//            }
            
			
			
			Java java = this.createWeblogicDeployerTask(deployable,true);
			
		   
	        
	        //deploy should be performed
	        java.createArg().setValue("-deploy");
	        
	        
	        
	        
	        //and name of file to deploy must be provided
	        
	        java.createArg().setValue("-source");
	        java.createArg().setValue(deployable.getFile());
	        
	        
	        //run task
	        //AntContainerExecutorThread webLogicRunner = new AntContainerExecutorThread(java);
	        //webLogicRunner.start();
			
			//dont run in a seperate thread, because we want to block until
	        //operation is complete and we want to get the exit code
	        if (java.executeJava() != 0){
	        	throw new ContainerException("weblogic.Deployer could not " +
	        			"complete operation. See message above.");
	        }
	        
	        
           
        }
        catch (Exception e)
        {
            throw new ContainerException("Failed to deploy the following " +
            		"deployable to " 
                + "Weblogic 10x:  " + deployable.getFile(), e);
        }
		
	}

	/**
	 * Redeploy.
	 * Currently, this only delegates to deploy(), because redeployment of whole apps
	 * is not supported in Weblogic
	 */
	public void redeploy(Deployable deployable) {
		
		
		 getLogger().info("Redeploying [" + deployable.getFile()+ "]",
	        		this.getClass().getName());
		
		try 
        {
		
//			Java java = this.createWeblogicDeployerTask(deployable);
//			
//		   
//	        
//	        //redeploy should be performed
//	        java.createArg().setValue("-redeploy");
//	        
//	        
//	        //and name of file to redeploy must be provided
//	       
//	        java.createArg().setValue(deployable.getFile());
//	        
//	        
//	        //run task
//	        AntContainerExecutorThread webLogicRunner = new AntContainerExecutorThread(java);
//	        webLogicRunner.start();
	    	
			
			//-redeploy option of weblogic.Deployer doesn't work with whole 
			//applications such as wars, but only with single files or dirs as 
			//parameter
			//so use normal deploy command instead
			
			
			deploy(deployable);
			
			
        } catch (Exception e)
        {
            throw new ContainerException("Failed to redeploy the following deployable from " 
                + "Weblogic 10x: " + deployable.getFile(), e);
        }
		
		
	}

	/**
	 * Undeploys deployable by calling weblogic.Deployer utility with proper parameters
	 * 
	 * @author Frank Bitzer (FBI)
	 */
	public void undeploy(Deployable deployable) {
		
		 getLogger().info("Undeploying [" + deployable.getFile()+ "]",
	        		this.getClass().getName());
		
		try 
        {
		
			Java java = this.createWeblogicDeployerTask(deployable,false);
			
		   
	        
	        //undeploy should be performed
	        java.createArg().setValue("-undeploy");
	        
	        
	        //run task
	        //AntContainerExecutorThread webLogicRunner = new AntContainerExecutorThread(java);
	        //webLogicRunner.start();
	        
	       
	      //dont run in a seperate thread, because we want to block until
	       //operation is complete and we want to get the exit code
	       if (java.executeJava() != 0){
	        	throw new ContainerException("weblogic.Deployer could not " +
	        			"complete operation. See message above.");
	       }
	        
	    	
        } catch (Exception e)
        {
            throw new ContainerException("Failed to undeploy the following deployable from " 
                + "Weblogic 10x: " + deployable.getFile(), e);
        }
	    
	    	
	}
	
	/**
	 * 
	 * Creates a java Ant task for weblogic.Deployer utility and sets
	 * common parameters and classpath needed for all deployment tasks
	 * 
	 * 
	 * 
	 * @param deployable
	 * @param isDeploy true, if planed action is -deploy, false else
	 * @return the java with necessary common parameters set
	 */
	private Java createWeblogicDeployerTask(Deployable deployable,
			boolean isDeploy){
		
		LocalContainer theContainer = this.getContainer();
	    
	    
	    if (!(theContainer instanceof 
	    		org.codehaus.cargo.container.weblogic.internal.AbstractWebLogicInstalledLocalContainer)){

	    	throw new ContainerException("Invalid Container.");
	    
	    	
	    }
	    
	    	
	    AbstractWebLogicInstalledLocalContainer myContainer 
	    		= (AbstractWebLogicInstalledLocalContainer) theContainer;
	    	
	    	
	    //create java task to interact with weblogic.deployer	
	    Java java = myContainer.createJavaTask();
	    	
	    java.setClassname("weblogic.Deployer");
	    
	    java.setFailonerror(true);
        java.setFork(true);
        
        
	    
	    setClasspath(java, myContainer.getHome());
	    
	    
        setParameters(java, deployable,isDeploy);
        
        
        return java;
		
	}
	
	
	
	/**
	 * Adds weblogic.jar and patches to classpath for java task
	 * 
	 * @param java
	 * @param homeDir home directory of weblogic installation 
	 * (not of current domain/server!)
	 */
	private void setClasspath(Java java, String homeDir){
		
		File serverDir = new File(homeDir, "server");
		
		
        Path classpath = java.createClasspath();
        classpath.createPathElement().setLocation(new File(serverDir, "lib/weblogic_sp.jar"));
        classpath.createPathElement().setLocation(new File(serverDir, "lib/weblogic.jar"));

	}
	
	
	/**
	 * Sets the follwing parameters to a Java task
	 * 
	 *  * adminurl
	 *  * user
	 *  * password
	 *  * name (of application to deploy/undeploy/redeploy)
	 *  
	 * @param java the task to edit
	 * @param deployable the deployable with which the task is performed. this is used to
	 * determine name of web application
	 * @param isDeploy true, if planed action is -deploy, false else
	 */
	private void setParameters(Java java, Deployable deployable, 
			boolean isDeploy){
		
		
		//java.createArg().setValue("-debug");
		
		
		java.createArg().setValue("-adminurl");
        java.createArg().setValue("t3://localhost:"
            + this.getContainer().getConfiguration().getPropertyValue(ServletPropertySet.PORT));
        
        java.createArg().setValue("-user");
        java.createArg().setValue(
        		this.getContainer().getConfiguration().getPropertyValue(WebLogicPropertySet.ADMIN_USER));
        
        java.createArg().setValue("-password");
        java.createArg().setValue(
        		this.getContainer().getConfiguration().getPropertyValue(WebLogicPropertySet.ADMIN_PWD));

        
        //if possible, provide name for webapp
        //else use filename
        //but name param must be specified so that module can be identified again
        //when undeploying
        String appName = "";
        if (deployable.getType() == DeployableType.WAR){
        	 	
        	appName = ((WeblogicWAR)deployable).getContext();
        	
        	getLogger().info("Name of application: " + appName,
            		this.getClass().getName());
        	
        	//we now must save the appname as context-root in the weblogic.xml 
        	//so that it is actually used by weblogic
        	if (isDeploy) {
        		((WeblogicWAR)deployable).setWeblogicContext(appName);
        	}
        	
        } else {
        	
        	appName = getFileHandler().getName(deployable.getFile());
        	
        	getLogger().info("Unable to get name of web application. " +
        			"Using filename: " + appName,
            		this.getClass().getName());
        	
        }
        
        java.createArg().setValue("-name");
    	java.createArg().setValue(appName);
   
        
       
	}
	

}
