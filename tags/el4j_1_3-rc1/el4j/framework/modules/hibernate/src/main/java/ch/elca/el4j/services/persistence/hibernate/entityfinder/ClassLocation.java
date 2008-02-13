package ch.elca.el4j.services.persistence.hibernate.entityfinder;

import java.net.URL;

import org.hibernate.proxy.HibernateProxyHelper;
import org.springframework.util.Assert;

/**
 * Describes the location of a class, accessable through the given classloader. The url points to
 * the real class file location.
 */
public class ClassLocation {

	private String className;

	private URL url;

	private ClassLoader classLoader;

	public ClassLocation(ClassLoader classLoader, String className, URL url) {
		Assert.notNull(classLoader);
		Assert.hasText(className);
		Assert.notNull(url);
		this.className = className;
		this.url = url;
		this.classLoader = classLoader;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getClassName() == null) ? 0 : getClassName().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != HibernateProxyHelper.getClassWithoutInitializingProxy(obj)) {
			return false;
		}
		final ClassLocation other = (ClassLocation) obj;
		if (getClassName() == null && other.getClassName() != null) {
			return false;
		}
		return getClassName().equals(other.getClassName());
	}
}
