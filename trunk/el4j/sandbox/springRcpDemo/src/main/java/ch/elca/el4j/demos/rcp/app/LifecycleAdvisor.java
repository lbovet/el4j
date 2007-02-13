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
package ch.elca.el4j.demos.rcp.app;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.config.ApplicationWindowConfigurer;
import org.springframework.richclient.application.config.DefaultApplicationLifecycleAdvisor;

/**
 * 
 * This class is the Application Lifecycle Advisor. It provides hooks for 
 * executing code at certain points of the lifecycle.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author David Stefan (DST)
 */
public class LifecycleAdvisor extends DefaultApplicationLifecycleAdvisor {

    /**
     * The logger.
     */
    private static Log s_logger = LogFactory.getLog(LifecycleAdvisor.class);

    /**
     * This method is called prior to the opening of an application window. Note
     * at this point the window control has not been created. This hook allows
     * programmatic control over the configuration of the window (by setting
     * properties on the configurer) and it provides a hook where code that
     * needs to be executed prior to the window opening can be plugged in (like
     * a startup wizard, for example).
     * 
     * @param configurer
     *            The application window configurer
     */
    public void onPreWindowOpen(ApplicationWindowConfigurer configurer) {

        // If you override this method, it is critical to allow the superclass
        // implementation to run as well.
        super.onPreWindowOpen(configurer);

        // Uncomment to hide the menubar, toolbar, or alter window size...
        // configurer.setShowMenuBar(false);
        configurer.setShowToolBar(false);
        // configurer.setInitialSize(new Dimension(640, 480));
    }

    /**
     * Called just after the command context has been internalized. At this
     * point, all the commands for the window have been created and are
     * available for use. If you need to force the execution of a command prior
     * to the display of an application window (like a login command), this is
     * where you'd do it.
     * 
     * @param window
     *            The window who's commands have just been created
     */
    public void onCommandsCreated(ApplicationWindow window) {
        if (s_logger.isInfoEnabled()) {
            s_logger.info("onCommandsCreated( windowNumber="
                + window.getNumber() + " )");
        }
    }

    /**
     * Called after the actual window control has been created.
     * 
     * @param window
     *            The window being processed
     */
    public void onWindowCreated(ApplicationWindow window) {
        if (s_logger.isInfoEnabled()) {
            s_logger.info("onWindowCreated( windowNumber=" + window.getNumber()
                + " )");
        }
    }

    /**
     * Called immediately after making the window visible.
     * 
     * @param window
     *            The window being processed
     */
    public void onWindowOpened(ApplicationWindow window) {
        if (s_logger.isInfoEnabled()) {
            s_logger.info("onWindowOpened( windowNumber=" + window.getNumber()
                + " )");
        }
    }

    /**
     * Called when the window is being closed. This hook allows control over
     * whether the window is allowed to close. By returning false from this
     * method, the window will not be closed.
     * 
     * @param window
     *            The window being processed
     * @return boolean indicator if window should be closed. <code>true</code>
     *         to allow the close, <code>false</code> to prevent the close.
     */
    public boolean onPreWindowClose(ApplicationWindow window) {
        if (s_logger.isInfoEnabled()) {
            s_logger.info("onPreWindowClose( windowNumber=" + window.getNumber()
                + " )");
        }
        return true;
    }

    /**
     * Called when the application has fully started. This is after the initial
     * application window has been made visible.
     */
    public void onPostStartup() {
        if (s_logger.isInfoEnabled()) {
            s_logger.info("onPostStartup()");
        }
    }

}
