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
package ch.elca.el4j.util.codingsupport;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * Tool for analyzing the classpath in maven projects. 
 * '<code>mvn exec:java</code>' uses an URLClassloader for the main class and
 * passes all project dependencies plus the target directories as URLs to that.
 * This tool allows the list of URLs to be easily retrieved:
 * Call <code>getClassPath(YourMainClass.class)</code> .
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL: https://el4j.svn.sourceforge.net/svnroot/el4j/trunk/el4j/etc/eclipse/codeTemplates.xml $",
 *    "$Revision: 2754 $",
 *    "$Date: 2008-03-04 09:04:15 +0100 (Tue, 04 Mar 2008) $",
 *    "$Author: swismer $"
 * );</script>
 *
 * @author David Bernhard (DBD)
 */
public abstract class ClassloaderTools {

    /**
     * Checks whether a class is loaded via an URLClassLoader,
     * in which case a call to <code>getClassPath</code> will work.
     * @param mainClass The class to check.
     * @return Whether this class was loaded via an URLClassLoader.
     */
    public static boolean isURLLoaded(Class < ? > mainClass) {
        ClassLoader cl = mainClass.getClassLoader();
        return (cl instanceof URLClassLoader);
    }

    /**
     * @param mainClass A class from which to obtain the classloader and its
     * classpath. 
     * @return A comma-separated list of the modules on the current maven
     * classpath.
     * @throws RuntimeException - if the class does not
     * come from an URLClassLoader
     */
    public static String getClassPath(Class < ? > mainClass) {
        ClassLoader cl = mainClass.getClassLoader();
        
        if (!isURLLoaded(mainClass)) {
            throw new RuntimeException(
                "The class " + mainClass
                + " was not loaded via an URLClassLoader.");
        }
        String classpath = "";
        URL[] urls = ((URLClassLoader) cl).getURLs();
            for (URL u : urls) {
                classpath += u.toExternalForm() + ", ";
            }
        // Remove final ", " again.
        classpath = classpath.substring(0, classpath.length() - 2);
        return classpath;
    }
    
    /**
     * @param mainClass A class from which to obtain the classloader and its
     * classpath. 
     * @return A String[] of the modules on the current maven
     * classpath.
     * @throws RuntimeException - if the class does not
     * come from an URLClassLoader
     */
    public static String[] getURLs(Class < ? > mainClass) {
        ClassLoader cl = mainClass.getClassLoader();
        
        if (!isURLLoaded(mainClass)) {
            throw new RuntimeException(
                "The class " + mainClass
                + " was not loaded via an URLClassLoader.");
        }
        URL[] urls = ((URLClassLoader) cl).getURLs();
        String[] urlStrings = new String[urls.length];
        for (int i = 0; i < urls.length; i++) {
            urlStrings[i] = urls[i].toString();
        }
        return urlStrings;
    }
}
