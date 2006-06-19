/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU General Public License (GPL) Version 2.0.
 * http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.util.collections;

import java.util.ListIterator;

/**
 * A view on a backing List showing only the elements accepted by a 
 * filter.
 * It may be iterated over (for instance using the extended for-statement).
 * Alternatively, you may copy its contents to an array or an ExtendedArrayList
 * (see 
 *{@link ch.elca.el4j.util.collections.impl.ExtendedArrayList#ExtendedArrayList(
 *Iterable) ExtendedArrayList(Iterable)}
 * 
 * @param <T> the element type
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Adrian Moos (AMS)
 */
public interface FilteredList<T> extends ExtendedList<T> {
    /** 
     * Returns a BiDiIterator for this collection pointing at the place
     * corresponding to {@code location}.
     * 
     * @param location An iterator for the backing collection. This reference
     *                 may be captured, i.e. it
     *                 shouldn't be be used in client code afterwards.
     * @return The iterator
     */
    public ListIterator<T> listIterator(ListIterator<? extends T> location);
}
