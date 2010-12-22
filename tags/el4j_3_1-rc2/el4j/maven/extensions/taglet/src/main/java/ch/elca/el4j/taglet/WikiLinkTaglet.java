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
package ch.elca.el4j.taglet;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;


/**
 * 
 * This is a javadoc taglet making it possible to use a @wikiLink tag in javadoc commments for classes.
 * Usage (default): @wikiLink MavenBuildSystem will create a link to 
 * http://wiki.elca.ch/twiki/el4j/bin/view/EL4J/MavenBuildSystem
 * Usage (using properties file): @wikiLink el4j  MavenBuildSystem will check in the base_urls.properties file
 * for the property el4j and use this as base. 
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 * 
 * @wikiLink CodingGuidelines
 *
 * @author Daniel Thomas (DTH)
 */
public class WikiLinkTaglet implements Taglet {

	/**
	 * The name of this taglet (will be used as @wikiLink).
	 */
	private static final String NAME = "wikiLink";
	
	/**
	 * Default base url.
	 */
	private static final String DEFAULT_BASE_URL = "http://wiki.elca.ch/twiki/el4j/bin/view/EL4J/";
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean inConstructor() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean inField() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean inMethod() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean inOverview() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean inPackage() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean inType() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isInlineTag() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString(Tag tag) {
		if (tag == null) {
			return "";
		} else {
			String url = createLink(tag);
			return (new StringBuilder().append("<dt><b>Link to wiki:</b></dt>").append(url)).toString();
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString(Tag[] tag) {
		if (tag == null || tag.length == 0) {
			return "";
		}
		
		StringBuilder response = new StringBuilder("<dt><b>Links to wiki:</b></dt>");
		
		
		// just add several lines of wiki links
		for (int i = 0; i < tag.length; i++) {
			String url = createLink(tag[i]);	
			if (url != null) {
				response.append(url);
			}
		}

		return response.toString();
	}
	
	/**
	 * Creates a nice string which contains the link specified in the tag, wrapped in a dd tag.
	 * 
	 * @param tag is the tag from which we get the information for the link.
	 * 
	 * @return a string containing the link specified in the tag in html syntax.
	 */	
	private String createLink(Tag tag) {
		String[] parts = tag.text().split("[\\s]+");

		String base = null;
		String title = null;
		
		if (parts.length == 1) {
			base = DEFAULT_BASE_URL;
			title = tag.text();
		} else if (parts.length == 2) {
			base = getAppropriateBase(parts[0]);
			title = parts[1].trim();
		} else {
			logWarning("@wikiLink: Incorrect usage of @wikiLink taglet, to many parameters.");
		}
		
		// if title is empty, return empty string
		if (title == null) {
			logWarning("@wikiLink: No title in wiki passed to taglet");
			return "";
		}
		
		return (new StringBuilder())
		.append("<dd><a href=\"").append(base).append(title).append("\">").append(title).append("</a></dd>").toString();

		
	}
	/**
	 * Logs a warning, at the moment it just prints to System.out.
	 * 
	 * 
	 * @param warning is the string to print 
	 */
	private void logWarning(String warning) {
		System.out.println("[WARNING]: " + warning);
	}
	
	

	/**
	 * Checks the properties file for the appropriate base url.
	 * 
	 * @param key
	 *            is the identifier/key for the base url
	 * @return a nice base url, which should be a valid url to a wiki
	 */

	private String getAppropriateBase(String key) {
		Properties props = new Properties();
		InputStream stream = WikiLinkTaglet.class.getClassLoader().getResourceAsStream("base_urls.properties");
		try {
			props.load(stream);
		} catch (IOException e) {
			logWarning("@wikiLink: Couldn't find file base_urls.properties.");
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					logWarning("@wikiLink: Couldn't close InputStream.");
				}
			}
		}
		// try and get the right value from the properties file
		return props.getProperty(key, DEFAULT_BASE_URL);
		
	}
	
	/**
	 * Registers this taglet.
	 * Needed in every taglet.
	 * 
	 * @param tagletMap the map of taglets
	 */
	public static void register(Map tagletMap) {
		WikiLinkTaglet tag = new WikiLinkTaglet();
		Taglet t = (Taglet) tagletMap.get(tag.getName());
		if (t != null) {
			tagletMap.remove(tag.getName());
		}
		tagletMap.put(tag.getName(), tag);
	}

}
