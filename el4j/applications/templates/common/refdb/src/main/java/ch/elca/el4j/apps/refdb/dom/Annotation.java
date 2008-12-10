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

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.validator.NotNull;

import ch.elca.el4j.services.persistence.generic.dto.AbstractIntKeyIntOptimisticLockingDto;

/**
 * Annotation domain object. This class describes an annotation of a reference
 * (formal publication, book, ...).
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
@Table(name = "ANNOTATIONS")
@SequenceGenerator(name = "keyid_generator",
	sequenceName = "annotation_sequence")
public class Annotation extends AbstractIntKeyIntOptimisticLockingDto {
	/**
	 * Related reference.
	 */
	private Reference m_reference;

	/**
	 * Name of the user annotating a reference.
	 */
	private String m_annotator;

	/**
	 * Grade assigned by the annotator to the reference. Value between 0 and 10
	 * (included), higher values indicate a better grade.
	 */
	private int m_grade;

	/**
	 * Comment that the annotator makes about the reference.
	 */
	private String m_content;

	/**
	 * Date when does the annotation has been inserted (created
	 * automatically).
	 */
	private Date m_whenInserted;

	/**
	 * @return Returns the annotator.
	 */
	@NotNull
	public String getAnnotator() {
		return m_annotator;
	}

	/**
	 * @param annotator
	 *            The annotator to set.
	 */
	public void setAnnotator(String annotator) {
		m_annotator = annotator;
	}

	/**
	 * @return Returns the content.
	 */
	@NotNull
	public String getContent() {
		return m_content;
	}

	/**
	 * @param content
	 *            The content to set.
	 */
	public void setContent(String content) {
		m_content = content;
	}

	/**
	 * @return Returns the grade.
	 */
	@NotNull
	public int getGrade() {
		return m_grade;
	}

	/**
	 * @param grade
	 *            The grade to set.
	 */
	public void setGrade(int grade) {
		this.m_grade = grade;
	}

	/**
	 * @return Returns the m_whenInserted.
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
	 * @return Returns the related reference.
	 */
	@NotNull
	@ManyToOne
	@JoinColumn(name = "keyToReference", nullable = false,
		unique = false, updatable = false)
	public Reference getReference() {
		return m_reference;
	}

	/**
	 * @param reference
	 *            The related reference to set.
	 */
	public void setReference(Reference reference) {
		m_reference = reference;
	}
}