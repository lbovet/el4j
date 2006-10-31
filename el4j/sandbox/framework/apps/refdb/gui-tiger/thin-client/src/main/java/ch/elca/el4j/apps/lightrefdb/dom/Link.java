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
package ch.elca.el4j.apps.lightrefdb.dom;

import javax.persistence.Entity;

import ch.elca.el4j.util.codingsupport.ObjectUtils;

/**
 * This class is a reference and describs an internet link (URL).
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Martin Zeltner (MZE)
 */
@Entity
public class Link extends Reference {
    /**
     * Contains the url of a web page.
     */
    private String m_url;

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

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return super.hashCode();
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean equals(Object object) {
        if (super.equals(object)
            && object instanceof Link) {
            Link other = (Link) object;
            return ObjectUtils.nullSaveEquals(m_url, other.m_url);
        } else {
            return false;
        }
    }
}