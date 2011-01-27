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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.ContextLoader;

/**
 * Bootstrap servlet to start up the WebApplicationContext.
 * Simply delegates to ModuleContextLoader.
 *
 * Note that this class has been deprecated for containers implementing
 * Servlet API 2.4 or higher in favour of ModuleContextLoaderListener.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 * @author Jonas Hauenstein (JHN)
 */
public class ModuleContextLoaderServlet extends HttpServlet {

	/**
	 * The Context Loader to be used.
	 */
	private ContextLoader contextLoader;

	/**
	 * Initialize the root web application context.
	 */
	public void init() throws ServletException {
		this.contextLoader = createContextLoader();
		this.contextLoader.initWebApplicationContext(getServletContext());
	}

	/**
	 * Create the ContextLoader to use. Can be overridden in subclasses.
	 * Creates the ModuleContextLoader as default.
	 * @return the new ContextLoader
	 */
	protected ContextLoader createContextLoader() {
		return new ModuleContextLoader();
	}

	/**
	 * Return the ContextLoader used by this servlet. 
	 * @return the current ContextLoader
	 */
	public ContextLoader getContextLoader() {
		return this.contextLoader;
	}


	/**
	 * Close the root web application context.
	 */
	public void destroy() {
		if (this.contextLoader != null) {
			this.contextLoader.closeWebApplicationContext(getServletContext());
		}
	}


	/**
	 * This should never even be called since no mapping to this servlet should
	 * ever be created in web.xml. That's why a correctly invoked Servlet 2.3
	 * listener is much more appropriate for initialization work ;-)
	 */
	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
		getServletContext().log(
				"Attempt to call service method on ContextLoaderServlet as ["
				+ request.getRequestURI() + "] was ignored");
		response.sendError(HttpServletResponse.SC_BAD_REQUEST);
	}


	/** {@inheritDoc} */
	public String getServletInfo() {
		return "ContextLoaderServlet for Servlet API 2.3 "
			+ "(deprecated in favor of ContextLoaderListener for Servlet API 2.4)";
	}
}
