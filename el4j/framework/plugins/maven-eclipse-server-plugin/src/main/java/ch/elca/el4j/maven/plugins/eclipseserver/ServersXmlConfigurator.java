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

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
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

import org.apache.maven.plugin.MojoExecutionException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Configuration utility class for eclipse's servers.xml file.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Jonas Hauenstein (JHN)
 */
public class ServersXmlConfigurator {

	/**
	 * Path to the servers.xml file.
	 */
	private File serversXmlFile;
	
	/**
	 * DOM representation of the servers.xml file.
	 */
	private Document xmldoc = null;
	
	/**
	 * Constructor.
	 * 
	 * @param xmlFile Path to the servers.xml file
	 * @throws MojoExecutionException
	 */
	public ServersXmlConfigurator(File xmlFile) throws MojoExecutionException {
		serversXmlFile = xmlFile;
		loadXML();
	}
	
	/**
	 * Checks if a runtime with the given runtime id is already configured
	 * inside the servers.xml file.
	 * 
	 * @param runtimeID The runtime id to check
	 * @return true if runtime is already configured, false otherwise
	 */
	public boolean checkForRuntimeID(String runtimeID) {
		if (xmldoc != null) {
			Element trt = null;
			XPath xpath = XPathFactory.newInstance().newXPath();
			try {
				trt = (Element) xpath.evaluate("//server[@runtime-id='"
					+ runtimeID + "']", xmldoc, XPathConstants.NODE);
			} catch (XPathExpressionException e) {
				trt = null;
			}
			if (trt != null) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Adds a server to the servers.xml file with the given parameters.
	 * If the XML file does not yet exist, a new file is created.
	 * 
	 * @param conf The parameters to specify the newly added server
	 * @throws MojoExecutionException
	 */
	public void addServerConfig(Map<String, String> conf) throws MojoExecutionException {
		
		//check for existence of file
		if (xmldoc == null) {
			createXML();
		}
		
		Element server = xmldoc.createElement("server");
		for (Map.Entry<String, String> e : conf.entrySet()) {
			server.setAttribute(e.getKey(), e.getValue());
		}
		xmldoc.getElementsByTagName("servers").item(0).appendChild(server);
		
		storeXML();	
	}
	
	/**
	 * Loads the XML file and creates the DOM representation of it.
	 * 
	 * @throws MojoExecutionException
	 */
	private void loadXML() throws MojoExecutionException {
		if (serversXmlFile.exists()) {
			
			//check if it is a writable file
			if (!serversXmlFile.isFile() || !serversXmlFile.canWrite()) {
				throw new MojoExecutionException(
					serversXmlFile + " file is not writable.");
			}
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;
			try {
				db = dbf.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				throw new MojoExecutionException(
					"Could not create new DOM parser.", e);
			}
			try {
				xmldoc = db.parse(serversXmlFile);
			} catch (SAXException e) {
				throw new MojoExecutionException(
					"Could not parse servers.xml.", e);
			} catch (IOException e) {
				throw new MojoExecutionException(
					"Could not access servers.xml.", e);
			}
			
			//check for servers element and add if not present
			if (xmldoc.getElementsByTagName("servers").getLength() == 0) {
				Element servers = xmldoc.createElement("servers");
				xmldoc.appendChild(servers);
			}
			
		} else {
			xmldoc = null;
		}
	}
	
	/**
	 * Creates the XML file and the basic structure in the DOM representation.
	 * 
	 * @throws MojoExecutionException
	 */
	private void createXML() throws MojoExecutionException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		DOMImplementation impl;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new MojoExecutionException(
				"Could not create new DOM parser.", e);
		}
		try {
			db = dbf.newDocumentBuilder();
			impl = db.getDOMImplementation();
		} catch (ParserConfigurationException e) {
			throw new MojoExecutionException(
				"Could not create new DOM parser.", e);
		}
		
		xmldoc = impl.createDocument(null, null, null);
		Element servers = xmldoc.createElement("servers");
		xmldoc.appendChild(servers);
	}
	
	/**
	 * Stores the XML file.
	 * 
	 * @throws MojoExecutionException
	 */
	private void storeXML() throws MojoExecutionException {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = tf.newTransformer();
		} catch (TransformerConfigurationException e) {
			throw new MojoExecutionException(
				"XML transformer configuration error.", e);
		}
		
		transformer.setOutputProperty(OutputKeys.ENCODING, AbstractServerAdderMojo.XML_ENCODING);
		transformer.setOutputProperty(OutputKeys.INDENT, AbstractServerAdderMojo.XML_INDENT);
		transformer.setOutputProperty(OutputKeys.STANDALONE, AbstractServerAdderMojo.XML_STANDALONE);
		transformer.setOutputProperty(OutputKeys.VERSION, AbstractServerAdderMojo.XML_VERSION);
		
		DOMSource src = new DOMSource(xmldoc);
		Result dest = new StreamResult(serversXmlFile);
		try {
			transformer.transform(src, dest);
		} catch (TransformerException e) {
			throw new MojoExecutionException(
				"XML transformer error.", e);
		}
	}
	
}
