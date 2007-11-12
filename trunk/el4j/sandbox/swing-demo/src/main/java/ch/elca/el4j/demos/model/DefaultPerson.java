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
        // System.out.println("firstName has changed to " + firstName);
    }

    @NotNull
    public String getLastName() {
        return m_lastName;
    }

    public void setLastName(String lastName) {
        m_lastName = lastName;
        // System.out.println("lastName has changed to " + lastName);
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
