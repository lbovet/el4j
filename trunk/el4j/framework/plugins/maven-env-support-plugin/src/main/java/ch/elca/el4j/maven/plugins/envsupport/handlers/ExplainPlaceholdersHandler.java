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
package ch.elca.el4j.maven.plugins.envsupport.handlers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import ch.elca.el4j.maven.plugins.envsupport.AbstractEnvSupportMojo;


/**
 * A verbose env.xml handler for &lt;placeholders&gt tags.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 */
public class ExplainPlaceholdersHandler extends AbstractExplainHandler {
	/**
	 * @param mojo      the current mojo
	 */
	public ExplainPlaceholdersHandler(AbstractEnvSupportMojo mojo) {
		super(mojo);
	}
	
	/** {@inheritDoc} */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (!m_resourceHeaderPrinted) {
			m_logger.info("");
			m_logger.info("  Properties placeholders declared in "
				+ m_mojo.getArtifactNameFromResource(m_currentResource));
			m_resourceHeaderPrinted = true;
		}
		if (qName.equals("placeholder")) {
			addProperty(attributes);
		} else if (qName.equals("remove-placeholder")) {
			removeProperty(attributes);
		}
	}
}
