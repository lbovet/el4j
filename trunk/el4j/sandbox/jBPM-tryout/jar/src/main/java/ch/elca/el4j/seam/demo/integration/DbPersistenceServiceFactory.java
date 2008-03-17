/**
 * 
 */
package ch.elca.el4j.seam.demo.integration;



import org.hibernate.Session;
import org.hibernate.SessionFactory;

import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;

import org.jbpm.svc.Service;

/**
 * @author Frank Bitzer (FBI)
 *
 */

public class DbPersistenceServiceFactory extends
		org.jbpm.persistence.db.DbPersistenceServiceFactory {

	private static final long serialVersionUID = 997L;

	
	SessionFactory sessionFactory;
	
	/**
	 * {@inheritDoc}
	 */
	public Service openService() {
	    
		//create instance of own service implementation
	    return new ch.elca.el4j.seam.demo.integration.DbPersistenceService(this);
	  }
	
	
	/**
	 * Retrieve Hibernate sessionFactory
	 */
	@Override
	public synchronized SessionFactory getSessionFactory() {
	   
		if (sessionFactory==null) {
	    	
			 if(Contexts.isApplicationContextActive()){
				 
				 //access seam component holding session
				 Session session = (Session)
				 	 Component.getInstance("hibernateSession");
				
				 //and extract sessionFactory
				sessionFactory = session.getSessionFactory();
				
			 }
			
	    }
	    
	    return sessionFactory;
	}
	
	/**
	 * Set sessionFactory
	 */
	@Override
	public void setSessionFactory(SessionFactory sessionFactory) {
	    this.sessionFactory = sessionFactory;
	  }
	
	
}
