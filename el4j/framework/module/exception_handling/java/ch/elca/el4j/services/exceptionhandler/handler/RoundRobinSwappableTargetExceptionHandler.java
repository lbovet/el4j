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

package ch.elca.el4j.services.exceptionhandler.handler;

import java.util.List;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.InitializingBean;

import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;

/**
 * This class implements a round robin strategy to swap a proxy's target. It
 * even supports repairing targets that are not contained in the list of
 * alternative targets.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Andreas Bur (ABU)
 */
public class RoundRobinSwappableTargetExceptionHandler
    extends AbstractSwappableTargetExceptionHandler
    implements InitializingBean {

    /** The list with alternative targets, used one after the other. */
    private List m_targets;
    
    /**
     * Sets the list of targets that are used one after the other if the current
     * target doesn't work anymore.
     * 
     * @param targets
     *      The list of targets to set.
     */
    public void setTargets(List targets) {
        m_targets = targets;
    }

    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(
                m_targets, "targets", this);
    }

    /**
     * {@inheritDoc}
     */
    protected Object getNewTarget(Object current, Throwable t,
            MethodInvocation invocation, Log logger) throws Throwable {

        // even works if 'current' isn't contained in the 'targets'-list
        int i = (m_targets.indexOf(current) + 1) % m_targets.size();      
        return m_targets.get(i);
    }
}
