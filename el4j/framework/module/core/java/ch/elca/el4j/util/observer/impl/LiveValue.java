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

import java.util.ArrayList;
import java.util.Collection;

import ch.elca.el4j.util.observer.InquisitiveValueObserver;
import ch.elca.el4j.util.observer.ObservableValue;



/**
 * A LiveValue is a derived value that stays up to date by itself.
 * 
 * <p>A LiveValue is defined by a method computing it. The method is usually 
 * implemented in a {@link Computable} and given to LiveValue's public 
 * constructor. Alternatively, the method may also be provided by overriding 
 * {@link #is()}. In that case, you may <i>not</i> assume that the subclass' 
 * constructor is invoked before {@code is()} is.
 * 
 * <p>A LiveValue is an ObservableValue. Therefore, it is possible to build 
 * directed acyclic graphs (and thus trees) of LiveValues. For 
 * obvious reasons, cyclic dependencies are not permitted; detection of one 
 * results in a {@link CyclicDependencyException} to be thrown.
 * 
 * <p>This class is thread safe.
 * 
 * <p>If the live value only
 * depends on values of type ObservableValue (which it obtains by invoking
 * {@link ObservableValue#get()}), the live value will keep itself up to date
 * by itself. Otherwise, updates must be requested manually using 
 * {@link #revise()}.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @param <T> see supertype
 * @see LiveValueFactory
 * @author Adrian Moos (AMS)
 */

// Implementation notes: By necessity, this class closely cooperates with 
// ObservableValue, in particular with method get(). 
// By "local code",
// we therefore mean any code in this class or in one of these methods.
//
// this class is a bit involved because the methods need both thread safety and
// reentrance protection (Java's monitors are reentrant).

public class LiveValue<T> extends AbstractObservableValue<T> 
    implements InquisitiveValueObserver<Object> {
    
    /** mutual exclusion for threads manipulating <code>m_currency</code>. */
    static final Object s_dirtiesMonitor = new Object(); 
    
    /** the live value currently beeing evaluated, 
     * or <code>null</code>, if there is no such live value.
     */
    static LiveValue<?> s_updating;

    /** a cached value's degree of currency. */
    private enum Currency {
        /** the cached value is outdated. */                  
        dirty,
        
        /** the cached value is currently beeing updated. */
        updating,
        
        /** the cached value is up to date. */
        current
    };
    
    /** the cached value's currency. */
    Currency m_currency;
    
    /** the observables this live value currently depends on. */
    Collection<ObservableValue<?>> m_dependencies 
        = java.util.Collections.emptyList();
    
    /** the computation defining this live value. */
    Computable<T> m_comp;
    

    /** creates this live value. Subclasses must override {@link #is()} 
     * and be prepared to receive calls to it before their constructor is
     * invoked. (This restriction can be circumvented by initializing the 
     * live value in a separate {@link Computable} before passing it to this 
     * class' other constructor) 
     */
    protected LiveValue() {
        super();
        init();
    }
    
    /** creates the live value defined by the supplied {@link Computable}. */
    public LiveValue(Computable<T> computation) {
        m_comp = computation;
        init();
    }

    /**
     * The computation represented by this live value. Uses other observables 
     * and
     * @return the live value's current value
     */
    protected T is() {
        return m_comp.is();        
    }

    // Liveness: If a Live Value is dirty, work() is in progress or 
    // ObservableValue.announce() is currently being executed. In either case
    // work() is executed before control returns outside local code. 
    
    /** thrown to indicate that a cyclic dependency (=non-terminating 
     * recursion) among rules prevents their evaluation. */
    static class CyclicDependencyException extends RuntimeException {
        /** creates it. */
        CyclicDependencyException(LiveValue<?> r) {
            super(r.toString() + " depends on itself (directly or indirectly)");
        }
    }
    
    /** updates the cached result. */
    private void update() {
        assert Thread.holdsLock(s_dirtiesMonitor);
        switch (m_currency) {
            case dirty:
                for (ObservableValue<?> o : m_dependencies) {
                    o.unsubscribe(this);
                }
                m_dependencies = new ArrayList<ObservableValue<?>>();
                
                LiveValue<?> previouslyUpdating = s_updating;
                m_currency = Currency.updating;
                s_updating = this;
                
                T v = is();
                
                m_currency = Currency.current;
                s_updating = previouslyUpdating;
                
                set(v);            
                break;
            case updating:
                assert false;
                break;
            case current:
                break;
            default:
                assert false;
        }
    }
    
    /** invoked by obs.get(). Note the currently updating live value's
     * dependency.
     */
    static void observableGetterInterceptor(ObservableValue<?> obs) {
        if (s_updating != null) {
            assert Thread.holdsLock(s_dirtiesMonitor);
            synchronized (s_dirtiesMonitor) {
                obs.subscribeSilently(s_updating);
                s_updating.m_dependencies.add(obs);                
            }
        }
    }
    
    /** determines the initial result. */
    private void init() {
        synchronized (s_dirtiesMonitor) {
            needsUpdate();
            update();
        }        
    }

    /** marks this live value's cached result for updating. */
    private void needsUpdate() {
        assert Thread.holdsLock(s_dirtiesMonitor);
        m_currency = Currency.dirty;
    }
    
    /** revise the result cached by this live value, i.e. ensure it is still 
     * correct.Invoke this method if non-observable input values may have 
     * changed. 
     * */
    public void revise() {
        synchronized (s_dirtiesMonitor) {
            needsUpdate();
            update();
        }
    }
    

    /** 
     * {@inheritDoc }.
     */
    @Override
    public T get() {
        synchronized (s_dirtiesMonitor) {
            switch (m_currency) {
                case dirty:
                    update();
                    break;
                case updating:
                    throw new CyclicDependencyException(this);
                case current:
                    break;
                default:
                    assert false;
            }
            assert m_currency == Currency.current;
            return super.get();
        }
    }

    /** not intended for public use. */
    // callers must call work() before returning
    public void changed(Object newvalue) {
        synchronized (s_dirtiesMonitor) {
            needsUpdate();
        }
    }    
    
    /** returns a textual description of this live value. */
    public String toString() {
        if (getClass().equals(LiveValue.class)) {
            return "LiveValue defined by: " + m_comp.toString();
        } else {
            return getClass().getName();
        }
    }

    /** not intended for public use. */
    public void notified() {
        synchronized (s_dirtiesMonitor) {
            update();
        }
    }
}
