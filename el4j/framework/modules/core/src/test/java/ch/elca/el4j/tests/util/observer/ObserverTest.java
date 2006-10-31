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
package ch.elca.el4j.tests.util.observer;

import ch.elca.el4j.util.observer.ValueObserver;
import ch.elca.el4j.util.observer.impl.Computable;
import ch.elca.el4j.util.observer.impl.LiveValue;
import ch.elca.el4j.util.observer.impl.SettableObservableValue;

import junit.framework.TestCase;

/**
 * An observer test.
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
public class ObserverTest extends TestCase implements ValueObserver<Boolean> {
    /** the inputs. */
    SettableObservableValue<Boolean> m_a, m_b, m_c;
    
    /** the intermediary nodes/outputs. */
    LiveValue<Boolean> m_d, m_e, m_f, m_g;
    
    /** the computable for {@code g}. */
    Computable<Boolean> m_comp;
    
    /** counts the number of change notifications received. */
    int m_notified;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUp() throws Exception {
        m_a = new SettableObservableValue<Boolean>(false);
        m_b = new SettableObservableValue<Boolean>(false);
        m_c = new SettableObservableValue<Boolean>(false);
        m_d = new LiveValue<Boolean>() {
            @Override
            protected Boolean is() {
                return m_a.get() != m_b.get(); 
            }            
        };
        m_e = new LiveValue<Boolean>() {
            @Override
            protected Boolean is() {
                return m_b.get() != m_c.get(); 
            }                        
        };
        m_f = new LiveValue<Boolean>() {
            @Override
            protected Boolean is() {
                return m_a.get() ? m_g.get() : m_b.get();
            }                        
        };
        m_comp = new Computable<Boolean>() {
            public Boolean is() {
                return (m_c.get() ? m_f.get() : m_e.get());
            }                                    
        };
        m_g = new LiveValue<Boolean>(m_comp);
        m_notified = 0;
    }
    
    /** tests subscription. */
    public void testSubscribe() {
        assertEquals(0, m_notified);
        m_g.subscribe(this);
        assertEquals(1, m_notified);
    }
    
    /** tests silent subscription. */
    public void testSilentSubscribe() {
        m_g.subscribeSilently(this);
        assertEquals(m_notified, 0);
    }

    /** tests propagation of changes. */
    public void testInputChange() {
        m_g.subscribe(this);
        check();
        m_a.set(true);
        check();
        m_b.set(true);
        check();
        m_a.set(false);
        check();
        m_b.set(false);
        check();
        m_c.set(true);
        check();
    }
    
    /** asserts that the state is consistent. */
    private void check() {
        check(m_g.get());
    }
    
    /**
     * asserts that the state is consistent.
     * @param newRef {@code g}'s current value
     */
    private void check(Boolean newRef) {
        assertEquals(m_notified % 2 == 0, (boolean) newRef);
        assertEquals(m_comp.is(), newRef);
    }
    
    /**
     * {@inheritDoc}
     */
    public void changed(Boolean newRef) {
        m_notified++;        
        check(newRef);
    }
}
