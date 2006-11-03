/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.util.collections;

import java.util.List;

/**
 * A view for a list where every element appears transformed. That is, if 
 * {@code source} is the backing list and {@code function} the element
 * transformation function,
 * 
 * <p>{@code this.get(i)} returns the same value as
 * {@code function.apply(source.get(i))} for any {@code i < source.size()}.
 * 
 * <p>Element order is taken from the source list. By consequence, changing
 * this list's element order changes the source list's element order.
 * 
 * <p>This view is read-only, i.e. its modifying operations throw 
 * an UnsupportedOperationException.
 * 
 * @param <I> the backing list's element type
 * @param <O> this list's element type
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
public interface TransformedList<I, O> extends ExtendedReorderableList<O> {
    /**
     * returns the backing list.
     * @return the backing list.
     */
    List<? extends I> getBacking();
}
