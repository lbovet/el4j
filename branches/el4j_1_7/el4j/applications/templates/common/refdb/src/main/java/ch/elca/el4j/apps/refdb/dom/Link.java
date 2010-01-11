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
package ch.elca.el4j.apps.refdb.dom;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.search.annotations.Indexed;

/**
 * Link domain object. This class is a reference and describes an internet link
 * (URL).
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Martin Zeltner (MZE)
 */
@Entity
@Indexed
@Table(name = "LINKS")
@PrimaryKeyJoinColumn(name = "KEYTOREFERENCE")
public class Link extends Reference {
	/**
	 * Contains the url of a web page.
	 */
	private String m_url;
	
	/** See corresponding getter for informations. */
	private final String m_type = "Link";

	/**
	 * @return Returns the url.
	 */
	public String getUrl() {
		return m_url;
	}

	/**
	 * @param url
	 *            The url to set.
	 */
	public void setUrl(String url) {
		this.m_url = url;
	}

	/** {@inheritDoc} */
	@Transient
	@Override
	public String getType() {
		return m_type;
	}
	
}