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
package ch.elca.el4j.services.search.criterias;


/**
 *
 * Criteria for the include pattern.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @deprecated no longer needed.
 * @author Alex Mathey (AMA)
 */
public class IncludeCriteria extends AbstractCriteria {

	/**
	 * Default constructor for remoting protocols like hessian added.
	 */
	protected IncludeCriteria() { }
	
	/**
	 * Constructor.
	 *
	 * @param field
	 *            Is the field the criteria is made for.
	 * @param value
	 *            Is the value of this criteria.
	 */
	public IncludeCriteria(String field, Object value) {
		super(field, value);
	}
		
	/**
	 * {@inheritDoc}
	 */
	public String getType() {
		return "include";
	}

	public String getSqlWhereCondition() {
		return "";
	}

}
