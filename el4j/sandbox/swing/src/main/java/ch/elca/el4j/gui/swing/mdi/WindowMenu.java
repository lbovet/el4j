package ch.elca.el4j.gui.swing.mdi;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;


/**
 * JMenu handling the Windows Menu for an MDI application.
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
public class WindowMenu extends JMenu {
    /**
     * The MDI window manager.
     */
    private WindowManager m_windowManager;

    /**
     * A minimal 'Window' menu containing only 'Close All'.
     */
    public WindowMenu() {
        this(new String[]{"closeAll"});
    }
    
    /**
     * A 'Window' menu containing the specified actions.
     * 
     * @param windowMenuActionNames   a list of actions as {@link String}s
     */
    public WindowMenu(String[] windowMenuActionNames) {
        createMenu("windowMenu", windowMenuActionNames);
    }

    
    /**
     * Set the window manager.
     * 
     * @param windowManager   the new window manager
     */
    public void setWindowManager(WindowManager windowManager) {
        m_windowManager = windowManager;
    }

    /**
     * Create the 'Window' menu.
     * @param menuName       the name of the menu
     * @param actionNames    the actions to be inserted
     */
    private void createMenu(String menuName, String[] actionNames) {
        setName(menuName);
        for (String actionName : actionNames) {
            if (actionName.equals("---")) {
                add(new JSeparator());
            } else {
                JMenuItem menuItem = new JMenuItem();
                menuItem.setAction(getAction(actionName));
                menuItem.setIcon(null);
                add(menuItem);
            }
        }
    }

    /**
     * @param actionName    the name of the action
     * @return              the action as {@link Action}
     */
    private javax.swing.Action getAction(String actionName) {
        ApplicationContext ac = Application.getInstance().getContext();
        return ac.getActionMap(this).get(actionName);
    }

    // All major window functions
    // Remark: A simple reflection-approach would prevent resource injection,
    //         so this approach is a little bit verbose, but it works well.
    
    /** @see WindowManager */
    @Action
    public void close() {
        if (m_windowManager != null) {
            m_windowManager.close();
        }
    }
    
    /** @see WindowManager */
    @Action
    public void closeAll() {
        if (m_windowManager != null) {
            m_windowManager.closeAll();
        }
    }
    
    /** @see WindowManager */
    @Action
    public void minimize() {
        if (m_windowManager != null) {
            m_windowManager.minimize();
        }
    }
    
    /** @see WindowManager */
    @Action
    public void minimizeAll() {
        if (m_windowManager != null) {
            m_windowManager.minimizeAll();
        }
    }
    
    /** @see WindowManager */
    @Action
    public void maximize() {
        if (m_windowManager != null) {
            m_windowManager.maximize();
        }
    }
    
    /** @see WindowManager */
    @Action
    public void maximizeAll() {
        if (m_windowManager != null) {
            m_windowManager.maximizeAll();
        }
    }
    
    /** @see WindowManager */
    @Action
    public void restore() {
        if (m_windowManager != null) {
            m_windowManager.restore();
        }
    }
    
    /** @see WindowManager */
    @Action
    public void restoreAll() {
        if (m_windowManager != null) {
            m_windowManager.restoreAll();
        }
    }
    
    /** @see WindowManager */
    @Action
    public void hide() {
        if (m_windowManager != null) {
            m_windowManager.hide();
        }
    }
    
    /** @see WindowManager */
    @Action
    public void hideAll() {
        if (m_windowManager != null) {
            m_windowManager.hideAll();
        }
    }
    
    /** @see WindowManager */
    @Action
    public void selectNext() {
        if (m_windowManager != null) {
            m_windowManager.selectNext();
        }
    }
    
    /** @see WindowManager */
    @Action
    public void selectPrevious() {
        if (m_windowManager != null) {
            m_windowManager.selectPrevious();
        }
    }
    
    /** @see WindowManager */
    @Action
    public void reset() {
        if (m_windowManager != null) {
            m_windowManager.reset();
        }
    }
    
    /** @see WindowManager */
    @Action
    public void resetAll() {
        if (m_windowManager != null) {
            m_windowManager.resetAll();
        }
    }
    
    /** @see WindowManager */
    @Action
    public void tileHorizontally() {
        if (m_windowManager != null) {
            m_windowManager.tileHorizontally();
        }
    }
    
    /** @see WindowManager */
    @Action
    public void tileVertically() {
        if (m_windowManager != null) {
            m_windowManager.tileVertically();
        }
    }
    
    /** @see WindowManager */
    @Action
    public void tile() {
        if (m_windowManager != null) {
            m_windowManager.tile();
        }
    }
    
    /** @see WindowManager */
    @Action
    public void cascade() {
        if (m_windowManager != null) {
            m_windowManager.cascade();
        }
    }
}
