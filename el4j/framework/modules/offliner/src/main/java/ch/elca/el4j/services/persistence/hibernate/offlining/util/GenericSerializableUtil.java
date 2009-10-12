/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU Lesser General Public License (LGPL)
 * Version 2.1. See http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.services.persistence.hibernate.offlining.util;

import java.io.Serializable;

/**
 * This class wraps the comparison of versions that the offliner needs for synchronizing.
 * It relies on hibernate version entries being comparable and that an update will 
 * change a version to a larger one.
 * <p>
 * Hibernate makes no guarantees except that the version must be numeric or a time
 * (calendar, timestamp) value. While this guarantees comparability it does not exclude
 * a numbering system where versions can decrease or overflow. We assume that in practice
 * this will not happen.
 * <p>
 * Due to java generics limitations we rely on a completely unchecked cast here.
 * "If you must do something ugly, put it in a class" ~Bruce Eckels
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author David Bernhard (DBD)
 */
public final class GenericSerializableUtil {
	
	/** No instances. */
	private GenericSerializableUtil() { }
	
	/**
	 * Comparison function analogous to Comparable.compare(first, second).
	 * <p>
	 * We have two key or version instances of which we know
	 * <ul><li>They are both Serializable.</li>
	 * <li>They are both of the same class.</li>
	 * <li>They implement Comparable on their class.</li></ul>
	 * However, we cannot cast to generics. Instead, we provide "safe" strategies for
	 * all practical cases (String, int, long) and do a completely unchecked cast in all
	 * other cases.
	 * 
	 * @param first The first object to compare.
	 * @param second The second object to compare.
	 * @return An integer fulfilling the contract of Comparable.compare().
	 */
	public static int compare(Serializable first, Serializable second) {
		if (first.getClass() != second.getClass()) {
			throw new IllegalArgumentException("Classes differ in SerialComparator.compare.");
		}

		int value;
		if (first instanceof String) {
			String str1 = (String) first;
			String str2 = (String) second;
			value = str1.compareTo(str2);
		} else if (first instanceof Integer) {
			Integer num1 = (Integer) first;
			Integer num2 = (Integer) second;
			value = num1.compareTo(num2);
		} else if (first instanceof Long) {
			Long num1 = (Long) first;
			Long num2 = (Long) second;
			value = num1.compareTo(num2);
		} else {
			value = uncheckedCompare(first, second);
		}
		return value;
	}
	
	/**
	 * Do a completely unchecked cast to Comparable and compare.
	 * As long as the above assumptions hold, this may work.
	 * @param first The first object.
	 * @param second The second object.
	 * @return the copmarison result.
	 */
	@SuppressWarnings("unchecked")
	public static int uncheckedCompare(Object first, Object second) {
		Comparable com1 = (Comparable) first;
		Comparable com2 = (Comparable) second;
		return com1.compareTo(com2);
	}
}
