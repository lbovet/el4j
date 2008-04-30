/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.apps.refdb.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;

import ch.elca.el4j.apps.refdb.dom.Annotation;

/**
 * 
 * This interface represents a DAO for the annotation domain object.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Alex Mathey (AMA)
 */
public interface AnnotationDao
    extends GenericReferencedObjectDao<Annotation, Integer> {
  
    /**
     * Get all annotations from one annotator.
     * 
     * @param annotator
     *            Is the name of the annotator.
     * @return Returns a list with annotations. Returns never <code>null</code>.
     * @throws DataAccessException
     *             If general data access problem occurred.
     */
    public List<Annotation> getAnnotationsByAnnotator(String annotator)
        throws DataAccessException;
}
