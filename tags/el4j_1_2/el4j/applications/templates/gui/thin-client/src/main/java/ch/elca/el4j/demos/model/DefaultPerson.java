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
import org.hibernate.validator.NotNull;

public class DefaultPerson implements Person {
    protected String m_firstName;
    protected String m_lastName;
    protected int m_age;
    protected List<MyNumber> m_numbers;
    protected List<Person> m_children;
    protected boolean m_smart;

    public DefaultPerson() {
        m_numbers = new ArrayList<MyNumber>();
        m_children = new ArrayList<Person>();
    }

    @Length(min = 3)
    public String getFirstName() {
        return m_firstName;
    }

    public void setFirstName(String firstName) {
        m_firstName = firstName;
    }

    @NotNull
    public String getLastName() {
        return m_lastName;
    }

    public void setLastName(String lastName) {
        m_lastName = lastName;
    }

    public int getAge() {
        return m_age;
    }

    public void setAge(int age) {
        m_age = age;
    }

    public List<MyNumber> getNumbers() {
        return m_numbers;
    }

    public void setNumbers(List<MyNumber> numbers) {
        m_numbers = numbers;
    }

    public List<Person> getChildren() {
        return m_children;
    }

    public void setChildren(List<Person> children) {
        m_children = children;
    }

    public boolean getSmart() {
        return m_smart;
    }

    public void setSmart(boolean smart) {
        m_smart = smart;
    }
}
