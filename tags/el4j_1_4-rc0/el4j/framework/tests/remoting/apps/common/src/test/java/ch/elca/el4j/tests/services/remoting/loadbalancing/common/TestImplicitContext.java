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
package ch.elca.el4j.tests.services.remoting.loadbalancing.common;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.elca.el4j.core.contextpassing.ImplicitContextPasser;
import ch.elca.el4j.core.contextpassing.ImplicitContextPassingRegistry;

/**
 * Defines the context that is implicitly passed to the server.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL: https://svn.sourceforge.net/svnroot/el4j/trunk/el4j/framework/demos/remoting/java/ch/elca/el4j/demos/remoting/TestImplicitContextPassingRegistry.java $",
 *    "$Revision: 245 $",
 *    "$Date: 2006-03-13 14:15:43 +0100 (Mon, 13 Mar 2006) $",
 *    "$Author: swisswheel $"
 * );</script>
 *
 * @author Stefan Pleisch (SPL)
 */
public class TestImplicitContext implements ImplicitContextPassingRegistry {

    /** Defines the value of the key if it has not yet been initialized. */
    public static final String NOT_INITIALIZED_KEY = "NOT_INITIALIZED";
          
    /**
     * {@inheritDoc}
     */
    public void registerImplicitContextPasser(
        ImplicitContextPasser passer) {
        // Do nothing.
    }

    /**
     * {@inheritDoc}
     */
    public void unregisterImplicitContextPasser(
        ImplicitContextPasser passer) {
        // Do nothing.
    }
    
    /**
     * {@inheritDoc}
     */
    public Map getAssembledImplicitContext() {
        Map map = new HashMap();
        map.put(UNIQUE_KEY, "testString");
        return map;
    }

    /**
     * {@inheritDoc}
     */
    public void pushAssembledImplicitContext(Map contexts) {
        s_logger.debug("Retrieving context: " + contexts.get(UNIQUE_KEY));
    }

    /** Name used for the unique key in the context map. */
    private static final String UNIQUE_KEY = "UNIQUE_KEY";
   
    /**
     * Private logger.
     */
    private static Log s_logger 
        = LogFactory.getLog(TestImplicitContext.class);
 

} // Class IdempotenceImplicitContext
