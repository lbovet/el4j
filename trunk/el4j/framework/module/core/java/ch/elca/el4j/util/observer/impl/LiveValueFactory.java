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
package ch.elca.el4j.util.observer.impl;

import java.util.Collection;

import ch.elca.el4j.util.observer.ObservableValue;

/**
 * Convenience class providing a few standard LiveValues.
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
public final class LiveValueFactory {
    /** This class can not be instantiated. */
    private LiveValueFactory() { }
        
    /** 
     * Returns a LiveValue backed by a Collection that yields the collection's
     * unique element or <code>null</code> if there is no such element.
     * 
     * @param oc the backing collection
     * @return see above. 
     */
    public static
        <T, OC extends ObservableValue<? extends Collection<? extends T>>> 
    LiveValue<T> theElementIn(final OC oc) {
        return new LiveValue<T>(new Computable<T>() {
            public T is() {
                Collection<? extends T> c = oc.get();
                if (c == null || c.size() != 1) {
                    return null;
                } else {
                    return c.iterator().next();
                }
            }            
        });
    }
    
    /**
     * Returns a LiveValue that yields the value of {@code upper} 
     * if it is not {@code null}, or the value of {@code lower} otherwise.
     * @param lower . 
     * @param upper .
     * @return .
     */
    public static <T> 
    LiveValue<T> shadow(final ObservableValue<? extends T> lower,
                        final ObservableValue<? extends T> upper) {
        return new LiveValue<T>(new Computable<T>() {
            public T is() {
                if (upper.get() == null) {
                    return lower.get();
                } else {
                    return upper.get();
                }
            }
        });
    }
}