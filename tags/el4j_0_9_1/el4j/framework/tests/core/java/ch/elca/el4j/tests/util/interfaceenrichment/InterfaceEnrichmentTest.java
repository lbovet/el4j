/*
 * EL4J, the Enterprise Library for Java, complementing Spring http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * This program is published under the GNU General Public License (GPL) license.
 * http://www.gnu.org/licenses/gpl.txt
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */

package ch.elca.el4j.tests.util.interfaceenrichment;

import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.RemoteException;

import org.springframework.util.ClassUtils;

import ch.elca.el4j.util.interfaceenrichment.EnrichmentDecorator;
import ch.elca.el4j.util.interfaceenrichment.InterfaceEnricher;
import ch.elca.el4j.util.interfaceenrichment.MethodDescriptor;

import junit.framework.TestCase;

/**
 * This is the test case for the interface enrichment.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public class InterfaceEnrichmentTest extends TestCase {
    /**
     * Test interface with methods which have an array as parameter.
     *
     * @author Martin Zeltner (MZE)
     */
    public interface MyArrayInterface {
        /**
         * @return Returns the complex array.
         */
        public MyValueObject[] getMyArray();
        
        /**
         * @param myArray
         *            Is the complex array to set.
         */
        public void setMyArray(MyValueObject[] myArray);
    }
    
    /**
     * Test value object, which is used in test interface above.
     *
     * @author Martin Zeltner (MZE)
     */
    public static class MyValueObject {
        /**
         * Property a.
         */
        private String m_a;

        /**
         * Property b.
         */
        private String m_b;
        
        /**
         * Property c.
         */
        private String m_c;
        
        /**
         * @return Returns the a.
         */
        public final String getA() {
            return m_a;
        }

        /**
         * @param a
         *            The a to set.
         */
        public final void setA(String a) {
            m_a = a;
        }

        /**
         * @return Returns the b.
         */
        public final String getB() {
            return m_b;
        }

        /**
         * @param b
         *            The b to set.
         */
        public final void setB(String b) {
            m_b = b;
        }

        /**
         * @return Returns the c.
         */
        public final String getC() {
            return m_c;
        }

        /**
         * @param c
         *            The c to set.
         */
        public final void setC(String c) {
            m_c = c;
        }
    }
    
    /**
     * Test enrichment decorator.
     *
     * @author Martin Zeltner (MZE)
     */
    public static class MyEnrichmentDecorator 
        implements EnrichmentDecorator {

        /**
         * {@inheritDoc}
         */
        public String changedInterfaceName(String originalInterfaceName) {
            return originalInterfaceName + "MyNew";
        }

        /**
         * {@inheritDoc}
         */
        public Class[] changedExtendedInterface(Class[] extendedInterfaces) {
            return new Class[] {Remote.class};
        }

        /**
         * {@inheritDoc}
         */
        public MethodDescriptor changedMethodSignature(
            MethodDescriptor method) {
            method.setThrownExceptions(new Class[] {RemoteException.class});
            return method; 
        }
    }

    /**
     * Tests if enrichment of an interface with array parameter and array return
     * type works correctly. 
     */
    public void testInterfaceEnrichmentWithArrayParameter() {
        InterfaceEnricher ie = new InterfaceEnricher();
        Class oldInterface = MyArrayInterface.class;
        EnrichmentDecorator ed = new MyEnrichmentDecorator();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        
        Class newInterface  = ie.createShadowInterfaceAndLoadItDirectly(
            oldInterface, ed, cl);

        assertNotNull(newInterface);
        assertEquals(ClassUtils.getShortName(oldInterface) + "MyNew", 
            ClassUtils.getShortName(newInterface));
        Method[] methods = newInterface.getMethods();
        assertEquals(2, methods.length);
        for (int i = 0; i < methods.length; i++) {
            assertEquals(1, methods[i].getExceptionTypes().length);
            assertEquals(RemoteException.class, 
                methods[i].getExceptionTypes()[0]);
        }
        assertEquals(1, newInterface.getInterfaces().length);
        assertEquals(Remote.class, newInterface.getInterfaces()[0]);
    }
}
