/**
 * 
 */
package ch.elca.el4j.seam.demo.integration;

import org.hibernate.Session;
import org.jbpm.JbpmContext;
import org.jbpm.persistence.db.DbPersistenceServiceFactory;
import org.jbpm.svc.Services;
import org.springframework.orm.hibernate3.SessionFactoryUtils;

/**
 * @author Frank Bitzer (FBI)
 * 
 */
public class DbPersistenceService extends
		org.jbpm.persistence.db.DbPersistenceService {

	private static final long serialVersionUID = 996L;

	public DbPersistenceService(
			DbPersistenceServiceFactory persistenceServiceFactory) {
		this(persistenceServiceFactory, getCurrentServices());
	}

	static Services getCurrentServices() {
		Services services = null;
		JbpmContext currentJbpmContext = JbpmContext.getCurrentJbpmContext();
		if (currentJbpmContext != null) {
			services = currentJbpmContext.getServices();
		}
		return services;
	}

	DbPersistenceService(DbPersistenceServiceFactory persistenceServiceFactory,
			Services services) {

		super(persistenceServiceFactory);

		this.persistenceServiceFactory = persistenceServiceFactory;
		this.isTransactionEnabled = persistenceServiceFactory
				.isTransactionEnabled();
		this.isCurrentSessionEnabled = persistenceServiceFactory
				.isCurrentSessionEnabled();
		this.services = services;

	}

	
	/**
	 * Use Hibernate sessionFactory to retrieve a Session instance.
	 */
	public Session getSession() {

		if ((session == null) && (getSessionFactory() != null)) {

			session = getSessionFactory().openSession();

			mustSessionBeClosed = true;
			mustSessionBeFlushed = true;
			mustConnectionBeClosed = false;

			isTransactionEnabled = !SessionFactoryUtils.isSessionTransactional(
					session, getSessionFactory());

			if (isTransactionEnabled) {

				beginTransaction();
			}

		}
		return session;
	}

}
