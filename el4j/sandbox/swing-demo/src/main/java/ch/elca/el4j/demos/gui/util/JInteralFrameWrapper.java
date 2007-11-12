package ch.elca.el4j.demos.gui.util;

import java.awt.Component;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.event.InternalFrameEvent;

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventSubscriber;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceMap;

/**
 * A wrapper for panels to make them an internal frame.
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
public class JInteralFrameWrapper extends JInternalFrame
    implements FrameWrapper {
    
    /**
     * The wrapped panel.
     */
    private JPanel m_panel;
    
    /**
     * @param name     the name of the frame (e.g. for resource injection)
     * @param panel    the wrapped panel
     */
    public JInteralFrameWrapper(String name, JPanel panel) {
        if (EventSubscriber.class.isInstance(panel)) {
            m_panel = panel;
            EventBus.subscribe(InternalFrameEvent.class,
                (EventSubscriber<?>) panel);
        }
        setName(name);
        // set title (in case title is not set in properties file)
        setTitle(name);
        
        add(panel);
        
        ApplicationContext appContext = Application.getInstance().getContext();
        ResourceMap map = appContext.getResourceMap(panel.getClass());

        // inject values from properties file
        map.injectComponents(panel);

        setClosable(true);
        setResizable(true);
        setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
        pack();
        setVisible(true);
    }
    
    /** {@inheritDoc} */
    public Component getContent() {
        return m_panel;
    }
    
    /** {@inheritDoc} */
    @Override
    public void dispose() {
        if (m_panel != null) {
            EventBus.unsubscribe(InternalFrameEvent.class, m_panel);
        }
        super.dispose();
    }
}
