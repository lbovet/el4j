package ch.elca.el4j.seam.generic;

import java.io.Serializable;

/**
 * Helper class to store name and "linked"-property of a column.
 *
 * @author  Baeni Christoph (CBA)
 */
public class TableColumn implements Comparable, Serializable {
	private String name;
	private boolean linked;
	
	public TableColumn(String decoratedName, boolean forceLinked) {
		linked = forceLinked || ((decoratedName.length() >= 1) && (decoratedName.charAt(0) == '@'));
		name = decoratedName.replaceFirst("^@","");
	}
	
	public TableColumn(String decoratedName) {
		this(decoratedName, false);
	}
	
	public String getName() {
		return name;
	}
	
	public String getLinked() {
		return linked ? "y" : "n";
	}
	
	public int compareTo(Object object)  {
		if (object instanceof TableColumn) {
			return name.compareTo(((TableColumn)object).getName());
		}
		return -1;
	}
}