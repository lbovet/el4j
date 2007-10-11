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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.springframework.context.ApplicationContext;

import ch.elca.el4j.gui.swing.exceptions.Exceptions;

/** 
 * Parent class for new applications. (For MDI applications refer to 
 *  {@link ch.elca.el4j.gui.swing.MDIApplication })
 * 
 * Additional features:
 *   * give access to a Spring application context
 *   * allows installing a handler for uncaught exceptions
 *     @see ch.elca.el4j.gui.swing.exceptions.Exceptions 
 *     @see ch.elca.el4j.gui.swing.exceptions.Handler
 *   * defines a convenience method for menus
 *   * defines a getAction(String) method
 *      (refer to recommended programming pattern with this method of 
 *       the app framework )
 */
public abstract class GUIApplication extends SingleFrameApplication {
	
	private static final Logger logger = Logger.getLogger(GUIApplication.class.getName());	
	
    /**
     * The Spring context.
     */
    protected ApplicationContext m_springContext;

    /**
     * Sets the Spring context.
     * 
     * @param springContext     the new Spring context
     */
    public void setSpringContext(ApplicationContext springContext) {
        m_springContext = springContext;
    }

    /**
     * @return      the Spring context
     */
    public ApplicationContext getSpringContext() {
        return m_springContext;
    }

    /**
     * Launch the application and do some adaptations for Spring ( { @link GUIApplication } class
     *  documentation for more information)
     * 
     *  CAVEAT: We used cut&paste to extend the launch method of the Application 
     *  class. At each change of the class Application of the app framework, 
     *   adapt the code of this method (the app framework does not allow
     *   a "clean" extension).
     * 
     * @param applicationClass      the application class to launch
     * @param args                  command line arguments
     * @param springContext			spring application context
     */
    public static synchronized <T extends GUIApplication> void launch(
    		final Class<T> applicationClass, final String[] args, final ApplicationContext springContext) {

    	// install exception handler
    	Thread.setDefaultUncaughtExceptionHandler(Exceptions.getInstance());

    	Runnable doCreateAndShowGUI = new Runnable() {
    		public void run() {
    			try {
    				GUIApplication application = Application.create(applicationClass);
    				Application.setInstance(application);
    				
    				// new: set the spring context early
    				application.setSpringContext(springContext);
    				application.initialize(args);
    				application.startup();
    				application.waitForReady();
    			}
    			catch (Exception e) {
    				String msg = String.format("Application %s failed to launch", applicationClass);
    				logger.log(Level.SEVERE, msg, e);
    				throw(new Error(msg, e));
    			}
    		}
    	};
    	EventQueue.invokeLater(doCreateAndShowGUI);	        
    }

    /**
     * Creates a JMenu out of a String array containing action names.
     * A separator is represented by the string "---"
     * 
     * @param menuName      the menu name
     * @param actionNames   the array of menu items
     * @return              a JMenu
     */
    protected JMenu createMenu(String menuName, String[] actionNames) {
        JMenu menu = new JMenu();
        menu.setName(menuName);
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
        org.jdesktop.application.ApplicationContext ac
            = Application.getInstance().getContext();
        
        for (Object candidate: m_instancesWithActionMappings) {
        	Action foundAction = ac.getActionMap(candidate).get(actionName); 
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
    
    /**
     * Holds objects that contain @Action methods
     *  (in order to allow distributing Action methods on
     *   different classes.
     */
    protected List<Object> m_instancesWithActionMappings = new ArrayList<Object>();
    
    { 
    	// always add "this" to the list of objects where Action methods 
    	//  can be found 
    	addActionMappingInstance(this);
    }
    
    /**
     * Add an object with @Action methods to the list of 
     *  objects in which to look for actions. 
     *   BTW: "this" is always added as first element in
     *    this array.
     * @param o 
     */
    public void addActionMappingInstance (Object o) {
    	m_instancesWithActionMappings.add(o);
    }
    
    /*
     * @see #addActionMappingInstance
     */
    public void removeActionMappingInstance (Object o) {
    	m_instancesWithActionMappings.remove(o);
    }
    
}
