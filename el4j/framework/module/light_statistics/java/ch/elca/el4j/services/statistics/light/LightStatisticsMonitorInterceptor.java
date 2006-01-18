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

package ch.elca.el4j.services.statistics.light;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

/**
 * Performance monitor interceptor that uses the JAMon library.
 * 
 * <p/>This one does not require logging to be set to <code>DEBUG</code> level,
 * in contrast to the one implemented by Spring.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Andreas Bur (ABU)
 * @see org.springframework.aop.interceptor.JamonPerformanceMonitorInterceptor
 */
public class LightStatisticsMonitorInterceptor implements MethodInterceptor {

    /**
     * {@inheritDoc}
     */
    public Object invoke(MethodInvocation invocation) throws Throwable {
        String name = invocation.getMethod().getDeclaringClass().getName()
            + "." + invocation.getMethod().getName();
        Monitor monitor = MonitorFactory.start(name);
        try {
            return invocation.proceed();
        } finally {
            monitor.stop();
        }
    }
}
