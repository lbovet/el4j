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

import java.util.Map;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;

/**
 * The <code>@svnLink</code> taglet.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 */
public class SvnLinkTaglet implements Taglet {
	/**
	 * The tag name.
	 */
	private static final String NAME = "svnLink";

	/** {@inheritDoc} */
	public String getName() {
		return NAME;
	}

	/** {@inheritDoc} */
	public boolean inField() {
		return false;
	}

	/** {@inheritDoc} */
	public boolean inConstructor() {
		return false;
	}

	/** {@inheritDoc} */
	public boolean inMethod() {
		return false;
	}

	/** {@inheritDoc} */
	public boolean inOverview() {
		return true;
	}

	/** {@inheritDoc} */
	public boolean inPackage() {
		return false;
	}

	/** {@inheritDoc} */
	public boolean inType() {
		return true;
	}

	/** {@inheritDoc} */
	public boolean isInlineTag() {
		return false;
	}

	/**
	 * Register this Taglet.
	 * 
	 * @param tagletMap
	 *            the map to register this tag to.
	 */
	@SuppressWarnings("unchecked")
	public static void register(Map tagletMap) {
		SvnLinkTaglet tag = new SvnLinkTaglet();
		Taglet t = (Taglet) tagletMap.get(tag.getName());
		if (t != null) {
			tagletMap.remove(tag.getName());
		}
		tagletMap.put(tag.getName(), tag);
	}

	/** {@inheritDoc} */
	public String toString(Tag tag) {
		String[] parts = tag.text().split(";");
		String revision = trimSvnStuff(parts[0]);
		String date = trimSvnStuff(parts[1]);
		String author = trimSvnStuff(parts[2]);
		String url = trimSvnStuff(parts[3]);
		String className = url.substring(url.lastIndexOf("/") + 1, url.length() - ".java".length());

		return "<dt><b>File-location:</b></dt><dd>" + "<a href=\"" + url + "\">" + className + "</a></dd>"
			+ "<dt><b>Last check-in date:</b></dt><dd>" + date + " <b> by </b> " + author
			+ " <b> for revision </b> " + revision + " </dd>";
	}

	/** {@inheritDoc} */
	public String toString(Tag[] tags) {
		if (tags.length == 0) {
			return null;
		}
		StringBuilder builder = new StringBuilder();
		for (Tag tag : tags) {
			builder.append(toString(tag));
		}
		return builder.toString();
	}

	/**
	 * @param input    the raw svn input string
	 * @return         the data
	 */
	private String trimSvnStuff(String input) {
		return input.substring(input.indexOf(":") + 2, input.length() - 1).trim();
	}
}
