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
package ch.elca.el4j.services.monitoring.jmx.display;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility for displaying HTML information and creating strings out of it.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author David Bernhard (DBD)
 */
public class HtmlDisplayManager implements DisplayManager {

	// Checkstyle: StaticVariableNameCheck off
	
	/** Tag to wrap around title. */
	private static final String TITLE_TAG = "h2";
	
	/** Tag to wrap around section headings. */
	private static final String SECTION_TAG = "h3";
	
	// Checkstyle: StaticVariableNameCheck on
	
	/**
	 * Title for this page.
	 */
	private String m_title;
	
	/**
	 * Holds the content (sections) of this page.
	 */
	private List<Section> m_content;
	
	/**
	 * Default constructor.
	 */
	public HtmlDisplayManager() {
		m_content = new LinkedList<Section>();
		m_title = "";
	}
	
	/** {@inheritDoc} */
	public void setTitle(String title) {
		m_title = title;
	}
	
	/** {@inheritDoc} */
	public void addSection(Section section) {
		m_content.add(section);
	}
	
	/**
	 * Creates a hyperlink.
	 * @param text The link's text.
	 * @param target The link's target.
	 * @return A Html hyperlink.
	 */
	private String linkTo(String text, String target) {
		return "<a href=\"" + target + "\">" + text + "</a>";
	}

	/**
	 * @param name The section name.
	 * @return The name modified to use as an anchor target.
	 */
	private String linkName(String name) {
		Pattern p = Pattern.compile("[^a-zA-Z0-9_]");
		Matcher m = p.matcher(name);
		return m.replaceAll("");
	}
	
	/** {@inheritDoc} */
	public String getPage() {
		String page = "";
		
		// Title
		if (!m_title.equals("")) {
			page += tag(TITLE_TAG, m_title) + "\n";
		}
		
		// Index
		Iterator<Section> i1 = m_content.iterator();
		String index = "";
		while (i1.hasNext()) {
			String name = i1.next().getName();
			index += tag("i", linkTo(name, "#" + linkName(name)) + " ");
		}
		page += index + "\n";
		
		// Sections
		Iterator<Section> i = m_content.iterator();
		while (i.hasNext()) {
			Section section = i.next();
			page += tag(SECTION_TAG, section.getName())
				+ "<a name=\"" + linkName(section.getName()) + "\" />\n";
			page += section.getContent() + "\n";
		}
			
		return page;
	}
	
	/**
	 * Wrap some text in a tag.
	 * @param tag The tag to wrap in.
	 * @param value The text.
	 * @return <code>&lt;tag&gt;</code>text<code>&lt;/tag&gt;</code>
	 */
	public static String tag(String tag, String value) {
		return "<" + tag + ">" + value + "</" + tag + ">";
	}
	
	/**
	 * @param value The text.
	 * @param tags A list of tags.
	 * @return the text wrapped recursively in the tags.
	 */
	public static String tagRecursive(String value, String ... tags) {
		String result = value;
		for (int i = tags.length - 1; i >= 0; i--) {
			result = tag(tags[i], result);
		}
		return result;
	}

}
