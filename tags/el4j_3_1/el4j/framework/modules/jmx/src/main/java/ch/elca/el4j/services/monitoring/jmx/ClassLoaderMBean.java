/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2010 by ELCA Informatique SA, Av. de la Harpe 22-24,
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

import java.util.HashMap;
import java.util.Map;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenMBeanAttributeInfoSupport;
import javax.management.openmbean.OpenMBeanConstructorInfoSupport;
import javax.management.openmbean.OpenMBeanInfoSupport;
import javax.management.openmbean.OpenMBeanOperationInfoSupport;
import javax.management.openmbean.OpenMBeanParameterInfoSupport;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Provides functionality to investigate the ClassLoaders and Classpaths in use.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Simon Stelling (SST)
 */
public class ClassLoaderMBean implements DynamicMBean {

	/**
	 * private logger.
	 */
	private static Logger s_logger = LoggerFactory.getLogger(ClassLoaderMBean.class);
	
	/**
	 * type of the return value of inspectClass(). initialized in static block.
	 */
	private static CompositeType s_propertiesType = null;

	/**
	 * type of the return value of getClassLoadersComplexType(). initialized in static block.
	 */
	private static TabularType s_tabularType = null;
	
	/**
	 * type of a tabular data row. initialized in static block.
	 */
	private static CompositeType s_itemType = null;
	
	/**
	 * property identifier for the classloader hierarchy.
	 */
	private static final String CLASSLOADER_HIERARCHY = "classloader hierarchy";
	
	/**
	 * property identifier for the classpath.
	 */
	private static final String CLASSPATH = "classpath";
	
	/**
	 * property identifier for the classpath found using the investigated class' classloader.
	 */
	private static final String CLASSPATH_USING_CLASSLOADER = "classpath using investigated class' ClassLoader";
	
	/**
	 * Lists all property names.
	 */
	private static String[] s_names 
		= {CLASSLOADER_HIERARCHY, CLASSPATH, CLASSPATH_USING_CLASSLOADER };
	
	/**
	 * initialized in static block.
	 */
	@SuppressWarnings("unchecked")
	private static OpenType[] s_types = null; 
	
	/**
	 * Lists all property descriptions.
	 */
	private static String[] s_descriptions 
		= {CLASSLOADER_HIERARCHY, CLASSPATH, CLASSPATH_USING_CLASSLOADER };
	
	/**
	 * OMBInfo used by JMX to interpret the return value of inspectClass() correctly.
	 */
	private OpenMBeanInfoSupport ombInfo;
	
	/**
	 * Initialzies s_itemType.
	 */
	static {
		try {
			
			s_itemType = new CompositeType("classloader", "classloader name", new String[] {"classloader"}, 
				new String[] {"classloader"}, new OpenType[] {SimpleType.STRING});
			s_tabularType = new TabularType(
				"classloader", "the classloader's name", s_itemType, new String[] {"classloader"});
			
			s_types = new OpenType[] {s_tabularType, SimpleType.STRING, SimpleType.STRING };
			
			s_propertiesType = new CompositeType(
				"properties", "a list of classloader-specific property", s_names, s_descriptions, s_types);
			
		} catch (OpenDataException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	/**
	 * Default Constructor.
	 */
	public ClassLoaderMBean() {
		// build OMBInfo
		OpenMBeanAttributeInfoSupport[] attributes = new OpenMBeanAttributeInfoSupport[0]; 
		OpenMBeanConstructorInfoSupport[] constructors = new OpenMBeanConstructorInfoSupport[1]; 
		OpenMBeanOperationInfoSupport[] operations = new OpenMBeanOperationInfoSupport[1];
		MBeanNotificationInfo[] notifications = new MBeanNotificationInfo[0];
		
		constructors[0] = new OpenMBeanConstructorInfoSupport("ClassLoaderMBean", "constructs a ClassLoaderMBean", 
			new OpenMBeanParameterInfoSupport[0]);
		
		
		OpenMBeanParameterInfoSupport[] params = new OpenMBeanParameterInfoSupport[1]; 
		params[0] = new OpenMBeanParameterInfoSupport(
			"classname", "the name of the class to investigate", SimpleType.STRING);
		operations[0] = new OpenMBeanOperationInfoSupport(
			"inspectClass", "inspects the given class", params, s_propertiesType, MBeanOperationInfo.INFO);
		ombInfo = new OpenMBeanInfoSupport(this.getClass().getName(), 
			"ClassLoader Inspector", attributes, constructors, operations, notifications);
	}
	
	/**
	 * Method available via JMX interface.
	 * <p>
	 * <em>Notes:</em>
	 * As this method takes a class<b>name</b>, it has to load the class object of the class first.
	 * This is done by invoking the ClassLoaderMBean's ClassLoader and asking it to load the the
	 * class for the given name. If you are experiencing ClassLoader issues and the class where you
	 * observe them was not loaded by the same classloader hierarchy as the ClassLoaderMBean, the
	 * information returned by this method is probably not of much use to you.
	 * 
	 * @param className the name of the class to investigate
	 * @return a CompositeData object containing all investigation information.
	 * @throws ClassNotFoundException 
	 */
	private CompositeData inspectClass(String className) throws ClassNotFoundException {
		ClassLoaderInvestigator helper = new ClassLoaderInvestigator(className);
		
		Map<String, Object> items = new HashMap<String, Object>();
		
		items.put(CLASSLOADER_HIERARCHY, getClassLoadersComplexType(helper.getClassLoaderHierarchy()));
		items.put(CLASSPATH, helper.getClassPathForInspectedClass());
		items.put(CLASSPATH_USING_CLASSLOADER, helper.getClassPathForInspectedClassUsingItsLoader());
		
		try {
			return new CompositeDataSupport(s_propertiesType, items);
		} catch (OpenDataException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param classLoaderHierarchy the string array containing all classloader names
	 * @return the TabularData containing the same information
	 */
	private TabularData getClassLoadersComplexType(String[] classLoaderHierarchy) {
		TabularDataSupport t = new TabularDataSupport(s_tabularType);
		for (String classloader : classLoaderHierarchy) {
			Map<String, String> item = new HashMap<String, String>();
			item.put("classloader", classloader);
			try {
				t.put(new CompositeDataSupport(s_itemType, item));
			} catch (OpenDataException e) {
				// won't happen
				s_logger.error("Unexpected OpenDataException: ", e);
			}
		}
		
		return t;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getAttribute(String attribute) 
		throws AttributeNotFoundException, MBeanException, ReflectionException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AttributeList getAttributes(String[] attributes) {
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public MBeanInfo getMBeanInfo() {
		return ombInfo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException,
		ReflectionException {
		try {
			if ("inspectClass".equals(actionName)) {
				if ((params.length == 1 && params[0] instanceof String)) {
					try {
						return inspectClass((String) params[0]);
					} catch (ClassNotFoundException e) {
						s_logger.error("exception occurred: ", e);
						throw new MBeanException(e);
					}
				}
			}
			throw new RuntimeException("method not found");
		} catch (NullPointerException e) {
			s_logger.error("exception occurred: ", e);
			throw e;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException,
		MBeanException, ReflectionException {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AttributeList setAttributes(AttributeList attributes) {
		return null;
	}
	
}
