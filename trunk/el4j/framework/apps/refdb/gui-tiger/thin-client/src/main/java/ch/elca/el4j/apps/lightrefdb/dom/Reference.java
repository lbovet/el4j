/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.apps.lightrefdb.dom;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Set;

import javax.persistence.Entity;

import ch.elca.el4j.apps.refdb.dom.Annotation;
import ch.elca.el4j.apps.refdb.dom.FileDescriptorView;
import ch.elca.el4j.apps.refdb.dom.File;
import ch.elca.el4j.services.persistence.generic.dto.AbstractIntKeyIntOptimisticLockingDto;
import ch.elca.el4j.util.dom.annotations.MemberOrder;


/**
 * A source of information.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Adrian Moos (AMS)
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
public class Reference extends AbstractIntKeyIntOptimisticLockingDto {
    /** This reference's name. */
    private String name;
    
    /** This reference's unique identifier; an ISBN for instance. */
    private String hashValue;
    
    /**
     * Short description of the reference (short summary, comment on the format
     * of the document, ...).
     */
    private String description;
    
    /** This reference's version. */
    private String version;
    
    /** Is only part of this reference available? */
    private boolean incomplete;
    
    /** The time this reference was entered into the database. */
    private Timestamp whenInserted;
    
    /** When was this reference last changed? */
    private Date date;
    
    /** the keyword(s) that apply to this reference. */
    private Set<Keyword> keywords;
    
    /**
     * Set of annotations for this reference (only used if Hibernate is used to
     * perform ORM).
     */
    private Set<Annotation> m_annotations;
    
    /**
     * Set of files for this reference (only used if Hibernate is used to
     * perform ORM).
     */
    private Set<File> m_files;
    
    /**
     * Set of file descriptor views for this reference (only used if Hibernate
     * is used to perform ORM).
     */
    private Set<FileDescriptorView> m_fileDescriptorViews;

    /***/
    public String getName() { return name; }
    /***/
    public void setName(String name) { this.name = name; }

    /***/
    public String getHashValue() { return hashValue; }
    /***/
    public void setHashValue(String hashValue) { this.hashValue = hashValue; }

    /***/
    public String getDescription() { return description; }
    /***/
    public void setDescription(String description) {
        this.description = description;
    }

    /***/
    public String getVersion() { return version; }
    /***/
    public void setVersion(String version) { this.version = version; }

    /***/
    public boolean isIncomplete() { return incomplete; }
    /***/
    public void setIncomplete(boolean incomplete) {
        this.incomplete = incomplete;
    }

    /***/
    public Timestamp getWhenInserted() {         
        if (whenInserted == null) {
            whenInserted = new Timestamp(System.currentTimeMillis());
        }
        return whenInserted;
    }
    /***/
    public void setWhenInserted(Timestamp whenInserted) {
        this.whenInserted = whenInserted;
    }

    /***/
    public Date getDate() { return date; }
    /***/
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
            this.date = date;
        } else {
            this.date = null;
        }
    }

    /***/
    public Set<Keyword> getKeywords() { return keywords; }
    /***/
    public void setKeywords(Set<Keyword> keywords) { this.keywords = keywords; }
    
    /***/
    public Set<Annotation> getAnnotations() { return m_annotations; }
    /***/
    public void setAnnotations(Set<Annotation> annotations) {
        m_annotations = annotations;
    }
    
    /***/
    public Set<FileDescriptorView> getFileDescriptorViews() {
        return m_fileDescriptorViews;
    }
    /***/
    public 
    void setFileDescriptorViews(Set<FileDescriptorView> fileDescriptorViews) {
        m_fileDescriptorViews = fileDescriptorViews;
    }

    /***/
    public Set<File> getFiles() { return m_files; }
    /***/
    public void setFiles(Set<File> files) { m_files = files; }
}