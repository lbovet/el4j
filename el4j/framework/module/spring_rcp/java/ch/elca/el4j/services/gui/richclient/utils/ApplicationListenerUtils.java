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
package ch.elca.el4j.services.gui.richclient.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.richclient.application.Application;

/**
 * Util class for application listeners.
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
public class ApplicationListenerUtils {
    /**
     * Hide constructor.
     */
    protected ApplicationListenerUtils() { }
    
    /**
     * @return Returns the application event multicaster if it is available,
     *         otherwise <code>null</code>. 
     */
    public static ApplicationEventMulticaster getApplicationEventMulticaster() {
        ApplicationContext appContext 
            = Application.services().getApplicationContext();
        try {
            ApplicationEventMulticaster eventMulticaster 
                = (ApplicationEventMulticaster) appContext.getBean(
                    AbstractApplicationContext
                        .APPLICATION_EVENT_MULTICASTER_BEAN_NAME);
            return eventMulticaster;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Registers given application listener for application events.
     * 
     * @param l Is the application listener to register.
     * @return Returns <code>true</code> if the given application listener could
     *         be successfully registered.
     */
    public static boolean registerApplicationListener(
        ApplicationListener l) {
        boolean success = false;
        ApplicationEventMulticaster eventMulticaster 
            = getApplicationEventMulticaster();
        if (eventMulticaster != null) {
            eventMulticaster.addApplicationListener(l);
            success = true;
        }
        return success;
    }
    
    /**
     * Unregisters given application listener for application events.
     * 
     * @param l Is the application listener to unregister.
     * @return Returns <code>true</code> if the given application listener could
     *         be successfully unregistered.
     */
    public static boolean unregisterApplicationListener(
        ApplicationListener l) {
        boolean success = false;
        ApplicationEventMulticaster eventMulticaster 
            = getApplicationEventMulticaster();
        if (eventMulticaster != null) {
            eventMulticaster.removeApplicationListener(l);
            success = true;
        }
        return success;
    }
}
