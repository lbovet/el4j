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
package ch.elca.el4j.maven.plugins.abbot;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import abbot.script.Script;

/**
 * Mojo for executing abbot tests.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL: https://el4j.svn.sourceforge.net/svnroot/el4j/trunk/el4j/etc/eclipse/codeTemplates.xml $",
 *    "$Revision: 2754 $",
 *    "$Date: 2008-03-04 09:04:15 +0100 (Tue, 04 Mar 2008) $",
 *    "$Author: swismer $"
 * );</script>
 *
 * @author David Bernhard (DBD)
 *
 * @goal abbot
 */
public class AbbotMojo extends AbstractMojo {
    
    /* Maven parameters */
    
    /** 
     * The default test output directory.
     * @parameter expression="${project.build.testOutputDirectory}"
     */
    private String m_outputDirectory;
    
    /**
     * The directory where test scripts are stored.
     * @parameter
     */
    private String testScriptDirectory;
    
    /**
     * The project we are dealing with.
     * @parameter expression="${project}"
     */
    private MavenProject m_project;

    /* Other parameters */
       
    /**
     * The default subfolder for abbot scripts.
     */
    private String m_defaultRelativeDirectory = "/abbot";
    
    /** 
     * The main method, called by maven. 
     * @throws MojoExecutionException From called functions.
     */
    public void execute() throws MojoExecutionException {
    
        // Set up the classpath/-loader for abbot. 
        URL[] runtimeCP = getClasspath(m_project);
        URLClassLoader myClassLoader 
            = (URLClassLoader) Thread.currentThread().getContextClassLoader();
        URL[] myCP = myClassLoader.getURLs();
        URL[] theCP = mergeArrays(runtimeCP, myCP);
        
        ClassLoader abbotCL = new URLClassLoader(theCP);
        
        /*
         * This does not seem to have any effect.
        try {
            // Switch classloader.
            Thread.currentThread().setContextClassLoader(abbotCL);
            */
        
        runTests(abbotCL);
        
            /*
        } finally {
            // Restore before exiting.
            Thread.currentThread().setContextClassLoader(myClassLoader);
        }
        */
    }
    
    /**
     * @return The test scripts in the directory provided, or the default one.
     * @throws MojoExecutionException If the directory does not exist.
     */
    private List<String> findScripts() throws MojoExecutionException {
        // Get the script directory.
        if (testScriptDirectory == null) {
            // Default value.
            testScriptDirectory 
                = m_outputDirectory + m_defaultRelativeDirectory;
        }
        
        File testDir = new File(testScriptDirectory);
        if (!testDir.isDirectory()) {
            throw new MojoExecutionException("Abbot error: ["
                + testScriptDirectory + "] is not a directory.");
        }
        
        return findFilesIn(testDir);
    }
    
    /** 
     * Run all the tests in the given directory through JUnit. 
     * @param abbotCL The ClassLoader to use for tests.
     * @throws MojoExecutionException If a test fails.
     */
    public void runTests(ClassLoader abbotCL) throws MojoExecutionException {
        
        List<String> scripts = findScripts(); 

        Class<?> testClass;
        Class<?> runnerClass;
        Class<?> suiteClass;
        Constructor<?> testConstructor;
        Method runMethod;
        Object suite;
        Method addMethod;
        try {
            testClass = abbotCL.loadClass(
                "junit.extensions.abbot.ScriptFixture");
            testConstructor = testClass.getConstructor(String.class);
            runnerClass = abbotCL.loadClass("junit.textui.TestRunner");
            runMethod = runnerClass.getMethod("run", 
                abbotCL.loadClass("junit.framework.Test"));
            suiteClass = abbotCL.loadClass("junit.framework.TestSuite");
            suite = suiteClass.newInstance();
            addMethod = suiteClass.getMethod("addTest", 
                abbotCL.loadClass("junit.framework.Test"));
        } catch (Throwable e) {
            getLog().error("Fatal error loading class.");
            throw new RuntimeException("Invalid class.", e);
        }

        // Run the tests.
        getLog().info("Running abbot tests.");
        
        for (String current : scripts) {
            getLog().info("Abbot test script: " + current);
         
            // Test test = new ScriptFixture(current);
            // TestRunner.run(test);

            try {
                Object test = testConstructor.newInstance(current);
                // suite.addTest(test)
                addMethod.invoke(suite, test); 
            } catch (Exception e) {
                throw new RuntimeException("Cannot create a test.", e);
            }

        }

        try {
            // TestRunner.run(suite)
            runMethod.invoke(null, suite); 
        } catch (Exception e) {
            throw new MojoExecutionException("Error during test: " + e);
        }
        
        getLog().info("Abbot tests complete.");
        
    }
    
    /**
     * @param first An URL[].
     * @param second An URL[].
     * @return The two parameters concatenated into one array.
     */
    private URL[] mergeArrays(URL[] first, URL[] second) {
        URL[] result = new URL[first.length + second.length];
        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
    
    /**
     * @param mProject The current project.
     * @return An array of URLs representing the project's classpath.
     */
    @SuppressWarnings("unchecked")
    private URL[] getClasspath(MavenProject mProject) 
        throws MojoExecutionException {
        
        try {
            List<String> runtimeCPStrings 
                = mProject.getRuntimeClasspathElements();
                    
            List<URL> urls = new LinkedList<URL>();
            
            for (String currentString : runtimeCPStrings) {
                File currentFile = new File(currentString);
                URL currentURL = currentFile.toURL();
                urls.add(currentURL);
            }
            
            URL[] runtimeCP = (URL[]) urls.toArray(new URL[0]);
            return runtimeCP;
        } catch (Exception e) {
            throw new MojoExecutionException(
                "Exception obtaining runtime classpath", e);
        }
    }

    /**
     * @param scriptDir A directory to search for scripts in. 
     * @return All scripts in this directory and its subdirectories.
     */
    protected List<String> findFilesIn(File scriptDir) {
        List<String> scripts = new ArrayList<String>();
        File[] flist = scriptDir.listFiles();
        for (File current : flist) {
            if (current.isDirectory()) {
                // recurse
                scripts.addAll(findFilesIn(current));
            } else if (Script.isScript(current)) {
                scripts.add(current.getAbsolutePath());
            }   
        }
        return scripts;
    }
}
