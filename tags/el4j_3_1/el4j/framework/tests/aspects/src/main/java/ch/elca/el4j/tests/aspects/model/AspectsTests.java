/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2010 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.tests.aspects.model;

import javax.persistence.Entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;

import ch.elca.el4j.services.persistence.generic.dto.AbstractIntKeyIntOptimisticLockingDto;

/**
 * Domain class to test aspects behavior in Spring/EL4J.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Martin Zeltner (MZE)
 */
@Entity
@GenericGenerator(name = "keyid_generator",
	strategy = "ch.elca.el4j.services.persistence.hibernate.TriggerAssignedIdentityGenerator")
public class AspectsTests extends AbstractIntKeyIntOptimisticLockingDto {
	/** Serial version UID. */
	private static final long serialVersionUID = 229623693736593007L;

	/** Name. */
	private String name;
	
	/** Description. */
	private String description;

	/**
	 * @return Returns the name.
	 */
	@NotEmpty
	@Length(max = 64)
	public String getName() {
		return name;
	}

	/**
	 * @param name Is the name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Returns the description.
	 */
	@Length(max = 256)
	public String getDescription() {
		return description;
	}

	/**
	 * @param description Is the description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}
}
