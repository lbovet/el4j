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

package ch.elca.el4j.tests.util.interfaceenrichment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.RemoteException;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import ch.elca.el4j.tests.util.interfaceenrichment.InterfaceEnrichmentTest.MyArrayInterface;
import ch.elca.el4j.util.interfaceenrichment.EnrichmentDecorator;
import ch.elca.el4j.util.interfaceenrichment.InterfaceEnricher;
import ch.elca.el4j.util.interfaceenrichment.MethodDescriptor;

/**
 * This is the test case for the interface enrichment.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Martin Zeltner (MZE)
 */
public class InterfaceEnrichmentTest {
	/**
	 * Test interface with methods which have an array as parameter.
	 *
	 * @author Martin Zeltner (MZE)
	 */
	@Component  // test whether annotations are copied to wrapper interface
	public interface MyArrayInterface {
		/**
		 * @return Returns the complex array.
		 */
		@Inject // test whether annotations are copied to wrapper interface
		public MyValueObject[] getMyArray();
		
		/**
		 * @param myArray
		 *            Is the complex array to set.
		 */
		public void setMyArray(MyValueObject[] myArray);
	}
	
	public interface SubInterface extends MyArrayInterface {
		public int getInt();
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
			InterfaceEnricher interfaceIndirector = new InterfaceEnricher();
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			
			Class[] changedInterfaces = new Class[extendedInterfaces.length + 1];
			for (int i = 0; i < extendedInterfaces.length; i++) {
				changedInterfaces[i] = interfaceIndirector.createShadowInterfaceAndLoadItDirectly(
					extendedInterfaces[i], this, cl);
			}
			changedInterfaces[extendedInterfaces.length] = Remote.class;
			
			return changedInterfaces;
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
	@Test
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
	
	@Test
	public void testSubInterfaceEnrichmentWithArrayParameter() {
		InterfaceEnricher ie = new InterfaceEnricher();
		Class oldInterface = SubInterface.class;
		EnrichmentDecorator ed = new MyEnrichmentDecorator();
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		
		Class newInterface  = ie.createShadowInterfaceAndLoadItDirectly(
			oldInterface, ed, cl);

		assertNotNull(newInterface);
		assertEquals(ClassUtils.getShortName(oldInterface) + "MyNew",
			ClassUtils.getShortName(newInterface));
		Method[] methods = newInterface.getMethods();
		assertEquals(3, methods.length);
		for (int i = 0; i < methods.length; i++) {
			assertEquals(1, methods[i].getExceptionTypes().length);
			assertEquals(RemoteException.class,
				methods[i].getExceptionTypes()[0]);
		}
		assertEquals(2, newInterface.getInterfaces().length);
		
		// test if parent interface is enriched too
		Class parentInterface = ie.createShadowInterfaceAndLoadItDirectly(
			MyArrayInterface.class, ed, cl);
		assertEquals(parentInterface, newInterface.getInterfaces()[0]);
		assertEquals(Remote.class, newInterface.getInterfaces()[1]);
	}
	
	/**
	 * Tests if enrichment of an interface copies annotations over
	 *  (on methods and on the interface).
	 */
	@Test
	public void testInterfaceEnrichmentWithAnnotations() {
	
		InterfaceEnricher ie = new InterfaceEnricher();
		Class<MyArrayInterface> oldInterface = MyArrayInterface.class;
		EnrichmentDecorator ed = new MyEnrichmentDecorator();
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		
		Class<?> newInterface  = ie.createShadowInterfaceAndLoadItDirectly(
			oldInterface, ed, cl);

		assertNotNull(newInterface);
		
		assertNotNull(newInterface.getAnnotation(Component.class));
		System.out.println(newInterface.getAnnotation(Component.class));
		try {
			Method annotatedMethod = newInterface.getMethod("getMyArray");
			assertNotNull(annotatedMethod.getAnnotation(Inject.class));
			System.out.println(annotatedMethod.getAnnotation(Inject.class));
		} catch (SecurityException e) {
			e.printStackTrace();
			Assert.fail("sec exception " + e);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			Assert.fail("no such method exception " + e);
		}
	}
	
}
