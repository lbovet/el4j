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
package ch.elca.el4j.services.gui.swing;

import java.awt.EventQueue;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.bushe.swing.event.EventServiceExistsException;
import org.bushe.swing.event.EventServiceLocator;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import ch.elca.el4j.core.context.ModuleApplicationContext;
import ch.elca.el4j.core.context.ModuleApplicationContextConfiguration;
import ch.elca.el4j.services.gui.swing.config.DefaultConfig;
import ch.elca.el4j.services.gui.swing.cookswing.TagLibraryFactory;
import ch.elca.el4j.services.gui.swing.cookswing.action.ActionsContextAware;
import ch.elca.el4j.services.gui.swing.eventbus.ExceptionThrowingEventService;
import ch.elca.el4j.services.gui.swing.exceptions.CookXmlExceptionHandler;
import ch.elca.el4j.services.gui.swing.exceptions.Exceptions;
import ch.elca.el4j.services.gui.swing.frames.ApplicationFrame;
import ch.elca.el4j.services.gui.swing.wrapper.JFrameWrapperFactory;
import ch.elca.el4j.util.config.GenericConfig;
import cookxml.cookswing.CookSwing;


/**
 * Parent class for GUI applications. (For MDI applications refer to
 *  {@link ch.elca.el4j.services.gui.swing.MDIApplication })
 *
 * Additional features:
 *  <ul>
 *   <li> give access to a Spring application context
 *   <li> allows installing a handler for uncaught exceptions, refer also to
 *         {@link ch.elca.el4j.services.gui.swing.exceptions.Exceptions} and
 *         {@link ch.elca.el4j.services.gui.swing.exceptions.Handler}
 *   <li> defines a convenience method for menus
 *   <li> defines a getAction(String) method
 *      (refer to recommended programming pattern with this method of
 *       the app framework )
 *   <li> defines an {@link ActionsContext} which allows to spread Actions over several classes
 *  </ul>
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 */
public abstract class GUIApplication extends SingleFrameApplication implements ActionsContextAware {

	/**
	 * The logger.
	 */
	private static final Logger s_logger = LoggerFactory.getLogger(
		GUIApplication.class);

	/**
	 * The Spring context.
	 */
	protected ApplicationContext springContext;
	
	/**
	 * The Actions context for resolving action names.
	 */
	protected ActionsContext actionsContext = ActionsContext.create(this);
	
	/**
	 * The configuration. This is used to set constant parameters like colors to mark values as
	 * invalid or default renderers.
	 */
	protected GenericConfig config;
	
	
	/**
	 * @return      the Spring application context
	 */
	public ApplicationContext getSpringContext() {
		return springContext;
	}
	
	/**
	 * Sets the Spring context.
	 *
	 * @param springContext     the new Spring context
	 */
	public void setSpringContext(ApplicationContext springContext) {
		if (springContext instanceof ModuleApplicationContext) {
			ModuleApplicationContext context = (ModuleApplicationContext) springContext;
			context.getBeanFactory().registerSingleton("GUIApplication", this);
			//context.getBeanFactory().registerSingleton("GUIApplicationConfig", getConfig());
		}
		this.springContext = springContext;
	}
	
	/**
	 * @return    the current configuration
	 */
	public GenericConfig getConfig() {
		return config;
	}
	
	/**
	 * @param config    the configuration to set
	 */
	public void setConfig(GenericConfig config) {
		this.config = config;
	}

	/**
	 * Launch the application and do some adaptations for Spring.
	 *
	 * @link GUIApplication } class documentation for more information) CAVEAT:
	 *       We used cut&paste to extend the launch method of the Application
	 *       class. At each change of the class Application of the app
	 *       framework, adapt the code of this method (the app framework does
	 *       not allow a "clean" extension).
	 * @param applicationClass
	 *            the application class to launch
	 * @param args
	 *            command line arguments
	 * @param contextConfig
	 *            spring application context configuration
	 */
	public static synchronized <T extends GUIApplication> void launch(
		final Class<T> applicationClass, final String[] args,
		final ModuleApplicationContextConfiguration contextConfig) {

		// install exception handler
		Thread.setDefaultUncaughtExceptionHandler(Exceptions.getInstance());
		// little hack to make it work also in Swing/AWT
		System.setProperty("sun.awt.exception.handler", Exceptions.class.getName());
		// register custom eventService that not only logs errors but also throws exceptions
		try {
			EventServiceLocator.setEventService(EventServiceLocator.SERVICE_NAME_EVENT_BUS,
				new ExceptionThrowingEventService());
		} catch (EventServiceExistsException e) {
			s_logger.warn("Unable to register EventService.", e);
		}
		
		// configure CookSwing
		CookSwing.setDefaultExceptionHandler(CookXmlExceptionHandler.getInstance());
		CookSwing.setDefaultAccessible(true);
		CookSwing.setSwingTagLibrary(TagLibraryFactory.getTagLibrary());

		Runnable doCreateAndShowGUI = new Runnable() {
			public void run() {
				try {
					GUIApplication application = Application.create(applicationClass);
					
					Application.setInstance(application);

					// new: set the spring context early
					application.setSpringContext(new ModuleApplicationContext(contextConfig));
					
					// set default config
					application.setConfig(new DefaultConfig());
					
					setupLookAndFeel(application);
					
					application.initialize(args);
					application.startup();
					application.waitForReady();
				} catch (Exception e) {
					String msg = String.format("Application %s failed to launch", applicationClass);
					s_logger.error(msg, e);
					throw (new Error(msg, e));
				}
			}
		};
		EventQueue.invokeLater(doCreateAndShowGUI);
	}
	
	/**
	 * @return    the current instance
	 */
	public static GUIApplication getInstance() {
		return (GUIApplication) Application.getInstance();
	}
	
	/**
	 * Show a component which should be created by Spring.
	 * @param beanName    the Spring bean name
	 */
	@SuppressWarnings("unchecked")
	public void show(String beanName) throws NoSuchBeanDefinitionException {
		if (!springContext.containsBean(beanName)) {
			throw new NoSuchBeanDefinitionException(beanName);
		}
		Class beanClass = springContext.getType(beanName);
		if (JComponent.class.isAssignableFrom(beanClass)) {
			show((JComponent) springContext.getBean(beanName));
		} else if (JDialog.class.isAssignableFrom(beanClass)) {
			show((JDialog) springContext.getBean(beanName));
		}
	}
	
	/**
	 * Show a nested component.
	 * @param component    the component to show
	 */
	public void show(JComponent component) {
		show(JFrameWrapperFactory.wrap(component));
	}
	
	/**
	 * Show a frame.
	 * @param frame    the frame to show
	 */
	public void show(ApplicationFrame frame) {
		frame.show();
	}
	
	/**
	 * Show the main frame.
	 * @param frame    the main frame
	 */
	public void showMain(JFrame frame) {
		setMainFrame(frame);
		showMain();
	}
	
	/**
	 * Show the main frame.
	 * @param component    the component to put into the main frame
	 */
	public void showMain(JComponent component) {
		super.show(component);
	}
	
	/**
	 * Show the main frame.
	 */
	public void showMain() {
		super.show(getMainFrame());
	}
	
	/** {@inheritDoc} */
	public ActionsContext getActionsContext() {
		return actionsContext;
	}
	
	/**
	 * Returns the action object for a specific object and action name.
	 * @param object        the object containing actions
	 * @param actionName    the action name as String
	 * @return              the corresponding action object
	 */
	public Action getAction(Object object, String actionName) {
		org.jdesktop.application.ApplicationContext ac
			= Application.getInstance().getContext();
		
		return ac.getActionMap(Object.class, object).get(actionName);
	}
	
	/**
	 * Returns the string for a specific resource id.
	 * @param id    the resource id
	 * @return      the corresponding string
	 */
	public String getString(String id) {
		org.jdesktop.application.ApplicationContext ac
			= Application.getInstance().getContext();

		return ac.getResourceMap().getString(id);
	}
	
	/** {@inheritDoc} */
	@Override
	protected void shutdown() {
		try {
			EventServiceLocator.setEventService(EventServiceLocator.SERVICE_NAME_EVENT_BUS, null);
		} catch (EventServiceExistsException e) {
			s_logger.warn("Unable to unregister EventService.", e);
		}
		
		if (springContext != null) {
			if (springContext instanceof ConfigurableApplicationContext) {
				((ConfigurableApplicationContext) springContext).close();
			}
		}
		super.shutdown();
	}

	/**
	 * Setup look and feel of the application.
	 * @param application    the GUI application
	 */
	private static void setupLookAndFeel(GUIApplication application) {
		// apply first look and feel in list that is available
		final String lnfKey = "Application.preferredLookAndFeel";
		String lnfs = application.getContext().getResourceMap().getString(lnfKey);
		if (lnfs != null) {
			for (String lnf : lnfs.split(",")) {
				try {
					lnf = lnf.trim();
					if (lnf.equalsIgnoreCase("system")) {
						String name = UIManager.getSystemLookAndFeelClassName();
						UIManager.setLookAndFeel(name);
					} else {
						UIManager.setLookAndFeel(lnf);
					}
					
					break;
				} catch (ClassNotFoundException e) {
					s_logger.info("Look and feel '" + lnf + "' is not available.");
					continue;
				} catch (InstantiationException e) {
					s_logger.info("Look and feel '" + lnf + "' is not available.");
					continue;
				} catch (IllegalAccessException e) {
					s_logger.info("Look and feel '" + lnf + "' is not available.");
					continue;
				} catch (UnsupportedLookAndFeelException e) {
					s_logger.info("Look and feel '" + lnf + "' is not available.");
					continue;
				
				}
			}
		}
	}
	
}
