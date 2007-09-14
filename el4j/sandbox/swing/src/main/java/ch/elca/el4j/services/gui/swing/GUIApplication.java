package ch.elca.el4j.services.gui.swing;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.springframework.context.ApplicationContext;

import ch.elca.el4j.services.gui.swing.exceptions.Exceptions;

public abstract class GUIApplication extends SingleFrameApplication {
    /**
     * The Spring context.
     */
    private static ApplicationContext s_springContext;

    /**
     * Sets the Spring context.
     * 
     * @param springContext     the new Spring context
     */
    public static void setSpringContext(ApplicationContext springContext) {
        GUIApplication.s_springContext = springContext;
    }

    /**
     * @return      the Spring context
     */
    public static ApplicationContext getSpringContext() {
        return s_springContext;
    }

    /**
     * Launch the application.
     * 
     * @param <T>                   the class type of the application
     * @param applicationClass      the application class
     * @param args                  command line arguments
     */
    public static synchronized <T extends Application> void launch(
            final Class<T> applicationClass, final String[] args) {
        
        // install exception handler
        Thread.setDefaultUncaughtExceptionHandler(Exceptions.getInstance());
        
        Application.launch(applicationClass, args);
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
                menuItem.setIcon(null);
                menu.add(menuItem);
            }
        }
        return menu;
    }
    
    /**
     * Returns the action object for an action name.
     * 
     * @param actionName   the action name as String
     * @return             the corresponding action object
     */
    protected javax.swing.Action getAction(String actionName) {
        org.jdesktop.application.ApplicationContext ac
            = Application.getInstance().getContext();
        return ac.getActionMap(this).get(actionName);
    }
}
