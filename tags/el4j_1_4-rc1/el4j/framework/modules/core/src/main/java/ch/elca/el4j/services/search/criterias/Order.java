package ch.elca.el4j.services.search.criterias;

import java.io.Serializable;

/**
 * Represents an order imposed upon a {@link QueryObject} result set
 *   Simplification of Order class of hibernate
 */
public class Order implements Serializable {

	private boolean ascending;
	private String propertyName;
	
	public String toString() {
		return propertyName + ' ' + (ascending?"asc":"desc");
	}

	/**
	 * Constructor for Order.
	 */
	protected Order(String propertyName, boolean ascending) {
		this.propertyName = propertyName;
		this.ascending = ascending;
	}


	/**
	 * Ascending order
	 *
	 * @param propertyName
	 * @return Order
	 */
	public static Order asc(String propertyName) {
		return new Order(propertyName, true);
	}

	/**
	 * Descending order
	 *
	 * @param propertyName
	 * @return Order
	 */
	public static Order desc(String propertyName) {
		return new Order(propertyName, false);
	}

    public boolean isAscending() {
        return ascending;
    }

    public String getPropertyName() {
        return propertyName;
    }

}
