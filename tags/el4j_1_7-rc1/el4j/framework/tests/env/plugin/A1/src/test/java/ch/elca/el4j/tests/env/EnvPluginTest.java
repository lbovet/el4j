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
package ch.elca.el4j.tests.env;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

import ch.elca.el4j.util.codingsupport.PropertiesHelper;
import ch.elca.el4j.util.env.EnvPropertiesUtils;


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

/**
 * This class tests if all env placeholder could be evaluated properly.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 */
public class EnvPluginTest {
	/**
	 * Test if env properties file contains the expected values.
	 */
	@Test
	public void testEnvPropertiesFile() {
		Properties envPlaceholderProperties = EnvPropertiesUtils.getEnvPlaceholderProperties();
		
		PropertiesHelper helper = new PropertiesHelper();
		Properties expectedProperties = helper.loadProperties("expected-env-placeholder.properties");
		
		Assert.assertEquals(expectedProperties, envPlaceholderProperties);
	}

}
