package ch.elca.el4j.services.persistence.hibernate.entityfinder;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.springframework.util.Assert;

/**
 * Utility to locate classes from the classpath.
 */
public class ClassLocator {
	/**
	 * Are the package names the found classes' package must start with.
	 */
	protected final String[] packageNames;

	/**
	 * Is the used classloader to lookup classes.
	 */
	protected final ClassLoader classLoader;

	/**
	 * Locates all classes that are in one of the given packages (inclusive sub-packages).
	 * 
	 * @param packageNames
	 *            Are the package names the found classes' package must start with.
	 * @throws ClassNotFoundException
	 *             If the context classloader of the current thread could not be fetched.
	 */
	public ClassLocator(String... packageNames) throws ClassNotFoundException {
		this(Thread.currentThread().getContextClassLoader(), packageNames);
	}

	/**
	 * Locates all classes that are in one of the given packages (inclusive sub-packages).
	 * 
	 * @param classLoader
	 *            Is the used classloader to lookup classes.
	 * @param packageNames
	 *            Are the package names the found classes' package must start with.
	 */
	public ClassLocator(ClassLoader classLoader, String... packageNames) {
		Assert.notNull(classLoader);
		Assert.notNull(packageNames);
		this.classLoader = classLoader;
		this.packageNames = packageNames;
	}

	/**
	 * Returns all located classes where the found classes' package starts with one of the given
	 * package names.
	 * 
	 * @return Returns the found class locations.
	 * @throws ClassNotFoundException If no class could be found for one of the given packages.
	 * @throws IOException If there was a general IO problem.
	 */
	public List<ClassLocation> getAllClassLocations() throws ClassNotFoundException, IOException {
		List<ClassLocation> classLocations = new ArrayList<ClassLocation>();

		for (String packageName : packageNames) {
			String path = packageName.replace('.', '/');
			Enumeration<URL> resources = classLoader.getResources(path);
			if (null == resources || !resources.hasMoreElements()) {
				throw new ClassNotFoundException("No resource for " + path);
			}

			while (resources.hasMoreElements()) {
				URL resource = resources.nextElement();
				if (resource.getProtocol().equalsIgnoreCase("FILE")) {
					loadDirectory(packageName, resource, classLocations);
				} else if (resource.getProtocol().equalsIgnoreCase("JAR")) {
					loadJar(packageName, resource, classLocations);
				} else {
					throw new ClassNotFoundException("Unknown protocol on class resource: "
							+ resource.toExternalForm());
				}
			}
		}
		return classLocations;
	}

	/**
	 * Tries to fill the given class location list with classes from the given package that are
	 * saved in a jar file.
	 * 
	 * @param packageName Is the name of the package the class's package has to start with.
	 * @param resource Is the real location of the given package name.
	 * @param classLocations Are the already found class locations.
	 * @throws IOException If there was a general IO problem.
	 */
	private void loadJar(String packageName, URL resource, List<ClassLocation> classLocations)
		throws IOException {
		JarURLConnection conn = (JarURLConnection) resource.openConnection();
		JarFile jarFile = conn.getJarFile();
		Enumeration<JarEntry> entries = jarFile.entries();
		String packagePath = packageName.replace('.', '/');

		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			if ((entry.getName().startsWith(packagePath) || entry.getName().startsWith(
					"WEB-INF/classes/" + packagePath))
					&& entry.getName().endsWith(".class")) {
				URL url = new URL("jar:"
						+ new URL("file", null, jarFile.getName())
								.toExternalForm() + "!/" + entry.getName());

				String className = entry.getName();
				if (className.startsWith("/")) {
					className = className.substring(1);
				}
				className = className.replace('/', '.');

				className = className.substring(0, className.length() - ".class".length());

				ClassLocation classLocation = new ClassLocation(classLoader, className, url);
				addClassLocation(classLocation, classLocations);
			}
		}

	}

	/**
	 * Does the same as {@link #loadJar(String, URL, List)} but the class is directly saved on
	 * the file system.
	 * 
	 * @see #loadJar(String, URL, List)
	 */
	private void loadDirectory(String packageName, URL resource,
		List<ClassLocation> classLocations) throws IOException {
		loadDirectory(packageName, resource.getFile(), classLocations);

	}

	/**
	 * The same as {@link #loadDirectory(String, URL, List)} but with the full "class" path instead
	 * of the url.
	 * 
	 * @see #loadDirectory(String, URL, List)
	 */
	private void loadDirectory(String packageName, String fullPath,
		List<ClassLocation> classLocations) throws IOException {
		File directory = new File(fullPath);
		if (!directory.isDirectory()) {
			throw new IOException("Invalid directory " + directory.getAbsolutePath());
		}

		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				loadDirectory(packageName + '.' + file.getName(), file.getAbsolutePath(),
					classLocations);
			} else if (file.getName().endsWith(".class")) {
				String simpleName = file.getName();
				simpleName = simpleName.substring(0, simpleName.length() - ".class".length());
				String className = String.format("%s.%s", packageName, simpleName);
				ClassLocation location = new ClassLocation(classLoader, className, new URL("file",
						null, file.getAbsolutePath()));
				addClassLocation(location, classLocations);
			}
		}
	}

	/**
	 * Adds the given class location to the given class location list if this list does not
	 * already contains it.
	 * 
	 * @param classLocation Is the class location to add.
	 * @param classLocations Are the already found class locations.
	 * @throws IOException If the given class location is already in the given list.
	 */
	private void addClassLocation(ClassLocation classLocation, List<ClassLocation> classLocations)
		throws IOException {
		if (classLocations.contains(classLocation)) {
			throw new IOException("Duplicate location found for: " + classLocation.getClassName());
		}
		classLocations.add(classLocation);
	}
}
