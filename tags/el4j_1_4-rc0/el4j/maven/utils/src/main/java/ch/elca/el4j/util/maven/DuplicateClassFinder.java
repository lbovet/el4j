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

package ch.elca.el4j.util.maven;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A tool for looking up duplicate class definitions in the classpath. 
 * It can also be used to inspect all class definitions loaded.
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
public class DuplicateClassFinder {

    /*
     * m_classes maps a fully qualified class name to a list of definitions.
     * Any time a class is redefined, its name is added to m_duplicates.
     * Invariant : m_duplicates contains exactly those class names that have
     * more than one definition.
     */
    
    /** The logger. */
    private static final Log s_log = LogFactory.getLog("DuplicateClassFinder");
    
    /** Holds all class names seen so far. */
    private Map<String, List<String>> m_classes;
    
    /** Holds urls to search for class definitions. */
    private List<URL> m_urls;
    
    /** Holds the names of duplicated classes. */
    private Set<String> m_duplicates;
    
    /** Whether we have completed the search yet. */
    private boolean m_searched = false;
    
    /**
     * Default constructor - initialize the members.
     */
    public DuplicateClassFinder() {
        m_classes = new HashMap<String, List<String>>();
        m_urls = new LinkedList<URL>();
        m_duplicates = new HashSet<String>();
    }
    
    /**
     * Helper to check that a search has been executed.
     * @param value The value to ckeck against.
     * @throws RuntimeException If the status is not correct.
     */
    private void assertSearched(boolean value) throws RuntimeException {
        if (!m_searched && value) {
            throw new RuntimeException("You must execute search() before "
                + "performing this operation.");
        }
        if (m_searched && !value) {
            throw new RuntimeException("You cannot perform this poeration "
                + "once a search has been performed.");
        }
    }
    
    /**
     * Output a warning when a duplicate is found.
     * @param className The class that is duplicated.
     * @param oldLoc The existing location.
     * @param newLoc The new location.
     */
    private void warnDuplicate(String className, String oldLoc, String newLoc) {
        s_log.warn("The class " + className + " is duplicated:");
        s_log.warn("Last seen at " + oldLoc + " , duplicated in " + newLoc);
    }
    
    /**
     * Add an URL to the search path.
     * @param url The URL to add.
     */
    public void addUrl(URL url) {
        assertSearched(false);
        s_log.debug("Added URL " + url);
        m_urls.add(url);
    }
    
    /**
     * Add all URLs of a given classloader to the search path.
     * @param cl The classloader.
     */
    public void addURLClassLoader(URLClassLoader cl) {
        for (URL url : cl.getURLs()) {
            addUrl(url);
        }
    }
    
    /**
     * Add a class's classloader search path. If the class is loaded by an
     * {@link URLClassLoader}, add its URLs. If not, add the system classpath.
     * @param c The class to add.
     */
    public void addClass(Class<?> c) {
        ClassLoader cl = c.getClassLoader();
        if (cl instanceof URLClassLoader) {
            addURLClassLoader((URLClassLoader) cl);
        } else {
            addSystemClassPath();
        }
    }
    
    /**
     * Add the system class path to the search path.
     */
    public void addSystemClassPath() {
        String cp = System.getProperty("java.class.path");
        String[] entries = cp.split(System.getProperty("path.separator"));
        for (String entry : entries) {
            try {
                addUrl(new URL(entry));
            } catch (MalformedURLException e) {
                throw new RuntimeException("Error adding system cp to urls.");
            }
        }
    }
    
    /**
     * Search the added urls for classes.
     */
    public void search() {
        assertSearched(false);
        m_searched = true;

        s_log.info("Searching ...");
        Iterator<URL> i = m_urls.iterator();
        
        // Iterate over all urls. If a directory or jar file, recurse.
        // Anything else is "unhandled"
        while (i.hasNext()) {
            URL next = i.next();
            
            if (!next.getProtocol().equalsIgnoreCase("FILE")) {
                s_log.warn("The entry " + next + "is unhandled.");
                return;
            } 
            
            if (next.toString().endsWith(".jar")) {
                searchJar(next);
            } else {
                searchDirectory(next);
            }
        }
        report();
    }
    
    /**
     * Display the search results.
     */
    public void report() {
        assertSearched(true);
        if (m_duplicates.isEmpty()) {
            s_log.info("The search was successful, no classes are duplicated.");
            return;
        }
    
        s_log.warn("The search found " + m_duplicates.size() 
            + " duplicated classes:");
        for (Iterator<String> i = m_duplicates.iterator(); i.hasNext();) {
            String current = i.next();
            List<String> theList = m_classes.get(current);
            s_log.warn("  Class " + current + " appears " 
                + theList.size() + " times, at:");
            for (String location : theList) {
                s_log.warn("    " + location);
            }
        }
    }
    
    /**
     * Search a base directory from the classpath. Ensures it is a directory,
     * then delegates to recurseDirectory.
     * @param url The directory.
     */
    private void searchDirectory(URL url) {
        s_log.info("Searching Base Directory: " + url);
        try {
            File baseDir = new File(url.toURI());
            if (!baseDir.isDirectory()) {
                s_log.warn("File " + url + " is not a directory.");
                return;
            }
            recurseDirectory(baseDir.getAbsolutePath(), "");
        } catch (URISyntaxException e) {
            throw new RuntimeException("URL syntax error", e);
        }
    }
    
    /**
     * Recursively search a directory, adding all .class files and recursing
     * into subdirectories.
     * @param base The base directory (classpath entry)
     * @param rel the current relative path and package prefix.
     */
    private void recurseDirectory(String base, String rel) {
        if (!rel.equals("")) {
            s_log.info("Recursing into: " + rel);
        }
        File[] files = new File(base + rel).listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                recurseDirectory(base, rel + "/" + file.getName());
            } else if (file.getName().endsWith(".class")) {
                addClassFile(file, rel);
            }
        }
    }
    
    /**
     * Add a .class file from the filesystem. Delegates to addClass.
     * @param file The class file.
     * @param rel The relative path and package name.
     */
    private void addClassFile(File file, String rel) {
        String pkg = rel;
        pkg = pkg.replaceAll("/", ".");
        pkg = pkg.replaceAll("\\\\", "."); 
        if (pkg.startsWith(".")) {
            pkg = pkg.substring(1);
        }
        String name = file.getName();
        name = name.substring(0, name.length() - ".class".length());
        addClass(pkg, name, file.getAbsolutePath());
    }
    
    /**
     * The method that actually adds classes. Check if it exists;
     * if not add it, if it does add it to duplicates and warn.
     * @param pkg The package name
     * @param name The class name
     * @param location The location of this .class file.
     */
    private void addClass(String pkg, String name, String location) {
        String className = pkg.equals("") ? name : pkg + "." + name;
        s_log.info("Adding class " + className + "  from " + location);
        if (m_classes.get(className) != null) {
            // OOPS! It exists already.
            // Warn, and link in to the list of duplicates.
            List<String> theList = m_classes.get(className);
            warnDuplicate(className, theList.get(theList.size() - 1), location);
            theList.add(location);
            m_duplicates.add(className);
        } else {
            // It's new. Create its list and add it.
            List <String> newList = new LinkedList<String>();
            newList.add(location);
            m_classes.put(className, newList);
        }
    }
    
    /**
     * Search a .jar file and add all .class files in it.
     * @param url The jar's url.
     */
    private void searchJar(URL url) {
        s_log.info("Searching jar: " + url);
        try {
            URL jar = new URL("jar:" + url.toExternalForm() + "!/");
            JarURLConnection conn = (JarURLConnection) jar.openConnection();
            JarFile jarFile = conn.getJarFile();
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry e = entries.nextElement();
                if (e.getName().endsWith(".class")) {
                    String path = e.getName(); 
                    // For package name, strip ".class" and
                    // change foo/bar to foo.bar
                    String pkg  
                        = path.substring(0, path.length() - ".class".length())
                            .replaceAll("/", ".");
                    if (pkg.startsWith(".")) {
                        pkg = pkg.substring(1);
                    }
                    int splitter = pkg.lastIndexOf(".");
                    String name;
                    if (splitter != -1) {
                        name = pkg.substring(splitter + 1);
                        pkg = pkg.substring(0, splitter);
                    } else {
                        // top-level package
                        name = pkg;
                        pkg = "";
                    }
                    addClass(pkg, name, jarFile.getName() + "!/" + path);
                }
            }
            
        } catch (IOException e) {
            throw new RuntimeException("IO Exception reading jar: " + url);
        }
    }
    
    /**
     * @return An iterator over all found classes, for report purposes.
     */
    Iterator<String> iterator() {
        assertSearched(true);        
        return m_classes.keySet().iterator();
    }
    
    /**
     * Looks up whether a class name exists.
     * @param name The fully qualified class name.
     * @return boolean.
     */
    public boolean hasClass(String name) {
        assertSearched(true);
        return (m_classes.get(name) != null);
    }
    
    /**
     * Returns all locations a class is defined at.
     * @param name The class name.
     * @return A list of locations.
     */
    public List<String> getLocations(String name) {
        if (!hasClass(name)) {
            return null;
        }
        return m_classes.get(name);
    }
    
    /**
     * @return Whether any duplicates were found.
     */
    public boolean duplicatesFound() {
        assertSearched(true);
        return (m_duplicates != null && m_duplicates.size() > 0); 
    }
    
    /**
     * @return A set of strings describing duplicated classes.
     */
    public Set<String> getAllDuplicates() {
        assertSearched(true);
        return m_duplicates;
    }
}
