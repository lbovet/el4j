/*
 * EL4J, the Enterprise Library for Java, complementing Spring http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * This program is published under the GNU General Public License (GPL) license.
 * http://www.gnu.org/licenses/gpl.txt
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch 
 */

package ch.elca.el4j.tests.remoting.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.elca.el4j.core.contextpassing.ImplicitContextPasser;
import ch.elca.el4j.core.contextpassing.ImplicitContextPassingRegistry;

/**
 * This class is used to test if the implicit context passing works.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public class TestImplicitContextPassingRegistry implements
        ImplicitContextPassingRegistry {
    /**
     * Private logger.
     */
    private static Log s_logger = LogFactory
            .getLog(TestImplicitContextPassingRegistry.class);

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
        map.put("testMessage", "Hello everybody, I am THE test message.");
        return map;
    }

    /**
     * {@inheritDoc}
     */
    public void pushAssembledImplicitContext(Map contexts) {
        s_logger.info("Test message: " + contexts.get("testMessage"));
    }
}