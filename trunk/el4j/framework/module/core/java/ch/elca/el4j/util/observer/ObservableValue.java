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
package ch.elca.el4j.util.observer;

/**
 * Instances of ObservableValue encapsulate an observable reference of type
 * {@code T}. The reference may not exist yet, be null, or point to an 
 * allocated object.
 * 
 * <p> ObservableValues should be accessed by at most one thread at a time. 
 * Reentrant calls are permitted.
 * 
 * <p> {@link InquisitiveValueObserver InquisitiveValueObservers} are 
 * supported. They can be subscribed like ordinary ValueObservers.   
 * 
 * <p> Note to implementors: If LiveValues may depend on instances of your
 * class, you must invoke 
 *{@link ch.elca.el4j.util.observer.impl.LiveValue#observableGetterInterceptor(
 *ObservableValue) LiveValue.observableGetterInterceptor()}
 * whenever {@link #get()} may have been invoked from a LiveValue. (We do not 
 * require this by static means to avoid loading LiveValue regardless of whether
 * it is used.)
 * 
 * @param <T> the type of object held. 
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @see "Observer Pattern" 
 * @see ValueObserver
 * @author Adrian Moos (AMS)
 */
public interface ObservableValue<T> {

    /** subscribe to this observable's change notifications without getting 
     * an initial change notification.
     * @param o the object to receive change notifications
     */
    void subscribeSilently(ValueObserver<? super T> o);

    /** subscribe to this observable's change notifications. The new observer
     * receives an initial notification if the observed reference already 
     * exists.
     * @param o the object to receive change notifications
     */
    void subscribe(ValueObserver<? super T> o);

    /** unsubscribes from this observable's change notifications, i.e. no change
     * notification will be sent anymore. Has no effect if o is not subscribed. 
     * @param o the object no longer to receive change notifications
     */
    void unsubscribe(ValueObserver<? super T> o);

    /** 
     * @return this observable's current value
     * @throws IllegalStateException if the observed reference does not
     *                               exist yet. 
     **/
    T get() throws IllegalStateException;
}