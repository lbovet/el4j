package ch.elca.ttrich;

import java.util.Comparator;

/**
 * Comparator needed to sort fields based on their humanization labels.
 *
 * @author  Baeni Christoph (CBA)
 */
public class HumanizationComparator implements Comparator {
	private String entityName;

	public HumanizationComparator(String entityName) {
		this.entityName = entityName;
	}
	
	public int compare(Object fieldName1, Object fieldName2) {
		String humanized1 = Humanization.getFieldName(entityName, (String)fieldName1);
		String humanized2 = Humanization.getFieldName(entityName, (String)fieldName2);
	
		return humanized1.compareToIgnoreCase(humanized2);
	}
}