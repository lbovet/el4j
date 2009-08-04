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

package ch.elca.el4j.services.monitoring.jmx;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Iterator;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;

import ch.elca.el4j.core.exceptions.BaseException;
import ch.elca.el4j.core.exceptions.BaseRTException;
import ch.elca.el4j.services.monitoring.jmx.display.DisplayManager;
import ch.elca.el4j.services.monitoring.jmx.display.HtmlDisplayManager;
import ch.elca.el4j.services.monitoring.jmx.display.HtmlTabulator;
import ch.elca.el4j.services.monitoring.jmx.display.Section;
import ch.elca.el4j.services.monitoring.jmx.util.PropertyReflector;
import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;

/**
 * The proxy class for a bean loaded in the ApplicationContext.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Raphael Boog (RBO)
 * @author Rashid Waraich (RWA)
 */
public class SpringBeanMB implements SpringBeanMBMBean {

	/**
	 * The domain of the Spring Bean proxy as it will be registered at the MBean
	 * Server.
	 */
	public static final String SPRING_BEAN_DOMAIN = "SpringBean";

	/**
	 * The name of the domain of the ObjectName.
	 */
	public static final String MBEAN_DOMAIN = "MBean";

	/**
	 * The name of the key of the ObjectName.
	 */
	public static final String MBEAN_KEY = "name";

	/**
	 * Private logger of this class.
	 */
	private static Logger s_logger = LoggerFactory.getLogger(SpringBeanMB.class);

	/**
	 * The Application Context where this Spring Bean is registered at.
	 */
	protected ApplicationContext m_applicationContext;

	/**
	 * The Application Context proxy for the real Application Context.
	 */
	protected ApplicationContextMB m_applicationContextMB;

	/**
	 * The class name of this class.
	 */
	protected Class m_class;

	/**
	 * The Bean Factory belonging to the referenced Application Context.
	 */
	protected BeanFactory m_beanFactory;

	/**
	 * The MBean Server where this Spring Bean is registered at.
	 */
	private MBeanServer m_mBeanServer;

	/**
	 * The bean definition name of this Spring Bean.
	 */
	private String m_name;

	/**
	 * The object name of this Spring Bean proxy.
	 */
	private ObjectName m_objectName;

	/**
	 * Constructor.
	 *
	 * @param name
	 *            The bean definition name of this Spring Bean
	 * @param acMB
	 *            The Application Context proxy
	 * @param ac
	 *            The real Application Context
	 * @param beanFactory
	 *            The Bean Factory belonging to this Application Context
	 * @param mBeanServer
	 *            The MBean Server where this Spring Bean is registered at
	 */
	public SpringBeanMB(String name, ApplicationContextMB acMB,
		ApplicationContext ac, BeanFactory beanFactory,
		MBeanServer mBeanServer) {

		this.m_name = name;
		this.m_applicationContextMB = acMB;
		this.m_applicationContext = ac;
		this.m_mBeanServer = mBeanServer;
		this.m_beanFactory = beanFactory;
		try {
			this.m_class = ac.getType(name);
		} catch (NullPointerException e) {
			// Spring throws a NPE here on some proxied beans.
			// May be a spring bug - in any case, need to catch it.
			this.m_class = null;
		}

	}

	/**
	 * Init method. Sets up this ApplicationContextMB.
	 *
	 * @throws BaseException
	 *             in case the initialization failed
	 */
	public void init() throws BaseException {

		// Set the object name of this object.
		setObjectName();

		// Register the Spring Bean at the MBean Server.
		registerSpringBean();

	}

	/**
	 * Sets the objectName to of this object.
	 */
	public void setObjectName() {

		String name = SPRING_BEAN_DOMAIN
			+ m_applicationContextMB.getInstanceCounter() + ":name="
			+ getName();

		try {
			m_objectName = new ObjectName(name);
		} catch (MalformedObjectNameException e) {
			CoreNotificationHelper.notifyMisconfiguration(
					"The string passed as a parameter does not have"
					+ " the right format.");
		}
	}

	/**
	 * Getter method for the objectName member variable.
	 *
	 * @return The objectName
	 */
	public ObjectName getObjectName() {
		return m_objectName;
	}

	/**
	 * Register this SpringBean at the MBean Server.
	 *
	 * @throws BaseException
	 *             in case the registration at the MBean Server failed
	 */
	protected void registerSpringBean() throws BaseException {

		if (getObjectName() == null) {
			String message = "The object name of the SpringBeanMB '"
				+ this.toString() + "' should not be null.";
			s_logger.error(message);
			throw new BaseRTException(message, (Object[]) null);
		} else {
			try {
				m_mBeanServer.registerMBean(this, getObjectName());
			} catch (InstanceAlreadyExistsException e) {
				String message = "The MBean is already under the "
					+ "control of the MBean server.";
				s_logger.error(message);
				throw new BaseException(message, e);
			} catch (MBeanRegistrationException e) {
				String message = "The MBean will not be registered.";
				s_logger.error(message);
				throw new BaseException(message, e);
			} catch (NotCompliantMBeanException e) {
				String message = "This object is not a JMX compliant"
					+ " MBean.";
				s_logger.error(message);
				throw new BaseException(message, e);
			}
		}
	}

	/**
	 * Getter method for the mBeanServer member variable.
	 *
	 * @return The MBean Server
	 */
	public MBeanServer getMBeanServer() {
		return m_mBeanServer;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		return m_name;
	}

	/**
	 * @return The bean definition for this bean.
	 */
	private BeanDefinition getDefinition() {
		DefaultListableBeanFactory dlbf
			= (DefaultListableBeanFactory) m_beanFactory;
		return dlbf.getBeanDefinition(getName());
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String[] getConfiguration() {
		
		// Extract the property values from the BeanDefinition object.
		MutablePropertyValues mpv = getDefinition().getPropertyValues();

		PropertyValue[] pv = mpv.getPropertyValues();
		String[] result = new String[pv.length];

		for (int i = 0; i < pv.length; i++) {
			result[i] = pv[i].getName() + " = " + pv[i].getValue().toString();
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public ObjectName getApplicationContextMB() {
		return m_applicationContextMB.getObjectName();
	}

	/**
	 * {@inheritDoc}
	 */
	public ObjectName[] getRegisteredMBean() {

		Loader loader = new Loader();

		return loader.getObjectNames(getMBeanServer(), MBEAN_DOMAIN, MBEAN_KEY,
			getName());
	}

	/**
	 * {@inheritDoc}
	 */
	public String getClassName() {
		return m_class.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean getIsSingleton() {
		return m_applicationContext.isSingleton(getName());
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String[] getInterceptors() {
		String[] interceptorNames = null;
		
		Object target =  m_applicationContext.getBean(m_name);
		if (target instanceof Advised) {
			Advised advised = (Advised) target;
			interceptorNames = new String[advised.getAdvisors().length];
			for (int i = 0; i < advised.getAdvisors().length; i++) {
				interceptorNames[i]
					= advised.getAdvisors()[i].getAdvice().getClass().getName();
			}
		}
		
		return interceptorNames;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean getIsProxied() {
		Object target =  m_applicationContext.getBean(m_name);
		if (target instanceof Advised) {
			return true;
		}
		return false;
	}

	/** {@inheritDoc} */
	public String getResourceDescription() {
		return getDefinition().getResourceDescription();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String[] getMethods() {
		MethodReflector r = new MethodReflector(m_class);
		String[] result = new String[r.countMethods()];
		while (r.hasNext()) {
			r.next();
			result[r.getPosition()] = r.getCurrentAsString();
		}
		return result;
	}

	/** {@inheritDoc} */
	public String[] getProperties() {
		Class<?> target = m_class;
		
		// Check if proxied, if so use target class.
		if (getIsProxied()) {
			try {
				Method getTarget = m_class.getMethod("getTargetClass",
					new Class[0]);
				Object bean = m_beanFactory.getBean(m_name);
				target = (Class<?>) getTarget.invoke(bean, (Object[]) null);
			} catch (Exception e) {
				throw new RuntimeException("Exception looking up target.");
			}
		}
		
		try {
			PropertyDescriptor[] pd = Introspector.getBeanInfo(target)
				.getPropertyDescriptors();
			String[] properties = new String[pd.length];
			for (int i = 0; i < pd.length; i++) {
				PropertyReflector r = new PropertyReflector(pd[i]);
				properties[i] = MethodReflector.className(r.getType())
					+ " " + r.getName() + " (" + r.getRW() + ")";
			}
			return properties;
		} catch (IntrospectionException e) {
			throw new RuntimeException("Introspection Exception");
		}
	}
	
	/** {@inheritDoc} */
	public String readProperty(String property) {
		final String err = "The property could not be read, because ";
		String result = "";
		
		// Uppercase the first letter, as the method will expect this.
		String propName = property.substring(0, 1).toUpperCase()
			+ property.substring(1);
		
		try {
			Object bean = m_beanFactory.getBean(m_name);
			Method m = null;
			try {
				m = bean.getClass()
					.getMethod("get" + propName, new Class<?>[0]);
			} catch (NoSuchMethodException e) {
				// Not "get-".
				try {
					m = bean.getClass().getMethod("is" + propName,
						new Class<?>[0]);
				} catch (NoSuchMethodException e2) {
					// This time, we are stuck.
					return err + "no get/is method was found.";
				}
			}
			// String returnType = MethodReflector
			//    .className(m.getReturnType().toString());
			Object o = m.invoke(bean, new Object[0]);
			result = MethodReflector.className(o.getClass().toString())
				+ " " + property + " = " + o.toString();
		} catch (Exception e) {
			result = err + "an excpetion occurred: "
				+ e.toString();
		}
		return result;
	}
	
	/**
	 * @param pd An array of descriptors.
	 * @param makeLinks Whether to make links to get*() methods.
	 * @return A table view of the properties.
	 */
	private String propertyTable(PropertyDescriptor[] pd, boolean makeLinks) {
		String result = "";
		
		HtmlTabulator table = new HtmlTabulator(
			"Name", "Type", "RW", "readMethod", "writeMethod");

		for (PropertyDescriptor current : pd) {
			PropertyReflector r = new PropertyReflector(current);

			String readLink = "";
			if (r.isReadable()) {
				// TODO : What is with the %2B ? A '+' ?
				String readUrl = "/InvokeAction//"
					+ m_objectName.getCanonicalName() + "/action=readProperty"
					+ "?action=readProperty&p1%2Bjava.lang.String="
					+ r.getName();
					/*
					+ (r.getReadMethod().startsWith("get")
						? r.getReadMethod().substring(3)
						: r.getReadMethod().substring(2));
					*/
				readLink = makeLinks
					? "<a href=\"" + readUrl + "\">"
						+ r.getReadMethod() + "</a>"
					: r.getReadMethod();
			}
			
			table.addRow(r.getName(),
						MethodReflector.className(r.getType()),
						"<code>" + r.getRW() + "</code>",
						readLink,
						r.isWritable() ? r.getWriteMethod() : "");
		}

		result += table.tabulate();
		return result;
	}
	
	/**
	 * Display a bean's properties.
	 * @param manager The {@link DisplayManager} to use.
	 */
	public void displayProperties(DisplayManager manager) {
		
		Section section = new Section("Properties");
		
		PropertyDescriptor[] pd;
		Class<?> target = m_class;
		
		// Check if proxied, if so use target class.
		if (getIsProxied()) {
			section.addLine("This bean is proxied");
			try {
				Method getTarget = m_class.getMethod("getTargetClass",
					new Class[0]);
				Object bean = m_beanFactory.getBean(m_name);
				target = (Class<?>) getTarget.invoke(bean, (Object[]) null);
			} catch (Exception e) {
				section.addWarning("Could not find target class, "
					+ e.toString());
			}
		}
		
		try {
			pd = Introspector.getBeanInfo(target).getPropertyDescriptors();
			// Trun off links if proxied.
			// TODO : Can we invoke proxied methods?
			section.add(propertyTable(pd, !getIsProxied()));
			
		} catch (IntrospectionException e) {
			section.addWarning("Introspection Exception.");
		}
		manager.addSection(section);
		
		if (getIsProxied()) {
			// Show the proxy's properties too.
			Section proxySection = new Section("Proxy Properties");
			try {
				proxySection.add(propertyTable(Introspector.getBeanInfo(m_class)
						.getPropertyDescriptors(), true));
			} catch (IntrospectionException e) {
				proxySection.addWarning(
					"Exception looking up proxy properties.");
			}
			manager.addSection(proxySection);
		}
	}
	
	/**
	 * Displays the properties this bean was loaded with.
	 * @param manager The {@link DisplayManager} to add to.
	 */
	public void displayConfiguration(DisplayManager manager) {
		Section section = new Section("Loading Properties");
		
		HtmlTabulator table = new HtmlTabulator("Name", "Value");
		for (String current : getConfiguration()) {
			// Invariant : getConfiguration returns a string of form
			// "key = value" .
			int position = current.indexOf("=");
			// Split at " = "
			String key = current.substring(0, position - 1);
			String value = current.substring(position + 1);
			table.addRow(key, value);
		}
		section.add(table.tabulate());
		manager.addSection(section);
	}
	
	/**
	 * @return Results of bean introspection on this bean as HTML.
	 */
	public String introspect() {
		
		DisplayManager page = new HtmlDisplayManager();

		page.setTitle("Results of introspection on bean "
			+ MethodReflector.className(m_class.toString()));
		
		Section infoSection = new Section("Bean");
		
		HtmlTabulator beanInfo = new HtmlTabulator("Item", "Value");
		beanInfo.addRow("Name", getName());
		beanInfo.addRow("Proxied", Boolean.toString(getIsProxied()));
		beanInfo.addRow("Singleton", Boolean.toString(getIsSingleton()));
		beanInfo.addRow("Defined in", (getResourceDescription() != null)
			? getResourceDescription() : "not available");
		
		infoSection.add(beanInfo.tabulate());
		infoSection.addLine("");
		
		// Interceptors.
		HtmlTabulator iTable = new HtmlTabulator("Interceptors");
		String[] interceptors = getInterceptors();
		if (interceptors == null || interceptors.length == 0) {
			iTable.addRow("none defined");
		} else {
			for (String i : interceptors) {
				iTable.addRow(i);
			}
		}
		infoSection.add(iTable.tabulate());
		
		page.addSection(infoSection);
		
		displayProperties(page);
		
		displayMethods(page);
		
		displayConfiguration(page);
		
		return page.getPage();
	}
	
	/**
	 * HTML listing of all methods in this class.
	 * @param manager The DisplayManager to use.
	 */
	public void displayMethods(DisplayManager manager) {
		Section section = new Section("Methods");
		HtmlTabulator table = new HtmlTabulator(
			"Return", "Name", "Parameters", "Throws");
		
		MethodReflector r = new MethodReflector(m_class);
		while (r.hasNext()) {
			r.next();
			table.addRow(r.getReturns(), r.getName(),
				r.getParameters(), r.getThrows());
		}
		section.add(table.tabulate());
		manager.addSection(section);
	}

	/**
	 * Helper class for investigating a bean's methods.
	 */
	static class MethodReflector implements Iterator<Method> {
		
		/**
		 * The class to reflect on.
		 */
		private Class<?> m_target;
		
		/**
		 * Helper variable for iterator functionality.
		 */
		private int m_current = -1;
		
		/**
		 * The current method of this iterator.
		 * INVARIANT : m is a valid method once next()
		 * has been called at least once.
		 */
		private Method m_method = null;
		
		/**
		 * Creates a MethodReflector from a bean class.
		 * @param target The target class to reflect upon.
		 */
		public MethodReflector(Class<?> target) {
			this.m_target = target;
		}
		
		/**
		 * @return The number of methods in this class.
		 */
		public int countMethods() {
			return m_target.getMethods().length;
		}
		
		/**
		 * @return The current iterator position.
		 */
		public int getPosition() {
			return m_current;
		}

		/** {@inheritDoc} */
		public boolean hasNext() {
			return (m_current < countMethods() - 1);
		}

		/** {@inheritDoc} */
		public Method next() {
			if (!hasNext()) {
				throw new IndexOutOfBoundsException();
			}
			m_current++;
			m_method = m_target.getMethods()[m_current];
			return m_method;
		}

		/**
		 * DO NOT USE!
		 * {@inheritDoc}
		 */
		public void remove() {
			throw new RuntimeException("Cannot remove methods.");
		}

		/**
		 * @return The return type of the current method.
		 */
		public String getReturns() {
			return className(m_method.getReturnType().toString());
		}
		
		/**
		 * @return The name of the current method.
		 */
		public String getName() {
			return m_method.getName();
		}
		
		/**
		 * Strips "class" or "interface" from a string.
		 * @param name A class/interface name.
		 * @return The name with its prefix stripped.
		 */
		public static String className(String name) {
			final String[] prefixes = new String[] {"class", "interface"};
			String result = name;
			
			for (String current : prefixes) {
				if (result.startsWith(current)) {
					result = result.substring(current.length() + 1);
				}
			}
			return result;
		}
		
		/**
		 * @param classes An array of {@link Class} objects.
		 * @return A comma-separated list of their names.
		 */
		public static String getClassNames(Class<?>[] classes) {
			String result = "";
			for (Class<?> p : classes) {
				String param = className(p.toString());
				
				result += param + ", ";
			}
			if (result.endsWith(", ")) {
				result = result.substring(0, result.length() - 2);
			}
			return result;
		}
		
		/**
		 * @return The parameters of the current method.
		 */
		public String getParameters() {
			return getClassNames(m_method.getParameterTypes());
		}
		
		/**
		 * @return The declared exceptions of the current method.
		 */
		public String getThrows() {
			return getClassNames(m_method.getExceptionTypes());
		}
		
		/**
		 * @return A string describing the curent method.
		 */
		public String getCurrentAsString() {
			
			return getReturns() + " " + getName()
				+ "(" + getParameters() + ")"
				+ ((getThrows().equals(""))
					? ("") : (" throws " + getThrows()));
		}
		
	}
}
