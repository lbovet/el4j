package ch.elca.el4j.services.persistence.hibernate.entityfinder;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import javax.persistence.Entity;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.ejb.Ejb3Configuration;
import org.hibernate.ejb.HibernateEntityManagerFactory;
import org.hibernate.ejb.event.CallbackHandlerConsumer;
import org.hibernate.event.EventListeners;
import org.hibernate.secure.JACCSecurityListener;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;


/**
 * Extends <a
 * href="http://www.springframework.org/docs/api/org/springframework/orm/hibernate3/annotation/AnnotationSessionFactoryBean.html"
 * target="_new">AnnotationSessionFactoryBean</a>. <br>
 *
 * Detects all classes that have a \@Entity annotation under a given parent package
 * (indicated with the property <code>autoDetectEntityPackage</code>). <a>
 *
 *
 * Source of the idea: secutix project
 */
public class EntityDetectorAnnotationSessionFactoryBean extends AnnotationSessionFactoryBean
	implements InitializingBean {

	private static final Log s_logger =
		LogFactory.getLog(EntityDetectorAnnotationSessionFactoryBean.class);

	/**
	 * Package name for given set of entities.
	 */
	private String autoDetectEntityPackage[];

	/**
	 * To simplify testing (there is not access to parent
	 *  field of same name)
	 */
	private Class<?>[] localAnnotatedClasses;
	
	/**
	 * Is full support for the JPA enabled.
	 */
	private boolean jpaFullSupportEnabled;

	/**
	 * Sets the 1 or n packages in which the entities are defined
	 *  Sample package: "org.hibernate.tests.entities"
	 *
	 * @param pack
	 *            The parent package name that contains the target entities
	 *            	(as strings)
	 */
	public void setAutoDetectEntityPackage(String... pack) {
		autoDetectEntityPackage = pack;
	}

	/**
	 * @return Returns the package prefixes in which we do the auto detection of entities.
	 */
	protected String[] getAutoDetectEntityPackage() {
		return autoDetectEntityPackage;
	}
	
	/**
	 * Specify the names of annotated packages, for which (including all
	 * sub packages) package-level JDK 1.5+ annotation metadata will be read.
	 * 
	 * @param annotatedPackages    a list of annotated packages
	 */
	public void setAutoDetectAnnotatedPackages(String[] annotatedPackages) {
		final Package[] packages = Package.getPackages();
		
		HashSet<String> detectedAnnotatedPackages = new HashSet<String>();
		for (String prefix : annotatedPackages) {
			for (Package p : packages) {
				if (p.getName().startsWith(prefix)) {
					detectedAnnotatedPackages.add(p.getName());
				}
			}
		}
		setAnnotatedPackages(detectedAnnotatedPackages.toArray(new String[0]));
	}

	/**
	 * Explicitly specify annotated entity classes.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void setAnnotatedClasses(Class[] annotatedClasses) {
		ArrayList<Class> classes;
		if (!ArrayUtils.isEmpty(annotatedClasses)) {
			classes = new ArrayList<Class>(Arrays.asList(annotatedClasses));
		} else {
			classes = new ArrayList<Class>();
		}
		localAnnotatedClasses = (Class[]) classes.toArray(new Class[classes.size()]);
		super.setAnnotatedClasses(localAnnotatedClasses);
	}

	/**
	 * Just to have them available for tests
	 * @return
	 */
	public Class<?>[] getAnnotatedClasses() {
		return localAnnotatedClasses;
	}
	
	/**
	 * Set the enabled flag for the full JPA support.
	 * @param enabled
	 */
	public void setJpaFullSupportEnabled(boolean enabled) {
		jpaFullSupportEnabled = enabled;
	}
	
	/**
	 * @return if the full Java Persistence API support is enabled.
	 */
	public boolean isJpaFullSupportEnabled() {
		return jpaFullSupportEnabled;
	}

	/**
	 * really do the searching.
	 */
	public void afterPropertiesSet() throws Exception {
		if (getAutoDetectEntityPackage() != null) {
			ArrayList<Class<?>> classes = new ArrayList<Class<?>>();;
			try {
				ClassLocator cl = new ClassLocator(getAutoDetectEntityPackage());
				for (ClassLocation loc : cl.getAllClassLocations()) {
					Class<?> clazz = Class.forName(loc.getClassName());
					Entity isEntity = (Entity) clazz.getAnnotation(Entity.class);
					if (isEntity != null) {
						classes.add(clazz);
						if (s_logger.isDebugEnabled()) {
							s_logger.debug("Adding entity " + clazz);
						}
					}
				}

				s_logger.debug("all detected hibernate entities"+
						StringUtils.arrayToCommaDelimitedString(classes.toArray()));

				// merge existing classes and new classes
				if (localAnnotatedClasses != null) {
					classes.addAll(Arrays.asList(localAnnotatedClasses));
				}

				localAnnotatedClasses = (Class[]) classes.toArray(new Class[classes.size()]);
				s_logger.debug("number of classes detected:"+localAnnotatedClasses.length);
				super.setAnnotatedClasses(localAnnotatedClasses);

			} catch (Exception e) {
				s_logger.fatal(e);
				throw new RuntimeException(e);
			}


		}
		super.afterPropertiesSet();
	}
	
	/** {@inheritDoc} *//*
	@Override
	protected Configuration newConfiguration() throws HibernateException {
		Ejb3Configuration cfg = new Ejb3Configuration();
		Ejb3Configuration configured = cfg.configure("TestEntityManager", null);
		return configured.getHibernateConfiguration();
		
	}*/
	
	/** {@inheritDoc} */
	protected SessionFactory newSessionFactory(Configuration config) throws HibernateException {
		if (jpaFullSupportEnabled) {
			Ejb3Configuration cfg = new Ejb3Configuration();
			// merge the ejb3 event listeners and the configured listeners we get
			Configuration newConfig = mergeEventListeners(config, cfg.getHibernateConfiguration());
			try {
				Field f = cfg.getClass().getDeclaredField("cfg");
				f.setAccessible(true);
				f.set(cfg, newConfig);
				Ejb3Configuration configured = cfg.configure("TestEntityManager", null);
//				HibernateEntityManagerFactory emf = (HibernateEntityManagerFactory) configured.buildEntityManagerFactory();
//				return emf.getSessionFactory();
				return configured.getHibernateConfiguration().buildSessionFactory();
			} catch (Throwable e) {
				s_logger.debug("Could not load SessionFactory with Ejb3Configuration. "
						+ "Not all JPA Annotations are available!");
				return newConfig.buildSessionFactory();
			}
		} else {
			return super.newSessionFactory(config);
		}
	}
	
	/**
	 * Merge the event listeners of two configurations.
	 * @param primaryConfig    the configuration to take as base.
	 * @param secondaryConfig  the configuration to take additional event listeners from.
	 * @return  the new configuration with the merged listeners.
	 */
	private Configuration mergeEventListeners(Configuration primaryConfig, Configuration secondaryConfig) {
		final Object[] readerMethodArgs = new Object[0];
		// get the event listeners
		EventListeners listenerConfig = primaryConfig.getEventListeners();
		EventListeners secondListenerConfig = secondaryConfig.getEventListeners();

		BeanInfo beanInfo = null;
		try {
			if (!listenerConfig.getClass().equals(secondListenerConfig.getClass())) {
				throw new HibernateException("Listeners are not of the same type!");
			}
			// get all the read methods from the introspector
			beanInfo = Introspector.getBeanInfo(listenerConfig.getClass(), Object.class);
			PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
			try {
				int i = 0;
				for (int max = pds.length; i < max; i++) {
					// read the specific listener arrays
					final Object listeners = pds[i].getReadMethod().invoke(listenerConfig, readerMethodArgs);
					final Object listeners2 = pds[i].getReadMethod().invoke(secondListenerConfig, readerMethodArgs);
					if (listeners == null) {
						throw new HibernateException("Listener [" + pds[i].getName() + "] was null");
					}
					if (listeners instanceof Object[]) {
						Object[] listenersArray2 = new Object[0];
						if (listeners2 == null) {
							continue;
						} else {
							if (listeners2 instanceof Object[]) {
								listenersArray2 = (Object[]) listeners2;
							} else {
								continue;
							}
						}
						Object[] listenersArray = (Object[]) listeners;
						// create a new array with the size of both listener arrays
						int newSize = listenersArray.length + listenersArray2.length;
						Class<?> clazz = pds[i].getReadMethod().getReturnType().getComponentType();
						Object newArray = Array.newInstance(clazz, newSize);
						System.arraycopy(listenersArray2, 0, newArray, 0, listenersArray2.length);
						System.arraycopy(listenersArray, 0, newArray, listenersArray2.length, listenersArray.length);
						// write the new array
						pds[i].getWriteMethod().invoke(listenerConfig, newArray);
					}
				}
			} catch (HibernateException e) {
				throw e;
			} catch (Throwable t) {
				throw new HibernateException("Unable to validate listener config", t);
			}
		} catch (Exception t) {
			throw new HibernateException("Unable to copy listeners", t);
		} finally {
			if (beanInfo != null) {
				// release the jdk internal caches everytime to ensure this
				// plays nicely with destroyable class-loaders
				Introspector.flushFromCaches(getClass());
			}
		}
		return primaryConfig;
	}
	
}
