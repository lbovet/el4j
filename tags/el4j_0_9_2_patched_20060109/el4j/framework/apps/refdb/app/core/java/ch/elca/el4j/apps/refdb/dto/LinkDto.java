/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * This program is published under the GNU General Public License (GPL) license.
 * http://www.gnu.org/licenses/gpl.txt
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.apps.refdb.dto;

import ch.elca.el4j.util.codingsupport.ObjectUtils;

/**
 * This class is a reference and describs an internet link (URL).
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Martin Zeltner (MZE)
 */
public class LinkDto extends ReferenceDto {
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
            && object instanceof LinkDto) {
            LinkDto other = (LinkDto) object;
            return ObjectUtils.nullSaveEquals(m_url, other.m_url);
        } else {
            return false;
        }
    }
}