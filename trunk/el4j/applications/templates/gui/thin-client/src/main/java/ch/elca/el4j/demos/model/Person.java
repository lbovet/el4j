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
package ch.elca.el4j.demos.model;

import java.util.List;

import org.joda.time.DateTime;

/**
 * A simple Person (example model).
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 */
public interface Person {

	public String getFirstName();


	public void setFirstName(String firstName);

	public String getLastName();

	public void setLastName(String lastName);

	public int getAge();

	public void setAge(int age);
	
	public List<MyNumber> getNumbers();
	
	public void setNumbers(List<MyNumber> numbers);
	
	public List<Person> getChildren() ;
	
	public void setChildren(List<Person> children);
	
	public boolean getSmart();
	
	public void setSmart(boolean smart);
	
	public void setBornOnThe(DateTime dateTime);
	
	/**
	 * Date when the Person was born.
	 * 
	 * @return a DateTime instance representing the date on which the person was born.
	 */
	public DateTime getBornOnThe();

}