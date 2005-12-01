/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * This program is published under the GNU General Public License (GPL) license.
 * http://www.gnu.org/licenses/gpl.txt
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.apps.refdb.rules;

import java.util.Date;

import org.springframework.core.closure.Constraint;
import org.springframework.rules.Rules;
import org.springframework.rules.support.DefaultRulesSource;

import ch.elca.el4j.apps.keyword.dto.KeywordDto;
import ch.elca.el4j.apps.refdb.dto.AnnotationDto;
import ch.elca.el4j.apps.refdb.dto.BookDto;
import ch.elca.el4j.apps.refdb.dto.FileDescriptorView;
import ch.elca.el4j.apps.refdb.dto.FileDto;
import ch.elca.el4j.apps.refdb.dto.FormalPublicationDto;
import ch.elca.el4j.apps.refdb.dto.LinkDto;
import ch.elca.el4j.apps.refdb.dto.ReferenceDto;

/**
 * Validation rules source for the reference database application.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public class ValidationRulesSource extends DefaultRulesSource {
    /**
     * Default constructor.
     */
    public ValidationRulesSource() {
        super();
        createKeywordRules();
        createReferenceRules();
        createLinkRules();
        createFormalPublicationRules();
        createBookRules();
        createAnnotationRules();
        createFileRules();
        createFileDescriptorViewRules();
    }
    
    /**
     * @return Returns rules for keyword dto classes.
     */
    protected Rules createKeywordRules() {
        return new Rules(KeywordDto.class) {
            protected void initRules() {
                add("name", getConstraintRequiredMaxLength(
                    Boundaries.MAX_LENGTH_KEYWORD_NAME));
                add("description", getConstraintNotRequiredMaxLength(
                    Boundaries.MAX_LENGTH_KEYWORD_DESCRIPTION));
            }            
        };
    }

    /**
     * @return Returns rules for reference dto classes.
     */
    protected Rules createReferenceRules() {
        return new Rules(ReferenceDto.class) {
            protected void initRules() {
                add("name", getConstraintRequiredMaxLength(
                    Boundaries.MAX_LENGTH_REFERENCE_NAME));
                add("hashValue", getConstraintNotRequiredMaxLength(
                    Boundaries.MAX_LENGTH_REFERENCE_HASHVALUE));
                add("description", getConstraintNotRequiredMaxLength(
                    Boundaries.MAX_LENGTH_REFERENCE_DESCRIPTION));
                add("version", getConstraintNotRequiredMaxLength(
                    Boundaries.MAX_LENGTH_REFERENCE_VERSION));
                add("documentDate", lt(new Date()));
            }            
        };
    }

    /**
     * @return Returns rules for link dto classes.
     */
    protected Rules createLinkRules() {
        return new Rules(LinkDto.class) {
            protected void initRules() {
                add("url", getConstraintNotRequiredMaxLength(
                    Boundaries.MAX_LENGTH_LINK_URL));
            }            
        };
    }
    
    /**
     * @return Returns rules for formal publication dto classes.
     */
    protected Rules createFormalPublicationRules() {
        return new Rules(FormalPublicationDto.class) {
            protected void initRules() {
                add("authorName", getConstraintNotRequiredMaxLength(
                    Boundaries.MAX_LENGTH_FORMALPUBLICATION_AUTHORNAME));
                add("publisher", getConstraintNotRequiredMaxLength(
                    Boundaries.MAX_LENGTH_FORMALPUBLICATION_PUBLISHER));
                add("pageNum", all(new Constraint[] {
                    range(Boundaries.MIN_VALUE_FORMALPUBLICATION_PAGENUM, 
                        Boundaries.MAX_VALUE_FORMALPUBLICATION_PAGENUM)
                }));
            }            
        };
    }
    
    /**
     * @return Returns rules for book dto classes.
     */
    protected Rules createBookRules() {
        return new Rules(BookDto.class) {
            protected void initRules() {
                add("isbnNumber", getConstraintNotRequiredMaxLength(
                    Boundaries.MAX_LENGTH_BOOK_ISBNNUMBER));
            }            
        };
    }
    
    /**
     * @return Returns rules for annotation dto classes.
     */
    protected Rules createAnnotationRules() {
        return new Rules(AnnotationDto.class) {
            protected void initRules() {
                add("annotator", getConstraintRequiredMaxLength(
                    Boundaries.MAX_LENGTH_ANNOTATION_ANNOTATOR));
                add("grade", all(new Constraint[] {
                    required(),
                    range(Boundaries.MIN_VALUE_ANNOTATION_GRADE,
                        Boundaries.MAX_VALUE_ANNOTATION_GRADE)
                }));
                add("content", getConstraintRequiredMaxLength(
                    Boundaries.MAX_SIZE_ANNOTATION_CONTENT));
            }            
        };
    }
    
    /**
     * @return Returns rules for file dto classes.
     */
    protected Rules createFileRules() {
        return new FileDescriptorViewRules(FileDto.class);
    }
    
    /**
     * @return Returns rules for file descriptor view classes.
     */
    protected Rules createFileDescriptorViewRules() {
        return new FileDescriptorViewRules(FileDescriptorView.class);
    }
    
    /**
     * Rules for file descriptor view.
     *
     * @author Martin Zeltner (MZE)
     */
    private class FileDescriptorViewRules extends Rules {
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
            add("name", getConstraintRequiredMaxLength(
                Boundaries.MAX_LENGTH_FILE_NAME));
            add("contentSize", all(new Constraint[] {
                required(),
                lte(Boundaries.MAX_SIZE_FILE_CONTENT)
            }));
        }            
    }
    
    /**
     * @param maxLength Is the maximal allowed length.
     * @return Returns a combined required and max length constraint.
     */
    private Constraint getConstraintRequiredMaxLength(int maxLength) {
        return all(new Constraint[] {
            required(), maxLength(maxLength)
        });
    }

    /**
     * @param maxLength Is the maximal allowed length.
     * @return Returns a max length constraint.
     */
    private Constraint getConstraintNotRequiredMaxLength(int maxLength) {
        return maxLength(maxLength);
    }
}
