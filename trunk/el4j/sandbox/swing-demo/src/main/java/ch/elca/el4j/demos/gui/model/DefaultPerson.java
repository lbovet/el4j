package ch.elca.el4j.demos.gui.model;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

public class DefaultPerson implements Person {
    protected String firstName;
	protected String lastName;
	protected int age;
    protected List<MyNumber> numbers;
    protected List<Person> children;
    protected boolean smart;
    
    public DefaultPerson() {
        numbers = new ArrayList<MyNumber>();
        children = new ArrayList<Person>();
    }

    @Length(min=3)
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
		//System.out.println("firstName has changed to " + firstName);
	}

	@NotNull
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
}
