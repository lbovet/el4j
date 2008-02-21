/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.web.context;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.ContextLoaderListener;

/**
 * Bootstrap listener to start up the root ModuleWebApplicationContext.
 * Simply delegates to ModuleContextLoader.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * Sample Configuration in web.xml:
 * 
 *  &lt;context-param&gt;
 *       &lt;description&gt;
 *           Configuration for the EL4J Application Context
 *       &lt;/description&gt;
 *       &lt;param-name&gt;contextConfigLocation&lt;/param-name&gt;
 *       &lt;param-value&gt;
 *           classpath*:mandatory/*.xml,
 *           classpath*:mandatory/generic/*.xml,
 *           classpath*:scenarios/db/raw/*.xml,
 *           classpath*:scenarios/dataaccess/hibernate/*.xml,
 *           classpath*:scenarios/dataaccess/hibernate/keyword/*.xml,
 *           classpath*:optional/interception/transactionJava5Annotations.xml
 *       &lt;/param-value&gt;
 *   &lt;/context-param&gt;
 *   
 *   &lt;context-param&gt;
 *       &lt;description&gt;
 *           Configuration locations to exclude from the application context.
 *       &lt;/description&gt;
 *       &lt;param-name&gt;exclusiveLocations&lt;/param-name&gt;
 *       &lt;param-value&gt;
 *           mandatory/keyword/keyword-core-config.xml
 *       &lt;/param-value&gt;
 *   &lt;/context-param&gt;
 *   
 *   &lt;context-param&gt;
 *       &lt;description&gt;
 *           Bean definition overriding allowed in the application context?
 *       &lt;/description&gt;
 *       &lt;param-name&gt;overrideBeanDefinitions&lt;/param-name&gt;
 *       &lt;param-value&gt;
 *           true
 *       &lt;/param-value&gt;
 *   &lt;/context-param&gt;
 *   
 *   &lt;listener&gt;
 *       &lt;listener-class&gt;
 *           ch.elca.el4j.web.context.ModuleContextLoaderListener
 *       &lt;/listener-class&gt;
 *   &lt;/listener&gt;
 *
 * @see org.springframework.web.context.ContextLoaderListener
 * @see ModuleContextLoader
 * @author Alex Mathey (AMA)
 */
public class ModuleContextLoaderListener extends ContextLoaderListener {

    /**
     * Create the ModuleContextLoader to use.
     * @return the new ModuleContextLoader
     */
    protected ContextLoader createContextLoader() {
        return new ModuleContextLoader();
    }
    
}
