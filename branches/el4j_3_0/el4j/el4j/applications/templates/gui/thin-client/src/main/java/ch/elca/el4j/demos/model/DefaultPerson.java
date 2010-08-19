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

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.Length;
import org.hibernate.validator.Min;
import org.hibernate.validator.NotNull;
import org.joda.time.DateTime;

/**
 * A simple implementation of {@link Person}.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 */
public class DefaultPerson implements Person {
	protected String firstName;
	protected String lastName;
	protected DateTime bornOnThe;
	protected List<MyNumber> numbers;
	protected List<Person> children;
	protected boolean smart;
	protected int age;

	public DefaultPerson() {
		numbers = new ArrayList<MyNumber>();
		children = new ArrayList<Person>();
	}
	
	public DefaultPerson (String firstName, String lastName, int age) {
		this();
		this.firstName = firstName;
		this.lastName = lastName;
		this.age = age;
	}
	
	public DefaultPerson(String firstName, String lastName, int age, DateTime bornOnThe) {
		this();
		this.firstName = firstName;
		this.lastName = lastName;
		this.age = age;
		this.bornOnThe = bornOnThe;
	}

	@Length(min = 3)
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@NotNull
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Min(0)
	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public List<MyNumber> getNumbers() {
		return numbers;
	}

	public void setNumbers(List<MyNumber> numbers) {
		this.numbers = numbers;
	}

	public List<Person> getChildren() {
		return children;
	}

	public void setChildren(List<Person> children) {
		this.children = children;
	}

	public boolean getSmart() {
		return smart;
	}

	public void setSmart(boolean smart) {
		this.smart = smart;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return firstName + " " + lastName + " (" + age + ")";
	}

	@Override
	public DateTime getBornOnThe() {
		return bornOnThe;
	}

	@Override
	public void setBornOnThe(DateTime dateTime) {
		bornOnThe = dateTime;
		
	}
}
