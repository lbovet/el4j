package ch.elca.el4j.services.gui.swing;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import org.bushe.swing.event.EventBus;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceMap;

import ch.elca.el4j.services.gui.swing.mdi.WindowManager;

public abstract class MDIApplication extends GUIApplication {

    protected WindowManager windowManager;
    protected JDesktopPane desktopPane;

    protected void show(JInternalFrame frame) {
        show(frame, JLayeredPane.DEFAULT_LAYER);
    }

    protected void show(JInternalFrame frame, int layer) {
        ApplicationContext appContext = Application.getInstance().getContext();
        ResourceMap map = appContext.getResourceMap(frame.getClass());
        
        frame.addInternalFrameListener(new ListenerToEvent());

        // inject values from properties file
        map.injectComponents(frame);

        desktopPane.add(frame, layer);
    }

    private final class ListenerToEvent implements InternalFrameListener {
        public void internalFrameClosing(InternalFrameEvent e) {
            EventBus.publish(e);
        }

        public void internalFrameClosed(InternalFrameEvent e) {
            EventBus.publish(e);
        }

        public void internalFrameOpened(InternalFrameEvent e) {
            EventBus.publish(e);
        }

        public void internalFrameIconified(InternalFrameEvent e) {
            EventBus.publish(e);
        }

        public void internalFrameDeiconified(InternalFrameEvent e) {
            EventBus.publish(e);
        }

        public void internalFrameActivated(InternalFrameEvent e) {
            EventBus.publish(e);
        }

        public void internalFrameDeactivated(InternalFrameEvent e) {
            EventBus.publish(e);
        }
    }
}
