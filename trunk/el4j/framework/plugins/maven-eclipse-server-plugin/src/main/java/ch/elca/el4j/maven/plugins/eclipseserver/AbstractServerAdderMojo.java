/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2009 by ELCA Informatique SA, Av. de la Harpe 22-24,
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import ch.elca.el4j.maven.plugins.AbstractSlf4jEnabledMojo;

/**
 * Abstract mojo to add servers and corresponding runtimes to eclipse.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Jonas Hauenstein (JHN)
 */
public abstract class AbstractServerAdderMojo extends AbstractSlf4jEnabledMojo {
	
	/**
	 * Path from eclipse workspace to the servers.xml configuration file.
	 */
	protected static final String SERVERS_XML_FILE = ".metadata/.plugins/org.eclipse.wst.server.core/servers.xml";
	
	/**
	 * Path from eclipse workspace to the runtime configuration preferences file.
	 */
	protected static final String SERVERS_RUNTIME_PREFS_FILE
		= ".metadata/.plugins/org.eclipse.core.runtime/.settings/org.eclipse.wst.server.core.prefs";

	/**
	 * Name of the Servers project directory inside the eclipse workspace.
	 */
	protected static final String SERVER_PROJECT_DIR = "Servers";
	
	/**
	 * The eclipse workspace directory.
	 * 
	 * @parameter expression="${eclipse.workspace}" default-value="${eclipse.workspace}"
	 * @required
	 */
	protected File workspace;
	
	/**
	 * Adds a server runtime with the given parameters to eclipse.
	 * It changes the content of the two necessary files inside
	 * the eclipse workspace.
	 * 
	 * @param runtimeParameter The parameters for the new runtime
	 * @throws MojoExecutionException
	 */
	protected void addRuntime(Map<String, String> runtimeParameter) throws MojoExecutionException {
		
		Properties rtc = new Properties();
		File rtcf = new File(workspace, SERVERS_RUNTIME_PREFS_FILE);
		
		if (rtcf.exists()) {
			//load properties from file if it exists
			if (!rtcf.isFile() || !rtcf.canWrite()) {
				throw new MojoExecutionException(
					SERVERS_RUNTIME_PREFS_FILE + " file is not writable.");
			}
			FileInputStream is = null;
			try {
				is = new FileInputStream(rtcf);
				rtc.load(is);
			} catch (FileNotFoundException e) {
				throw new MojoExecutionException(
					"File not found " + SERVERS_RUNTIME_PREFS_FILE, e);
			} catch (IOException e) {
				throw new MojoExecutionException(
					"Failed to read file " + SERVERS_RUNTIME_PREFS_FILE, e);
			} finally {
				IOUtils.closeQuietly(is);
			}
			
		} else {
			try {
				rtcf.createNewFile();
			} catch (IOException e) {
				throw new MojoExecutionException(
					"Failed to create file " + SERVERS_RUNTIME_PREFS_FILE, e);
			}
		}
		
		//check for required parameters
		if (!rtc.containsKey("module-start-timeout")) {
			rtc.put("module-start-timeout", "300000");
		}
		if (!rtc.containsKey("eclipse.preferences.version")) {
			rtc.put("eclipse.preferences.version", "1");
		}
		
		//check for runtime config
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new MojoExecutionException(
				"XML parser configuration error", e);
		}
		
		Document xmldoc;
		if (rtc.containsKey("runtimes")) {
			//load xml with dom
			try {
				InputSource is = new InputSource();
				is.setCharacterStream(new StringReader(rtc.getProperty("runtimes")));
				xmldoc = db.parse(is);
			} catch (SAXException e) {
				throw new MojoExecutionException(
					"SAX parser error", e);
			} catch (IOException e) {
				throw new MojoExecutionException(
					"XML IO Exception error", e);
			}
		} else {
			//create new xml with dom
			DOMImplementation impl = db.getDOMImplementation();
			xmldoc = impl.createDocument(null, null, null);
			Element runtimes = xmldoc.createElement("runtimes");
			xmldoc.appendChild(runtimes);
		}
		
		//check for already configured runtime in given location and...
		Element trt = null;
		if (runtimeParameter.containsKey("location")) {
			XPath xpath = XPathFactory.newInstance().newXPath();
			try {
				trt = (Element) xpath.evaluate("//runtime[@location='"
					+ runtimeParameter.get("location") + "']", xmldoc, XPathConstants.NODE);
			} catch (XPathExpressionException e) {
				trt = null;
			}
		}
		
		//...add runtime only, if it has not been found in XML structure
		if (trt == null) {
			Element runtime = xmldoc.createElement("runtime");
			for (Map.Entry<String, String> e : runtimeParameter.entrySet()) {
				runtime.setAttribute(e.getKey(), e.getValue());
			}
			xmldoc.getElementsByTagName("runtimes").item(0).appendChild(runtime);
		}
		
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = tf.newTransformer();
		} catch (TransformerConfigurationException e) {
			throw new MojoExecutionException(
				"XML transformer configuration error.", e);
		}
		
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.STANDALONE, "no");
		transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
		
		//convert dom to string
		StringWriter sw = new StringWriter();
		StreamResult result = new StreamResult(sw);
		DOMSource source = new DOMSource(xmldoc);
		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			throw new MojoExecutionException(
				"XML transformer error.", e);
		}
		
		rtc.remove("runtimes");
		rtc.put("runtimes", sw.toString());
		
		//store properties
		OutputStream o = null;
		try {
			o = new FileOutputStream(rtcf);
			rtc.store(o, null);
		} catch (FileNotFoundException e) {
			throw new MojoExecutionException(
				"File not found " + SERVERS_RUNTIME_PREFS_FILE, e);
		} catch (IOException e) {
			throw new MojoExecutionException(
				"Failed to write file " + SERVERS_RUNTIME_PREFS_FILE, e);
		} finally {
			IOUtils.closeQuietly(o);
		}
		
	}
	
	/**
	 * Creates a new Project directory for the server Settings.
	 * If tomcateConfDir is not null, it also adds a direct symlink
	 * (linkedResources) to the specified tomcat configurations directory.
	 * 
	 * @param tomcatConfDir Path to the tomcat configuration directory
	 * @throws MojoExecutionException
	 */
	public void createServerProject(File tomcatConfDir) throws MojoExecutionException {
		
		File projectfile = new File(workspace, SERVER_PROJECT_DIR + "/.project");
		projectfile.getParentFile().mkdirs();
		
		//create project file with symlink
		VelocityContext context = new VelocityContext();
		if (tomcatConfDir != null) {
			context.put("tomcatconflink", tomcatConfDir.getAbsolutePath().replace("\\", "/"));
		}
		applyTemplate(context, projectfile, "serverprojectfile.vm");
		
		//create config file in project dir
		File settings = new File(workspace, SERVER_PROJECT_DIR + "/.settings/"
			+ SERVERS_RUNTIME_PREFS_FILE.substring(SERVERS_RUNTIME_PREFS_FILE.lastIndexOf("/")));
		
		settings.getParentFile().mkdirs();
		
		if (!settings.exists()) {
			try {
				settings.createNewFile();
			} catch (IOException e) {
				throw new MojoExecutionException(
					"Failed to create file " + settings.getAbsolutePath(), e);
			}
		}
		
		Properties p = new Properties();
		p.put("org.eclipse.wst.server.core.isServerProject", "true");
		p.put("eclipse.preferences.version", "1");
		
		OutputStream o = null;
		try {
			o = new FileOutputStream(settings);
			p.store(o, null);
		} catch (FileNotFoundException e) {
			throw new MojoExecutionException(
				"File not found " + settings.getAbsolutePath(), e);
		} catch (IOException e) {
			throw new MojoExecutionException(
				"Failed to write file " + settings.getAbsolutePath(), e);
		} finally {
			IOUtils.closeQuietly(o);
		}
		
	}

	/**
	 * Uses a Template class (and template files) to automatically write the
	 * parsed template to a file.
	 * 
	 * @param context HashMap holding the configuration details
	 * @param out The file we want to write to
	 * @param template The name of the templates file
	 * @throws MojoExecutionException
	 */
	
	protected void applyTemplate(VelocityContext context, File out, String template) throws MojoExecutionException {
		out.getParentFile().mkdirs();
		try {
			Writer configWriter = new FileWriter(out);
			BufferedReader templateReader = new BufferedReader(new InputStreamReader(
				AbstractServerAdderMojo.class.getResourceAsStream("/" + template)));
			Velocity.evaluate(context, configWriter, "projectfile", templateReader);
			configWriter.close();
		} catch (IOException e) {
			throw new MojoExecutionException("Unable to write file " + out.getAbsolutePath(), e);
		}
	}
	

}
