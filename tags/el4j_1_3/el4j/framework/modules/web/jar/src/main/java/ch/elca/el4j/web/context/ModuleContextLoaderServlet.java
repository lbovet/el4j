/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU General Public License (GPL) Version 2.0.
 * http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.web.context;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.ContextLoaderServlet;

/**
 * Bootstrap servlet to start up the WebApplicationContext.
 * Simply delegates to ModuleContextLoader.
 * 
 * Note that this class has been deprecated for containers implementing
 * Servlet API 2.4 or higher in favour of ModuleContextLoaderListener.
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
public class ModuleContextLoaderServlet extends ContextLoaderServlet {
    /** {@inheritDoc} */
    @Override
    protected ContextLoader createContextLoader() {
        return new ModuleContextLoader();
    }
}
