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
package ch.elca.el4j.services.remoting.servlet;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.sun.xml.ws.transport.http.servlet.ServletAdapterList;
import com.sun.xml.ws.transport.http.servlet.SpringBinding;
import com.sun.xml.ws.transport.http.servlet.WSServletDelegate;

import ch.elca.el4j.services.remoting.RemotingServiceExporter;
import ch.elca.el4j.services.remoting.protocol.Jaxws;

/**
 * This class represents a customized JAX-WS {@link HttpServlet}. Instead of
 * using the {@link SpringBinding}s specified in special Spring XML format,
 * this class searches for JAX-WS bindings in el4j-remoting-protocol form.
 * 
 * Remark: This class cannot extend ModuleContextLoaderServlet because
 * doPost, doGet, doPut and doDelete are final.
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
public class WSSpringServlet extends HttpServlet {

    /**
     * The servlet delegate.
     */
    private WSServletDelegate m_delegate;

    
    /** {@inheritDoc} */
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);

        // get the configured adapters from Spring
        WebApplicationContext wac = WebApplicationContextUtils
            .getRequiredWebApplicationContext(getServletContext());

        Set<SpringBinding> bindings = new LinkedHashSet<SpringBinding>();

        Map<?, ?> map = wac.getBeansOfType(RemotingServiceExporter.class);
        for (Object instance : map.values()) {
            RemotingServiceExporter exporter
                = (RemotingServiceExporter) instance;
            
            if (exporter.getRemoteProtocol() instanceof Jaxws) {
                Jaxws protocol = (Jaxws) exporter.getRemoteProtocol();
                if (protocol.getJaxwsBinding() != null) {
                    bindings.add(protocol.getJaxwsBinding());
                }
            }
        }
        
        // bindings declared in the jaxws-spring manner
        //bindings.addAll(wac.getBeansOfType(SpringBinding.class).values());

        // create adapters
        ServletAdapterList l = new ServletAdapterList();
        for (SpringBinding binding : bindings) {
            binding.create(l);
        }

        m_delegate = new WSServletDelegate(l, getServletContext());
    }

    /** {@inheritDoc} */
    protected void doPost(HttpServletRequest request,
        HttpServletResponse response) throws ServletException {
        m_delegate.doPost(request, response, getServletContext());
    }

    /** {@inheritDoc} */
    protected void doGet(HttpServletRequest request,
        HttpServletResponse response) throws ServletException {
        m_delegate.doGet(request, response, getServletContext());
    }

    /** {@inheritDoc} */
    protected void doPut(HttpServletRequest request,
        HttpServletResponse response) throws ServletException {
        m_delegate.doPut(request, response, getServletContext());
    }

    /** {@inheritDoc} */
    protected void doDelete(HttpServletRequest request,
        HttpServletResponse response) throws ServletException {
        m_delegate.doDelete(request, response, getServletContext());
    }
}
