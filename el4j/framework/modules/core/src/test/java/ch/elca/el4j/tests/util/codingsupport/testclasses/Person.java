package ch.elca.el4j.tests.util.codingsupport.testclasses;

import java.util.List;

/**
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 */
public interface Person {

	public String getFirstName();

	public void setFirstName(String firstName);

	public String getLastName();

	public void setLastName(String lastName);

	public int getAge();

	public void setAge(int age);
	
	
	public List<Person> getChildren() ;
	
	public void setChildren(List<Person> children);
	
	public boolean getSmart();
	
	public void setSmart(boolean smart);

}