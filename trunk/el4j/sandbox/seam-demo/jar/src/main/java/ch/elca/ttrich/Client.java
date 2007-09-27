package ch.elca.ttrich;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Version;
import org.hibernate.validator.*;

@Entity
public class Client implements Serializable {
	private Integer id;
	private Integer version;
	
	private String enterprise;
	private String activity;
	private String address;
	
	@Id @GeneratedValue
	public Integer getId() { return id; }
	public void setId(Integer id) { this.id = id; }
	
	@Version
	public Integer getVersion() { return version; }
	private void setVersion(Integer version) { this.version = version; }
	
	@NotNull @Length(max=32)
	public String getEnterprise() { return enterprise; }
	public void setEnterprise(String enterprise) { this.enterprise = enterprise; }
	
	@Length(max=256)
	public String getActivity() { return activity; }
	public void setActivity(String activity) { this.activity = activity; }
	
	@Length(max=64)
	public String getAddress() { return address; }
	public void setAddress(String address) { this.address = address; }
	
	public String toString() {
		return enterprise;
	}
}
