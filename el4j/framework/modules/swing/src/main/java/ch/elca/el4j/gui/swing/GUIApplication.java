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
package ch.elca.el4j.gui.swing;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.UIManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bushe.swing.event.EventServiceExistsException;
import org.bushe.swing.event.EventServiceLocator;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

import ch.elca.el4j.core.context.ModuleApplicationContext;
import ch.elca.el4j.core.context.ModuleApplicationContextConfiguration;
import ch.elca.el4j.gui.swing.config.DefaultConfig;
import ch.elca.el4j.gui.swing.cookswing.TagLibraryFactory;
import ch.elca.el4j.gui.swing.eventbus.ExceptionThowingEventService;
import ch.elca.el4j.gui.swing.exceptions.CookXmlExceptionHandler;
import ch.elca.el4j.gui.swing.exceptions.Exceptions;
import ch.elca.el4j.gui.swing.frames.ApplicationFrame;
import ch.elca.el4j.gui.swing.wrapper.JFrameWrapperFactory;
import ch.elca.el4j.util.config.GenericConfig;

import cookxml.cookswing.CookSwing;
import cookxml.core.exceptionhandler.StrictExceptionHandler;


/**
 * Parent class for GUI applications. (For MDI applications refer to
 *  {@link ch.elca.el4j.gui.swing.MDIApplication })
 *
 * Additional features:
 *  <ul>
 *   <li> give access to a Spring application context
 *   <li> allows installing a handler for uncaught exceptions, refer also to {@link ch.elca.el4j.gui.swing.exceptions.Exceptions} and
 *         {@link ch.elca.el4j.gui.swing.exceptions.Handler}
 *   <li> defines a convenience method for menus
 *   <li> defines a getAction(String) method
 *      (refer to recommended programming pattern with this method of
 *       the app framework )
 *  </ul>
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Stefan Wismer (SWI)
 */
public abstract class GUIApplication extends SingleFrameApplication {

	/**
	 * The logger.
	 */
	private static final Log s_logger = LogFactory.getLog(
		GUIApplication.class);

	/**
	 * The Spring context.
	 */
	protected ApplicationContext m_springContext;
	
	/**
	 * The configuration. This is used to set constant parameters like colors to mark values as
	 * invalid or default renderers.
	 */
	protected GenericConfig m_config;
	
	
	/**
	 * @return      the Spring application context
	 */
	public ApplicationContext getSpringContext() {
		return m_springContext;
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
		m_springContext = springContext;
	}
	
	/**
	 * @return    the current configuration
	 */
	public GenericConfig getConfig() {
		return m_config;
	}
	
	/**
	 * @param config    the configuration to set
	 */
	public void setConfig(GenericConfig config) {
		m_config = config;
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
				new ExceptionThowingEventService());
		} catch (EventServiceExistsException e1) {
			s_logger.warn("Unable to register EventService");
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
		if (!m_springContext.containsBean(beanName)) {
			throw new NoSuchBeanDefinitionException(beanName);
		}
		Class beanClass = m_springContext.getType(beanName);
		if (JComponent.class.isAssignableFrom(beanClass)) {
			show((JComponent) m_springContext.getBean(beanName));
		} else if (JDialog.class.isAssignableFrom(beanClass)) {
			show((JDialog) m_springContext.getBean(beanName));
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

	/**
	 * Creates a JMenu out of a String array containing action names. A
	 * separator is represented by the string "---"
	 *
	 * @param menuName
	 *            the menu name
	 * @param actionNames
	 *            the array of menu items
	 * @return a JMenu
	 */
	protected JMenu createMenu(String menuName, String[] actionNames) {
		JMenu menu = new JMenu();
		menu.setName(menuName);
		return initMenu(actionNames, menu);
	}
	
	/**
	 * Creates a JPopupMenu out of a String array containing action names. A
	 * separator is represented by the string "---"
	 *
	 * @param actionNames
	 *            the array of menu items
	 * @return a JPopupMenu
	 */
	protected JPopupMenu createPopup(String[] actionNames) {
		JPopupMenu menu = new JPopupMenu();
		return initMenu(actionNames, menu);
	}
	
	/**
	 * Fills a menu with menu items.
	 *
	 * @param <T>            the menu type (e.g. JMenu, JPopupMenu)
	 * @param actionNames    the array of menu items
	 * @param menu           the menu to insert the items
	 * @return               a menu
	 */
	private <T extends JComponent> T initMenu(String[] actionNames, T menu) {
		for (String actionName : actionNames) {
			if (actionName.equals("---")) {
				menu.add(new JSeparator());
			} else {
				JMenuItem menuItem = new JMenuItem();
				menuItem.setAction(getAction(actionName));
				//menuItem.setIcon(null);
				menu.add(menuItem);
			}
		}
		return menu;
	}
	
	/**
	 * Returns the first action object found for an action name.
	 *  (Looks in the internal list of candidate objects.)
	 *
	 * @param actionName   the action name as String
	 * @return             the corresponding action object
	 * @see #addActionMappingInstance(Object)
	 */
	public Action getAction(String actionName) {
		for (Object candidate : m_instancesWithActionMappings) {
			Action foundAction = getAction(candidate, actionName);
			if (foundAction != null) {
				return foundAction;
			}
		}
		return null;
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
		
		return ac.getActionMap(object).get(actionName);
	}
	
	public Action getAction(Class<?> cls, Object object, String actionName) {
		org.jdesktop.application.ApplicationContext ac
			= Application.getInstance().getContext();
		
		return ac.getActionMap(cls, object).get(actionName);
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
	
	/**
	 * Holds objects that contain @Action methods
	 *  (in order to allow distributing Action methods on
	 *   different classes.
	 */
	protected List<Object> m_instancesWithActionMappings
		= new ArrayList<Object>();
	{
		// always add "this" to the list of objects where Action methods
		// can be found
		addActionMappingInstance(this);
	}
	
	/**
	 * Add an object with @Action methods to the list of
	 *  objects in which to look for actions.
	 *   BTW: "this" is always added as first element in
	 *    this array.
	 * @param o the object with @Action methods
	 */
	public void addActionMappingInstance(Object o) {
		m_instancesWithActionMappings.add(o);
	}
	
	/*
	 * @see #addActionMappingInstance
	 */
	/** {@inheritDoc} */
	public void removeActionMappingInstance(Object o) {
		m_instancesWithActionMappings.remove(o);
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
				} catch (Exception e) {
					s_logger.info("Look and feel '" + lnf + "' is not available.");
					// try next
					continue;
				}
			}
		}
	}
	
}
