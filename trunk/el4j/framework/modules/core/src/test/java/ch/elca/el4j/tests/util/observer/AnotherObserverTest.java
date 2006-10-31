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

import ch.elca.el4j.util.observer.ObservableValue;
import ch.elca.el4j.util.observer.ValueObserver;
import ch.elca.el4j.util.observer.impl.Computable;
import ch.elca.el4j.util.observer.impl.LiveValue;
import ch.elca.el4j.util.observer.impl.SettableObservableValue;

import junit.framework.TestCase;

/**
 * Another test case for the observer package.
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
public class AnotherObserverTest extends TestCase 
                              implements ValueObserver<Integer> {
    
    /** the size of this test. Must be in 1..32 */
    static final int SIZE = 30;
    
    
    /** the first value. */
    SettableObservableValue<Integer> m_origin;
    
    /** the list of all values. */
    ObservableValue<Integer>[] m_values;
    
    /** the last value. */
    ObservableValue<Integer> m_last;
    
    /** counts the number of change notifications received. */
    int m_notified;
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void setUp() throws Exception {
        m_origin = new SettableObservableValue<Integer>(0);

        m_values = (ObservableValue<Integer>[]) new ObservableValue[SIZE];
        m_values[0] = m_origin;
        for (int i = 1; i < m_values.length; i++) {
            Sum s = new Sum();
            s.m_i = i;
            m_values[i] = new LiveValue(s);
        }

        m_last = m_values[m_values.length - 1];
        
        m_notified = 0;
    }
    
    /***/
    class Sum implements Computable<Integer> {
        /***/
        int m_i;
        
        /**
         * returns the sum of all values with index smaller {@code i}.
         * @return see above
         */
        public Integer is() {
            int result = 0;
            for (int j = 0; j < m_i; j++) {
                result += m_values[j].get();
            }
            return result;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void changed(Integer newRef) {
        m_notified++;
    }
    
    /** tests update propagation. */
    public void testUpdate() {
        assertEquals("to do" , 0, m_notified);

        m_last.subscribe(this);
        assertEquals(0, (int) m_last.get());
        assertEquals(1, m_notified);
        
        m_origin.set(1);
        assertEquals(2, m_notified);
        assertEquals(
            1 << (m_values.length - 2),
            (int) m_last.get()
        );
    }
    
    /** tests unsubscription. */
    public void testUnsubscribe() {
        m_last.subscribe(new ValueObserver<Integer>() {
            public void changed(Integer newRef) {
                if (m_origin.get() == 0) {
                    m_last.unsubscribe(this);
                } else {
                    fail("Notification received after unsubscription.");
                }
            }
        });
        
        m_origin.set(1);
    }
}
