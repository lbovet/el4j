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

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;


import org.hibernate.annotations.ForceDiscriminator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Type;
import org.hibernate.validator.AssertTrue;

import org.hibernate.validator.NotNull;

import ch.elca.el4j.apps.keyword.dom.Keyword;
import ch.elca.el4j.services.persistence.generic.dto.AbstractIntKeyIntOptimisticLockingDto;
import ch.elca.el4j.util.codingsupport.ObjectUtils;
import ch.elca.el4j.util.dom.annotations.MemberOrder;

/**
 * Reference domain object. This is the base reference class and describes a
 * source of information.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 * @author Alex Mathey (AMA)
 */
@MemberOrder({
    "name",
    "hashValue",
    "description",
    "version",
    "incomplete",
    "whenInserted",
    "date",
    "keywords"
})
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING,
    name = "CLASSTYPE")
@DiscriminatorValue("REFERENCE")
@Table(name = "REFERENCESTABLE")
@SequenceGenerator(name = "keyid_generator", 
    sequenceName = "reference_sequence")
public class Reference extends AbstractIntKeyIntOptimisticLockingDto {
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
    private Set<Keyword> m_keywords = new HashSet<Keyword>();

    /**
     * Set of annotations for this reference.
     */
    private Set<Annotation> m_annotations;
    
    /**
     * Set of files for this reference.
     */
    private Set<File> m_files;
    
    /**
     * Set of file descriptor views for this reference.
     */
    private Set<FileDescriptorView> m_fileDescriptorViews;
    
    /**
     * @return Returns the date.
     */
    @Column(name = "DOCUMENTDATE")
    //FBI: in this case, type is necessary for hibernate to parse date correctly
    @Type(type = "date")
    public Date getDate() {
        return m_date;
    }

    /**
     * @param date
     *            The date to set.
     */
    //FBI: in this case, type is necessary for hibernate to parse date correctly
    @Type(type = "date")
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

    //Checkstyle: MagicNumber off
    
    /**
     * @return Returns the name.
     */
    @NotNull
    //@Length(min = 3)
    public String getName() {
        return m_name;
    }

    // Checkstyle: MagicNumber on
    
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
    @OneToMany (
        cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
    @JoinTable(
            name = "REFERENCEKEYWORDRELATIONSHIPS",
            joinColumns = { @JoinColumn(name = "KEYREFERENCE") },
            inverseJoinColumns = { @JoinColumn(name = "KEYKEYWORD") }
    )
    @LazyCollection(value = LazyCollectionOption.FALSE)
    public Set<Keyword> getKeywords() {
        return m_keywords;
    }

    /**
     * @param keywords
     *            The keywords to set.
     */
    public void setKeywords(Set<Keyword> keywords) {
        m_keywords = keywords;
    }
    
    /**
     * @return Returns the set of annotations for this reference (only used if
     *         Hibernate is used to perform ORM).
     */
    @Transient
    public Set<Annotation> getAnnotations() {
        return m_annotations;
    }
    
    /**
     * @param annotations
     *            The set of annotations for this reference (only used if
     *            Hibernate is used to perform ORM).
     */
    public void setAnnotations(Set<Annotation> annotations) {
        m_annotations = annotations;
    }
    
    /**
     * @return Returns the set of files for this reference (only used if
     *         Hibernate is used to perform ORM).
     */
    @Transient
    public Set<File> getFiles() {
        return m_files;
    }
    
    /**
     * @param files
     *            The set of files for this reference (only used if
     *            Hibernate is used to perform ORM).
     */
    public void setFiles(Set<File> files) {
        m_files = files;
    }
    
    /**
     * @return Returns the set of file descriptor views for this reference (only
     *         used if Hibernate is used to perform ORM).
     */
    @Transient
    public Set<FileDescriptorView> getFileDescriptorViews() {
        return m_fileDescriptorViews;
    }
    
    /**
     * @param fileDescriptorViews
     *            The set of file descriptor views for this reference (only used
     *            if Hibernate is used to perform ORM).
     */
    public void setFileDescriptorViews(Set<FileDescriptorView>
        fileDescriptorViews) {
        m_fileDescriptorViews = fileDescriptorViews;
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
            && object instanceof Reference) {
            Reference other = (Reference) object;

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
    
    /**
     * Checks whether the reference is valid. Should always be true.
     * @return true if the reference is valid, false otherwise
     */
    @AssertTrue
    public boolean invariant() {
        // Ensure that the creation date of the referenced document is
        // smaller than its insertion date. 
        if (getDate() != null) {
            return (getDate().getTime() <= getWhenInserted().getTime());
        }
        return true;
    }
    
}

