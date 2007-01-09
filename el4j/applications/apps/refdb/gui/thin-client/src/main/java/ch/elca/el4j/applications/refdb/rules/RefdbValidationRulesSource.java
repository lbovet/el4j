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
package ch.elca.el4j.applications.refdb.rules;

import java.util.Calendar;
import java.util.Date;

import org.springframework.core.closure.Constraint;
import org.springframework.rules.Rules;
import org.springframework.rules.support.DefaultRulesSource;

import ch.elca.el4j.applications.keyword.dom.Keyword;
import ch.elca.el4j.applications.refdb.dom.Annotation;
import ch.elca.el4j.applications.refdb.dom.Book;
import ch.elca.el4j.applications.refdb.dom.File;
import ch.elca.el4j.applications.refdb.dom.FileDescriptorView;
import ch.elca.el4j.applications.refdb.dom.FormalPublication;
import ch.elca.el4j.applications.refdb.dom.Link;

/**
 * Validation rules source for the reference database application.
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
public class RefdbValidationRulesSource extends DefaultRulesSource {
    /**
     * Default constructor.
     */
    public RefdbValidationRulesSource() {
        addRules(new KeywordRules());
        addRules(new LinkRules());
        addRules(new FormalPublicationRules());
        addRules(new BookRules());
        addRules(new AnnotationRules());
        addRules(new FileDescriptorViewRules(File.class));
        addRules(new FileDescriptorViewRules(FileDescriptorView.class));
    }
    
    /**
     * Rules for keywords.
     *
     * @author Martin Zeltner (MZE)
     */
    protected class KeywordRules extends Rules {
        /**
         * Default constructor.
         */
        public KeywordRules() {
            super(Keyword.class);
        }

        /**
         * Constructor.
         * 
         * @param domainObjectClass Is the class where to use the rules.
         */
        public KeywordRules(Class domainObjectClass) {
            super(domainObjectClass);
        }
        
        /**
         * {@inheritDoc}
         */
        protected void initRules() {
            super.initRules();
            add("name", getConstraintRequiredMaxLength(
                RefdbBoundaries.MAX_LENGTH_KEYWORD_NAME));
            add("description", getConstraintNotRequiredMaxLength(
                RefdbBoundaries.MAX_LENGTH_KEYWORD_DESCRIPTION));
        }            
    }

    /**
     * Rules for references.
     *
     * @author Martin Zeltner (MZE)
     */
    protected class ReferenceRules extends Rules {
        /**
         * Is the oldest possible reference date. 
         */
        protected final Date m_oldestPossibleReferenceDate;
        
        /**
         * Constructor.
         * 
         * @param domainObjectClass Is the class where to use the rules.
         */
        public ReferenceRules(Class domainObjectClass) {
            super(domainObjectClass);
            Calendar c = Calendar.getInstance();
            // Checkstyle: MagicNumber off
            c.set(Calendar.YEAR, 1980);
            c.set(Calendar.DAY_OF_YEAR, 1);
            c.set(Calendar.MILLISECOND, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.HOUR, 0);
            c.set(Calendar.AM_PM, Calendar.AM);
            m_oldestPossibleReferenceDate = c.getTime();
            // Checkstyle: MagicNumber on
        }
        
        /**
         * {@inheritDoc}
         */
        protected void initRules() {
            super.initRules();
            add("name", getConstraintRequiredMaxLength(
                RefdbBoundaries.MAX_LENGTH_REFERENCE_NAME));
            add("hashValue", getConstraintNotRequiredMaxLength(
                RefdbBoundaries.MAX_LENGTH_REFERENCE_HASHVALUE));
            add("description", getConstraintNotRequiredMaxLength(
                RefdbBoundaries.MAX_LENGTH_REFERENCE_DESCRIPTION));
            add("version", getConstraintNotRequiredMaxLength(
                RefdbBoundaries.MAX_LENGTH_REFERENCE_VERSION));
            add("date", or(
                range(m_oldestPossibleReferenceDate, new Date()), 
                not(required())));
        }            
    }
    
    /**
     * Rules for links.
     *
     * @author Martin Zeltner (MZE)
     */
    protected class LinkRules extends ReferenceRules {
        /**
         * Default constructor.
         */
        public LinkRules() {
            super(Link.class);
        }

        /**
         * Constructor.
         * 
         * @param domainObjectClass Is the class where to use the rules.
         */
        public LinkRules(Class domainObjectClass) {
            super(domainObjectClass);
        }
        
        /**
         * {@inheritDoc}
         */
        protected void initRules() {
            super.initRules();
            add("url", getConstraintNotRequiredMaxLength(
                RefdbBoundaries.MAX_LENGTH_LINK_URL));
        }            
    }

    /**
     * Rules for formal publications.
     *
     * @author Martin Zeltner (MZE)
     */
    protected class FormalPublicationRules extends ReferenceRules {
        /**
         * Default constructor.
         */
        public FormalPublicationRules() {
            super(FormalPublication.class);
        }

        /**
         * Constructor.
         * 
         * @param domainObjectClass Is the class where to use the rules.
         */
        public FormalPublicationRules(Class domainObjectClass) {
            super(domainObjectClass);
        }
        
        /**
         * {@inheritDoc}
         */
        protected void initRules() {
            super.initRules();
            add("authorName", getConstraintNotRequiredMaxLength(
                RefdbBoundaries.MAX_LENGTH_FORMALPUBLICATION_AUTHORNAME));
            add("publisher", getConstraintNotRequiredMaxLength(
                RefdbBoundaries.MAX_LENGTH_FORMALPUBLICATION_PUBLISHER));
            add("pageNum", all(new Constraint[] {
                range(RefdbBoundaries.MIN_VALUE_FORMALPUBLICATION_PAGENUM, 
                    RefdbBoundaries.MAX_VALUE_FORMALPUBLICATION_PAGENUM)
            }));
        }            
    }

    /**
     * Rules for books.
     *
     * @author Martin Zeltner (MZE)
     */
    protected class BookRules extends FormalPublicationRules {
        /**
         * Default constructor.
         */
        public BookRules() {
            super(Book.class);
        }

        /**
         * Constructor.
         * 
         * @param domainObjectClass Is the class where to use the rules.
         */
        public BookRules(Class domainObjectClass) {
            super(domainObjectClass);
        }
        
        /**
         * {@inheritDoc}
         */
        protected void initRules() {
            super.initRules();
            add("isbnNumber", getConstraintNotRequiredMaxLength(
                RefdbBoundaries.MAX_LENGTH_BOOK_ISBNNUMBER));
        }            
    }

    /**
     * Rules for annotations.
     *
     * @author Martin Zeltner (MZE)
     */
    protected class AnnotationRules extends Rules {
        /**
         * Default constructor.
         */
        public AnnotationRules() {
            super(Annotation.class);
        }

        /**
         * Constructor.
         * 
         * @param domainObjectClass Is the class where to use the rules.
         */
        public AnnotationRules(Class domainObjectClass) {
            super(domainObjectClass);
        }

        /**
         * {@inheritDoc}
         */
        protected void initRules() {
            super.initRules();
            add("annotator", getConstraintRequiredMaxLength(
                RefdbBoundaries.MAX_LENGTH_ANNOTATION_ANNOTATOR));
            add("grade", all(new Constraint[] {
                required(),
                range(RefdbBoundaries.MIN_VALUE_ANNOTATION_GRADE,
                    RefdbBoundaries.MAX_VALUE_ANNOTATION_GRADE)
            }));
            add("content", getConstraintRequiredMaxLength(
                RefdbBoundaries.MAX_SIZE_ANNOTATION_CONTENT));
        }            
    }

    /**
     * Rules for file descriptor view.
     *
     * @author Martin Zeltner (MZE)
     */
    protected class FileDescriptorViewRules extends Rules {
        /**
         * Constructor.
         * 
         * @param domainObjectClass Is the class where to use the rules.
         */
        public FileDescriptorViewRules(Class domainObjectClass) {
            super(domainObjectClass);
        }
        
        /**
         * {@inheritDoc}
         */
        protected void initRules() {
            super.initRules();
            add("name", getConstraintRequiredMaxLength(
                RefdbBoundaries.MAX_LENGTH_FILE_NAME));
            add("contentSize", all(new Constraint[] {
                required(),
                lte(RefdbBoundaries.MAX_SIZE_FILE_CONTENT)
            }));
        }            
    }
    
    /**
     * @param maxLength Is the maximal allowed length.
     * @return Returns a combined required and max length constraint.
     */
    protected Constraint getConstraintRequiredMaxLength(int maxLength) {
        return all(new Constraint[] {
            required(), maxLength(maxLength)
        });
    }

    /**
     * @param maxLength Is the maximal allowed length.
     * @return Returns a max length constraint.
     */
    protected Constraint getConstraintNotRequiredMaxLength(int maxLength) {
        return maxLength(maxLength);
    }
}
