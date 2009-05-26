/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2009 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.maven.plugins.envsupport;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import ch.elca.el4j.env.InvalidEnvXmlContentException;
import ch.elca.el4j.env.xml.EnvXml;
import ch.elca.el4j.maven.plugins.envsupport.handlers.ExplainBeanOverridesHandler;
import ch.elca.el4j.maven.plugins.envsupport.handlers.ExplainPlaceholdersHandler;

/**
 * Abstract base class for all 'list env properties'-mojos. 
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
public abstract class AbstractEnvListMojo extends AbstractEnvSupportMojo {
	
	/** {@inheritDoc} */
	public void execute() throws MojoExecutionException, MojoFailureException {
		initializeFiltering();
		
		EnvXml env = new EnvXml(getResourceLoader().getResolver(), true);
		if (env.hasValidConfigurations()) {
			env.setOverrideValues(m_filterProperties);
			env.registerHandler(EnvXml.ENV_GROUP_PLACEHOLDERS, new ExplainPlaceholdersHandler(this, getLog()));
			env.registerHandler(EnvXml.ENV_GROUP_BEAN_OVERRIDES, null);
			
			try {
				env.getGroupConfiguration(EnvXml.ENV_GROUP_PLACEHOLDERS);
			} catch (InvalidEnvXmlContentException e) {
				throw new MojoExecutionException(e.getMessage());
			}
			
			getLog().info("------------------------------------------------------------------------");
			
			env.registerHandler(EnvXml.ENV_GROUP_PLACEHOLDERS, null);
			env.registerHandler(EnvXml.ENV_GROUP_BEAN_OVERRIDES, new ExplainBeanOverridesHandler(this, getLog()));
			
			try {
				env.getGroupConfiguration(EnvXml.ENV_GROUP_BEAN_OVERRIDES);
			} catch (InvalidEnvXmlContentException e) {
				throw new MojoExecutionException(e.getMessage());
			}
		}
	}
}
