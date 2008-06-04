package ch.elca.el4j.tests.util.codingsupport.testclasses;

import java.util.ArrayList;
import java.util.List;

public class DefaultPerson implements Person {
    protected String firstName;
	protected String lastName;
	protected int age;
    protected List<Person> children;
    protected boolean smart;
    
    public DefaultPerson() {

        children = new ArrayList<Person>();
    }

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
		//System.out.println("firstName has changed to " + firstName);
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
		//System.out.println("lastName has changed to " + lastName);
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
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
}
