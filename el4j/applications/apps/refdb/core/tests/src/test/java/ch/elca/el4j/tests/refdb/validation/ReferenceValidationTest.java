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
package ch.elca.el4j.tests.refdb.validation;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;

import org.hibernate.validator.ClassValidator;
import org.hibernate.validator.InvalidValue;

import ch.elca.el4j.applications.refdb.dom.Reference;

import junit.framework.TestCase;

//Checkstyle: MagicNumber off

/**
 * 
 * This test class tests custom validation of a reference domain
 * object.
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
public class ReferenceValidationTest extends TestCase {

    /**
     * This test creates a reference which has a document date which is a later
     * date than its whenInserted date, and then validates it. The validator
     * should return one invalid value, since the constraint about the dates has
     * been violated. 
     */
    public void testValidate() {
        Reference reference = new Reference();
        reference.setName("Java");
        reference.setDescription("Java related reference");
        Calendar c = Calendar.getInstance();
        c.set(2006, Calendar.JANUARY, 03);
        reference.setWhenInserted(new Timestamp(c.getTimeInMillis()));
        c.set(2006, Calendar.JULY, 11);        
        reference.setDate(new Date(c.getTimeInMillis()));
        
        ClassValidator<Reference> referenceValidator 
            = new ClassValidator<Reference>(Reference.class);
        InvalidValue[] validationMessages = referenceValidator
            .getInvalidValues(reference);
        assertEquals("The number of invalid values returned by the validator"
            + " must me equal to 1.", 1, validationMessages.length);
    }
    
}

//Checkstyle: MagicNumber on
