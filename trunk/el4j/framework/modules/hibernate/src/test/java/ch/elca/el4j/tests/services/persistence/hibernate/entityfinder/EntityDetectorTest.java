package ch.elca.el4j.tests.services.persistence.hibernate.entityfinder;

import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;

import ch.elca.el4j.services.persistence.hibernate.entityfinder.EntityDetectorAnnotationSessionFactoryBean;
import ch.elca.el4j.tests.services.persistence.hibernate.entityfinder.entities.Entity1;
import ch.elca.el4j.tests.services.persistence.hibernate.entityfinder.entities.Entity2;
import junit.framework.TestCase;

/**
 * Test for {@link EntityDetectorAnnotationSessionFactoryBean}
 * @author pos
 *
 */
public class EntityDetectorTest extends TestCase {

	
	
	public void testEntityDetector() {
		// test it only programmatically
		
		EntityDetectorAnnotationSessionFactoryBean 
			testee = new EntityDetectorAnnotationSessionFactoryBean();
		
		testee.setAutoDetectEntityPackage("ch.elca.el4j.tests.services.persistence.hibernate.entityfinder.entities");
		
		try {
			testee.afterPropertiesSet();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Class<?>[] annotatedClasses = testee.getAnnotatedClasses();
		
		assertTrue(annotatedClasses.length == 2);
		assertTrue(annotatedClasses[0].equals(Entity1.class) ||
				annotatedClasses[0].equals(Entity2.class));
		assertTrue(annotatedClasses[1].equals(Entity1.class) ||
				annotatedClasses[1].equals(Entity2.class));
		assertTrue(annotatedClasses[0]!=annotatedClasses[1]);
	}
	
}
