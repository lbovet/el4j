package ch.elca.el4j.seam.demo.entities;

import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.Range;
import org.hibernate.validator.Length;
import java.util.*;

@Entity
public class Offer implements Serializable {
	public enum State { won, lost, pending, noAnswer; }
	
	private Integer id;
	private Integer version;
	
	private Client client;
	private long number;
	private String offer;
	private long cost;
	private State state;
	private Employee responsible;
	private Collection<Employee> redactors;
	private boolean enhancements;
	
	public Offer() {
		redactors = new HashSet<Employee>();
	}
	
	@Id @GeneratedValue
	public Integer getId() { return id; }
	public void setId(Integer id) { this.id = id; }
	
	@Version
	public Integer getVersion() { return version; }
	private void setVersion(Integer version) { this.version = version; }
	
	@NotNull
	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	public Client getClient() { return client; }
	public void setClient(Client client) { this.client = client; }
	
	@NotNull @Range(min=1)
	public long getNumber() { return number; }
	public void setNumber(long number) { this.number = number; }
	
	@NotNull @Length(max=64)
	public String getOffer() { return offer; }
	public void setOffer(String offer) { this.offer = offer; }
	
	@Range(min=0)
	public long getCost() { return cost; }
	public void setCost(long cost) { this.cost = cost; }
	
	public State getState() { return state; }
	public void setState(State state) { this.state = state; }
	
	@NotNull
	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	public Employee getResponsible() { return responsible; }
	public void setResponsible(Employee responsible) { this.responsible = responsible; }
	
	@ManyToMany(targetEntity = Employee.class,
					 cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	public Collection<Employee> getRedactors() { return redactors; }
	public void setRedactors(Collection<Employee> redactors) { this.redactors = redactors; }
	
	public boolean getEnhancements() { return this.enhancements; }
	public void setEnhancements(boolean enhancements) { this.enhancements = enhancements; }
}
