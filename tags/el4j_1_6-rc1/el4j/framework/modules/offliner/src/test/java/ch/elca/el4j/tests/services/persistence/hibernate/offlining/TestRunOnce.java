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
package ch.elca.el4j.tests.services.persistence.hibernate.offlining;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;

import ch.elca.el4j.core.context.ModuleApplicationContext;
import ch.elca.el4j.services.persistence.generic.dao.DaoRegistry;
import ch.elca.el4j.services.persistence.hibernate.offlining.Offliner;
import ch.elca.el4j.services.persistence.hibernate.offlining.chunk.ChunkingStrategyImpl;
import ch.elca.el4j.services.persistence.hibernate.offlining.impl.OfflinerClientImpl;
import ch.elca.el4j.services.persistence.hibernate.offlining.impl.OfflinerInfo;
import ch.elca.el4j.services.persistence.hibernate.offlining.impl.OffliningServer;
import ch.elca.el4j.services.persistence.hibernate.offlining.util.OffliningStateTable;
import ch.elca.el4j.util.objectwrapper.ObjectWrapper;


/**
 * Operations that should be done once per test run / JVM like setting up the application contexts.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author David Bernhard (DBD)
 */
public class TestRunOnce {

	/** The local application context. */
	private static ApplicationContext s_localContext;
	
	/** The offliner. */
	private static Offliner s_offliner;
	
	/**
	 * Constructor. Initialize context once.
	 * @param strategies The chunking strategies map.
	 */
	public TestRunOnce(Map<Class<?>, ChunkingStrategyImpl> strategies) {
		if (s_localContext != null) {
			return;
		}
		
		String[] config = new String[] {
			"classpath*:mandatory/*.xml",
			"classpath*:scenarios/db/raw/*.xml",
			"classpath*:scenarios/dataaccess/hibernate/*.xml",
			"classpath*:optional/interception/transactionJava5Annotations.xml",
			"classpath:Hibernate.xml",
			"classpath:common.xml",
			"classpath:local.xml"
		};

		s_localContext = new ModuleApplicationContext(config, true);
		
		Map<Class<?>, ChunkingStrategyImpl> classes = new LinkedHashMap<Class<?>, ChunkingStrategyImpl>();
		classes.putAll(strategies);
		
		// Note : We cannot use spring to set up the offliner as we have a strategy-dependent
		// parameter "classes". Real applications will of course just have one strategy
		// per class and do this with spring.
		
		OfflinerInfo info = new OfflinerInfo();
		info.setClasses(classes);
		info.setWrapper((ObjectWrapper) s_localContext.getBean("objectWrapper")); 
		info.setClientDaoRegistry((DaoRegistry) s_localContext.getBean("daoRegistry"));
		info.setServerDaoRegistry((DaoRegistry) s_localContext.getBean("daoRegistryRemote"));
		info.setServer((OffliningServer) s_localContext.getBean("server"));
		info.setStateTable((OffliningStateTable) s_localContext.getBean("stateTable"));
		
		s_offliner = new OfflinerClientImpl(info);
	}
	
	/**
	 * @return The offliner.
	 */
	public Offliner getOffliner() {
		return s_offliner;
	}
		
}
