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

import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.validator.NotNull;

import ch.elca.el4j.services.persistence.generic.dto.AbstractIntKeyIntOptimisticLockingDto;
import ch.elca.el4j.util.codingsupport.ObjectUtils;

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
     * Primary key of related reference.
     */
    private int m_keyToReference;

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
     * Timestamp when does the annotation has been inserted (created
     * automatically).
     */
    private Timestamp m_whenInserted;

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
     * @return Returns the key to reference.
     */
    @NotNull
    public int getKeyToReference() {
        return m_keyToReference;
    }

    /**
     * @param keyToReference
     *            The key to reference to set.
     */
    public void setKeyToReference(int keyToReference) {
        m_keyToReference = keyToReference;
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
            && object instanceof Annotation) {
            Annotation other = (Annotation) object;

            return m_keyToReference == other.m_keyToReference
                && ObjectUtils.nullSaveEquals(m_annotator, other.m_annotator)
                && m_grade == other.m_grade
                && ObjectUtils.nullSaveEquals(m_content, other.m_content)
                && org.springframework.util.ObjectUtils.nullSafeEquals(
                    m_whenInserted, other.m_whenInserted);
        } else {
            return false;
        }
    }
}