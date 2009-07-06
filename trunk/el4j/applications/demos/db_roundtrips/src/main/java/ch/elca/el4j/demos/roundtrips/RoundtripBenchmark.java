/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2008 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.demos.roundtrips;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import ch.elca.el4j.apps.keyword.dao.KeywordDao;
import ch.elca.el4j.apps.keyword.dom.Keyword;
import ch.elca.el4j.core.context.ModuleApplicationContext;
import ch.elca.el4j.services.monitoring.DbLogger;
import ch.elca.el4j.services.persistence.generic.dao.impl.DefaultDaoRegistry;

//Checkstyle: UncommentedMain off
/**
 * This class reports roundtrip counts for various Hibernate operations.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Stefan Wismer (SWI)
 */
public final class RoundtripBenchmark {

	/**
	 * Locations of configuration files.
	 */
	private static final String[] CONFIG_LOCATIONS = {
		"classpath*:mandatory/*.xml",
		"classpath*:mandatory/keyword/*.xml",
		"classpath*:scenarios/db/raw/*.xml",
		"classpath*:scenarios/dataaccess/hibernate/*.xml",
		"classpath*:scenarios/dataaccess/hibernate/keyword/*.xml",
		"classpath*:optional/keyword/test-interceptor-config.xml",
		"classpath*:optional/interception/transactionJava5Annotations.xml"};

	/**
	 * Private dummy constructor.
	 */
	private RoundtripBenchmark() { }
	
	/**
	 * Run the DB roundtrip benchmark.
	 * 
	 * @param args None
	 */
	public static void main(String[] args) {
		RoundtripBenchmark benchmark = new RoundtripBenchmark();
		benchmark.runBenchmark();
	}
	
	/**
	 * Run the roundtrip benchmark.
	 */
	public void runBenchmark() {
		ModuleApplicationContext appContext = new ModuleApplicationContext(CONFIG_LOCATIONS, true);
		
		DefaultDaoRegistry daoRegistry = (DefaultDaoRegistry) appContext.getBean("daoRegistry");
		KeywordDao dao = (KeywordDao) daoRegistry.getFor(Keyword.class);
		HibernateTransactionManager transactionManager
			= (HibernateTransactionManager) appContext.getBean("transactionManager");
		
		dao.deleteAll();
		
		Keyword keyword = createKeyword("Java");
		
		int dbRoundtrips = DbLogger.getRoundtripCount();
		keyword = dao.saveOrUpdate(keyword);
		System.out.println("saveOrUpdate (transient entity): " + (DbLogger.getRoundtripCount() - dbRoundtrips));
		
		keyword.setName("Java2");
		dbRoundtrips = DbLogger.getRoundtripCount();
		keyword = dao.saveOrUpdate(keyword);
		System.out.println("saveOrUpdate (persistent entity): " + (DbLogger.getRoundtripCount() - dbRoundtrips));
		
		dbRoundtrips = DbLogger.getRoundtripCount();
		keyword = dao.findById(keyword.getKey());
		System.out.println("findById: " + (DbLogger.getRoundtripCount() - dbRoundtrips));
		
		dbRoundtrips = DbLogger.getRoundtripCount();
		keyword = dao.refresh(keyword);
		System.out.println("refresh: " + (DbLogger.getRoundtripCount() - dbRoundtrips));
		
		dbRoundtrips = DbLogger.getRoundtripCount();
		keyword = dao.reload(keyword);
		System.out.println("reload: " + (DbLogger.getRoundtripCount() - dbRoundtrips));
		
		dbRoundtrips = DbLogger.getRoundtripCount();
		dao.findByCriteria(DetachedCriteria.forClass(Keyword.class));
		System.out.println("findByCriteria: " + (DbLogger.getRoundtripCount() - dbRoundtrips));
		
		
		dbRoundtrips = DbLogger.getRoundtripCount();
		dao.delete(keyword);
		System.out.println("delete: " + (DbLogger.getRoundtripCount() - dbRoundtrips));
		
		keyword = createKeyword("Java4");
		keyword = dao.saveOrUpdate(keyword);
		
		dbRoundtrips = DbLogger.getRoundtripCount();
		dao.deleteById(keyword.getKey());
		System.out.println("deleteById: " + (DbLogger.getRoundtripCount() - dbRoundtrips));
		
		keyword = createKeyword("Java5");
		keyword = dao.saveOrUpdate(keyword);

		TransactionStatus transaction;
		
		dbRoundtrips = DbLogger.getRoundtripCount();
		transaction = transactionManager.getTransaction(new DefaultTransactionDefinition());
		keyword.setName("Java6");
		transactionManager.getSessionFactory().getCurrentSession().clear();
		dao.getConvenienceHibernateTemplate().update(keyword);
		transactionManager.getSessionFactory().getCurrentSession().flush();
		transactionManager.commit(transaction);
		System.out.println("update (detached entity): " + (DbLogger.getRoundtripCount() - dbRoundtrips));
		
		dbRoundtrips = DbLogger.getRoundtripCount();
		transaction = transactionManager.getTransaction(new DefaultTransactionDefinition());
		keyword.setName("Java7");
		transactionManager.getSessionFactory().getCurrentSession().clear();
		dao.getConvenienceHibernateTemplate().save(keyword);
		transactionManager.getSessionFactory().getCurrentSession().flush();
		transactionManager.commit(transaction);
		System.out.println("save (detached entity): " + (DbLogger.getRoundtripCount() - dbRoundtrips));
		
		//Logger.getLogger("org.hibernate").setLevel(Level.DEBUG);
		dbRoundtrips = DbLogger.getRoundtripCount();
		transaction = transactionManager.getTransaction(new DefaultTransactionDefinition());
		keyword.setName("Java8");
		transactionManager.getSessionFactory().getCurrentSession().clear();
		keyword = (Keyword) dao.getConvenienceHibernateTemplate().merge(keyword);
		transactionManager.getSessionFactory().getCurrentSession().flush();
		transactionManager.commit(transaction);
		System.out.println("merge (detached entity): " + (DbLogger.getRoundtripCount() - dbRoundtrips));
		//Logger.getLogger("org.hibernate").setLevel(Level.ERROR);
		
		dbRoundtrips = DbLogger.getRoundtripCount();
		transaction = transactionManager.getTransaction(new DefaultTransactionDefinition());
		keyword.setName("Java9");
		transactionManager.getSessionFactory().getCurrentSession().clear();
		keyword = (Keyword) dao.saveOrUpdate(keyword);
		transactionManager.getSessionFactory().getCurrentSession().flush();
		transactionManager.commit(transaction);
		System.out.println("saveOrUpdate (detached entity): " + (DbLogger.getRoundtripCount() - dbRoundtrips));
		
		dbRoundtrips = DbLogger.getRoundtripCount();
		transaction = transactionManager.getTransaction(new DefaultTransactionDefinition());
		dao.saveOrUpdate(createKeyword("insert1"));
		System.out.println("saveOrUpdate (1 of 2 entities in one transaction): "
			+ (DbLogger.getRoundtripCount() - dbRoundtrips));
		dao.saveOrUpdate(createKeyword("insert2"));
		transactionManager.getSessionFactory().getCurrentSession().flush();
		transactionManager.commit(transaction);
		System.out.println("saveOrUpdate (2 of 2 entities in one transaction): "
			+ (DbLogger.getRoundtripCount() - dbRoundtrips));
		// oracle needs only 3 (2 x seq.next, 1 x insert) roundtrips because it batches the insert statements
		// derby needs 4: (2 x seq.next, 2 x insert)
		
		appContext.close();
	}
	
	/**
	 * Create a keyword.
	 * @param name    the name of the keyword
	 * @return        a keyword with the given name
	 */
	private Keyword createKeyword(String name) {
		Keyword keyword = new Keyword();
		keyword.setName(name);
		keyword.setDescription("Java related documentation");
		
		return keyword;
	}
}
