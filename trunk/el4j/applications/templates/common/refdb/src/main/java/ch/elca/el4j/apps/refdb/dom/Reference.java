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

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Store;
import org.hibernate.validator.AssertTrue;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

import ch.elca.el4j.apps.keyword.dom.Keyword;
import ch.elca.el4j.core.metadata.ContainedClass;
import ch.elca.el4j.services.persistence.generic.dto.AbstractIntKeyIntOptimisticLockingDto;

/**
 * Reference domain object. This is the base reference class and describes a
 * source of information.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Martin Zeltner (MZE)
 * @author Alex Mathey (AMA)
 */

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "REFERENCESTABLE")
@SequenceGenerator(name = "keyid_generator",
	sequenceName = "reference_sequence")
public abstract class Reference extends AbstractIntKeyIntOptimisticLockingDto {
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
	 * Date when does the reference has been inserted (created
	 * automatically).
	 */
	private Date m_whenInserted;

	/**
	 * Date of the referenced document (publication date, last update, ...).
	 */
	private Date m_date;

	/**
	 * Set of keywords for this reference.
	 */
	private Set<Keyword> m_keywords;

	/**
	 * Set of annotations for this reference.
	 */
	private Set<Annotation> m_annotations;
	
	/**
	 * Set of files for this reference.
	 */
	private Set<File> m_files;
	
	/**
	 * The type this reference is actually of.
	 * @return the type of this reference.
	 */
	@Transient
	public abstract String getType();
	
	/**
	 * @return Returns the date.
	 */
	@Column(name = "DOCUMENTDATE")
	@Temporal(TemporalType.DATE)
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
	@Field(index = Index.TOKENIZED, store = Store.NO)
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
	@Field(index = Index.TOKENIZED, store = Store.NO)
	@NotNull(message = "{Reference.name}")
	@Length(min = 3, message = "{Reference.name}")
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
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	public Date getWhenInserted() {
		if (m_whenInserted == null) {
			m_whenInserted = new Date();
		}
		return m_whenInserted;
	}

	/**
	 * @param whenInserted
	 *            The whenInserted to set.
	 */
	public void setWhenInserted(Date whenInserted) {
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
	@ContainedClass(Keyword.class)
	public Set<Keyword> getKeywords() {
		if (m_keywords == null) {
			m_keywords = new HashSet<Keyword>();
		}
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
	@OneToMany(mappedBy = "reference", cascade = { CascadeType.ALL })
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	@ContainedClass(Annotation.class)
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
	@OneToMany(mappedBy = "reference", cascade = { CascadeType.ALL })
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	@ContainedClass(File.class)
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
	
	/** {@inheritDoc} */
	public String toString() {
		return m_name;
	}
	/**
	 * Checks whether the reference is valid. Should always be true.
	 * @return true if the reference is valid, false otherwise
	 */
	@AssertTrue(message = "{Reference.dateInvariant}")
	public boolean invariant() {
		// Ensure that the creation date of the referenced document is
		// smaller than its insertion date.
		if (getDate() != null) {
			return (getDate().getTime() <= getWhenInserted().getTime());
		}
		return true;
	}
	
}

