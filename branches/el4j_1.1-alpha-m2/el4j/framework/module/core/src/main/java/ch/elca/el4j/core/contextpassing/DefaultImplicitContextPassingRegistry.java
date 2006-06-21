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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Default implementation of <code>ImplicitContextPassingRegistry</code>. To
 * register the implicit context passers, their classname is used as id.
 * 
 * <script type="text/javascript">printFileStatus 
 * ("$URL$",
 *  "$Revision$",
 *  "$Date$", 
 *  "$Author$" ); </script>
 * 
 * @author Andreas Pfenninger (APR)
 */
public class DefaultImplicitContextPassingRegistry implements
        ImplicitContextPassingRegistry {

    /** All registered passers. */
    private Map m_registeredPassers = new HashMap();

    /**
     * {@inheritDoc}
     */
    public void registerImplicitContextPasser(
            ImplicitContextPasser passer) {
        String id = passer.getClass().getName();
        m_registeredPassers.put(id, passer);
    }

    /**
     * {@inheritDoc}
     */
    public void unregisterImplicitContextPasser(
            ImplicitContextPasser passer) {
        String id = passer.getClass().getName();
        m_registeredPassers.remove(id);
    }

    /**
     * {@inheritDoc}
     */
    public Map getAssembledImplicitContext() {
        Map context = new HashMap();
        Iterator it = m_registeredPassers.keySet().iterator();
        while (it.hasNext()) {
            String id = (String) it.next();
            ImplicitContextPasser passer 
                = (ImplicitContextPasser) m_registeredPassers.get(id);
            Object ctx = passer.getImplicitlyPassedContext();
            context.put(id, ctx);
        }
        return context;
    }

    /**
     * {@inheritDoc}
     */
    public void pushAssembledImplicitContext(Map contexts) {
        Iterator it = m_registeredPassers.keySet().iterator();
        while (it.hasNext()) {
            String id = (String) it.next();
            ImplicitContextPasser passer 
                = (ImplicitContextPasser) m_registeredPassers.get(id);
            Object ctx = contexts.get(id);
            passer.pushImplicitlyPassedContext(ctx);
        }
    }
}
