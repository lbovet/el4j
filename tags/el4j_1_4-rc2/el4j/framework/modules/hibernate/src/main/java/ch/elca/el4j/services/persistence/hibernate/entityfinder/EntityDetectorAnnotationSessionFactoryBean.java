package ch.elca.el4j.services.persistence.hibernate.entityfinder;

import java.util.ArrayList;
import java.util.Arrays;

import javax.persistence.Entity;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;
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
	 * really do the searching
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
}
