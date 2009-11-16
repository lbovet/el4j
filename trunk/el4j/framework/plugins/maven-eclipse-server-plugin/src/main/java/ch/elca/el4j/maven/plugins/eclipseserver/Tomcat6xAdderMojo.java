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
	 * Runtime ID of apache tomcat server.
	 */
	private static final String TOMCAT_RUNTIME_ID = "Apache Tomcat v6.0";
	
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
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		
		File serversfile = new File(workspace, SERVERS_XML_FILE);
		ServersXmlConfigurator serversconfig = new ServersXmlConfigurator(serversfile);
		
		//check if server is already configured
		if (!serversconfig.checkForRuntimeID(TOMCAT_RUNTIME_ID)) {
			getLog().info("No tomcat 6x server is configured. Adding configuration...");
			
			//create new server project folder
			createServerProject(tomcat6xconf);
			getLog().info("  server project directory created");	
		
			//add apache to servers.xml
			Map<String, String> tomcatconf = new HashMap<String, String>();
			tomcatconf.put("auto-publish-setting", "2");
			tomcatconf.put("auto-publish-time", "1");
			tomcatconf.put("configuration-id", "/" + SERVER_PROJECT_DIR + "/tomcat6x_conf_link");
			tomcatconf.put("deployDir", "wtpwebapps");
			tomcatconf.put("hostname", "localhost");
			tomcatconf.put("id", "Tomcat v6.0 Server at localhost");
			tomcatconf.put("name", "Tomcat v6.0 Server at localhost");
			tomcatconf.put("runtime-id", TOMCAT_RUNTIME_ID);
			tomcatconf.put("server-type", "org.eclipse.jst.server.tomcat.60");
			tomcatconf.put("server-type-id", "org.eclipse.jst.server.tomcat.60");
			tomcatconf.put("start-timeout", "45");
			tomcatconf.put("stop-timeout", "15");
			tomcatconf.put("testEnvironment", "true");
			tomcatconf.put("timestamp", "0");
			serversconfig.addServerConfig(tomcatconf);
			getLog().info("  added server to servers.xml");
			

			//add runtime
			Map<String, String> runtimeconf = new HashMap<String, String>();
			runtimeconf.put("id", TOMCAT_RUNTIME_ID);
			runtimeconf.put("location", tomcat6xhome.getAbsolutePath());
			runtimeconf.put("name", "Apache Tomcat v6.0");
			runtimeconf.put("runtime-type-id", "org.eclipse.jst.server.tomcat.runtime.60");
			runtimeconf.put("timestamp", "0");
			addRuntime(runtimeconf);
			getLog().info("  added runtime configuration to org.eclipse.wst.server.core.prefs");
			
			
			//create TOMCAT_PREFS_FILE
			Properties tp = new Properties();
			tp.setProperty("locationorg.eclipse.jst.server.tomcat.runtime.60", tomcat6xhome.getAbsolutePath());
			tp.setProperty("eclipse.preferences.version", "1");
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
