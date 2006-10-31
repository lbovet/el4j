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
package ch.elca.el4j.util.observer.impl;


import java.util.LinkedList;
import java.util.List;

import ch.elca.el4j.util.codingsupport.Reject;
import ch.elca.el4j.util.codingsupport.annotations.ImplementationAssumption;
import ch.elca.el4j.util.observer.InquisitiveValueObserver;
import ch.elca.el4j.util.observer.ObservableValue;
import ch.elca.el4j.util.observer.ValueObserver;

/**
 * Abstract ObservableValue featuring value and observer storage as well as 
 * notification.
 * 
 * @param <T> see supertype.
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
public abstract class AbstractObservableValue<T> implements ObservableValue<T> {
    /** no comment ... */
    private static class ValueHolder<T> {
        /** the value held by this. */
        private T m_value;    
    }
    
    /** a holder for the current reference or {@code null},
     * if there isn't one yet. */
    private ValueHolder<T> m_holder = null;
    
    /** the currently subscribed observers. */
    private List<ValueObserver<? super T>> m_obs 
        = new LinkedList<ValueObserver<? super T>>();

    /** creates a new ObservableValue. Subclasses must invoke 
     * {@link #set(Object)} to
     * set the initial reference before {@link #get()} is invoked. */
    protected AbstractObservableValue() { }
    
    /** creates a new ObservableValue.
     * @param initialReference the observable's initial value
     **/
    public AbstractObservableValue(T initialReference) {
        set(initialReference);
    }
    
    /**
     * {@inheritDoc}
     */
    public void subscribeSilently(ValueObserver<? super T> o) {
        Reject.ifNull(o);
        m_obs.add(o);
    }
    
    /**
     * {@inheritDoc}
     */
    public void subscribe(ValueObserver<? super T> o) {
        subscribeSilently(o);
        if (m_holder != null) {
            o.changed(m_holder.m_value);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void unsubscribe(ValueObserver<? super T> o) {
        m_obs.remove(o);
    }
    
    /** informs the subscribed observers. */
    @ImplementationAssumption(
        "An observer processing a notification never unsubscribes "
        + "(directly or indirectly) a yet-to-be-notified observer.")
    private void announce() {
        List<ValueObserver<? super T>> observersSnapshot 
            = new LinkedList<ValueObserver<? super T>>(m_obs); 
        for (ValueObserver<? super T> o : observersSnapshot) {
            o.changed(m_holder.m_value);
        }
        for (ValueObserver<? super T> o : observersSnapshot) {
            if (o instanceof InquisitiveValueObserver<?>) {
                InquisitiveValueObserver<? super T> ivo 
                    = (InquisitiveValueObserver<? super T>) o;
                // don't we all love the tersity of generics? ;)
                
                ivo.notified();
            }
        }
    }
    
    /** updates this observer's current value and announces the change. */
    protected void set(T newValue) {
        if (m_holder != null) {
            if (equal(m_holder.m_value, newValue)) { return; }
        } else {
            m_holder = new ValueHolder<T>();
        }
        m_holder.m_value = newValue;
        announce();
    }
    
    /**
     * {@inheritDoc}
     */
    public T get() throws IllegalStateException {
        LiveValue.observableGetterInterceptor(this);
        if (m_holder == null) {
            throw new IllegalStateException(
                "reference does not exist yet."
            );
        }
        return m_holder.m_value;
    }
    
    
    /** defines when two references are considered equal. */
    // Is not static in order to enable overriding. 
    protected boolean equal(T a, T b) {
        if (a == null) {
            return b == null;
        } else {
            return a.equals(b);
        }
    }
}