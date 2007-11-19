package ch.elca.el4j.seam.demo.entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Version;
import org.hibernate.validator.*;

@Entity
public class Employee implements Serializable {
	private Integer id;
	private Integer version;
	
	private String visa;
	private String firstName;
	private String lastName;
	private boolean active;
	
	@Id @GeneratedValue
	public Integer getId() { return id; }
	public void setId(Integer id) { this.id = id; }
	
	@Version
	public Integer getVersion() { return version; }
	private void setVersion(Integer version) { this.version = version; }
	
	@NotNull @Length(min=3,max=3)
	public String getVisa() { return visa; }
	public void setVisa(String visa) { this.visa = visa; }
	
	@NotNull @Length(max=32)
	public String getFirstName() { return firstName; }
	public void setFirstName(String firstName) { this.firstName = firstName; }
	
	@NotNull @Length(max=32)
	public String getLastName() { return lastName; }
	public void setLastName(String lastName) { this.lastName = lastName; }
	
	public boolean isActive() { return active; }
	public void setActive(boolean active) { this.active = active; }
	
	public String toString() {
		return lastName + " " + firstName + " (" + visa + ")";
	}
}
