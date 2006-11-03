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
 * This class is a reference and describs a formal publication.
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
public class FormalPublication extends Reference {
    /**
     * Name of the author.
     */
    private String m_authorName;

    /**
     * Name of the publisher.
     */
    private String m_publisher;

    /**
     * Number pages of the publication.
     */
    private int m_pageNum;

    /**
     * @return Returns the authorName.
     */
    public String getAuthorName() {
        return m_authorName;
    }

    /**
     * @param authorName
     *            The authorName to set.
     */
    public void setAuthorName(String authorName) {
        m_authorName = authorName;
    }

    /**
     * @return Returns the pageNum.
     */
    public int getPageNum() {
        return m_pageNum;
    }

    /**
     * @param pageNum
     *            The pageNum to set.
     */
    public void setPageNum(int pageNum) {
        m_pageNum = pageNum;
    }

    /**
     * @return Returns the publisher.
     */
    public String getPublisher() {
        return m_publisher;
    }

    /**
     * @param publisher
     *            The publisher to set.
     */
    public void setPublisher(String publisher) {
        m_publisher = publisher;
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
            && object instanceof FormalPublication) {
            FormalPublication other = (FormalPublication) object;

            return ObjectUtils.nullSaveEquals(m_authorName, other.m_authorName)
                && ObjectUtils.nullSaveEquals(m_publisher,  other.m_publisher)
                && m_pageNum == other.m_pageNum;
        } else {
            return false;
        }
    }
}