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

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Map.Entry;

import org.theshoemakers.which4j.Which4J;

/**
 * Helper class which provides the functionality used through the ClassLoaderMBean.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Simon Stelling (SST)
 */
public class ClassLoaderInvestigator {
	
	/**
	 * The class investigated by this Investigator.
	 */
	private Class<?> inspectedClass;
	
	/**
	 * Sole constructor.
	 * @param className the name of the class to investigate.
	 */
	public ClassLoaderInvestigator(String className) throws ClassNotFoundException {
		inspectedClass = Class.forName(className);
	}
	
	/**
	 * @return the classloader hierarchy of the investigated class. the first element is the lowest
	 * in the hierarchy, the topmost is the loader which has the bootstrap classloader as parent.
	 */
	public String[] getClassLoaderHierarchy() {
		List<String> loaders = new ArrayList<String>();
		ClassLoader currentLoader = inspectedClass.getClassLoader();
		while (currentLoader != null) {
			loaders.add(currentLoader.getClass().getName());
			currentLoader = currentLoader.getParent();
		}
		return loaders.toArray(new String[0]);
	}

	/**
	 * @return classpath obtained from Which4J for the inspected class
	 */
	public String getClassPathForInspectedClass() {
		return Which4J.which(inspectedClass);
	}
	
	/**
	 * @return classpath obtained from Which4J for the inspected class using its ClassLoader
	 */
	public String getClassPathForInspectedClassUsingItsLoader() {
		return Which4J.which(inspectedClass, inspectedClass.getClassLoader());
	}
	
	/**
	 * @return the system properties as an array of 2-element arrays
	 */
	public String[][] getProperties() {
		Properties p = System.getProperties();
		String[][] list = new String[2][p.entrySet().size()];
		int i = 0;
		for (Entry<Object, Object> e : p.entrySet()) {
			list[i] = new String[] {e.getKey().toString(), e.getValue().toString()};
			i++;
		}
		return list;
	} 

}
