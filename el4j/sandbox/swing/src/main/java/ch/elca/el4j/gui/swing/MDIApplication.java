package ch.elca.el4j.gui.swing;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import org.bushe.swing.event.EventBus;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceMap;

import ch.elca.el4j.gui.swing.mdi.WindowManager;
import ch.elca.el4j.gui.swing.mdi.WindowMenu;


 /** 
 * Parent class for new MDI applications.
 *  
 *  Additional features:
 *   * allows adding internal frames (for the Documents of MDI) to the application
 *      Internal frames can (optionally) minimize themselves 
 *       { @link JInternalFrame#setIconifiable(boolean) }
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Stefan Wismer (SWI)
 * @author Philipp Oser (POS)
 */
public abstract class MDIApplication extends GUIApplication {
   
    /** 
     * The desktop pane of this MDIApplication.
     *  @see #createDefaultDesktopPane() 
     */
    protected JDesktopPane m_desktopPane;

    /**
     * Helps to manage the mdi menu and pane.
     */
    protected WindowManager m_windowManager;    
    
    
    /**
     * Adds an internal frame to the MDI application.
     * @param frame     the frame to add
     * @see #showInternalFrame(JInternalFrame,int)
     */
    protected void showInternalFrame(JInternalFrame frame) {
        showInternalFrame(frame, JLayeredPane.DEFAULT_LAYER);
    }

    /**
     * Adds an internal frame to the MDI application
     *   In particular: keeps track of the frame, adds listeners
     *    to frame events and ensures properties of the frame
     *    are stored persistently.
     * @param frame the internal frame to add
     * @param index the position at which to insert the 
     *          component, or -1 to append the component to the end
     */
    protected void showInternalFrame(JInternalFrame frame, int index) {
        ApplicationContext appContext = Application.getInstance().getContext();
        ResourceMap map = appContext.getResourceMap(frame.getClass());
        
        frame.addInternalFrameListener(new ListenerToEvent());

        // inject values from properties file
        map.injectComponents(frame);

        m_desktopPane.add(frame, index);
    }

    /**
     * Creates a default desktop pane with a default Menu This method could be
     * overridden in case you would like another desktop pane. <br>
     * Stores the created desktop pane in the {@link #m_desktopPane}
     */
    protected void createDefaultDesktopPane() {
        m_desktopPane = new JDesktopPane();

        // create window manager and add window menu
        WindowMenu windowMenu = new WindowMenu();
        m_windowManager = new WindowManager(m_desktopPane, windowMenu);
        windowMenu.setWindowManager(m_windowManager);
        getMainFrame().getJMenuBar().add(windowMenu,
            getMainFrame().getJMenuBar().getMenuCount() - 1);
    }

    /**
     * Helper that listens to events of the internal frames.
     */
    private final class ListenerToEvent implements InternalFrameListener {
        /** {@inheritDoc} */
        public void internalFrameClosing(InternalFrameEvent e) {
            EventBus.publish(e);
        }

        /** {@inheritDoc} */
        public void internalFrameClosed(InternalFrameEvent e) {
            EventBus.publish(e);
        }

        /** {@inheritDoc} */
        public void internalFrameOpened(InternalFrameEvent e) {
            EventBus.publish(e);
        }

        /** {@inheritDoc} */
        public void internalFrameIconified(InternalFrameEvent e) {
            EventBus.publish(e);
        }

        /** {@inheritDoc} */
        public void internalFrameDeiconified(InternalFrameEvent e) {
            EventBus.publish(e);
        }

        /** {@inheritDoc} */
        public void internalFrameActivated(InternalFrameEvent e) {
            EventBus.publish(e);
        }

        /** {@inheritDoc} */
        public void internalFrameDeactivated(InternalFrameEvent e) {
            EventBus.publish(e);
        }
    }
}
