/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU Lesser General Public License (LGPL)
 * Version 2.1. See http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.maven.plugins.eclipseserver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;


/**
 * Maven mojo to add a tomcat server and the necessary runtime to eclipse.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Jonas Hauenstein (JHN)
 * 
 * @goal tomcat6x
 */
public class Tomcat6xAdderMojo extends AbstractServerAdderMojo {

	/**
	 * Path from eclipse workspace to the tomcat preferences file.
	 */
	private static final String TOMCAT_PREFS_FILE
		= ".metadata/.plugins/org.eclipse.core.runtime/.settings/org.eclipse.jst.server.tomcat.core.prefs";
	
	/**
	 * The home / runtime directory of apache tomcat.
	 * 
	 * @parameter expression="${tomcat6x.home}" default-value="${tomcat6x.home}"
	 * @required
	 */
	protected File tomcat6xhome;
	
	/**
	 * The directory where the apache tomcat configuration files are stored.
	 * 
	 * @parameter expression="${tomcat6x.confdir}" default-value="${tomcat6x.home}/conf"
	 * @required
	 */
	protected File tomcat6xconf;
		
	/**
	 * Tomcat auto-publish-setting configuration value.
	 * 
	 * @parameter default-value="2"
	 * @required
	 */
	protected String tomcat6xAutoPublishSetting;

	/**
	 * Tomcat auto-publish-time configuration value.
	 * 
	 * @parameter default-value="1"
	 * @required
	 */
	protected String tomcat6xAutoPublishTime;
	
	/**
	 * Tomcat deployDir configuration value.
	 * 
	 * @parameter default-value="wtpwebapps"
	 * @required
	 */
	protected String tomcat6xDeployDir;
	
	/**
	 * Tomcat hostname configuration value.
	 * 
	 * @parameter default-value="localhost"
	 * @required
	 */
	protected String tomcat6xHostname;

	/**
	 * Tomcat id configuration value.
	 * 
	 * @parameter default-value="Tomcat v6.0 Server at localhost"
	 * @required
	 */
	protected String tomcat6xId;
	
	/**
	 * Tomcat name configuration value.
	 * 
	 * @parameter default-value="Apache Tomcat v6.0"
	 * @required
	 */
	protected String tomcat6xName;
	
	/**
	 * Tomcat runtime-id configuration value.
	 * 
	 * @parameter default-value="Apache Tomcat v6.0"
	 * @required
	 */
	protected String tomcat6xRuntimeId;	

	/**
	 * Tomcat runtime-type-id configuration value.
	 * 
	 * @parameter default-value="org.eclipse.jst.server.tomcat.runtime.60"
	 * @required
	 */
	protected String tomcat6xRuntimeTypeId;
	
	/**
	 * Tomcat server-type configuration value.
	 * 
	 * @parameter default-value="org.eclipse.jst.server.tomcat.60"
	 * @required
	 */
	protected String tomcat6xServerType;	

	/**
	 * Tomcat server-type-id configuration value.
	 * 
	 * @parameter default-value="org.eclipse.jst.server.tomcat.60"
	 * @required
	 */
	protected String tomcat6xServerTypeId;
	
	/**
	 * Tomcat start-timeout configuration value.
	 * 
	 * @parameter default-value="45"
	 * @required
	 */
	protected String tomcat6xStartTimeout;

	/**
	 * Tomcat stop-timeout configuration value.
	 * 
	 * @parameter default-value="15"
	 * @required
	 */
	protected String tomcat6xStopTimeout;	
	
	/**
	 * Tomcat testEnvironment configuration value.
	 * 
	 * @parameter default-value="true"
	 * @required
	 */
	protected String tomcat6xTestEnvironment;
	
	/**
	 * Tomcat timestamp configuration value.
	 * 
	 * @parameter default-value="0"
	 * @required
	 */
	protected String tomcat6xTimestamp;	

	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		
		File serversfile = new File(workspace, SERVERS_XML_FILE);
		ServersXmlConfigurator serversconfig = new ServersXmlConfigurator(serversfile);
		
		//check if server is already configured
		if (!serversconfig.checkForRuntimeID(tomcat6xRuntimeId)) {
			getLog().info("No tomcat 6x server is configured. Adding configuration...");
			
			//create new server project folder
			createServerProject(tomcat6xconf);
			getLog().info("  server project directory created");	
		
			//add apache to servers.xml
			Map<String, String> tomcatconf = new HashMap<String, String>();
			tomcatconf.put("auto-publish-setting", tomcat6xAutoPublishSetting);
			tomcatconf.put("auto-publish-time", tomcat6xAutoPublishTime);
			tomcatconf.put("configuration-id", "/" + SERVER_PROJECT_DIR + "/tomcat6x_conf_link");
			tomcatconf.put("deployDir", tomcat6xDeployDir);
			tomcatconf.put("hostname", tomcat6xHostname);
			tomcatconf.put("id", tomcat6xId);
			tomcatconf.put("name", tomcat6xId);
			tomcatconf.put("runtime-id", tomcat6xRuntimeId);
			tomcatconf.put("server-type", tomcat6xServerType);
			tomcatconf.put("server-type-id", tomcat6xServerTypeId);
			tomcatconf.put("start-timeout", tomcat6xStartTimeout);
			tomcatconf.put("stop-timeout", tomcat6xStopTimeout);
			tomcatconf.put("testEnvironment", tomcat6xTestEnvironment);
			tomcatconf.put("timestamp", tomcat6xTimestamp);
			serversconfig.addServerConfig(tomcatconf);
			getLog().info("  added server to servers.xml");
			

			//add runtime
			Map<String, String> runtimeconf = new HashMap<String, String>();
			runtimeconf.put("id", tomcat6xRuntimeId);
			runtimeconf.put("location", tomcat6xhome.getAbsolutePath());
			runtimeconf.put("name", tomcat6xName);
			runtimeconf.put("runtime-type-id", tomcat6xRuntimeTypeId);
			runtimeconf.put("timestamp", tomcat6xTimestamp);
			addRuntime(runtimeconf);
			getLog().info("  added runtime configuration to org.eclipse.wst.server.core.prefs");
			
			
			//create TOMCAT_PREFS_FILE
			Properties tp = new Properties();
			tp.setProperty("locationorg.eclipse.jst.server.tomcat.runtime.60", tomcat6xhome.getAbsolutePath());
			tp.setProperty("eclipse.preferences.version", AbstractServerAdderMojo.ECLIPSE_PREFERENCES_VERSION);
			File tpf = new File(workspace, TOMCAT_PREFS_FILE);
			
			if (!tpf.exists()) {
				try {
					tpf.createNewFile();
				} catch (IOException e) {
					throw new MojoExecutionException(
						"Failed to create file " + TOMCAT_PREFS_FILE, e);
				}
			}
			
			OutputStream o = null;
			try {
				o = new FileOutputStream(tpf);
				tp.store(o, null);
			} catch (FileNotFoundException e) {
				throw new MojoExecutionException(
					"File not found " + TOMCAT_PREFS_FILE, e);
			} catch (IOException e) {
				throw new MojoExecutionException(
					"Failed to write file " + TOMCAT_PREFS_FILE, e);
			} finally {
				IOUtils.closeQuietly(o);
			}
			getLog().info("  added configuration to org.eclipse.jst.server.tomcat.core.prefs");
			
			
			getLog().info("      done.");
			
		} else {
			getLog().info("A tomcat 6x server is already configured.");
		}
		
	}

}
