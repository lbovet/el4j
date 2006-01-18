/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.core.contextpassing;

import java.util.Map;

/**
 * Registry for implicit context passers. This registry has to be set up with
 * the remote module to enable implicit context passing. Every bean that uses 
 * passing of implicit contexts needs to have a context passer bean extending
 * <code>ImplicitContextPasser</code> and register it in the registry. 
 * In the client - server scenario, one registry instance on the client side and
 * another one on the server side is needed.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Andreas Pfenninger (APR)
 */
public interface ImplicitContextPassingRegistry {

    /**
     * Register a new implicit context passer to the registry.
     * 
     * @param passer
     *           The implicit context passer to register.
     */
    public void registerImplicitContextPasser(
            ImplicitContextPasser passer);
    
    /**
     * Unregister a registered implicit context passer.
     * 
     * @param passer
     *            The implicit context passer to unregister.
     */
    public void unregisterImplicitContextPasser(
            ImplicitContextPasser passer);

    /**
     * This method is used by remoting infrastructures on the client side to 
     * collect what needs to be added to the context. This method calls the 
     * <code>getImplicitlyPassedContext</code> method of all registered 
     * implicit context passers. 
     * It returns a map with the "id -> context" mapping that needs to be 
     * passed with the remote invocation.
     * 
     * @return The implicit context map.
     */
    public Map getAssembledImplicitContext();

    /**
     * This method is used by remoting infrastructures on the server side to 
     * push the context to the beans. It calls the 
     * <code>pushImplicitlyPassedContext</code> method on all registered 
     * implicit context passers.
     * Its context's parameter holds the "id -> context" mappings that are 
     * passed with the remote invocation. 
     * 
     * @param contexts 
     *            The received implicit context map that holds the 
     *            "id -> context" mappings that are passed with the remote
     *            invocation.
     */
    public void pushAssembledImplicitContext(Map contexts);

}
