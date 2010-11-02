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
package ch.elca.el4j.services.security.filters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elca.el4j.util.env.EnvPropertiesUtils;

/**
 * Blocks requests from unauthorized IP addresses. It answers with the 401
 * (Unauthorized) status code if the IP is not authorized. Authorized IPs are
 * forwarded directly.
 * <p>
 * 
 * The list of authorized IP addresses are read from a configurable system
 * property or env property, if the system property is not defined.
 * The format is <code>x1.y1.z1.w1[,x2.y2.z2.w2]</code> whereas
 * <code>*</code> can be used to match any character sequence.
 * 
 * <p>
 * Configuration:
 * 
 * <pre>
 * &lt;filter&gt;
 *     &lt;filter-name&gt;IP Adress Filter&lt;/filter-name&gt;
 *     &lt;filter-class&gt;ch.elca.el4j.services.security.filters.IPAddressFilter&lt;/filter-class&gt;
 *     &lt;init-param&gt;
 *         &lt;param-name&gt;ipAddresses&lt;/param-name&gt;
 *         &lt;param-value&gt;operation.ipAddresses&lt;/param-value&gt;
 *     &lt;/init-param&gt;
 * &lt;/filter&gt;
 * </pre>
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Fabian Reichlin (FRE)
 * @author Laurent Bovet (LBO)
 */
public class IPAddressFilter implements Filter {

	/**     */
	public static final String PROPERTY_PARAM_NAME = "ipAddresses";

	/**     */
	public static final String WILDCARD = "*";

	/**     */
	private static Logger s_log
		= LoggerFactory.getLogger(IPAddressFilter.class);

	/**     */
	private List<Pattern> m_ipList;

	/**     */
	private String m_filterPropertyName;

	/**     */
	private boolean m_disabled = false;

	/** {@inheritDoc} */
	public void init(FilterConfig config) throws ServletException {
		
		m_filterPropertyName = config.getInitParameter(PROPERTY_PARAM_NAME);
		
		if (m_filterPropertyName == null) {
			String message = "Missing required parameter "
				+ "'" + PROPERTY_PARAM_NAME + "'";
			s_log.error(message);
			throw new ServletException(message);
		}
		
		s_log.debug("Using property: " + m_filterPropertyName);
	}

	/** {@inheritDoc} */
	public void doFilter(ServletRequest request, ServletResponse response,
		FilterChain chain)
		throws IOException, ServletException {

		s_log.debug("Checking IP address: " + request.getRemoteAddr());

		if (m_ipList == null) {
			initList();
		}
		
		boolean accessGranted = false;
		if (!m_disabled) {
			for (Pattern pattern : m_ipList) {
				if (pattern.matcher(request.getRemoteAddr()).matches()) {
					accessGranted = true;
					break;
				}
			}
		} else {
			accessGranted = true;
		}

		if (accessGranted) {
			s_log.debug("Permission granted");
			chain.doFilter(request, response);
		} else {
			if (response instanceof HttpServletResponse) {
				((HttpServletResponse) response).sendError(
					HttpServletResponse.SC_UNAUTHORIZED);
			}
			s_log.debug("Permission denied");
		}
	}

	/** {@inheritDoc} */
	public void destroy() {
		// nothing to do
	}

	/**
	 * Inits the list of authorized IP addresses. If no IP addresses
	 * are defined, a ServletException is thrown.
	 * 
	 * @throws ServletException Empty or misconfigured FilterConfig file.
	 */
	private void initList() throws ServletException {
		
		String ipListString = System.getProperty(m_filterPropertyName);
		
		// no system property set -> use env property
		if (ipListString == null) {
			ipListString = EnvPropertiesUtils.getEnvPlaceholderProperties().getProperty(m_filterPropertyName);
		}

		s_log.debug("Authorized IP addresses: " + ipListString);

		if (ipListString == null) {
			throw new ServletException("Missing required system or env property "
				+ "'" + m_filterPropertyName + "'");
		}

		if (ipListString.equals(WILDCARD)) {
			m_disabled = true;
		}
		
		// remove spaces and split
		List<String> ips = Arrays.asList(ipListString.replaceAll(" ", "").split(","));
		m_ipList = new ArrayList<Pattern>(ips.size());
		for (String ipPattern : ips) {
			ipPattern = ipPattern.replaceAll("\\.", "\\\\.");
			ipPattern = ipPattern.replaceAll("\\*", ".*");
			m_ipList.add(Pattern.compile(ipPattern));
		}
	}
}
