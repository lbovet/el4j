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
package ch.elca.el4j.services.statistics.detailed.contextpassing;


import ch.elca.el4j.core.contextpassing.AbstractImplicitContextPasser;
import ch.elca.el4j.core.contextpassing.ImplicitContextPassingRegistry;
/**
 * 
 * This ImplicitContextPasser passes the SharedContext as implicit context.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Rashid Waraich (RWA)
 */
public class SharedContextImplicitContextPasser 
    extends AbstractImplicitContextPasser {
    
    /**
     * The key to the shared context.
     */
    public static final String SHARED_CONTEXT = "SHARED CONTEXT";

    /**
     * {@inheritDoc}
     */
    public Object getImplicitlyPassedContext() {
        return SharedContext.getContext().get(SHARED_CONTEXT);
    }

    /**
     * {@inheritDoc}
     */
    public void pushImplicitlyPassedContext(Object context) {
        SharedContext.getContext().put(SHARED_CONTEXT, context);
    }

    /**
     * {@inheritDoc}
     */
    public void setImplicitContextPassingRegistry(
        ImplicitContextPassingRegistry registry) {
        super.setImplicitContextPassingRegistry(registry);
    }
}
