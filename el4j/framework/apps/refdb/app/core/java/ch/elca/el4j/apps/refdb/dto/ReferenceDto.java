/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU General Public License (GPL) Version 2.0.
 * http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.apps.refdb.dto;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import ch.elca.el4j.services.persistence.generic.dto.AbstractIntKeyIntOptimisticLockingDto;
import ch.elca.el4j.util.codingsupport.ObjectUtils;

/**
 * This is the base reference class.
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
public class ReferenceDto extends AbstractIntKeyIntOptimisticLockingDto {
    /**
     * Name of the reference (book title, ...).
     */
    private String m_name;

    /**
     * Hash value contains a reference code (library reference code, ...).
     */
    private String m_hashValue;

    /**
     * Short description of the reference (short summary, comment on the format
     * of the document, ...).
     */
    private String m_description;

    /**
     * Version of the document.
     */
    private String m_version;

    /**
     * Flag for a reference (can be interpreted as a kind of 'to be defined').
     * Default value is true.
     */
    private boolean m_incomplete = true;

    /**
     * Timestamp when does the reference has been inserted (created
     * automatically).
     */
    private Timestamp m_whenInserted;

    /**
     * Date of the referenced document (publication date, last update, ...).
     */
    private Date m_date;

    /**
     * Set of keywords for this reference.
     */
    private List m_keywords;

    /**
     * @return Returns the date.
     */
    public Date getDate() {
        return m_date;
    }

    /**
     * @param date
     *            The date to set.
     */
    public void setDate(Date date) {
        if (date != null) {
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.set(Calendar.MILLISECOND, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.HOUR, 0);
            c.set(Calendar.AM_PM, Calendar.AM);
            date.setTime(c.getTimeInMillis());
            m_date = date;
        } else {
            m_date = null;
        }
    }

    /**
     * @return Returns the description.
     */
    public String getDescription() {
        return m_description;
    }

    /**
     * @param description
     *            The description to set.
     */
    public void setDescription(String description) {
        m_description = description;
    }

    /**
     * @return Returns the hashValue.
     */
    public String getHashValue() {
        return m_hashValue;
    }

    /**
     * @param hashValue
     *            The hashValue to set.
     */
    public void setHashValue(String hashValue) {
        m_hashValue = hashValue;
    }

    /**
     * @return Returns the incomplete.
     */
    public boolean isIncomplete() {
        return m_incomplete;
    }

    /**
     * @param incomplete
     *            The incomplete to set.
     */
    public void setIncomplete(boolean incomplete) {
        m_incomplete = incomplete;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return m_name;
    }

    /**
     * @param name
     *            The name to set.
     */
    public void setName(String name) {
        m_name = name;
    }

    /**
     * @return Returns the version.
     */
    public String getVersion() {
        return m_version;
    }

    /**
     * @param version
     *            The version to set.
     */
    public void setVersion(String version) {
        m_version = version;
    }

    /**
     * @return Returns the whenInserted.
     */
    public Timestamp getWhenInserted() {
        if (m_whenInserted == null) {
            m_whenInserted = new Timestamp(System.currentTimeMillis());
        }
        return m_whenInserted;
    }

    /**
     * @param whenInserted
     *            The whenInserted to set.
     */
    public void setWhenInserted(Timestamp whenInserted) {
        m_whenInserted = whenInserted;
    }

    /**
     * @return Returns the keywords.
     */
    public List getKeywords() {
        return m_keywords;
    }

    /**
     * @param keywords
     *            The keywords to set.
     */
    public void setKeywords(List keywords) {
        m_keywords = keywords;
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
            && object instanceof ReferenceDto) {
            ReferenceDto other = (ReferenceDto) object;

            return ObjectUtils.nullSaveEquals(m_name, other.m_name)
                && ObjectUtils.nullSaveEquals(m_hashValue, other.m_hashValue)
                && ObjectUtils.nullSaveEquals(
                    m_description, other.m_description)
                && ObjectUtils.nullSaveEquals(m_version, other.m_version)
                && m_incomplete == other.m_incomplete
                && org.springframework.util.ObjectUtils.nullSafeEquals(
                    m_whenInserted, other.m_whenInserted)
                && org.springframework.util.ObjectUtils.nullSafeEquals(
                    m_date, other.m_date);
        } else {
            return false;
        }
    }
}

