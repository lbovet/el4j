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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.lob.ClobImpl;
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
	 * Private logger.
	 */
	private static Log s_logger = LogFactory.getLog(Annotation.class);
	
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
	private String m_content;// = null;
	
	/** 
	 * See corresponding setter method for more details.
	 * Field declared as transient to be excluded from serialization.
	 * Note: Hibernate's blob is not capable of serialization, 
	 * therefore we have fields for the data: hibernate's blob and the content
	 * for user convenience and serialization (client-server exchange).
	 */
	private transient Clob m_data;

	/**
	 * Date when does the annotation has been inserted (created
	 * automatically).
	 */
	private Date m_whenInserted;

	/**
	 * @return Returns the annotator.
	 */
	@NotNull
	@Column(length = 64)
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
	@Transient
	public String getContent() {
		if (m_content == null) {
			// Read content out of hibernate's clob the first time used.
			InputStream in = null;
			ByteArrayOutputStream out = null;
			String primitiveData = null;
			Clob clob = m_data;
			if (clob != null) {
				try {
					in = clob.getAsciiStream();
					out = new ByteArrayOutputStream();
					byte[] buffer = new byte[4096];
					int readBytes;
					while ((readBytes = in.read(buffer)) > 0) {
						out.write(buffer, 0, readBytes);
					}
					primitiveData = out.toString();
				} catch (IOException e) {
					s_logger.error("Error while reading content stream.", e);
				} catch (SQLException e) {
					s_logger.error("Error while retrieving content.", e);
				} finally {
					if (in != null) {
						try {
							in.close();
						} catch (Exception e) {
							s_logger.error("Error while closing input stream.");
						}
					}
					if (out != null) {
						try {
							out.close();
						} catch (Exception e) {
							s_logger.error("Error while closing output stream.");
						}
					}
				}
			}
			if (primitiveData != null) {
				m_content = primitiveData.length() > 0 ? primitiveData : null;
				// Recreate Clob for hibernate (eg. derby returns an unusable clob object)
				m_data = Hibernate.createClob(primitiveData);
			}
		}
		return m_content;
	}

	/**
	 * @param content
	 *            The content to set.
	 */
	public void setContent(String content) {
		if (content != null && content.length() > 0) {
			// Set the clob as well as the content for hibernate.
			setData(Hibernate.createClob(content));
			m_content = content;
		} else {
			setData(null);
			m_content = null;
		}
	}
	
	/**
	 * Content of the file converted to Clob. Used by hibernate only!
	 * @return Returns the data.
	 */
	@NotNull(message = "{Annotation.data}")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(name = "content", length = Integer.MAX_VALUE - 1)
	public Clob getData() {
		if (m_data == null) {
			// Re-set the clob if null (eg. after serialization)
			setContent(m_content);
		}
		return m_data;
	}
	
	/**
	 * Set the content as Clob. Used by hibernate only!
	 * @param data    the data to set.
	 */
	public void setData(Clob data) {
		m_data = data;
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
	
	/** {@inheritDoc} */
	public String toString() {
		return (m_reference == null ? "null" : m_reference.toString())
			+ "/" + (m_annotator == null ? "unknown" : m_annotator);
	}
}
