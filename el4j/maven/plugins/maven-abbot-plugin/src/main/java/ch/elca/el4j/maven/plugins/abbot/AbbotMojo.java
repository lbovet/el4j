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
import java.lang.reflect.InvocationTargetException;
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
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
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
		
		// TestSuite suite = new TestSuite();
		Object suite = construct(abbotCL, "junit.framework.TestSuite", new Object[0]);

		getLog().info("Running abbot tests.");
		
		for (String current : scripts) {
			getLog().info("Abbot test script: " + current);

			// Test test = new ScriptFixture(current);
			Object test = construct(abbotCL, "junit.extensions.abbot.ScriptFixture", current);
			
			// suite.addTest(test);
			castCall(suite, "addTest", new Object[] {test}, new Class<?>[] {cls(abbotCL, "junit.framework.Test")});
		}
		
		// TestResult result = TestRunner.run(suite);
		Object result = staticCastCall(abbotCL, "junit.textui.TestRunner", "run", 
			new Object[] {suite}, new Class<?>[] {cls(abbotCL, "junit.framework.Test")});
		
		// int numFailures = result.faliureCount();
		int numFailures = (Integer) call(result, "failureCount", new Object[0]);
		
		// int numErrors = result.errorCount();
		int numErrors = (Integer) call(result, "errorCount", new Object[0]);
		
		if (numErrors > 0 || numFailures > 0) {
			getLog().error("Abbot tests failed. There were " + numErrors + " errors and "
				+ numFailures + " failures.");
			throw new MojoExecutionException("Abbot tests failed.");
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
	
	/*
	 * Utility methods for "type-free java". Needed because we have to do everything "by name"
	 * over the abbot classloader using "Object" as type.
	 */
	
	/**
	 * Create an object reflectively by calling the constructor.
	 * @param cl The class loader to use.
	 * @param className The name of the class to create.
	 * @param parameters The constructor parameters.
	 * @return The instantiated object.
	 */
	private Object construct(ClassLoader cl, String className, Object... parameters) {
		try {
			Class<?> objectClass = cl.loadClass(className);
			Class<?>[] paramClasses = toClass(parameters);
			
			Constructor<?> c = objectClass.getConstructor(paramClasses);
			return c.newInstance(parameters);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Class not found: " + className, e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("No such constructor.", e);
		} catch (InstantiationException e) {
			throw new RuntimeException("Cannot instantiate class " + className, e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Constructor not accessible", e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException("Constructor threw exception", e);
		}
	}
	
	/**
	 * Call a method reflectively.
	 * @param target The target object.
	 * @param methodName The method name.
	 * @param parameters The method parameters.
	 * @return The return value.
	 */
	private Object call(Object target, String methodName, Object... parameters) {
		Class<?> targetClass = target.getClass();
		
		Class<?>[] paramClasses = toClass(parameters);
		
		try {
			Method method = targetClass.getMethod(methodName, paramClasses);
			Object result = method.invoke(target, parameters);
			return result;
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("Method does not exist", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Method not accessible", e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException("Method threw exception", e);
		}
	}
	
	/**
	 * Call a method reflectively, providing an explicit class for each parameter.
	 * @param target The target object.
	 * @param methodName The method name.
	 * @param parameters The method parameters.
	 * @param paramClasses The parameter classes to use.
	 * @return The return value.
	 */
	private Object castCall(Object target, String methodName, Object[] parameters, Class<?>[] paramClasses) {
		Class<?> targetClass = target.getClass();
		
		try {
			Method method = targetClass.getMethod(methodName, paramClasses);
			Object result = method.invoke(target, parameters);
			return result;
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("Method does not exist", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Method not accessible", e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException("Method threw exception", e);
		}
	}
	
	/**
	 * Call a static method relfectively.
	 * @param cl The classloader to use.
	 * @param className The name of the class containing the method.
	 * @param methodName The method to call.
	 * @param parameters The method parameters.
	 * @param paramClasses The parameter classes to use.
	 * @return The call result.
	 */
	private Object staticCastCall(ClassLoader cl, String className, String methodName, Object[] parameters
		, Class<?>[] paramClasses) {
		try {
			Class<?> cls = cl.loadClass(className);
			Method method = cls.getMethod(methodName, paramClasses);
			return method.invoke(null, parameters);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("No such class: " + className, e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("No such mothod: " + methodName, e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Method not accessible", e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException("Method threw exception", e);
		}
	}
	
	/**
	 * Transform an array of objects to their respective class objects.
	 * @param objects The objects.
	 * @return The class objects for these objects.
	 */
	private Class<?>[] toClass(Object... objects) {
		Class<?>[] classes = new Class<?>[objects.length];
		
		for (int i = 0; i < objects.length; i++) {
			classes[i] = objects[i].getClass();
		}
		
		return classes;
	}
	
	/**
	 * Get the class object from a class loader by name.
	 * @param cl The class loader.
	 * @param className The class name.
	 * @return The class object.
	 */
	private Class<?> cls(ClassLoader cl, String className) {
		try {
			return cl.loadClass(className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("No such class: " + className);
		}
	}
}
