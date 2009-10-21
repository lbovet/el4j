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

import static org.junit.Assert.assertEquals;

import org.hibernate.validator.ClassValidator;
import org.hibernate.validator.InvalidValue;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;

import ch.elca.el4j.apps.refdb.dom.Link;
import ch.elca.el4j.apps.refdb.dom.Reference;

//Checkstyle: MagicNumber off

/**
 *
 * This test class tests custom validation of a reference domain
 * object.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Alex Mathey (AMA)
 */
public class ReferenceValidationTest {

	/**
	 * This test creates a reference which has a document date which is a later date than its whenInserted date, and
	 * then validates it. The validator should return one invalid value, since the constraint about the dates has been
	 * violated.
	 */
	@Test
	public void testValidate() {
		Reference reference = new Link();
		reference.setName("Java");
		reference.setDescription("Java related reference");

		reference.setWhenInserted(new DateTime(2006, 1, 3, 0, 0, 0, 0));
		reference.setDate(new LocalDate(2006, 7, 11));

		ClassValidator<Reference> referenceValidator = new ClassValidator<Reference>(Reference.class);
		InvalidValue[] validationMessages = referenceValidator.getInvalidValues(reference);
		assertEquals("The number of invalid values returned by the validator" + " must me equal to 1.", 1,
			validationMessages.length);
	}

}

// Checkstyle: MagicNumber on
