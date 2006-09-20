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
package ch.elca.el4j.services.gui.richclient;

import java.awt.EventQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationException;
import org.springframework.richclient.application.SplashScreen;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import ch.elca.el4j.core.context.ModuleApplicationContext;
import ch.elca.el4j.core.context.ModuleApplicationContextConfiguration;

/**
 * The main driver for a Spring Rich Client application. This class displays a
 * configurable splash screen and instantiates the rich client
 * <code>Application</code> instance. <b>ATTENTION:</b> This class has the
 * same name in Spring RCP. The idea is that the people from Spring RCP will
 * change their class in a next release so we do not have to serve a separate
 * class in the future. <script type="text/javascript">printFileStatus ("$URL:
 * https://svn.sourceforge.net/svnroot/el4j/trunk/el4j/framework/modules/spring_rcp/src/main/java/ch/elca/el4j/services/gui/richclient/ApplicationLauncher.java
 * $", "$Revision$", "$Date: 2006-03-13 14:15:43 +0100 (Mo, 13 Mrz 2006)
 * $", "$Author$" );</script>
 * 
 * @author Keith Donald
 * @author Martin Zeltner (MZE)
 * @author Alex Mathey (AMA)
 * @see Application
 */
public class ApplicationLauncher {
    /**
     * Id of the splash screen bean.
     */
    public static final String SPLASH_SCREEN_BEAN_ID = "splashScreen";

    /**
     * Id of the application bean.
     */
    public static final String APPLICATION_BEAN_ID = "application";

    /**
     * Private logger.
     */
    private static Log s_logger = LogFactory.getLog(ApplicationLauncher.class);

    /**
     * Startup application context.
     */
    private ApplicationContext m_startupContext;

    /**
     * Splash screen bean.
     */
    private SplashScreen m_splashScreen;

    /**
     * Main application context.
     */
    private ApplicationContext m_rootApplicationContext;

    /**
     * Root application context paths.
     */
    private String[] m_rootApplicationContextPaths;

    /**
     * Root application context configuration.
     */
    private ModuleApplicationContextConfiguration 
    m_rootApplicationContextConfiguration = null;

    /**
     * Launch the application using the spring application context at the
     * provided path for configuration.
     * 
     * @param rootContextPath
     *            the classpath application context path
     */
    public ApplicationLauncher(String rootContextPath) {
        this(new String[] {rootContextPath});
    }

    /**
     * Launch the application using the spring application context at the
     * provided paths for configuration.
     * 
     * @param rootContextPath
     *            the classpath application context paths
     */
    public ApplicationLauncher(String[] rootContextPath) {
        this(null, rootContextPath);
    }

    /**
     * Launch the application using the spring application context at the
     * provided paths for configuration. The startup context path is loaded
     * first to allow for quick loading of the application splash screen.
     * 
     * @param startupContext
     *            the startup context classpath
     * @param rootContextPath
     *            the classpath application context path
     */
    public ApplicationLauncher(String startupContext, String rootContextPath) {
        this(startupContext, new String[] {rootContextPath});
    }

    /**
     * Launch the application using the spring application context at the
     * provided paths for configuration. The startup context path is loaded
     * first to allow for quick loading of the application splash screen.
     * 
     * @param startupContextPath
     *            the startup context classpath
     * @param rootContextPath
     *            the classpath application context paths
     */
    public ApplicationLauncher(String startupContextPath,
        String[] rootContextPath) {
        Assert.notEmpty(rootContextPath, "One or more root rich client "
            + "application context paths must be provided");
        this.m_startupContext = loadStartupContext(startupContextPath);
        this.m_rootApplicationContextPaths = rootContextPath;
        launchMyRichClient();
    }

    /**
     * Launch the application from the pre-loaded application context.
     * 
     * @param startupContextPath
     *            the startup context classpath
     * @param rootApplicationContext
     *            the pre-loaded application context.
     */
    public ApplicationLauncher(String startupContextPath,
        ApplicationContext rootApplicationContext) {
        this.m_startupContext = loadStartupContext(startupContextPath);
        setRootApplicationContext(rootApplicationContext);
        launchMyRichClient();
    }

    /**
     * Launch the application from the pre-loaded application context.
     * 
     * @param rootApplicationContext
     *            the pre-loaded application context.
     */
    public ApplicationLauncher(ApplicationContext rootApplicationContext) {
        setRootApplicationContext(rootApplicationContext);
        launchMyRichClient();
    }

    /**
     * @param startupContextPath
     *            the startup context classpath
     * @param applicationContextConfiguration
     *            the configuration of the classpath application context
     */
    public ApplicationLauncher(String startupContextPath,
        ModuleApplicationContextConfiguration applicationContextConfiguration) {
        this.m_startupContext = loadStartupContext(startupContextPath);
        this.m_rootApplicationContextConfiguration = applicationContextConfiguration;
        launchMyRichClient();
    }

    /**
     * @param context
     *            Is the root application context to set.
     */
    private void setRootApplicationContext(ApplicationContext context) {
        Assert.notNull(context,
            "The root rich client application context is required");
        this.m_rootApplicationContext = context;
    }

    /**
     * Loads the startup application context.
     * 
     * @param startupContextPath
     *            Is the startup context path.
     * @return Returns the created appliaction context.
     */
    protected ApplicationContext loadStartupContext(String startupContextPath) {
        s_logger.debug("Loading startup context...");
        try {
            if (StringUtils.hasText(startupContextPath)) {
                return new ModuleApplicationContext(startupContextPath, false);
            }
        } catch (Exception e) {
            s_logger.warn("Exception occured initializing startup context.", e);
        }
        return null;
    }

    /**
     * Loads the root application context.
     * 
     * @param contextPaths
     *            Are the root context paths.
     * @return Returns the created appliaction context.
     */
    protected ApplicationContext loadRootApplicationContext(
        String[] contextPaths) {
        try {
            return new ModuleApplicationContext(contextPaths, false);
        } catch (Exception e) {
            s_logger.warn("Exception occured initializing application startup "
                + "context.", e);
            throw new ApplicationException(
                "Unable to start rich client application", e);
        }
    }

    /**
     * Loads the root application context from a
     * ModuleApplicationContextConfiguration.
     * 
     * @param applicationContextConfiguration
     *            The application context configuration to load the application
     *            context from
     * @return Returns the created application context.
     */
    protected ApplicationContext loadRootApplicationContext(
        ModuleApplicationContextConfiguration applicationContextConfiguration) {
        try {
            return new ModuleApplicationContext(applicationContextConfiguration
                .getInclusiveConfigLocations(), applicationContextConfiguration
                .getExclusiveConfigLocations(), applicationContextConfiguration
                .isAllowBeanDefinitionOverriding(),
                applicationContextConfiguration.getParent(),
                applicationContextConfiguration.isMergeWithOuterResources());
        } catch (Exception e) {
            s_logger.warn("Exception occured initializing application startup "
                + "context.", e);
            throw new ApplicationException(
                "Unable to start rich client application", e);

        }
    }

    /**
     * Launch this rich client application; with the startup context loading
     * first, built from the <code>startupContextPath</code> location in the
     * classpath.
     * <p>
     * It is recommended that the startup context contain contain a splash
     * screen definition for quick loading & display.
     * <p>
     * Once the splash screen is displayed, the main application context is then
     * initialized, built from the <code>contextPaths</code> location(s) in
     * the classpath. The root application bean is retrieved and the startup
     * lifecycle begins.
     */
    private void launchMyRichClient() {
        /**
         * If startup context exists display splash screen.
         */
        if (m_startupContext != null) {
            displaySplashScreen(m_startupContext);
        }

        /**
         * Load root application context if it is not already loaded.
         */
        if (m_rootApplicationContext == null) {

            if (m_rootApplicationContextConfiguration == null) {
                this.m_rootApplicationContext
                    = loadRootApplicationContext(m_rootApplicationContextPaths);
            } else {
                this.m_rootApplicationContext
                    = loadRootApplicationContext(
                        m_rootApplicationContextConfiguration);
            }
        }

        /**
         * If startup context is not available, lookup the splash screen in root
         * application context.
         */
        if (m_startupContext != null) {
            displaySplashScreen(m_rootApplicationContext);
        }

        /**
         * Load the application.
         */
        try {
            Application application = (Application) m_rootApplicationContext
                .getBean(APPLICATION_BEAN_ID, Application.class);
            application.openWindow(application.getLifecycleAdvisor()
                .getStartingPageId());
            application.getLifecycleAdvisor().onPostStartup();
        } catch (NoSuchBeanDefinitionException e) {
            s_logger.error("A single "
                + "org.springframework.richclient.Application bean definition "
                + "must be defined in the main application context", e);
            throw e;
        } catch (RuntimeException e) {
            s_logger
                .error("Exception occured initializing Application bean", e);
            throw new ApplicationException(
                "Unable to start rich client application", e);
        } finally {
            destroySplashScreen();
            s_logger.debug("Launcher thread exiting.");
        }
    }

    /**
     * Displays the splash screen if one exists.
     * 
     * @param beanFactory
     *            Is the bean factory where to get the spalsh screen bean.
     */
    private void displaySplashScreen(BeanFactory beanFactory) {
        try {
            if (beanFactory.containsBean(SPLASH_SCREEN_BEAN_ID)) {
                this.m_splashScreen = (SplashScreen) beanFactory.getBean(
                    SPLASH_SCREEN_BEAN_ID, SplashScreen.class);
                s_logger.debug("Displaying application splash screen.");
                this.m_splashScreen.splash();
            } else {
                s_logger.debug("No splash screen bean found.");
            }
        } catch (Exception e) {
            s_logger.warn("Unable to load and display startup splash screen.",
                e);
        }
    }

    /**
     * Destroys the splash screen.
     */
    private void destroySplashScreen() {
        if (m_splashScreen != null) {
            s_logger.debug("Closing splash screen.");
            new SplashScreenCloser(m_splashScreen);
        }
    }

    /**
     * Closes the splash screen in the event dispatching (GUI) thread.
     * 
     * @author Keith Donald
     * @see SplashScreen
     */
    public static class SplashScreenCloser {

        /**
         * Closes the currently-displayed, non-null splash screen.
         * 
         * @param splashScreen
         *            Is the splash screen to close.
         */
        public SplashScreenCloser(final SplashScreen splashScreen) {

            /*
             * Removes the splash screen. Invoke this <code> Runnable </code>
             * using <code> EventQueue.invokeLater </code> , in order to remove
             * the splash screen in a thread-safe manner.
             */
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    splashScreen.dispose();
                }
            });
        }
    }

}
