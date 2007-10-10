package ch.elca.el4j.demos.gui.model;

import java.util.List;

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

}