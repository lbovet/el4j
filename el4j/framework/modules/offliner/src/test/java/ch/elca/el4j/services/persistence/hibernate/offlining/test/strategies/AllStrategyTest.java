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
package ch.elca.el4j.services.persistence.hibernate.offlining.test.strategies;

import java.util.LinkedHashMap;
import java.util.Map;

import ch.elca.el4j.services.persistence.hibernate.offlining.chunk.AllStrategyImpl;
import ch.elca.el4j.services.persistence.hibernate.offlining.chunk.ChunkingStrategyImpl;
import ch.elca.el4j.services.persistence.hibernate.offlining.test.AbstractStrategyDependentTests;
import ch.elca.el4j.services.persistence.hibernate.offlining.testclasses.Person;
import ch.elca.el4j.services.persistence.hibernate.offlining.testclasses.SimplePerson;


/**
 * Test using ChunkingStrategy.ALL.
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
public class AllStrategyTest extends AbstractStrategyDependentTests {

	/** {@inheritDoc} */
	@Override
	protected Map<Class<?>, ChunkingStrategyImpl> getStrategy() {
		LinkedHashMap<Class<?>, ChunkingStrategyImpl> map = new LinkedHashMap<Class<?>, ChunkingStrategyImpl>();
		ChunkingStrategyImpl strategy = new AllStrategyImpl();
		map.put(Person.class, strategy);
		map.put(SimplePerson.class, strategy);
		return map;
	}
}
